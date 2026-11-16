import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista simplemente enlazada con referencias al primero y al último nodo.
 * Se usa para los inscritos de un grupo.
 *
 * @param <T> tipo de los elementos
 */
public class LinkedList<T> implements Iterable<T>
{
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /** Agrega al final. O(1) gracias a tail. */
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    /** Elimina la primera aparición de item. O(n). */
    public boolean remove(T item) {
        Node<T> previous = null;
        Node<T> current = head;
        while (current != null && !current.data.equals(item)) {
            previous = current;
            current = current.next;
        }
        if (current == null) return false;
        if (previous == null) head = current.next;
        else previous.next = current.next;
        if (current == tail) tail = previous;
        size--;
        return true;
    }

    /** O(n). */
    public boolean contains(T item) {
        for (Node<T> current = head; current != null; current = current.next)
            if (current.data.equals(item)) return true;
        return false;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) throw new NoSuchElementException();
                T item = current.data;
                current = current.next;
                return item;
            }
        };
    }

    /** "[a, b, c]". O(n). */
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
