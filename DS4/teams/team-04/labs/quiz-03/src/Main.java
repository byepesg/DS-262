import java.util.Arrays;

public class Main {

    // Etiquetas: la columna de cada Position coincide con su posición en este arreglo
    private static final String[] ETIQUETAS = {"A", "B", "N1", "N2", "N3", "N4", "N5", "N6"};

    private static Position pos(int i) {
        return new Position(0, i); // fila 0, columna i -> ETIQUETAS[i]
    }

    private static String etiqueta(Position p) {
        return (p == null) ? "[   ]" : String.format("[%-3s]", ETIQUETAS[p.column()]);
    }

    public static void main(String[] args) {
        System.out.println("\n==================================================");
        System.out.println("  TRAZA DE SNAKE (Pregunta 3)");
        System.out.println("==================================================");
        probarTrazaSnake();
    }

    // ---------- P3 ----------
    private static void probarTrazaSnake() {
        // Condiciones iniciales: size = capacity = 2, cola = A, cabeza = B
        Snake snake = new Snake(2);
        snake.addHead(pos(0)); // A (cola)
        snake.addHead(pos(1)); // B (cabeza)
        imprimirPaso(snake, "Estado inicial (cola A, cabeza B)", false);

        // Secuencia del enunciado: true = come, false = no come
        boolean[] come = {false, true, false, false, true, false};
        for (int k = 0; k < come.length; k++) {
            String n = "N" + (k + 1);
            boolean resize = ejecutarMovimiento(snake, pos(k + 2), come[k]);
            String titulo = n + (come[k] ? " (come): addHead(" + n + ")"
                                         : " (no come): addHead(" + n + "), removeTail()");
            imprimirPaso(snake, titulo, resize);
        }
    }

    private static boolean ejecutarMovimiento(Snake snake, Position nuevaCabeza, boolean comeManzana) {
        int capInicial = snake.capacity();
        snake.addHead(nuevaCabeza);      // MOVE siempre hace addHead primero
        if (!comeManzana) {
            snake.removeTail();          // luego removeTail si no come
        }
        return snake.capacity() > capInicial;
    }

    private static void imprimirPaso(Snake snake, String titulo, boolean huboResize) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.println("¿Hubo resize?: " + (huboResize ? "SÍ" : "NO"));
        System.out.println("size = " + snake.size() + " | capacity = " + snake.capacity()
                + " | tail (físico) = " + snake.getTailIndex() + " | head (físico) = " + snake.getHeadIndex());

        Position[] fisico = snake.getData();
        String[] visual = new String[snake.capacity()];
        for (int i = 0; i < snake.capacity(); i++) {
            visual[i] = etiqueta(fisico[i]);
        }
        System.out.println("Arreglo físico: " + Arrays.toString(visual));
    }
}