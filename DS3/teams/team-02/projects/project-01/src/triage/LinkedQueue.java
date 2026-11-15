package triage;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola generica implementada con una lista enlazada simple con referencias
 * a la cabeza (frente) y a la cola (final).
 */
public class LinkedQueue<T> implements Iterable<T> {

    private static class Node<T> {

        private final T value;
        private Node<T> next;

        Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedQueue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void enqueue(T value) {
        Node<T> node = new Node<>(value);
        if (isEmpty()) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        T value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        return head.value;
    }

    /**
     * Elimina la primera aparicion de value, conservando el orden del resto.
     *
     * @return true si el elemento estaba en la cola
     */
    public boolean remove(T value) {
        Node<T> previous = null;
        Node<T> current = head;
        while (current != null && !current.value.equals(value)) {
            previous = current;
            current = current.next;
        }
        if (current == null) {
            return false;
        }
        if (previous == null) {
            head = current.next;
        } else {
            previous.next = current.next;
        }
        if (current == tail) {
            tail = previous;
        }
        size--;
        return true;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    /**
     * Recorre la cola desde el frente hasta el final sin modificarla.
     */
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
