/** Teaching-oriented BST adapted from Streib & Soma, Guide to Data Structures, Ch. 8.
 * Distinct integer keys; recursive updates return the root of the modified subtree.
 */
public class StreibBST {
    private static class Node {
        int data;
        Node left, right;
        Node(int data) { this.data = data; }
    }

    private Node root;

    public void insert(int value) { root = insert(value, root); }

    private Node insert(int value, Node p) {
        if (p == null) return new Node(value);
        if (value < p.data) p.left = insert(value, p.left);
        else if (value > p.data) p.right = insert(value, p.right);
        // Duplicate keys are ignored.
        return p;
    }

    public boolean contains(int value) { return contains(value, root); }

    private boolean contains(int value, Node p) {
        if (p == null) return false;
        if (value == p.data) return true;
        return value < p.data ? contains(value, p.left) : contains(value, p.right);
    }

    public Integer minimum() {
        Node p = findMin(root);
        return p == null ? null : p.data;
    }

    private Node findMin(Node p) {
        if (p != null) while (p.left != null) p = p.left;
        return p;
    }

    public Integer maximum() {
        Node p = root;
        if (p != null) while (p.right != null) p = p.right;
        return p == null ? null : p.data;
    }

    public void remove(int value) { root = remove(value, root); }

    private Node remove(int value, Node p) {
        if (p == null) return null;
        if (value < p.data) p.left = remove(value, p.left);
        else if (value > p.data) p.right = remove(value, p.right);
        else {
            if (p.left == null) return p.right;  // leaf or right child only
            if (p.right == null) return p.left;  // left child only
            Node successor = findMin(p.right);
            p.data = successor.data;
            p.right = remove(successor.data, p.right);
        }
        return p;
    }

    public void printInorder() { inorder(root); System.out.println(); }

    private void inorder(Node p) {
        if (p == null) return;
        inorder(p.left);
        System.out.print(p.data + " ");
        inorder(p.right);
    }

    public static void main(String[] args) {
        StreibBST bst = new StreibBST();
        for (int x : new int[]{50, 30, 70, 20, 40, 60, 80}) bst.insert(x);
        System.out.print("Before: "); bst.printInorder();
        System.out.println("Min=" + bst.minimum() + ", Max=" + bst.maximum());
        System.out.println("Contains 40? " + bst.contains(40));
        bst.remove(50); // two children; successor is 60
        System.out.print("After:  "); bst.printInorder();
    }
}
