@SuppressWarnings("unchecked")
public class DynamicArray<T> {
    // invariantes
    // 1. 0 <= size <= capacity
    // 2. capacity >= 1
    // 3. 
    private T[] data;
    private int size;
    private int capacity;
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad no puede ser menor o igual a cero");
        }
        this.data = (T[]) new Object[initialCapacity];
        this.capacity = initialCapacity;
        this.size = 2;
    }
    
    public int size() {
        return size;
    }
    
    private int capacity() {
        return capacity;
    }
    
    public T get(int index) { // complejidad O(1)
        if (index < size && index >= 0){
            return data[index];
        }
        else {
            throw new IndexOutOfBoundsException("indice fuera del array");
        }
    }
    
    public void set(int index, T value) { // complejidad O(1)
        if (index < size && index >= 0) {
            data[index] = value;
        }
        else {
            throw new IndexOutOfBoundsException("indice fuera del array");
        }
    }

    public void append(T value) { // complejidad O(n), porque al crear un nuevo array, debo asignar 1 por 1 los valores del anterior array al sigui
        if (size == capacity) {
            resize(capacity*2);
        }
        data[size++] = value;
    }
    
    public T removeLast() {
        if (size == 0) {
            throw new IllegalArgumentException("La capacidad no puede ser menor o igual a cero");
        }

        T datoDevolver = data[size - 1];
        data[size - 1] = null;
        size--;
        return datoDevolver;
    }
    
    private void resize(int newCapacity) {
        T[] temporalArray = (T[]) new Object[newCapacity]; 
        for (int i = 0; i < size; i++) {
            temporalArray[i] = data[i]; 
        }

        data = null;
        data = temporalArray;
        this.capacity = newCapacity;
    }
}