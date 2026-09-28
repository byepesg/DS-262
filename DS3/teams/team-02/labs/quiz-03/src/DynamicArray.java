public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException(
                "initialCapacity must be >= 1, got " + initialCapacity);
        this.capacity = initialCapacity;
        this.size = 0;
        this.data = (T[]) new Object[initialCapacity];
    }

    public int size() { return size; }
    public int capacity() { return capacity; }

    public T get(int index) {
        checkIndex(index);
        return data[index];
    }

    public void set(int index, T value) {
        checkIndex(index);
        data[index] = value;
    }

    public void append(T value) {
        if (size == capacity) resize(2 * capacity);
        data[size] = value;
        size = size + 1;
    }

    public T removeLast() {
        if (size == 0)
            throw new java.util.NoSuchElementException(
                "removeLast() on empty DynamicArray");
        T value = data[size - 1];
        data[size - 1] = null; // no afecta capacity
        size = size - 1;
        return value;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i = i + 1) {
            newData[i] = data[i]; // copia elemento por elemento
        }
        data = newData;
        capacity = newCapacity;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException(
                "index " + index + " out of bounds for size " + size);
    }
}
