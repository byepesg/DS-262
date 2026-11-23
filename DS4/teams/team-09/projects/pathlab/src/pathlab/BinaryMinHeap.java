package pathlab;

import java.util.Arrays;

/** Heap mínimo binario, con crecimiento geométrico del arreglo interno. */
public final class BinaryMinHeap<T extends Comparable<? super T>> {
    private Object[] heap = new Object[16];
    private int size;

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public void add(T item) {
        if (item == null) throw new IllegalArgumentException("No se permiten nulos.");
        if (size == heap.length) heap = Arrays.copyOf(heap, heap.length * 2);
        int index = size++;
        while (index > 0) {
            int parent = (index - 1) / 2;
            T above = at(parent);
            if (item.compareTo(above) >= 0) break;
            heap[index] = above;
            index = parent;
        }
        heap[index] = item;
    }

    public T removeMin() {
        if (isEmpty()) throw new IllegalStateException("Heap vacío.");
        T minimum = at(0);
        T last = at(--size);
        heap[size] = null;
        if (size == 0) return minimum;
        int parent = 0;
        while (2 * parent + 1 < size) {
            int child = 2 * parent + 1;
            if (child + 1 < size && at(child + 1).compareTo(at(child)) < 0) child++;
            if (last.compareTo(at(child)) <= 0) break;
            heap[parent] = at(child);
            parent = child;
        }
        heap[parent] = last;
        return minimum;
    }

    @SuppressWarnings("unchecked")
    private T at(int index) { return (T) heap[index]; }
}
