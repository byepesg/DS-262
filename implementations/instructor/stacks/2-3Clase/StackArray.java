public class StackArray{

    private int top;
    private int[] sarray;
    private static final int N=3;

    public StackArray(){
        this(N);
    }
    public StackArray(int n){
        top = 0;
        sarray = new int[n];
    }

    public boolean empty(){
        return top <=0;
    }
    public boolean full(){
        return top >= sarray.length;
    }

    public int pop(){
        if(empty()) throw new RuntimeException("Stack is empty");
        top--;
        return sarray[top];
    }
    public void push(int item){
        if(full()) throw new RuntimeException("Stack is empty");
        sarray[top] = item;
        top++;
    }

}