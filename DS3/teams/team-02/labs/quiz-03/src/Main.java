public class Main {
    // Nombres de las posiciones usadas en la traza (A = cola inicial, B = cabeza inicial,
    // C..H = nuevas cabezas de N1..N6). Arreglos nativos, busqueda lineal.
    private static final String[] NAMES = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final Position[] POS = new Position[NAMES.length];

    public static void main(String[] args) {
        for (int i = 0; i < POS.length; i = i + 1) {
            POS[i] = new Position(0, i); // la serpiente avanza hacia la derecha
        }

        Snake s = new Snake(2);
        s.addHead(POS[0]); // A: cola
        s.addHead(POS[1]); // B: cabeza  -> size = capacity = 2
        print("Inicial (cola A, cabeza B)", s, false);

        boolean[] eats = {false, true, false, false, true, false}; // N1..N6
        for (int i = 0; i < eats.length; i = i + 1) {
            int capBefore = s.capacity();
            s.addHead(POS[i + 2]);            // primero addHead
            if (!eats[i]) s.removeTail();     // luego removeTail si no come
            boolean resized = s.capacity() != capBefore;
            String name = "N" + (i + 1) + (eats[i] ? " (come)" : " (no come)")
                + ", nueva cabeza " + NAMES[i + 2];
            print(name, s, resized);
        }
    }

    private static String label(Position p) {
        if (p == null) return "_";
        for (int i = 0; i < POS.length; i = i + 1) {
            if (POS[i].equals(p)) return NAMES[i];
        }
        return "?";
    }

    private static void print(String name, Snake s, boolean resized) {
        StringBuilder phys = new StringBuilder("[");
        for (int i = 0; i < s.capacity(); i = i + 1) {
            phys.append(label(s.rawAt(i)));
            if (i < s.capacity() - 1) phys.append(",");
        }
        phys.append("]");

        StringBuilder logical = new StringBuilder("[");
        for (int i = 0; i < s.size(); i = i + 1) {
            logical.append(label(s.get(i)));
            if (i < s.size() - 1) logical.append(",");
        }
        logical.append("]");

        System.out.println(name);
        System.out.println("  arreglo fisico = " + phys);
        System.out.println("  logico (cola->cabeza) = " + logical);
        System.out.println("  start = " + s.startIndex() + ", size = " + s.size()
            + ", capacity = " + s.capacity() + ", resize = " + (resized ? "SI" : "no"));
    }
}
