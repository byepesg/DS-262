public class Node{
    int key;
    Node left;
    Node right;
    
    Node(int key){
        this.key=key;
        this.left= null;
        this.right=null;
    }
    
    public static void Preorder(Node v){
    
    if (v==null){
        return;
    }
    
    System.out.println(v.key);
    Preorder(v.left);
    Preorder(v.right);
    }
}