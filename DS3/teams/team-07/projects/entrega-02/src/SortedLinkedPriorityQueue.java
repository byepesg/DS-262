/**
 * Cola de prioridad implementada con una lista enlazada ordenada:
 * el primer nodo siempre es el de mayor prioridad.
 *
 * - insert: O(n), recorre hasta encontrar la posición.
 * - removeHighest / peekHighest: O(1), toma el primer nodo.
 * - remove / contains: O(n).
 *
 * Con empates, el nuevo elemento queda DESPUÉS de los iguales (estable).
 *
 * @param <T> tipo de los elementos
 */
public class SortedLinkedPriorityQueue<T extends Comparable<T>> implements PriorityQueueADT<T>
{
    private Node<T> head;
    private int size;

    public SortedLinkedPriorityQueue() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public void insert(T item) {
        Node<T> newNode = new Node<>(item);
        if (head == null || item.compareTo(head.data) < 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null && current.next.data.compareTo(item) <= 0)
                current = current.next;
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    @Override
    public T removeHighest() {
        if (isEmpty()) throw new IllegalStateException("La cola de prioridad está vacía");
        T item = head.data;
        head = head.next;
        size--;
        return item;
    }

    @Override
    public T peekHighest() {
        if (isEmpty()) throw new IllegalStateException("La cola de prioridad está vacía");
        return head.data;
    }

    @Override
    public boolean remove(T item) {
        if (head == null) return false;
        if (head.data.equals(item)) {
            head = head.next;
            size--;
            return true;
        }
        Node<T> current = head;
        while (current.next != null && !current.next.data.equals(item))
            current = current.next;
        if (current.next == null) return false;
        current.next = current.next.next;
        size--;
        return true;
    }

    @Override
    public boolean contains(T item) {
        for (Node<T> current = head; current != null; current = current.next)
            if (current.data.equals(item)) return true;
        return false;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int size() {
        return size;
    }

    /** Elementos en orden de prioridad: "[a, b, c]". O(n). */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<T> current = head; current != null; current = current.next) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
