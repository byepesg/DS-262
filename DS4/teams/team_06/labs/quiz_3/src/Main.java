public class Main {
    private static String nombrePosicion(Position position) {
        if (position == null) {
            return "null";
        }

        if (position.x == 5 && position.y == 5) return "A";
        if (position.x == 5 && position.y == 6) return "B";
        if (position.x == 5 && position.y == 7) return "N1";
        if (position.x == 5 && position.y == 8) return "N2";
        if (position.x == 6 && position.y == 8) return "N3";
        if (position.x == 7 && position.y == 8) return "N4";
        if (position.x == 7 && position.y == 7) return "N5";
        if (position.x == 7 && position.y == 6) return "N6";
        return "(" + position.x + "," + position.y + ")";
    }

    public static void main(String[] args) {
        
        Position[] frutas = {
            new Position(5, 8), // N2
            new Position(7, 7)  // N5
        };

        // Instancia directa con tu constructor existente
        Snake s = new Snake(10, frutas);

        Position[] movimientos = {
            new Position(0, 1),  // N1: Derecha (no come)
            new Position(0, 1),  // N2: Derecha (come fruta)
            new Position(1, 0),  // N3: Abajo (no come)
            new Position(1, 0),  // N4: Abajo (no come)
            new Position(0, -1), // N5: Izquierda (come fruta)
            new Position(0, -1)  // N6: Izquierda (no come)
        };

        String[] movs = {"N1", "N2", "N3", "N4", "N5", "N6"};
        String[] acciones = {"no come", "come", "no come", "no come", "come", "no come"};

        for (int i = 0; i < movimientos.length; i++) {
            int capAntes = s.capacity();
            s.actualizarPosiciones(movimientos[i]);
            s.actualizarCuadricula();

            System.out.println(movs[i] + " -> " + acciones[i]);
            System.out.println("Tablero:");
            for (int fila = 0; fila < s.cuadriculaSize; fila++) {
                for (int columna = 0; columna < s.cuadriculaSize; columna++) {
                    String celda = s.cuadricula[fila][columna];
                    System.out.print(celda == null ? ". " : celda + " ");
                }
                System.out.println();
            }
            System.out.print("Después del movimiento: data = [");
            for (int j = 0; j < s.capacity(); j++) {
                System.out.print(nombrePosicion(s.data[j]));
                if (j < s.capacity() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
            System.out.println("posTail = " + s.posTail);
            System.out.println("size = " + s.size());
            System.out.println("capacity = " + s.capacity());
            System.out.println("resize = " + (s.capacity() != capAntes ? "Sí" : "No"));
            System.out.println();
        }
    }
}