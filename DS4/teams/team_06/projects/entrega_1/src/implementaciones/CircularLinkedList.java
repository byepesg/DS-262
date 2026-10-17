public class CircularLinkedList {
    Node head;
    Node tail;

    public class Node {
        int key;
        Node nextNode;
        Node prevNode;

        public Node(int key) {
            this.key = key;
            this.nextNode = null;
            this.prevNode = null;
        }
    }

    public CircularLinkedList() {
        head = null;
        tail = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    // O(1)
    public void pushFront(int key) {
        Node newNode = new Node(key);

        if (isEmpty()) {
            head = tail = newNode;
            newNode.nextNode = newNode;
            newNode.prevNode = newNode;
            return;
        }

        newNode.nextNode = head;
        newNode.prevNode = tail;
        head.prevNode = newNode;
        tail.nextNode = newNode;
        head = newNode;
    }

    public int topFront() {
        if (isEmpty()) throw new IllegalStateException("lista vacía");
        return head.key;
    }

    // O(1)
    public void popFront() {
        if (isEmpty()) throw new IllegalStateException("lista vacía");

        if (head == tail) {
            head = tail = null;
            return;
        }
        head = head.nextNode;
        head.prevNode = tail;
        tail.nextNode = head;
    }

    // O(1)
    public void pushBack(int key) {
        Node newNode = new Node(key);
    
        if (isEmpty()) {
            head = tail = newNode;
            newNode.nextNode = newNode;
            newNode.prevNode = newNode;
            return;
        }
        newNode.prevNode = tail;
        newNode.nextNode = head;
        tail.nextNode = newNode;
        head.prevNode = newNode;
        tail = newNode;
    }

    public int topBack() {
        if (isEmpty()) throw new IllegalStateException("lista vacía");
        return tail.key;
    }

    // O(1)
    public void popBack() {
        if (isEmpty()) throw new IllegalStateException("lista vacía");

        if (head == tail) {
            head = tail = null;
            return;
        }
        tail = tail.prevNode;
        tail.nextNode = head;
        head.prevNode = tail;
    }

    // O(n)
    public boolean find(int key) {
        if (isEmpty()) return false;

        Node probe = head;
        do {
            if (probe.key == key) return true;
            probe = probe.nextNode;
        } while (probe != head);

        return false;
    }

    // O(n) - elimina la primera ocurrencia
    public void erase(int key) {
        if (isEmpty()) return;

        if (head.key == key) {
            popFront();
            return;
        }
        Node current = head.nextNode;
        while (current != head) {
            if (current.key == key) {
                if (current == tail) {
                    popBack();
                } else {
                    current.prevNode.nextNode = current.nextNode;
                    current.nextNode.prevNode = current.prevNode;
                }
                return;
            }
            current = current.nextNode;
        }
    }

    // O(1) dado el nodo
    public void addAfter(Node node, int key) {
        if (node == null) throw new IllegalArgumentException("Node cannot be null");

        Node newNode = new Node(key);
        newNode.prevNode = node;
        newNode.nextNode = node.nextNode;
        node.nextNode.prevNode = newNode;
        node.nextNode = newNode;

        if (node == tail) {
            tail = newNode;
        }
    }

    // O(1) gracias a prevNode
    public void addBefore(Node node, int key) {
        if (node == null) throw new IllegalArgumentException("Node cannot be null");

        if (node == head) {
            pushFront(key);
            return;
        }
        addAfter(node.prevNode, key);
    }

    // O(n)
    public void printList() {
        if (isEmpty()) {
            System.out.println("(vacía)");
            return;
        }

        Node current = head;
        do {
            System.out.print(current.key + " <-> ");
            current = current.nextNode;
        } while (current != head);

        System.out.println("(head)");
    }

    // recorrido adelante, para pruebas
    String fwd() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node c = head;
        do { sb.append(c.key).append(c.nextNode == head ? "" : ","); c = c.nextNode; } while (c != head);
        return sb.append("]").toString();
    }

    // recorrido atrás, para pruebas
    String bwd() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node c = tail;
        do { sb.append(c.key).append(c.prevNode == tail ? "" : ","); c = c.prevNode; } while (c != tail);
        return sb.append("]").toString();
    }
}