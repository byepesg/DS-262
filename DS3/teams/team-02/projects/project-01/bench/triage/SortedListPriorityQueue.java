package triage;

/**
 * Alternativa descartada en el diseno, implementada solo para compararla en
 * el benchmark: una lista enlazada simple ordenada por (urgencia, llegada).
 * Extraer el mas prioritario cuesta O(1) (es la cabeza), pero insertar
 * cuesta O(n) porque hay que recorrer la lista hasta la posicion correcta.
 */
final class SortedListPriorityQueue {

    private static final class Node {

        private final Ticket value;
        private Node next;

        Node(Ticket value) {
            this.value = value;
        }
    }

    private Node head;
    private int size;

    void insert(Ticket t) {
        Node node = new Node(t);
        if (head == null || comesBefore(t, head.value)) {
            node.next = head;
            head = node;
        } else {
            Node current = head;
            while (current.next != null && !comesBefore(t, current.next.value)) {
                current = current.next;
            }
            node.next = current.next;
            current.next = node;
        }
        size++;
    }

    Ticket removeNext() {
        Ticket t = head.value;
        head = head.next;
        size--;
        return t;
    }

    boolean isEmpty() {
        return size == 0;
    }

    private static boolean comesBefore(Ticket a, Ticket b) {
        if (a.getUrgency() != b.getUrgency()) {
            return a.getUrgency() < b.getUrgency();
        }
        return a.getArrivalOrder() < b.getArrivalOrder();
    }
}
