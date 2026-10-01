public class Main {

    public static void main(String[] args) {

        // Create a stack with capacity 3
        StackArray stack = new StackArray(3);

        // Check the initial state
        System.out.println("Is the stack empty? " + stack.empty());

        // Push three elements
        stack.push(18);
        stack.push(11);
        stack.push(7);

        // The stack should now be full
        System.out.println("Is the stack full? " + stack.full());

        // Pop elements
        System.out.println("Popped: " + stack.pop());
        System.out.println("Popped: " + stack.pop());
        System.out.println("Popped: " + stack.pop());

        // The stack should now be empty again
        System.out.println("Is the stack empty? " + stack.empty());
    }
}