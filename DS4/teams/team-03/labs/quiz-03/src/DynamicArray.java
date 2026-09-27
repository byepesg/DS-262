public class DynamicArray<T> {
    private T[] data; //es un arreglo de cualquier tipo
    private int size;
    private int capacity;

    public DynamicArray(int initialCapacity) { //constructor
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad inicial debe ser >= 1");
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.data = (T[]) new Object[initialCapacity];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de los limites!");
        }
        
        return data[index];
    }

    public void set(int index, T value) {
        
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de los limites!");
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
            throw new IllegalStateException("El arreglo está vacío");
        }
        
        T temp = data[size - 1];
        
        data[size - 1] = null; //para que el garbage collector lo borre
        size--;
        
        return temp; 
    }

    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        
        //el copiado
        for (int i = 0; i < size; i++) { 
            newData[i] = data[i]; 
        }
        
        this.data = newData;
        this.capacity = newCapacity;

    }
}