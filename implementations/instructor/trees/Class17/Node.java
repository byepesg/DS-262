package Class17;
 class Node{

    private int key;
    public Node left=null;
    public Node right=null;
    /* 
    public Node(int keyExternal){
        this.key=keyExternal;
        this.left = null;
        this.right = null;
    }
*/
public Node(){

}
public Node(int keyExternal){
    this.key = keyExternal;
    this.left = null;
    this.right= null;
}
public Node(int keyExternal, int reescribirInt){
this.key = keyExternal;
if (reescribirInt < 200) this.key = reescribirInt;
}
public Node(int keyExternal, Node N){
this.key = keyExternal;
this.left = N;
}
public Node(int keyExternal,Node NL, Node NR){
this.key = keyExternal;
this.left = NL;
this.right = NR;
    
}
public void setKey(int keyExternal){
    this.key = keyExternal;    
}
public int getKey(){
    return this.key;
}

public void setLeft(Node LR){
    this.left = LR;
}

public Node getLeft(){
return this.left;
}

public void setRight(Node NR){
    this.right = NR;
}
public Node getRight(){
    return this.right;
}
}