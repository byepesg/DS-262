public class Snake {
    private Position[] snake;
    private int head, size, capacity;

    public Snake() {
        this.snake = new Position[2];
        this.capacity = 2;
        this.size = 0;
        this.head = 0;
    }

    public int capacity() {
        return this.capacity;
    }

    private int tail() {
        return (this.head - this.size + this.capacity) % this.capacity;
    }

    public void addHead(Position p) {
        if (this.size == this.capacity)
            this.resize(this.capacity * 2);

        this.snake[this.head] = p;
        this.head = (this.head + 1) % this.capacity;
        this.size++;
    }

    public Position removeTail() {
        if (this.size == 0)
            throw new IllegalStateException("Cannot remove the tail of an empty snake.");

        int tail = this.tail();
        Position p = this.snake[tail];
        this.snake[tail] = null;
        this.size--;

        return p;
    }

    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= this.size)
            throw new IndexOutOfBoundsException("Invalid index value, must be 0 <= index < size");

        return this.snake[(this.tail() + logicalIndex) % this.capacity];
    }

    private void resize(int newCapacity) {
        Position[] newArray = new Position[newCapacity];

        for (int i = 0; i < this.size; i++){
            newArray[i] = this.get(i);
        }

        this.snake = newArray;
        this.capacity = newCapacity;
        this.head = this.size;
    }

    @Override
    public String toString() {
        StringBuilder physical = new StringBuilder("[");
        for (int i = 0; i < this.capacity; i++) {
            Position p = this.snake[i];
            physical.append(p == null ? "_" : format(p));
            if (i < this.capacity - 1)
                physical.append(", ");
        }
        physical.append("]");

        StringBuilder logical = new StringBuilder("[");
        for (int i = 0; i < this.size; i++) {
            logical.append(format(this.get(i)));
            if (i < this.size - 1)
                logical.append(", ");
        }
        logical.append("]");

        return String.format("physical=%-44s head=%d tail=%d size=%d capacity=%d logical(tail->head)=%s",
                physical, this.head, this.tail(), this.size, this.capacity, logical);
    }

    private static String format(Position p) {
        return "(" + p.row() + "," + p.column() + ")";
    }
}