package triage;

import java.util.ArrayList;
import java.util.List;

/**
 * Indice por identificador con un arbol binario de busqueda (BST) sin
 * balanceo.
 *
 * Invariante: para todo nodo x, los identificadores del subarbol izquierdo
 * son menores que x.key y los del derecho son mayores; no hay repetidos.
 *
 * Todas las operaciones cuestan O(h). Como los identificadores suelen
 * llegar en orden (A001, A002, ...), el arbol degenera en una cadena con
 * h = n - 1 y las operaciones cuestan Theta(n). Por eso el sistema usa
 * {@link AvlIndex}; esta clase se conserva para la comparacion experimental.
 *
 * La insercion y la busqueda son iterativas (espacio O(1)) para soportar
 * arboles degenerados grandes sin desbordar la pila de llamadas.
 */
public class BstIndex implements TicketIndex {

    private static class Node {

        String key;
        Ticket value;
        Node left;
        Node right;

        Node(Ticket t) {
            this.key = t.getId();
            this.value = t;
        }
    }

    private Node root;
    private int size;

    @Override
    public boolean insert(Ticket t) {
        Node fresh = new Node(t);
        if (root == null) {
            root = fresh;
            size++;
            return true;
        }
        Node current = root;
        while (true) {
            int c = fresh.key.compareTo(current.key);
            if (c == 0) {
                return false;
            }
            if (c < 0) {
                if (current.left == null) {
                    current.left = fresh;
                    break;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = fresh;
                    break;
                }
                current = current.right;
            }
        }
        size++;
        return true;
    }

    @Override
    public Ticket find(String id) {
        Node current = root;
        while (current != null) {
            int c = id.compareTo(current.key);
            if (c == 0) {
                return current.value;
            }
            current = c < 0 ? current.left : current.right;
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        if (find(id) == null) {
            return false;
        }
        root = delete(root, id);
        size--;
        return true;
    }

    /**
     * Devuelve la nueva raiz del subarbol, que el llamador debe reasignar.
     * Caso de dos hijos: se copia el sucesor inorder y se elimina del
     * subarbol derecho.
     */
    private static Node delete(Node n, String id) {
        int c = id.compareTo(n.key);
        if (c < 0) {
            n.left = delete(n.left, id);
        } else if (c > 0) {
            n.right = delete(n.right, id);
        } else {
            if (n.left == null) {
                return n.right;
            }
            if (n.right == null) {
                return n.left;
            }
            Node s = n.right;
            while (s.left != null) {
                s = s.left;
            }
            n.key = s.key;
            n.value = s.value;
            n.right = delete(n.right, s.key);
        }
        return n;
    }

    @Override
    public int size() {
        return size;
    }

    /** Iterativa por niveles para no desbordar la pila en cadenas largas. */
    @Override
    public int height() {
        if (root == null) {
            return -1;
        }
        LinkedQueue<Node> level = new LinkedQueue<>();
        level.enqueue(root);
        int h = -1;
        while (!level.isEmpty()) {
            h++;
            LinkedQueue<Node> next = new LinkedQueue<>();
            for (Node n : level) {
                if (n.left != null) {
                    next.enqueue(n.left);
                }
                if (n.right != null) {
                    next.enqueue(n.right);
                }
            }
            level = next;
        }
        return h;
    }

    @Override
    public List<Ticket> inorder() {
        List<Ticket> out = new ArrayList<>(size);
        LinkedStack<Node> stack = new LinkedStack<>();
        Node current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            out.add(current.value);
            current = current.right;
        }
        return out;
    }

    @Override
    public boolean isValid() {
        List<Ticket> keys = inorder();
        if (keys.size() != size) {
            return false;
        }
        for (int i = 1; i < keys.size(); i++) {
            if (keys.get(i - 1).getId().compareTo(keys.get(i).getId()) >= 0) {
                return false;
            }
        }
        return true;
    }
}
