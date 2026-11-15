public class CircularQueue {
    // una implementacion de queue usando arrays circulares
    private final double[] queue;
    private int write = 0;
    private int read = 0;
    private int size = 0;
    private final int capacity;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        this.capacity = capacity;
        this.queue = new double[capacity];
    }

    public void enqueue(double value) {
        if (isFull()) {
            throw new IllegalStateException("La cola está llena");
        }
        queue[write] = value;
        write = (write + 1) % capacity;
        size++;
    }

    public double dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }

        double value = queue[read];
        queue[read] = 0;
        read = (read + 1) % capacity;
        size--;
        return value;
    }

    public void slide(double value) {
        if (isFull()) {
            dequeue();
        }
        enqueue(value);
    }

    public double get(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("i=" + i);
        }
        return queue[(read + i) % capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}