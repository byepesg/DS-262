package pathlab;

/** Cola FIFO de tamaño fijo, implementada sobre un arreglo circular. */
public final class CircularQueue<T> {
    private final Object[] elements;
    private int head;
    private int size;

    public CircularQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacidad positiva requerida.");
        elements = new Object[capacity];
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public void add(T element) {
        if (element == null) throw new IllegalArgumentException("No se permiten elementos nulos.");
        if (size == elements.length) throw new IllegalStateException("Cola llena.");
        int tail = (head + size) % elements.length;
        elements[tail] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T remove() {
        if (isEmpty()) throw new IllegalStateException("Cola vacía.");
        T result = (T) elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return result;
    }
}
