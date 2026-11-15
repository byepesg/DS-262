package estructurasDatos;

/**
 * Cola FIFO genérica basada en Lista Simplemente Enlazada.
 * 
 * @param <T> Tipo de elemento almacenado en la cola.
 */
public class SinglyLinkedListQueue<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> cabeza; // Frente de la cola (se extrae en dequeue)
    private Nodo<T> cola;   // Final de la cola (se inserta en enqueue)

    public SinglyLinkedListQueue() {
        this.cabeza = null;
        this.cola = null;
    }

    public void enqueue(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (cola == null) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.siguiente = nuevoNodo;
            cola = nuevoNodo;
        }
    }

    public T dequeue() {
        if (estaVacia()) {
            return null;
        }
        T dato = cabeza.dato;
        cabeza = cabeza.siguiente;
        if (cabeza == null) {
            cola = null;
        }
        return dato;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }
}