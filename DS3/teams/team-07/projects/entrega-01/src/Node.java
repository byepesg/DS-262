/**
 * Nodo de una lista simplemente enlazada. Lo usan LinkedQueue y LinkedStack.
 *
 * @param <T> tipo del dato que guarda el nodo
 */
public class Node<T>
{
    public T data;
    public Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}
