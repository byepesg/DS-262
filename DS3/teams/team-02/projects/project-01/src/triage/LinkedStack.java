package triage;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Pila generica implementada con una lista enlazada simple con referencia
 * unicamente al tope.
 */
public class LinkedStack<T> implements Iterable<T> {

    private static class Node<T> {

        private final T value;
        private final Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    public LinkedStack() {
        this.top = null;
        this.size = 0;
    }

    public void push(T value) {
        top = new Node<>(value, top);
        size++;
    }

    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("La pila esta vacia");
        }
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La pila esta vacia");
        }
        return top.value;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    /**
     * Recorre la pila del tope al fondo sin modificarla.
     *
     * <p>Entrega 1: el historial se leia desapilando todo a una pila
     * auxiliar y volviendo a apilar (dos pasadas y {@code h} nodos nuevos).
     * Entrega 2: un iterador de solo lectura hace una sola pasada.</p>
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Node<T> current = top;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }
}
