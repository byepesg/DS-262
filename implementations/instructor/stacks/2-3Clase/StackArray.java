public class StackArray {


    int[] sarray; // The array that stores the stack elements
    
    private static final int N = 3; // The index of the next available position
    
    private int top;

    public StackArray(){
        this(N);
    }

    public StackArray(int n){
        top = 0;
        sarray = new int[n];
       
    }


}
