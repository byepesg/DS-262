/******************************************************************************

Welcome to GDB Online.
GDB online is an online compiler and debugger tool for C, C++, Python, Java, PHP, Ruby, Perl,
C#, OCaml, VB, Swift, Pascal, Fortran, Haskell, Objective-C, Assembly, HTML, CSS, JS, SQLite, Prolog.
Code, Compile, Run and Debug online from anywhere in world.

*******************************************************************************/

public class SinglyLinkedList {

    private Node head;

    // =========================
    // NODE
    // =========================
    private static class Node {
        int key;
        Node next;

        Node(int key) {
            this.key = key;
            this.next = null;
        }
    }

    // =========================
    // PushFront
    // O(1)
    // =========================
    public void pushFront(int key) {
        Node newNode = new Node(key);

        newNode.next = head;
        head = newNode;
    }

    // =========================
    // TopFront
    // O(1)
    // =========================
    public int topFront() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }

        return head.key;
    }

    // =========================
    // PopFront
    // O(1)
    // =========================
    public void popFront() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }

        head = head.next;
    }

    // =========================
    // PushBack
    // O(n)
    // =========================
    public void pushBack(int key) {
        Node newNode = new Node(key);

        if (head == null) {
            head = newNode;
            return;
        }

        Node current = head;

        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
    }

    // =========================
    // TopBack
    // O(n)
    // =========================
    public int topBack() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }

        Node current = head;

        while (current.next != null) {
            current = current.next;
        }

        return current.key;
    }

    // =========================
    // PopBack
    // O(n)
    // =========================
    public void popBack() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }

        // Only one element
        if (head.next == null) {
            head = null;
            return;
        }

        Node current = head;

        // Stop at the node BEFORE the last node
        while (current.next.next != null) {
            current = current.next;
        }

        current.next = null;
    }

    // =========================
    // Find
    // O(n)
    // =========================
    public boolean find(int key) {
        Node current = head;

        while (current != null) {

            if (current.key == key) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    // =========================
    // Erase
    // O(n)
    // Removes first occurrence
    // =========================
    public void erase(int key) {

        if (head == null) {
            return;
        }

        // The element is at the beginning
        if (head.key == key) {
            head = head.next;
            return;
        }

        Node current = head;

        while (current.next != null) {

            if (current.next.key == key) {
                current.next = current.next.next;
                return;
            }

            current = current.next;
        }
    }

    // =========================
    // Empty
    // O(1)
    // =========================
    public boolean empty() {
        return head == null;
    }

    // =========================
    // AddAfter
    // O(1) if node is known
    // =========================
    private void addAfter(Node node, int key) {

        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        Node newNode = new Node(key);

        newNode.next = node.next;
        node.next = newNode;
    }

    // =========================
    // AddBefore
    // O(n)
    // =========================
    private void addBefore(Node node, int key) {

        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        // Insert before head
        if (node == head) {
            pushFront(key);
            return;
        }

        Node current = head;

        // Find predecessor
        while (current != null && current.next != node) {
            current = current.next;
        }

        if (current == null) {
            throw new IllegalArgumentException(
                "Node does not belong to the list"
            );
        }

        Node newNode = new Node(key);

        newNode.next = node;
        current.next = newNode;
    }

    // =========================
    // Print
    // O(n)
    // =========================
    public void printList() {

        Node current = head;

        while (current != null) {
            System.out.print(current.key + " -> ");
            current = current.next;
        }

        System.out.println("null");
    }
}
