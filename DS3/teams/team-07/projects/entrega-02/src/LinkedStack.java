/**
 * Pila (LIFO) enlazada. Se usa para el carrito de selección: deshacer la
 * última acción es sacar el elemento del tope.
 *
 * @param <T> tipo de los elementos
 */
public class LinkedStack<T>
{
    private Node<T> top;
    private int size;

    public LinkedStack() {
        this.top = null;
        this.size = 0;
    }

    /** Agrega un elemento en el tope. O(1). */
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /** Saca y devuelve el elemento del tope. O(1). */
    public T pop() {
        if (isEmpty()) throw new IllegalStateException("La pila está vacía");
        T item = top.data;
        top = top.next;
        size--;
        return item;
    }

    /** Devuelve el elemento del tope sin sacarlo. O(1). */
    public T peek() {
        if (isEmpty()) throw new IllegalStateException("La pila está vacía");
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}
