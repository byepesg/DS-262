import java.util.NoSuchElementException;

/**
 * Pregunta 1 - Arreglo dinamico implementado desde cero.
 *
 * Invariantes (se cumplen antes y despues de cada operacion publica):
 *   I1. 1 <= capacity  y  0 <= size <= capacity
 *   I2. capacity == data.length
 *   I3. El elemento logico i (0 <= i < size) esta guardado en data[i]
 *       (los elementos ocupan un prefijo contiguo del arreglo, en orden).
 *   I4. data[i] == null para todo size <= i < capacity (no quedan referencias viejas).
 *   I5. capacity nunca disminuye (removeLast no contrae).
 *
 * Complejidad worst-case:  get O(1), set O(1), removeLast O(1),
 *                          append O(n) (solo cuando hay resize), O(1) amortizado.
 */
public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity debe ser >= 1, fue " + initialCapacity);
        }
        this.capacity = initialCapacity;
        this.data = (T[]) new Object[initialCapacity];   // new T[n] no compila (type erasure)
        this.size = 0;
    }

    public int size()     { return size; }
    public int capacity() { return capacity; }

    /** O(1) worst-case: acceso directo por indice. */
    public T get(int index) {
        checkIndex(index);
        return data[index];
    }

    /** O(1) worst-case. */
    public void set(int index, T value) {
        checkIndex(index);
        data[index] = value;
    }

    /** O(n) worst-case (si hay resize), O(1) amortizado. */
    public void append(T value) {
        if (size == capacity) {                 // lleno: la siguiente insercion duplica
            if (capacity > Integer.MAX_VALUE / 2) {
                throw new IllegalStateException("capacidad maxima alcanzada");
            }
            resize(2 * capacity);
        }
        data[size] = value;
        size++;
    }

    /** O(1) worst-case. No reduce la capacidad. */
    public T removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("removeLast sobre un arreglo vacio");
        }
        size--;
        T value = data[size];
        data[size] = null;                      // mantiene I4 y evita "loitering"
        return value;
    }

    /** Theta(size): copia elemento por elemento (sin Arrays.copyOf ni System.arraycopy). */
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
        capacity = newCapacity;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("indice " + index + " fuera de [0, " + size + ")");
        }
    }
}
