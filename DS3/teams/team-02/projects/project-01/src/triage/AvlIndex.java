package triage;

import java.util.ArrayList;
import java.util.List;

/**
 * Indice por identificador con un arbol AVL.
 *
 * Invariantes:
 * - orden BST: para todo nodo x, claves(x.left) menores que x.key menores
 *   que claves(x.right), sin repetidos;
 * - balance: para todo nodo, BF = height(left) - height(right) pertenece a
 *   {-1, 0, 1}, con height(null) = -1;
 * - cada nodo guarda su altura correcta.
 *
 * El invariante de balance garantiza h = O(log n), de modo que buscar,
 * insertar y eliminar cuestan O(log n) en el peor caso, incluso si los
 * identificadores llegan ordenados.
 */
public class AvlIndex implements TicketIndex {

    private static class Node {

        String key;
        Ticket value;
        Node left;
        Node right;
        int height;

        Node(Ticket t) {
            this.key = t.getId();
            this.value = t;
            this.height = 0;
        }
    }

    private Node root;
    private int size;
    private long rotations;

    // ------------------------------------------------------------------
    // Operaciones publicas
    // ------------------------------------------------------------------

    @Override
    public boolean insert(Ticket t) {
        if (find(t.getId()) != null) {
            return false;
        }
        root = insert(root, t);
        size++;
        return true;
    }

    /** Busqueda iterativa: un unico camino raiz-hoja, O(log n), espacio O(1). */
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

    @Override
    public int size() {
        return size;
    }

    @Override
    public int height() {
        return height(root);
    }

    /** Numero total de rotaciones simples realizadas (para los experimentos). */
    public long rotations() {
        return rotations;
    }

    @Override
    public List<Ticket> inorder() {
        List<Ticket> out = new ArrayList<>(size);
        inorder(root, out);
        return out;
    }

    @Override
    public boolean isValid() {
        return checkHeights(root) != Integer.MIN_VALUE
                && isBst(root, null, null)
                && count(root) == size;
    }

    // ------------------------------------------------------------------
    // Recursion sobre la estructura
    // ------------------------------------------------------------------

    /** Devuelve la nueva raiz del subarbol ya rebalanceado. */
    private Node insert(Node n, Ticket t) {
        if (n == null) {
            return new Node(t);
        }
        if (t.getId().compareTo(n.key) < 0) {
            n.left = insert(n.left, t);
        } else {
            n.right = insert(n.right, t);
        }
        return rebalance(n);
    }

    private Node delete(Node n, String id) {
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
        return rebalance(n);
    }

    private static void inorder(Node n, List<Ticket> out) {
        if (n == null) {
            return;
        }
        inorder(n.left, out);
        out.add(n.value);
        inorder(n.right, out);
    }

    // ------------------------------------------------------------------
    // Balanceo
    // ------------------------------------------------------------------

    private static int height(Node n) {
        return n == null ? -1 : n.height;
    }

    private static void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private static int balanceFactor(Node n) {
        return height(n.left) - height(n.right);
    }

    /**
     * Restaura |BF| <= 1 en n:
     * LL -> rotacion derecha; LR -> izquierda en el hijo y derecha en n;
     * RR -> rotacion izquierda; RL -> derecha en el hijo e izquierda en n.
     */
    private Node rebalance(Node n) {
        updateHeight(n);
        int bf = balanceFactor(n);
        if (bf > 1) {
            if (balanceFactor(n.left) < 0) {
                n.left = rotateLeft(n.left);
            }
            return rotateRight(n);
        }
        if (bf < -1) {
            if (balanceFactor(n.right) > 0) {
                n.right = rotateRight(n.right);
            }
            return rotateLeft(n);
        }
        return n;
    }

    /**
     * y con hijo izquierdo x pasa a ser hijo derecho de x; el subarbol
     * intermedio x.right pasa a ser y.left. El inorder no cambia. Theta(1).
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node middle = x.right;
        x.right = y;
        y.left = middle;
        updateHeight(y);
        updateHeight(x);
        rotations++;
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node middle = y.left;
        y.left = x;
        x.right = middle;
        updateHeight(x);
        updateHeight(y);
        rotations++;
        return y;
    }

    // ------------------------------------------------------------------
    // Verificacion de invariantes
    // ------------------------------------------------------------------

    /** Altura real si las alturas guardadas y los BF son correctos; si no, MIN_VALUE. */
    private static int checkHeights(Node n) {
        if (n == null) {
            return -1;
        }
        int hl = checkHeights(n.left);
        int hr = checkHeights(n.right);
        if (hl == Integer.MIN_VALUE || hr == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        int h = 1 + Math.max(hl, hr);
        if (h != n.height || Math.abs(hl - hr) > 1) {
            return Integer.MIN_VALUE;
        }
        return h;
    }

    /** Verificacion con intervalos de claves permitidas (min, max) exclusivos. */
    private static boolean isBst(Node n, String min, String max) {
        if (n == null) {
            return true;
        }
        if (min != null && n.key.compareTo(min) <= 0) {
            return false;
        }
        if (max != null && n.key.compareTo(max) >= 0) {
            return false;
        }
        return isBst(n.left, min, n.key) && isBst(n.right, n.key, max);
    }

    private static int count(Node n) {
        return n == null ? 0 : 1 + count(n.left) + count(n.right);
    }
}
