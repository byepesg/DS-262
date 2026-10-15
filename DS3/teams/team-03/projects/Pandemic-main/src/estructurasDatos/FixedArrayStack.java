package estructurasDatos;

/**
 * Estructura de datos Pila (Stack) de capacidad fija.
 * Sigue el principio LIFO (Last-In, First-Out).
 */
public class FixedArrayStack<T> {

    private final T[] elementos;
    private int top; // Apunta al índice del elemento en la cima (-1 si está vacía)
    private final int capacity;

    @SuppressWarnings("unchecked")
    public FixedArrayStack(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        }
        this.capacity = capacity;
        this.elementos = (T[]) new Object[capacity];
        this.top = -1;
    }

    public void push(T elemento) {
        if (isFull()) {
            throw new IllegalStateException("La pila está llena (Stack Overflow).");
        }
        top++;
        elementos[top] = elemento;
    }

    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T elementoExtraido = elementos[top];
        elementos[top] = null; 
        top--;
        return elementoExtraido;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == capacity - 1;
    }

    public int obtenerTamano() {
        return top + 1;
    }
}
