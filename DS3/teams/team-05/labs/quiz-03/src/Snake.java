import java.util.Map;

public class Snake {
    private Position[] data;
    private int size;
    private int capacity;
    private int tail;
    private boolean lastOperationResized;

    public Snake(Position initialTail, Position initialHead) {
        this.capacity = 2;
        this.size = 2;
        this.tail = 0;
        this.data = new Position[capacity];
        this.data[0] = initialTail;
        this.data[1] = initialHead;
        this.lastOperationResized = false;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }
    
    public int tail() {
        return tail;
    }

    public boolean wasResized() {
        return lastOperationResized;
    }

    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + logicalIndex);
        }
        int physicalIndex = (tail + logicalIndex) % capacity;
        return data[physicalIndex];
    }

    public Position getPhysical(int physicalIndex) {
        if (physicalIndex < 0 || physicalIndex >= capacity) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }
        return data[physicalIndex];
    }

    public void addHead(Position p) {
        lastOperationResized = false;
        if (size == capacity) {
            resize(capacity * 2);
            lastOperationResized = true;
        }
        int physicalHeadIndex = (tail + size) % capacity;
        data[physicalHeadIndex] = p;
        size++;
    }

    public Position removeTail() {
        if (size == 0) {
            throw new IllegalStateException("La serpiente está vacía");
        }
        Position removedTail = data[tail];
        data[tail] = null;
        tail = (tail + 1) % capacity;
        size--;
        return removedTail;
    }

    private void resize(int newCapacity) {
        Position[] newData = new Position[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = get(i);
        }
        this.data = newData;
        this.capacity = newCapacity;
        this.tail = 0;
    }

    public void printPhysicalState(Map<Position, String> labels, boolean forcedResizeStatus) {
        System.out.print("[ ");
        for (int i = 0; i < capacity; i++) {
            if (data[i] == null) {
                System.out.print("null ");
            } else {
                System.out.print(labels.getOrDefault(data[i], data[i].toString()) + " ");
            }
        }
        System.out.println("] | tail=" + tail + " | size=" + size + " | capacity=" + capacity + " | resize=" + forcedResizeStatus);
    }
}