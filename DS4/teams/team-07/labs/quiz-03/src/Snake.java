package src;
public class Snake {
    
    private Object[] data;
    private int capacity;
    private int size;
    private int tailIndex;
    public Snake(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity debe ser >= 1");
        }
        capacity = initialCapacity;
        size = 0;
        tailIndex = 0;
        data = new Object[capacity];
    }

    private int physicalIndex(int LogicalIndex) {
        return (tailIndex + LogicalIndex) % capacity;
        // TailIndex es la posicion fisica en cada momento 
        // y logical index seria la posocion logica del arreglo
    }
    
    public void addHead(Position p){
        if (size == capacity){
            resize(capacity*2);
        }
        int newHeadPhyisical = (tailIndex+size) % capacity; //Seria la nueva posicion fisica + la cabeza
        data[newHeadPhyisical] = p; //Recibe una nueva posicon p
        size++;
    }

    public Position removeTail(){
        if (size == 0) {
                throw new IllegalStateException("Snake vacía");
        }
        Position value = (Position) data[tailIndex];
        data[tailIndex] = null;
        tailIndex = (tailIndex+1) % capacity;
        size--;
        return value;
    }

    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (Position) data[physicalIndex(logicalIndex)];
    }

    public int size() { 
        return size; 
    }

    public int capacity() { 
        return capacity; 
    }

    public void resize(int newCapacity){
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i< size; i++){
            newData[i] = data[i];
        }
        data = newData;
        tailIndex = 0; //Para que no copie los datos perdidos o Null
        capacity = newCapacity;
    }
}
