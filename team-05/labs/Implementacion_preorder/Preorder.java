Main.java:

public class Main

{
    public static void main(String[] args) {
    Node raiz = new Node(0);
    raiz.left = new Node(1);
    raiz.right = new Node(2);
    raiz.left.left = new Node (3);
    raiz.right.right = new Node (4);
    
    preorder(raiz);

    }
    public static void preorder(Node v){
        if (v == null) {
            return;
        }
        System.out.print(v.key);
        preorder(v.left);
        preorder(v.right);
    }
}

Node.java:

public class Node {
    int key;
    Node left;
    Node right;

    public Node (int key) {
        this.key = key;
        this.left = null;
        this.right = null;
    }
}