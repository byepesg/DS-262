public class Snake {
    private Position[] Data;   
    private int size, head, tail, capacity;

    public Snake(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad inicial debe ser al menos 1");
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.head = -1;
        this.tail = 0;
        this.Data = new Position[initialCapacity];
    }

    public void addHead(Position p) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        head = (head + 1) % capacity;
        
        Data[head] = p;
        size++;
    }

    public Position removeTail() {
        if (size == 0) {
            throw new IllegalStateException("No se puede eliminar de una serpiente vacia");
        }
        Position removed = Data[tail];
        Data[tail] = null;
        tail = (tail + 1) % capacity;
        size--;
        return removed;
    }

    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException("El indice esta fuera de rango: " + logicalIndex);
        }
        int actualIndex = (tail + logicalIndex) % capacity;
        return Data[actualIndex];
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.capacity;
    }

    private void resize(int newCapacity) {
        Position[] newData = new Position[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = Data[(tail + i) % capacity];
        }
        Data = newData;
        tail = 0;
        head = size - 1;
        capacity = newCapacity;
    }
}