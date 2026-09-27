package src;
public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity debe ser >= 1");
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.data = (T[]) new Object[capacity];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        return data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        data[index] = value;
    }

    public void append(T value) {
        if (size == capacity) {
            resize(capacity * 2); 
            //Si esta lleno duplica capacidad y lo copia
        }
        data[size] = value;
        size++;
    }

    public T removeLast() {
        if (size == 0) {
            throw new IllegalStateException("La estructura está vacía");
        }
        T value = data[size - 1]; //Lee
        data[size - 1] = null; // Limpia
        size--; //Disminuye el tamaño
        return value;    //Devuelve el valor
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        //Aqui se hace la copia del arreglo y se duplica su tamaño
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
        capacity = newCapacity;
    }
}
