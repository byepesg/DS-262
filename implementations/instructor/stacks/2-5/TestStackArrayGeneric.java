/**
 * Section 2.5 - Testing the Generic Stack
 *
 * Demonstrates Java autoboxing and unboxing
 * with StackArrayGeneric<Integer>.
 */
public class TestStackArrayGeneric {

    public static void main(String[] args) {

        /*
         * T is replaced by Integer.
         *
         * We cannot use:
         *
         *     StackArrayGeneric<int>
         *
         * because Java generic types require reference types.
         */
        StackArrayGeneric<Integer> stack;

        stack = new StackArrayGeneric<Integer>();


        /*
         * Autoboxing:
         *
         * Java automatically converts:
         *
         * int -> Integer
         */
        stack.push(2);
        stack.push(3);
        stack.push(4);


        /*
         * Unboxing occurs automatically when necessary
         * when the values are retrieved.
         */
        while (!stack.empty()) {
            System.out.println(stack.pop());
        }
    }
}