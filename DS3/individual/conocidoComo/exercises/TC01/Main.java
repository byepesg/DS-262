import java.util.Random;
import java.util.Scanner;

//Implementación de un queue genérico
class NodoQueue {
    Position celda;
    NodoQueue siguiente;

    public NodoQueue(Position celda) {
        this.celda = celda;
        this.siguiente = null;
    }
}

class Queue {
    private NodoQueue primero;
    private NodoQueue ultimo;

    public Queue() {
        this.primero = null;
        this.ultimo = null;
    }

    public void enqueue(Position celda) {
        NodoQueue nuevo = new NodoQueue(celda);
        if (ultimo != null) {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        if (primero == null) {
            primero = nuevo;
        }
    }

    public Position dequeue() {
        if (primero == null) {
            return null;
        }
        Position celda = primero.celda;
        primero = primero.siguiente;
        if (primero == null) {
            ultimo = null;
        }
        return celda;
    }

    public boolean isEmpty() {
        return primero == null;
    }
}


public class Main {
    
    public static int n = 10;
    public static int[][] matriz = new int[n][n];
    
    public static int[][] semilla(int[][] matriz) {
        Random aleatorio = new Random();
        for (int i = 0; i < n; i++) {
            System.out.println("");
            for (int j = 0; j < n; j++) {
                boolean vale = aleatorio.nextBoolean();
                if (vale) {
                    System.out.print("* ");
                    matriz[i][j] = 1;
                } else {
                    System.out.print("0 ");
                    matriz[i][j] = 0;
                }
            }
        }
        System.out.println();
        return matriz;
    }
    
    public static void imprimirMatriz(int[][] malla) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (malla[i][j] == 1) {
                    System.out.print("* ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public static int gradoCelda(Position celda) {
        int miGrado = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {

                if (i == 0 && j == 0) continue; 
                
                int filaVecino = celda.row() + i;
                int colVecino = celda.column() + j;
                
                // Validación de límites del arreglo cuando se vaya a contar los grados de una celda
                if (filaVecino >= 0 && filaVecino < n && colVecino >= 0 && colVecino < n) {
                    if (matriz[filaVecino][colVecino] == 1) {
                        miGrado = miGrado + 1;
                    }
                }
            }
        }
        return miGrado;
    }
    
    public static int evaluarEstado(Position celda) {
        int grado = gradoCelda(celda);
        int estadoActual = matriz[celda.row()][celda.column()];
        
        if (estadoActual == 1 && (grado == 2 || grado == 3)) {
            return 1;
        } else if (estadoActual == 0 && grado == 3) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("--- GENERACIÓN 0 (INICIAL) ---");
        semilla(matriz);
        
        Genoma genoma = new Genoma();
        genoma.push(matriz);
        
        int generacionActual = 0;
        boolean continuar = true;
        
        // Menú interactivo
        while (continuar) {
            System.out.print("\n¿Deseas generar la siguiente generación? (1 para Sí, 0 para No): ");
            String opcion = scanner.nextLine().trim();
            
            if (opcion.equals("1")) {
                generacionActual++;
                

                Queue queueCeldas = new Queue();
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        queueCeldas.enqueue(new Position(i, j));
                    }
                }
                
                int[][] nuevaMatriz = new int[n][n];
                

                while (!queueCeldas.isEmpty()) {
                    Position celdaEvaluada = queueCeldas.dequeue();
                    nuevaMatriz[celdaEvaluada.row()][celdaEvaluada.column()] = evaluarEstado(celdaEvaluada);
                }
                
                matriz = nuevaMatriz;
                genoma.push(matriz); // Guardar en el historial 
                
                System.out.println("\n--- GENERACIÓN " + generacionActual + " ---");
                imprimirMatriz(matriz);
                
            } else if (opcion.equals("0")) {
                continuar = false;
                System.out.println("\nEjecución finalizada por el usuario.");
                System.out.println("El historial (Genoma) tiene guardadas " + genoma.size() + " generaciones en el stack.");
            } else {
                System.out.println("Opción no válida. Escribe 1 para sí, 0 para no.");
            }
        }
        
        scanner.close();
    }
}