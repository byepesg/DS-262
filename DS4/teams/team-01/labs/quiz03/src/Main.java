/******************************************************************************

Welcome to GDB Online.
GDB online is an online compiler and debugger tool for C, C++, Python, Java, PHP, Ruby, Perl,
C#, OCaml, VB, Swift, Pascal, Fortran, Haskell, Objective-C, Assembly, HTML, CSS, JS, SQLite, Prolog.
Code, Compile, Run and Debug online from anywhere in world.

*******************************************************************************/

public class Main{
    
	public static void main(String[] args) {
	    
	    System.out.println("Traza de la pregunta 3");

      
        Snake snake = new Snake(2);
        Position posA = new Position(0, 0); 
        Position posB = new Position(0, 1);

        snake.addHead(posA);
        snake.addHead(posB);

        System.out.println("Estado Inicial ");
        imprimirEstado(snake);

        System.out.println("Movimiento 1 (No come)");
        snake.addHead(new Position(0, 2));
        snake.removeTail();
        imprimirEstado(snake);

        System.out.println("Movimiento 2 (Come)");
        snake.addHead(new Position(0, 3));
        imprimirEstado(snake);

        System.out.println("Movimiento 3 (No come)");
        snake.addHead(new Position(1, 3));
        snake.removeTail();
        imprimirEstado(snake);

        System.out.println("Movimiento 4 (No come)");
        snake.addHead(new Position(1, 2));
        snake.removeTail();
        imprimirEstado(snake);

        System.out.println("\n--- Movimiento N5 (Come) ---");
        snake.addHead(new Position(1, 1));
        imprimirEstado(snake);

        System.out.println("\n--- Movimiento N6 (No come) ---");
        snake.addHead(new Position(1, 0));
        snake.removeTail();
        imprimirEstado(snake);
    }

    private static void imprimirEstado(Snake snake) {
        System.out.println("Size: " + snake.size() + " /  Capacity: " + snake.capacity());
        System.out.print("Contenido logico (desde Cola hasta Cabeza): [ ");
        for (int i = 0; i < snake.size(); i++) {
            System.out.print(snake.get(i) + (i < snake.size() - 1 ? ", " : " "));
        }
        System.out.println("]");
    }
}
