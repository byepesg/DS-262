/**
 * Section 2.5 - Generic Types
 *
 * Generic array-based stack.
 *
 * T represents the type of object that will
 * be stored in the stack.
 */
public class StackArrayGeneric<T> {

    private static final int N = 3;

    private int top;

    /*
     * Instead of int[], the array now stores
     * elements of generic type T.
     */
    private T[] sarray;


    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public StackArrayGeneric() {
        this(N);
    }


    public StackArrayGeneric(int n) {

        top = 0;

        /*
         * Java does not allow:
         *
         *     new T[n]
         *
         * Therefore, an Object array is created and
         * cast to T[].
         *
         * The compiler will produce an unchecked-cast warning.
         */
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


    /**
     * pop() now returns T instead of int.
     */
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

    /**
     * push() now receives an object of type T.
     */
    public void push(T item) {

        if (full()) {
            throw new RuntimeException("Stack is full");
        }

        sarray[top] = item;
        top++;
    }
}