import java.util.Random;
import java.util.Scanner;
public class Main {

	//creamos la matriz que funciona de tablero de juego
    public static int n = 7;
    public static int[][] matriz = new int[n][n];

    public static boolean foodAt(Position newHead) {
        if (matriz[newHead.row()][newHead.column()] == 2) {
            matriz[newHead.row()][newHead.column()] = 0; // Desocupa la casilla
            return true;
        }
        return false;
    }

	// Versión 1: Sin parámetros (opción por defecto aleatoria)
	public static void generateFood() {
	    Position[] casillasVacias = new Position[n * n];
	    int cantidadVacias = 0;

	    // Recolectar casillas libres
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            if (matriz[i][j] == 0) {
	                casillasVacias[cantidadVacias] = new Position(i, j);
	                cantidadVacias++;
	            }
	        }
	    }

	    if (cantidadVacias == 0) {
	        throw new IllegalStateException("WIN");
	    }

	    // Elegir casilla aleatoria y llamar a la versión con parámetros
	    Random aleatorio = new Random();
	    Position foodPosition = casillasVacias[aleatorio.nextInt(cantidadVacias)];
	    generateFood(foodPosition); 
	}

	// Versión 2: Con parámetro explícito (ubica la comida en una posición dada)
	public static void generateFood(Position casilla) {
	    int r = casilla.row();
	    int c = casilla.column();

	    // Verificación de límites del tablero
	    if (r < 0 || r >= n || c < 0 || c >= n) {
	        throw new IllegalArgumentException("La posición dada está fuera del tablero: " + casilla);
	    }

	    // Verificación de casilla ocupada
	    if (matriz[r][c] != 0) {
	        throw new IllegalStateException("La casilla " + casilla + " ya está ocupada.");
	    }

	    // Asignar la comida
	    matriz[r][c] = 2;
	}

    public static void imprimirTablero() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] == 0) {
                    System.out.print("O ");
                } else if (matriz[i][j] == 1) {
                    System.out.print("5 ");
                } else if (matriz[i][j] == 2) {
                    System.out.print("* ");
                }
            }
            System.out.println();
        }
    }

	public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

       
        System.out.println("==========================================");
        System.out.println("        ¡BIENVENIDO A CULEBRITA!          ");
        System.out.println("==========================================");
        System.out.println("El tablero cuenta con una dimensión de " + n + "x" + n + ".\n");

        Snake snake = new Snake(2); //capacity de Snake inicializada a 2

        // 2. Impresión del tablero inicial (solo serpiente)
        System.out.println("--- Tablero Inicial (Serpiente colocada) ---");
        imprimirTablero();

        // 3. Selección manual de la primera comida
        boolean comidaUbicada = false;
        while (!comidaUbicada) {
            try {
                System.out.println("\n--- Disponer primera comida ---");
                System.out.print("Ingrese la fila (0 a " + (n - 1) + "): ");
                int r = scanner.nextInt();
                System.out.print("Ingrese la columna (0 a " + (n - 1) + "): ");
                int c = scanner.nextInt();

                generateFood(new Position(r, c));
                comidaUbicada = true;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + " Intente de nuevo.");
            }
        }

        // 4. Tablero listo con comida y serpiente
        System.out.println("\n--- Tablero Listo (Serpiente + Comida) ---");
        imprimirTablero();

        // 5. Bucle de juego
        boolean jugando = true;
        while (jugando) {
            System.out.println("\nDirecciones: 1=LEFT, 2=UP, 3=RIGHT, 4=DOWN (0 para Salir)");
            System.out.print("Seleccione dirección de movimiento: ");
            int direccion = scanner.nextInt();

            if (direccion == 0) {
                System.out.println("Juego finalizado por el usuario.");
                break;
            }

            int capacidadPrevia = snake.capacity();

            try {
                snake.move(direccion);

                // Imprimir Tablero
                System.out.println("\n--- Estado del Tablero ---");
                imprimirTablero();

				// Detección de Resize
				boolean huboResize = (snake.capacity() > capacidadPrevia);

				if (huboResize) {
				    System.out.println("\n[¡RESIZE!] La capacidad se ha duplicado a: " + snake.capacity());
				}

				// Imprime size, capacity, front y las variables de estado usadas para snake
				snake.imprimirEstadoInterno();

            } catch (IllegalStateException e) {
                if ("GAME_OVER".equals(e.getMessage())) {
                    System.out.println("\n==========================================");
                    System.out.println("  ¡GAME OVER! Has colisionado.  ");
                    System.out.println("==========================================");
                } else if ("WIN".equals(e.getMessage())) {
                    System.out.println("\n==========================================");
                    System.out.println("  ¡VICTORIA! Ocupaste todo el tablero. ");
                    System.out.println("==========================================");
                } else {
                    System.out.println("\nEstado del juego: " + e.getMessage());
                }
                jugando = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Entrada no válida: " + e.getMessage());
            }
        }

        scanner.close();
    }
}