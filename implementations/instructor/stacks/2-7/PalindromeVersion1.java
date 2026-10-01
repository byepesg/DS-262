import java.util.Scanner;

/**
 * Section 2.7 - Palindrome Version 1
 *
 * Stores the entire word in a stack and compares
 * the characters as they are popped.
 */
public class PalindromeVersion1 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int i;
        int j;

        String word;

        boolean isPalindrome;

        /*
         * Notice that the variable is declared using
         * the interface type rather than the concrete
         * StackArrayGeneric class.
         */
        StackGeneric<Character> stack;


        System.out.print("Enter a word: ");
        word = scanner.next();


        /*
         * Create enough stack space to store every
         * character in the word.
         */
        stack = new StackArrayGeneric<Character>(word.length());


        // Push every character onto the stack.
        for (i = 0; i < word.length(); i++) {

            stack.push(word.charAt(i));
        }


        j = 0;

        isPalindrome = true;


        /*
         * Compare the characters popped from the stack
         * against the characters from the beginning
         * of the original word.
         */
        while (!stack.empty() && isPalindrome) {

            if (stack.pop() != word.charAt(j)) {
                isPalindrome = false;
            }

            j++;
        }


        if (isPalindrome) {
            System.out.println(
                "The word entered is a palindrome."
            );
        } else {
            System.out.println(
                "The word entered is NOT a palindrome."
            );
        }

        scanner.close();
    }
}