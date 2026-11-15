/**
 * Stack implemented using an array.
 *
 * This implementation follows the design presented in
 * "Guide to Data Structures: A Concise Introduction Using Java"
 * by James T. Streib and Takako Soma.
 *
 * A stack is a LIFO (Last-In, First-Out) data structure:
 * the last item pushed onto the stack is the first item
 * removed from it.
 */
public class StackArray {

    /*
     * Default capacity of the stack.
     *
     * static:
     *     N belongs to the class rather than to each individual object.
     *
     * final:
     *     The value cannot be changed after it is initialized.
     */
    private static final int N = 3;

    /*
     * 'top' indicates the position where the NEXT element
     * will be inserted.
     *
     * IMPORTANT:
     * 'top' is NOT the index of the current top element.
     *
     * Therefore:
     *
     *     top == 0  -> the stack is empty
     *     top == 1  -> one element is stored at index 0
     *     top == 2  -> two elements are stored at indices 0 and 1
     *
     * In general:
     *
     *     number of elements = top
     *     next free position = top
     *     current top element = sarray[top - 1]
     */
    private int top;

    /*
     * Array used to physically store the elements of the stack.
     *
     * At this point in the book, the stack stores only integers.
     * Generics are introduced later.
     */
    private int[] sarray;


    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Creates a stack using the default capacity N.
     *
     * Instead of duplicating the initialization code, this
     * constructor calls the constructor that receives the
     * desired capacity.
     */
    public StackArray() {
        this(N);
    }


    /**
     * Creates a stack with a capacity specified by the user.
     *
     * @param n maximum number of integers that can be stored
     */
    public StackArray(int n) {

        /*
         * The stack initially contains no elements.
         *
         * Since 'top' represents the next available position,
         * top = 0 means that the first element will be inserted
         * at sarray[0].
         */
        top = 0;

        /*
         * Allocate an integer array containing n positions.
         */
        sarray = new int[n];
    }


    // =========================================================
    // VALUE-RETURNING METHODS
    // =========================================================

    /**
     * Determines whether the stack is empty.
     *
     * Since top represents the number of elements currently
     * stored, top == 0 means that the stack contains no elements.
     *
     * The book uses <= instead of == so that a negative value
     * would also be considered an empty stack.
     *
     * @return true if the stack is empty; false otherwise
     */
    public boolean empty() {
        return top <= 0;
    }


    /**
     * Determines whether the stack is full.
     *
     * Suppose the array has length 3.
     *
     * Valid array indices are:
     *
     *     0, 1, 2
     *
     * After three elements have been inserted, top becomes 3.
     * Therefore, when:
     *
     *     top >= sarray.length
     *
     * there is no free position available for another element.
     *
     * @return true if the stack is full; false otherwise
     */
    public boolean full() {
        return top >= sarray.length;
    }


    /**
     * Removes and returns the element at the top of the stack.
     *
     * Because 'top' indicates the NEXT free position rather than
     * the position of the current top element, it must first be
     * decremented.
     *
     * Example:
     *
     *     Array:
     *
     *     index       0    1    2
     *               +----+----+----+
     *               | 18 | 11 |  7 |
     *               +----+----+----+
     *
     *     top = 3
     *
     * The current top element is therefore located at:
     *
     *     top - 1 = 2
     *
     * So pop performs:
     *
     *     top--;
     *     return sarray[top];
     *
     * @return the integer removed from the top of the stack
     */
    public int pop() {

        /*
         * Attempting to pop from an empty stack is an error.
         *
         * Instead of returning a special value such as -1,
         * this version follows the book's later implementation
         * and throws a runtime exception.
         */
        if (empty()) {
            throw new RuntimeException("Stack is empty");
        }

        /*
         * Move top back to the position of the current
         * top element.
         */
        top--;

        /*
         * Return the element.
         *
         * Notice that the value is not physically erased from
         * the array. This is not a problem because top determines
         * which positions logically belong to the stack.
         *
         * A future push operation will simply overwrite this
         * position.
         */
        return sarray[top];
    }


    // =========================================================
    // VOID METHODS
    // =========================================================

    /**
     * Adds an integer to the top of the stack.
     *
     * @param item integer to be pushed onto the stack
     */
    public void push(int item) {

        /*
         * Before inserting an element, verify that there is
         * available space in the array.
         */
        if (full()) {
            throw new RuntimeException("Stack is full");
        }

        /*
         * Store the new item at the next available position.
         */
        sarray[top] = item;

        /*
         * Move top to the next available position.
         */
        top++;
    }
}