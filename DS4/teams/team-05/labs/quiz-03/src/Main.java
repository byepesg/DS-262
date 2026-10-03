package QUIZ_3;

public class Main {
    public static void main(String[] args) {
        Snake snake = new Snake(2);
        // Introducimos A y B pasando sus valores ASCII como si fuera la "fila"
        snake.addHead(new Position('A', 0)); 
        snake.addHead(new Position('B', 0));
        snake.printState("Estado Inicial", false);

        // N1 (no come)
        snake.addHead(new Position('C', 0));
        snake.removeTail();
        snake.printState("N1 (No come)", true); // Hubo resize de 2 a 4

        // N2 (come)
        snake.addHead(new Position('D', 0));
        snake.printState("N2 (Come)", false);

        // N3 (no come)
        snake.addHead(new Position('E', 0));
        snake.removeTail();
        snake.printState("N3 (No come)", false);

        // N4 (no come)
        snake.addHead(new Position('F', 0));
        snake.removeTail();
        snake.printState("N4 (No come)", false);

        // N5 (come)
        snake.addHead(new Position('G', 0));
        snake.printState("N5 (Come)", false);

        // N6 (no come)
        snake.addHead(new Position('H', 0));
        snake.removeTail();
        snake.printState("N6 (No come)", true); // Hubo resize de 4 a 8
    }
}