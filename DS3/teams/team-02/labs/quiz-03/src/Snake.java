public class Snake {
    private Position[] data;
    private int size;
    private int capacity;
    private int start;

    public Snake(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException(
                "initialCapacity must be >= 1, got " + initialCapacity);
        this.capacity = initialCapacity;
        this.size = 0;
        this.start = 0;
        this.data = new Position[initialCapacity];
    }

    public int size() { return size; }
    public int capacity() { return capacity; }

    private int physicalIndex(int logicalIndex) {
        return (start + logicalIndex) % capacity;
    }

    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size)
            throw new IndexOutOfBoundsException(
                "logicalIndex " + logicalIndex + " out of bounds for size " + size);
        return data[physicalIndex(logicalIndex)];
    }

    // O(1) amortizado
    public void addHead(Position p) {
        if (size == capacity) resize(2 * capacity);
        int physIdx = physicalIndex(size);
        data[physIdx] = p;
        size = size + 1;
    }

    // O(1) worst-case: nunca dispara resize, nunca desplaza elementos
    public Position removeTail() {
        if (size == 0)
            throw new java.util.NoSuchElementException("removeTail() on empty Snake");
        Position result = data[start];
        data[start] = null;
        start = (start + 1) % capacity;
        size = size - 1;
        return result;
    }

    private void resize(int newCapacity) {
        Position[] newData = new Position[newCapacity];
        for (int i = 0; i < size; i = i + 1) {
            newData[i] = data[physicalIndex(i)]; // orden logico creciente
        }
        data = newData;
        start = 0;
        capacity = newCapacity;
    }

    // Solo para depuracion/traza (Pregunta 3f)
    public int startIndex() { return start; }
    public Position rawAt(int physicalIndex) { return data[physicalIndex]; }
}
