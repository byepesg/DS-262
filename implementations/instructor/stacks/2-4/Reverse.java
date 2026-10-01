import java.util.Scanner;

/**
 * Section 2.4 - Reversing Integers
 *
 * Demonstrates how the LIFO behavior of a stack
 * can be used to reverse a sequence of integers.
 */
public class Reverse {

    public static void main(String[] args) {

        int number;

        // StackArray is the class implemented in Section 2.3.
        StackArray stack = new StackArray();

        Scanner scanner = new Scanner(System.in);


        // =====================================================
        // PUSH PHASE
        // =====================================================

        System.out.print("Enter an integer or a -1 to stop: ");
        number = scanner.nextInt();

        /*
         * Continue while:
         *
         * 1. The input is non-negative.
         * 2. The stack is not full.
         */
        while (number >= 0 && !stack.full()) {

            stack.push(number);

            System.out.print("Enter an integer or a -1 to stop: ");
            number = scanner.nextInt();
        }


        // =====================================================
        // POP PHASE
        // =====================================================

        System.out.println();
        System.out.print("The reverse integers are: ");

        /*
         * Because the stack is LIFO, popping all elements
         * produces them in reverse order.
         */
        while (!stack.empty()) {
            System.out.print(stack.pop() + " ");
        }

        System.out.println();

        scanner.close();
    }
}