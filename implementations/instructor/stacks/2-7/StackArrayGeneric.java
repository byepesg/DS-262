/**
 * Array implementation of the StackGeneric interface.
 */
public class StackArrayGeneric<T> implements StackGeneric<T> {

    private static final int N = 3;

    private int top;

    private T[] sarray;


    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public StackArrayGeneric() {
        this(N);
    }


    public StackArrayGeneric(int n) {

        top = 0;

        sarray = (T[]) new Object[n];
    }


    // =====================================================
    // VALUE-RETURNING METHODS
    // =====================================================

    public boolean empty() {
        return top <= 0;
    }


    public boolean full() {
        return top >= sarray.length;
    }


    public T pop() {

        if (empty()) {
            throw new RuntimeException("Stack is empty");
        }

        top--;

        return sarray[top];
    }


    // =====================================================
    // VOID METHODS
    // =====================================================

    public void push(T item) {

        if (full()) {
            throw new RuntimeException("Stack is full");
        }

        sarray[top] = item;
        top++;
    }
}