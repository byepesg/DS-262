package triage;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola generica implementada con una lista doblemente enlazada con
 * referencias a la cabeza (frente) y a la cola (final).
 *
 * <p>Ademas de las operaciones FIFO, la cola devuelve al insertar un
 * {@link Node} que funciona como "localizador" del elemento: con el, el
 * elemento puede retirarse de cualquier posicion en tiempo constante sin
 * recorrer la lista. Esto es lo que permite cancelar una solicitud en
 * {@code O(1)} cuando se combina con el indice por identificador.</p>
 *
 * <p>Entrega 1: lista simplemente enlazada; retirar un elemento arbitrario
 * exigia recorrerla para encontrar el nodo anterior ({@code O(n)}).
 * Entrega 2: se agrega la referencia {@code prev} en cada nodo.</p>
 */
public class LinkedQueue<T> implements Iterable<T> {

    /**
     * Nodo de la lista. Fuera de esta clase solo se usa como localizador
     * opaco: su contenido se lee con {@link #value()} y no puede modificarse.
     */
    public static final class Node<T> {

        private final T value;
        private Node<T> prev;
        private Node<T> next;
        private LinkedQueue<T> owner;

        private Node(T value, LinkedQueue<T> owner) {
            this.value = value;
            this.owner = owner;
        }

        public T value() {
            return value;
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

    /**
     * Inserta al final de la cola. {@code O(1)}.
     *
     * @return localizador del nodo insertado
     */
    public Node<T> enqueue(T value) {
        Node<T> node = new Node<>(value, this);
        if (isEmpty()) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
        size++;
        return node;
    }

    /**
     * Inserta al frente de la cola. {@code O(1)}. Se usa para deshacer una
     * atencion: el elemento vuelve a la posicion que ocupaba.
     *
     * @return localizador del nodo insertado
     */
    public Node<T> enqueueFront(T value) {
        Node<T> node = new Node<>(value, this);
        if (isEmpty()) {
            tail = node;
        } else {
            head.prev = node;
            node.next = head;
        }
        head = node;
        size++;
        return node;
    }

    /**
     * Retira y devuelve el elemento del frente. {@code O(1)}.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        Node<T> node = head;
        unlink(node);
        return node.value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        return head.value;
    }

    /**
     * Retira el nodo indicado, conservando el orden del resto. {@code O(1)}.
     *
     * @throws IllegalArgumentException si el nodo no pertenece a esta cola
     *         (por ejemplo, porque ya fue retirado)
     */
    public T remove(Node<T> node) {
        if (node == null || node.owner != this) {
            throw new IllegalArgumentException("El nodo no pertenece a esta cola");
        }
        unlink(node);
        return node.value;
    }

    private void unlink(Node<T> node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
        node.owner = null;
        size--;
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
