public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad debe ser mayor o igual a 1");
        }
        capacity = initialCapacity;
        data = (T[]) new Object[capacity];
        size = 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
        }
        return data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + index);
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
            throw new IllegalStateException("El arreglo esta vacio");
        }
        size--;
        T value = data[size];
        data[size] = null;
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
