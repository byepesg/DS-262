package triage;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Min-heap binario generico sobre un arreglo dinamico, con indices desde
 * cero: los hijos de i estan en 2i+1 y 2i+2 y su padre en (i-1)/2.
 *
 * Invariantes:
 * - forma: los elementos ocupan data[0..size-1] sin huecos (arbol completo),
 *   por lo que la altura es floor(log2 n);
 * - orden: para todo i > 0, data[padre(i)] es menor o igual que data[i]
 *   segun el comparador, por lo que el minimo esta en data[0].
 *
 * El orden lo define un Comparator, de modo que el mismo heap sirve para
 * tickets (ordenados por plazo) y para las distancias de Dijkstra.
 */
public class MinHeap<T> {

    private Object[] data;
    private int size;
    private final Comparator<? super T> cmp;

    public MinHeap(Comparator<? super T> cmp) {
        if (cmp == null) {
            throw new IllegalArgumentException("El comparador no puede ser null");
        }
        this.cmp = cmp;
        this.data = new Object[8];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** O(log n) en el peor caso; Theta(1) amortizado para crecer el arreglo. */
    public void insert(T value) {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, size);
            data = bigger;
        }
        data[size] = value;
        siftUp(size);
        size++;
    }

    /** Theta(1). */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("El heap esta vacio");
        }
        return get(0);
    }

    /** O(log n): el ultimo elemento pasa a la raiz y baja con sift-down. */
    public T removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("El heap esta vacio");
        }
        T min = get(0);
        size--;
        data[0] = data[size];
        data[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    /** Verifica la propiedad de orden en todo el arreglo, Theta(n). */
    public boolean isValid() {
        for (int i = 1; i < size; i++) {
            if (cmp.compare(get((i - 1) / 2), get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (cmp.compare(get(i), get(parent)) < 0) {
                swap(data, i, parent);
                i = parent;
            } else {
                return;
            }
        }
    }

    /** Iterativo: espacio auxiliar O(1). Baja por el hijo de mayor prioridad. */
    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            if (left >= size) {
                return;
            }
            int child = left;
            int right = left + 1;
            if (right < size && cmp.compare(get(right), get(left)) < 0) {
                child = right;
            }
            if (cmp.compare(get(child), get(i)) < 0) {
                swap(data, i, child);
                i = child;
            } else {
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private T get(int i) {
        return (T) data[i];
    }

    private static void swap(Object[] a, int i, int j) {
        Object tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    // ------------------------------------------------------------------
    // Heap-Sort in-place
    // ------------------------------------------------------------------

    /**
     * Ordena ascendentemente segun cmp con Heap-Sort in-place:
     * build-heap bottom-up de un max-heap en O(n) y luego n-1 extracciones
     * del maximo hacia el final del arreglo, cada una O(log n).
     * Tiempo Theta(n log n) en el peor caso; espacio auxiliar O(1).
     */
    public static <T> void heapSort(T[] a, Comparator<? super T> cmp) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDownMax(a, n, i, cmp);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(a, 0, end);
            siftDownMax(a, end, 0, cmp);
        }
    }

    private static <T> void siftDownMax(T[] a, int size, int i, Comparator<? super T> cmp) {
        while (true) {
            int left = 2 * i + 1;
            if (left >= size) {
                return;
            }
            int child = left;
            int right = left + 1;
            if (right < size && cmp.compare(a[right], a[left]) > 0) {
                child = right;
            }
            if (cmp.compare(a[child], a[i]) > 0) {
                swap(a, i, child);
                i = child;
            } else {
                return;
            }
        }
    }
}
