import java.util.Random;

public class JuegoDeLaVida {

    static final int FILAS = 20;
    static final int COLUMNAS = 40;
    static final int GENERACIONES = 100;
    static final long VELOCIDAD_MS = 150;

    public static void main(String[] args) throws InterruptedException {
        boolean[][] tablero = crearTableroAleatorio();

        for (int gen = 0; gen < GENERACIONES; gen++) {
            limpiarConsola();
            imprimirTablero(tablero, gen);
            tablero = siguienteGeneracion(tablero);
            Thread.sleep(VELOCIDAD_MS);
        }
    }

    static boolean[][] crearTableroAleatorio() {
        boolean[][] tablero = new boolean[FILAS][COLUMNAS];
        Random rand = new Random();
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                tablero[y][x] = rand.nextDouble() < 0.25;
            }
        }
        return tablero;
    }

    static boolean[][] siguienteGeneracion(boolean[][] actual) {
        boolean[][] siguiente = new boolean[FILAS][COLUMNAS];
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                int vecinas = contarVecinasVivas(actual, y, x);
                boolean viva = actual[y][x];
                siguiente[y][x] = viva
                        ? (vecinas == 2 || vecinas == 3)
                        : (vecinas == 3);
            }
        }
        return siguiente;
    }

    static int contarVecinasVivas(boolean[][] tablero, int y, int x) {
        int contador = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dy == 0 && dx == 0) continue;
                int ny = (y + dy + FILAS) % FILAS;
                int nx = (x + dx + COLUMNAS) % COLUMNAS;
                if (tablero[ny][nx]) contador++;
            }
        }
        return contador;
    }

    static void imprimirTablero(boolean[][] tablero, int generacion) {
        StringBuilder sb = new StringBuilder();
        sb.append("Generación: ").append(generacion).append("\n");
        for (boolean[] fila : tablero) {
            for (boolean celda : fila) {
                sb.append(celda ? "█" : "·");
            }
            sb.append("\n");
        }
        System.out.print(sb);
    }

    static void limpiarConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
}