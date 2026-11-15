  
@SuppressWarnings("unchecked")
  
public class DynamicArray<T> {
            
  private T[] data;
  private int size;
  private int capacity;
        
  public DynamicArray(int initialCapacity) {
                
    if (initialCapacity < 1) {
                    
        throw new IllegalArgumentException("La capacidad inicial debe ser al menos 1");
                    
    }
                
    this.capacity = initialCapacity;
    this.size = 0;
    this.data = (T[]) new Object[initialCapacity];
                
  }
        
  public int size() {
      
    return this.size;
    
  }
        
  public int capacity() {
      
    return this.capacity;
    
  }
        
  public T get(int index) {
      
    if (index < 0 || index >= size) {
        
        throw new IndexOutOfBoundsException("El indice esta fuera de rango: " + index);
    }
    
    return data[index];
    
  }
        
  public void set(int index, T value) {
      
    if (index < 0 || index >= size) {
        
        throw new IndexOutOfBoundsException("El indice esta fuera de rango: " + index);
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
                    
        throw new IllegalStateException("No se puede eliminar de un arreglo vacio");
                    
    }
                
    T removedValue = data[size - 1];
    data[size - 1] = null;
    size--;
    return removedValue;
    
  }
        
  private void resize(int newCapacity) {
                
    T[] newData = (T[]) new Object[newCapacity];
    for (int i = 0; i < size; i++) {
                    
        newData[i] = data[i];
                    
    }
                
    this.data = newData;
    this.capacity = newCapacity;
    
  }
}