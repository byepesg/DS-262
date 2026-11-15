public class Main {

    static String[] names = {"A", "B", "C", "D", "E", "F", "G", "H"};
    static Position[] cells = {
        new Position(0, 0), new Position(0, 1), new Position(0, 2), new Position(0, 3),
        new Position(1, 3), new Position(2, 3), new Position(2, 2), new Position(2, 1)
    };

    static String name(Position p) {
        if (p == null) {
            return "_";
        }
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].equals(p)) {
                return names[i];
            }
        }
        return "?";
    }

    static void print(String step, Snake s, boolean resized) {
        String array = "[";
        for (int i = 0; i < s.capacity(); i++) {
            array += name(s.cell(i));
            if (i < s.capacity() - 1) {
                array += ", ";
            }
        }
        array += "]";

        String snake = "";
        for (int i = 0; i < s.size(); i++) {
            snake += name(s.get(i));
        }

        System.out.println(step + "  arreglo=" + array + "  serpiente=" + snake
                + "  front=" + s.front() + "  size=" + s.size()
                + "  capacity=" + s.capacity() + "  resize=" + (resized ? "SI" : "no"));
    }

    static void move(Snake s, String step, int cell, boolean eats) {
        int before = s.capacity();
        s.move(cells[cell], eats);
        print(step + (eats ? " (come)   " : " (no come)"), s, s.capacity() != before);
    }

    public static void main(String[] args) {
	System.out.println("Jeicob Stiven Murillo Ramos (1000334659)");
	System.out.println("Andres Santiago Morales Robayo (1011080401)");
        System.out.println("TRAZA SNAKE");
        Snake s = new Snake(cells[0], cells[1]);
        print("Inicio        ", s, false);
        move(s, "N1", 2, false);
        move(s, "N2", 3, true);
        move(s, "N3", 4, false);
        move(s, "N4", 5, false);
        move(s, "N5", 6, true);
        move(s, "N6", 7, false);

        System.out.println();
        System.out.println("PRUEBA DYNAMIC ARRAY");
        DynamicArray<Integer> a = new DynamicArray<>(1);
        for (int i = 0; i < 9; i++) {
            a.append(i);
            System.out.println("append(" + i + ")  size=" + a.size() + "  capacity=" + a.capacity());
        }
        a.set(0, 100);
        System.out.println("get(0)=" + a.get(0));
        System.out.println("removeLast()=" + a.removeLast() + "  size=" + a.size() + "  capacity=" + a.capacity());

        try {
            new DynamicArray<Integer>(0);
        } catch (IllegalArgumentException e) {
            System.out.println("Capacidad 0: " + e.getMessage());
        }
        try {
            a.get(20);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("get(20): " + e.getMessage());
        }
        try {
            new DynamicArray<Integer>(3).removeLast();
        } catch (IllegalStateException e) {
            System.out.println("removeLast vacio: " + e.getMessage());
        }
    }
}
