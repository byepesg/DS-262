import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== REPRODUCCIÓN DE LA TRAZA DE LA PREGUNTA 3 ===");

        Position posA  = new Position(0, 0);
        Position posB  = new Position(0, 1);
        Position posN1 = new Position(0, 2);
        Position posN2 = new Position(0, 3);
        Position posN3 = new Position(0, 4);
        Position posN4 = new Position(0, 5);
        Position posN5 = new Position(0, 6);
        Position posN6 = new Position(0, 7);

        Map<Position, String> labels = new HashMap<>();
        labels.put(posA, "A");
        labels.put(posB, "B");
        labels.put(posN1, "N1");
        labels.put(posN2, "N2");
        labels.put(posN3, "N3");
        labels.put(posN4, "N4");
        labels.put(posN5, "N5");
        labels.put(posN6, "N6");

        // Estado Inicial
        Snake snake = new Snake(posA, posB);
        System.out.print("Inicio (A, B) : ");
        snake.printPhysicalState(labels, false);

        // N1 (no come): addHead -> removeTail
        snake.addHead(posN1);
        boolean resizeInN1 = snake.wasResized();
        snake.removeTail();
        System.out.print("N1 (no come)  : ");
        snake.printPhysicalState(labels, resizeInN1);

        // N2 (come): solo addHead
        snake.addHead(posN2);
        System.out.print("N2 (come)     : ");
        snake.printPhysicalState(labels, snake.wasResized());

        // N3 (no come): addHead -> removeTail
        snake.addHead(posN3);
        boolean resizeInN3 = snake.wasResized();
        snake.removeTail();
        System.out.print("N3 (no come)  : ");
        snake.printPhysicalState(labels, resizeInN3);

        // N4 (no come): addHead -> removeTail
        snake.addHead(posN4);
        boolean resizeInN4 = snake.wasResized();
        snake.removeTail();
        System.out.print("N4 (no come)  : ");
        snake.printPhysicalState(labels, resizeInN4);

        // N5 (come): solo addHead
        snake.addHead(posN5);
        System.out.print("N5 (come)     : ");
        snake.printPhysicalState(labels, snake.wasResized());

        // N6 (no come): addHead -> removeTail
        snake.addHead(posN6);
        boolean resizeInN6 = snake.wasResized();
        snake.removeTail();
        System.out.print("N6 (no come)  : ");
        snake.printPhysicalState(labels, resizeInN6);
    }
}