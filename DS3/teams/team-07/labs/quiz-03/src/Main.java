public class Main
{
    // A, B, N1, N2, N3, N4, N5, N6
    private static final Position[] POSITIONS = {
            new Position(0, 0), new Position(0, 1),
            new Position(0, 2), new Position(0, 3), new Position(1, 3),
            new Position(1, 2), new Position(1, 1), new Position(1, 0)
    };

    public static void main(String[] args) {
        Snake snake = new Snake();
        snake.addHead(POSITIONS[0]); // A = tail
        snake.addHead(POSITIONS[1]); // B = head
        System.out.printf("%-12s %s resize=no%n", "Initial", snake);

        // Question 3.f: N1 (no eat), N2 (eat), N3 (no eat), N4 (no eat), N5 (eat), N6 (no eat)
        boolean[] eats = { false, true, false, false, true, false };

        for (int n = 0; n < eats.length; n++) {
            int capacityBefore = snake.capacity();

            // MOVE: addHead is always executed first, then removeTail if no food was eaten.
            snake.addHead(POSITIONS[n + 2]);
            if (!eats[n])
                snake.removeTail();

            String label = "N" + (n + 1) + (eats[n] ? " (eats)" : " (no eat)");
            boolean resized = snake.capacity() != capacityBefore;
            System.out.printf("%-12s %s resize=%s%n", label, snake, resized ? "yes" : "no");
        }
    }
}