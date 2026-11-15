package QUIZ_3;

import java.util.NoSuchElementException;

public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            // Validamos que la capacidad inicial no sea menor a 1
            throw new IllegalArgumentException("La capacidad inicial debe ser al menos 1"); //
        }
        this.capacity = initialCapacity;
        this.size = 0;
        
        // Dado que new T[n] no compila en Java, usamos Object y hacemos cast
        this.data = (T[]) new Object[initialCapacity]; //
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.capacity;
    }

    public T get(int index) {
        // Validamos contra SIZE, no contra capacity. Tener espacio no significa tener un elemento.
        if (index < 0 || index >= this.size) { //
            throw new IndexOutOfBoundsException("Índice fuera del rango lógico"); //
        }
        return this.data[index];
    }

    public void set(int index, T value) {
        // Misma validación estricta con size
        if (index < 0 || index >= this.size) { //
            throw new IndexOutOfBoundsException("Índice fuera del rango lógico"); //
        }
        this.data[index] = value;
    }

    public void append(T value) {
        // Si el tamaño lógico alcanza la capacidad física, debemos duplicar el espacio
        if (this.size == this.capacity) { //
            resize(this.capacity * 2); //
        }
        // Insertamos en el primer espacio disponible
        this.data[this.size] = value;
        this.size++;
    }

    public T removeLast() {
        if (this.size == 0) {
            //si está vacío lanzamos esta excepción
            throw new NoSuchElementException("No se puede eliminar de una estructura vacía");
        }
        
        // Reducimos el tamaño lógico. El dato físico queda en memoria, pero lógicamente ya no es accesible. 
        this.size--; 
        return this.data[this.size]; 
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        // La copia se hace elemento por elemento, sin usar System.arraycopy
        for (int i = 0; i < this.size; i++) { //
            newData[i] = this.data[i];
        }
        this.data = newData;
        this.capacity = newCapacity;
    }
}