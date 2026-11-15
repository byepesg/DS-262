public class DynamicArray<T> {

    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacidad inicial debe ser minimo de 1");
        }

        capacity = initialCapacity;
        size = 0;
        data = (T[]) new Object[capacity];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        return data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        data[index] = value;
    }

    public void append(T value) {
        if (size == capacity) {
            resize(capacity * 2);
        }

        data[size] = value;
        size++;
    }

    public T removeLast() {
        if (size == 0) {
            throw new IllegalStateException("El Array está vacio");
        }

        T value = data[size - 1];

        data[size - 1] = null;
        size--;

        return value;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];

        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }

        data = newData;
        capacity = newCapacity;
    }
}