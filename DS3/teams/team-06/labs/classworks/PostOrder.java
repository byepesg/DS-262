//Código PostOrder Jeicob Stiven Murillo Ramos y Andres Santiago Morales Robayo

class Node {
    char key;
    Node left;
    Node right;

    Node(char key) {
        this.key = key;
        this.left = null;
        this.right = null;
    }
}

public class Main {

    static void postorder(Node v) {
        if (v == null) {
            return;
        }
        postorder(v.left);
        postorder(v.right);
        System.out.print(v.key + " ");
    }

    public static void main(String[] args) {

        Node root = new Node('A');
        root.left = new Node('B');
        root.right = new Node('C');
        root.left.left = new Node('D');
        root.left.right = new Node('E');
        root.right.left = new Node('F');
        root.right.right = new Node('G');

        System.out.print("Postorden: ");
        postorder(root);
        System.out.println();
    }
}