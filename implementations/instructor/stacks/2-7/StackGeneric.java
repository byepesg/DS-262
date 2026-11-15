/**
 * Section 2.7 - Generic Stack Interface
 *
 * Defines the operations that every generic
 * stack implementation must provide.
 */
public interface StackGeneric<T> {

    public boolean empty();

    public boolean full();

    public T pop();

    public void push(T item);
}