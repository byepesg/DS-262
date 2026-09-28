public class Main {
    public static void main(String[] args) {
        System.out.println(" TRAZA DEL QUIZ 03  Pregunta 3.f (Con Giros) ");

        Snake snake = new Snake(2);

        // Estado inicial: Avanza a la derecha
        snake.addHead(new Position(5, 5)); // Posición A (Cola)
        snake.addHead(new Position(5, 6)); // Posición B (Cabeza)

        System.out.println("\nESTADO INICIAL (Cola A, Cabeza B):");
        snake.imprimirEstado();

        // Creamos las posiciones simulando giros en el tablero
        Position pC = new Position(5, 7); // N1: Sigue a la derecha
        Position pD = new Position(4, 7); // N2: Gira hacia arriba
        Position pE = new Position(3, 7); // N3: Sigue hacia arriba
        Position pF = new Position(3, 6); // N4: Gira a la izquierda
        Position pG = new Position(3, 5); // N5: Sigue a la izquierda
        Position pH = new Position(4, 5); // N6: Gira hacia abajo
        //Ejecución del quiz
        ejecutarMovimiento(snake, "N1 (no come)", pC, false);
        ejecutarMovimiento(snake, "N2 (come)", pD, true);
        ejecutarMovimiento(snake, "N3 (no come)", pE, false);
        ejecutarMovimiento(snake, "N4 (no come)", pF, false);
        ejecutarMovimiento(snake, "N5 (come)", pG, true);
        ejecutarMovimiento(snake, "N6 (no come)", pH, false);
    }

    private static void ejecutarMovimiento(Snake snake, String nombreMovimiento, Position nuevaCabeza, boolean come) {
        System.out.println("\n--- Movimiento: " + nombreMovimiento + " ---");

        int capAnterior = snake.capacity();

        snake.addHead(nuevaCabeza);
        if (!come) {
            snake.removeTail();
        }

        boolean huboResize = snake.capacity() > capAnterior;
        System.out.println("¿Ocurrió un resize?: " + (huboResize ? "SÍ (de " + capAnterior + " a " + snake.capacity() + ")" : "NO"));

        snake.imprimirEstado();
    }
}