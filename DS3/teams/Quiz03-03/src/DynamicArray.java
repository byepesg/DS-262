public class DynamicArray<T> {
 
    private Object[] data;
    private int size;
    private int capacity;
 
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException(
                "initialCapacity debe ser >= 1, se recibió: " + initialCapacity);
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.data = new Object[capacity];
    }
 
    public int size() {
        return size;
    }
 
    public int capacity() {
        return capacity;
    }
 
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }
 /// prueba 2
    public void set(int index, T value) {
        checkIndex(index);
        data[index] = value;
    }
 
    public void append(T value) {
        if (size == capacity) {
            int newCapacity = 2 * capacity; // política fija del enunciado
            resize(newCapacity);
        }
        data[size] = value;
        size = size + 1;
    }
 
    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (size == 0) {
            throw new IllegalStateException("removeLast() sobre un DynamicArray vacío");
        }
        T value = (T) data[size - 1];
        data[size - 1] = null; // se libera la referencia, pero NO se reduce capacity
        size = size - 1;
        return value;
    }
 
    /**
     * Redimensiona el arreglo físico a newCapacity, copiando explícitamente
     * elemento por elemento (sin Arrays.copyOf ni System.arraycopy) y
     * preservando el orden lógico.
     */
    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
        capacity = newCapacity;
    }
 
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "Índice " + index + " fuera de rango [0, " + size + ")");
        }
    }
}