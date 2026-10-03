public class Snake {
    private Position[] data;
    private int front;
    private int size;
    private int capacity;

    public Snake(Position tail, Position head) {
        capacity = 2;
        data = new Position[capacity];
        data[0] = tail;
        data[1] = head;
        front = 0;
        size = 2;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public int front() {
        return front;
    }

    private int physical(int logicalIndex) {
        return (front + logicalIndex) % capacity;
    }

    public void addHead(Position p) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        data[physical(size)] = p;
        size++;
    }

    public Position removeTail() {
        if (size == 0) {
            throw new IllegalStateException("La serpiente esta vacia");
        }
        Position tail = data[front];
        data[front] = null;
        front = (front + 1) % capacity;
        size--;
        return tail;
    }

    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + logicalIndex);
        }
        return data[physical(logicalIndex)];
    }

    private void resize(int newCapacity) {
        Position[] newData = new Position[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[physical(i)];
        }
        data = newData;
        capacity = newCapacity;
        front = 0;
    }

    public void move(Position newHead, boolean eats) {
        addHead(newHead);
        if (!eats) {
            removeTail();
        }
    }

    public Position cell(int i) {
        return data[i];
    }
}
