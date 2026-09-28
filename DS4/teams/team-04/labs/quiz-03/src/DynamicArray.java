import java.util.NoSuchElementException;

//Creacion de arreglo dinamico 
public class DynamicArray<T> {
    private T[] data; //Datos del arreglo
    private int size; //Tamaño actual del arreglo
    private int capacity;
    
@SuppressWarnings("unchecked")
    //constructor con capacidad inicial que es lo mismo a la capacidad definida en el arreglo
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad inicial debe ser mayor que cero");
        }

        this.data = (T[]) new Object[initialCapacity]; //aqui se verifica que el arreglo sea de tipo generico y se crea un nuevo arreglo de objetos con la capacidad inicial
        this.size = 0; // tamaño inicial del arreglo es 0 porque solo va a almacenar elementos cuando se agreguen
        this.capacity = initialCapacity; // la capacidad inicial del arreglo es la que se pasa como parametro
    }
    
    public int size() {
        return size; 
    }

    public int capacity() {
        return capacity; 
    }

    public T get (int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de rango / No valido ddebe estar entre 0 y " + (size - 1));
        }
        return data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de rango");
        }
        data[index] = value; // se cambia el valor del elemento en la posicion index por el valor T
    }

@SuppressWarnings({"unchecked", "ManualArrayToCollectionCopy"})
    private void resize(int newCapacity) {
        if (newCapacity < size) {
            throw new IllegalArgumentException("La nueva capacidad debe ser mayor o igual al tamaño actual");
        }
        
        T[] newData = (T[]) new Object[newCapacity]; // se crea un nuevo arreglo de objetos con la nueva capacidad
        
        for (int i = 0; i < size; i++) {
            newData[i] = data[i]; // se copian los elementos del arreglo original al nuevo arreglo
        }
        data = newData; // se asigna el nuevo arreglo al arreglo original
        capacity = newCapacity; // se actualiza la capacidad del arreglo
    }

    public void append(T value) {
        if (size == capacity) {
            resize (capacity * 2);
        }

        data[size] = value; // se agrega el valor al final del arreglo
        size++; // se incrementa el tamaño del arreglo
    }

    public T removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("No hay elementos para eliminar");
        }
        T removedValue = data[size - 1]; // se guarda el valor del ultimo elemento del arreglo
        data[size - 1] = null; // se elimina el ultimo elemento del arreglo
        size--;
        return removedValue;
    }
}
        

        

    
