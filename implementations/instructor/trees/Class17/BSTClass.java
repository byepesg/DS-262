
package Class17;

public class BSTClass {

    public static Node root;
    
    public static Node insert(int key){
        Node n = new Node(key);
        return n;
    }
    public static void main(String[] args) {
        /* 
        Node obj1 = new Node(2);
        System.out.println(obj1.key);
        */

        Node node1 = new Node(69);
        root = node1;
        Node node2 = insert(72);
        
        System.out.println(root.getKey());
        System.out.println(node2.getKey());

        if(root.getKey()>node2.getKey()){
            root.setLeft(node2);
        }else if(root.getKey()<node2.getKey()){
            root.setRight(node2);
        }

        System.out.println(root.getLeft());
        System.out.println(root.getRight());
    }
}
