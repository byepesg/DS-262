import java.util.Scanner;

/**
 * Section 2.7 - Palindrome Version 2
 *
 * Improved version that stores only the first half
 * of the word in the stack.
 */
public class PalindromeVersion2 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int i;
        int j;

        String word;

        boolean isPalindrome;

        StackGeneric<Character> stack;


        System.out.print("Enter a word: ");
        word = scanner.next();


        /*
         * Only half of the word needs to be stored.
         */
        stack =
            new StackArrayGeneric<Character>(word.length() / 2);


        // Push the first half of the word.
        for (i = 0; i < word.length() / 2; i++) {

            stack.push(word.charAt(i));
        }


        /*
         * i now indicates the beginning of the
         * second half.
         */
        j = i;


        /*
         * For an odd-length word, skip the middle
         * character.
         *
         * Example:
         *
         *     l e v e l
         *         ^
         *       ignore
         */
        if (word.length() % 2 != 0) {
            j++;
        }


        isPalindrome = true;


        /*
         * Compare the first half in reverse order
         * with the second half of the word.
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