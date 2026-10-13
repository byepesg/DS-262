/** CLRS-style BST adapted to Java from Cormen et al., Introduction to Algorithms,
 * Binary Search Trees chapter. Explicit parent pointers and TRANSPLANT deletion.
 * Distinct integer keys; duplicate inserts are ignored for comparison with StreibBST.
 */
public class CormenBST {
    private static class Node {
        int key;
        Node left, right, parent;
        Node(int key) { this.key = key; }
    }

    private Node root;

    private Node treeSearch(Node x, int key) {
        if (x == null || x.key == key) return x;
        return key < x.key ? treeSearch(x.left, key) : treeSearch(x.right, key);
    }

    public boolean contains(int key) { return treeSearch(root, key) != null; }

    private Node treeMinimum(Node x) {
        while (x != null && x.left != null) x = x.left;
        return x;
    }

    private Node treeMaximum(Node x) {
        while (x != null && x.right != null) x = x.right;
        return x;
    }

    public Integer minimum() {
        Node x = treeMinimum(root);
        return x == null ? null : x.key;
    }

    public Integer maximum() {
        Node x = treeMaximum(root);
        return x == null ? null : x.key;
    }

    public void insert(int key) {
        if (contains(key)) return;
        Node z = new Node(key);
        Node y = null, x = root;
        while (x != null) {
            y = x;
            x = z.key < x.key ? x.left : x.right;
        }
        z.parent = y;
        if (y == null) root = z;
        else if (z.key < y.key) y.left = z;
        else y.right = z;
    }

    /** Replace subtree rooted at u with subtree rooted at v. */
    private void transplant(Node u, Node v) {
        if (u.parent == null) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        if (v != null) v.parent = u.parent;
    }

    public void remove(int key) {
        Node z = treeSearch(root, key);
        if (z == null) return;
        if (z.left == null) transplant(z, z.right);
        else if (z.right == null) transplant(z, z.left);
        else {
            Node y = treeMinimum(z.right); // successor
            if (y.parent != z) {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
        }
    }

    public Integer successor(int key) {
        Node x = treeSearch(root, key);
        if (x == null) return null;
        if (x.right != null) return treeMinimum(x.right).key;
        Node y = x.parent;
        while (y != null && x == y.right) { x = y; y = y.parent; }
        return y == null ? null : y.key;
    }

    public void printInorder() { inorder(root); System.out.println(); }

    private void inorder(Node x) {
        if (x == null) return;
        inorder(x.left);
        System.out.print(x.key + " ");
        inorder(x.right);
    }

    public static void main(String[] args) {
        CormenBST bst = new CormenBST();
        for (int x : new int[]{50, 30, 70, 20, 40, 60, 80}) bst.insert(x);
        System.out.print("Before: "); bst.printInorder();
        System.out.println("Min=" + bst.minimum() + ", Max=" + bst.maximum());
        System.out.println("Successor of 50=" + bst.successor(50));
        bst.remove(50); // successor node (60) is moved to root
        System.out.print("After:  "); bst.printInorder();
    }
}



