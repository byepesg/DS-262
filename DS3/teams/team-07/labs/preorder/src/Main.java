
public class Main
{
    public static int count = 0;

    public static void main(String[] args) {
        Node node = new Node(12);
        node.left = new Node(24);
        node.right = new Node(45);
        node.left.left = new Node(3);
        node.left.right = new Node(5);
        node.right.left = new Node(14);
        node.right.left.left = new Node(27);

        preorder(node);
    }

    public static void preorder(Node v)
    {
        if (v == null) return;

        System.out.println(v.key);
        preorder(v.left);
        preorder(v.right);
    }
}