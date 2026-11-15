/**
 * Cola (FIFO) enlazada con referencias al frente y al final. Modela el orden
 * de llegada de los estudiantes dentro de una franja de citas.
 *
 * @param <T> tipo de los elementos
 */
public class LinkedQueue<T>
{
    private Node<T> front;
    private Node<T> rear;
    private int size;

    public LinkedQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    /** Agrega un elemento al final. O(1). */
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    /** Saca y devuelve el elemento del frente. O(1). */
    public T dequeue() {
        if (isEmpty()) throw new IllegalStateException("La cola está vacía");
        T item = front.data;
        front = front.next;
        if (front == null)
            rear = null;
        size--;
        return item;
    }

    /** Devuelve el elemento del frente sin sacarlo. O(1). */
    public T peek() {
        if (isEmpty()) throw new IllegalStateException("La cola está vacía");
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }
}
