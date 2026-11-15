import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Reproduce la traza de la Pregunta 3 f) y ejecuta casos de prueba.
 * Uso:  java Main            (traza + pruebas + experimento por defecto)
 *       java Main m g seed   (experimento: m movimientos, g manzanas)
 */
public class Main {

    // La serpiente avanza a la derecha por la fila 0: la columna identifica cada celda.
    // A=(0,0)  B=(0,1)  N1=(0,2) ... N6=(0,7)
    private static final String[] NAMES = {"A", "B", "N1", "N2", "N3", "N4", "N5", "N6"};

    private static String name(Position p) {
        return p == null ? "_" : NAMES[p.column()];
    }

    private static String physical(Snake s) {
        StringBuilder sb = new StringBuilder("[");
        for (int j = 0; j < s.capacity(); j++) {
            if (j > 0) sb.append(" | ");
            sb.append(String.format("%-2s", name(s.rawCell(j))));
        }
        return sb.append("]").toString();
    }

    private static String logical(Snake s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(name(s.get(i)));
        }
        return sb.toString();
    }

    private static void printState(String label, Snake s, boolean resized) {
        System.out.printf("%-14s fisico=%-38s tail=%d head=%d size=%d capacity=%d resize=%s%n",
                label, physical(s), s.tailIndex(), s.headIndex(), s.size(), s.capacity(),
                resized ? "SI" : "no");
        System.out.printf("%-14s logico (cola->cabeza): %s%n", "", logical(s));
    }

    static void trace() {
        System.out.println("===== TRAZA PREGUNTA 3 f) =====");
        Snake s = new Snake(new Position(0, 0), new Position(0, 1));   // A = cola, B = cabeza
        printState("Inicial", s, false);

        boolean[] eats = {false, true, false, false, true, false};     // N1..N6
        for (int t = 0; t < eats.length; t++) {
            int before = s.resizeCount();
            boolean ok = s.move(new Position(0, t + 2), eats[t]);
            if (!ok) { System.out.println("GAME OVER"); return; }
            printState("N" + (t + 1) + (eats[t] ? " (come)" : " (no come)"), s, s.resizeCount() > before);
        }
        System.out.println("Total resizes = " + s.resizeCount() + ", total copias = " + s.copyCount());
        System.out.println();
    }

    // ---------------------------------------------------------------- pruebas
    private static int passed = 0, failed = 0;

    private static void check(boolean cond, String msg) {
        if (cond) passed++; else { failed++; System.out.println("  FALLO: " + msg); }
    }

    private static void expectThrows(Class<? extends Throwable> type, Runnable r, String msg) {
        try { r.run(); check(false, msg + " (no lanzo excepcion)"); }
        catch (Throwable e) { check(type.isInstance(e), msg + " (lanzo " + e + ")"); }
    }

    static void tests() {
        System.out.println("===== PRUEBAS =====");
        // --- DynamicArray
        expectThrows(IllegalArgumentException.class, () -> new DynamicArray<Integer>(0), "DA: capacidad 0");
        DynamicArray<Integer> a = new DynamicArray<>(1);
        for (int i = 0; i < 10; i++) a.append(i);
        check(a.size() == 10 && a.capacity() == 16, "DA: capacidades 1,2,4,8,16");
        check(a.get(0) == 0 && a.get(9) == 9, "DA: get");
        a.set(3, 33);
        check(a.get(3) == 33, "DA: set");
        check(a.removeLast() == 9 && a.size() == 9 && a.capacity() == 16, "DA: removeLast no contrae");
        expectThrows(IndexOutOfBoundsException.class, () -> a.get(9), "DA: get(size)");
        expectThrows(IndexOutOfBoundsException.class, () -> a.get(-1), "DA: get(-1)");
        expectThrows(IndexOutOfBoundsException.class, () -> a.set(9, 1), "DA: set(size)");
        DynamicArray<String> e = new DynamicArray<>(3);
        expectThrows(NoSuchElementException.class, e::removeLast, "DA: removeLast vacio");

        // --- Snake: buffer circular con wrap-around y resize "desenrollando"
        Snake s = new Snake(4);
        for (int i = 0; i < 4; i++) s.addHead(new Position(0, i));    // 0 1 2 3
        s.removeTail(); s.removeTail();                              // _ _ 2 3
        s.addHead(new Position(0, 4)); s.addHead(new Position(0, 5)); // 4 5 2 3 (dio la vuelta)
        check(s.tailIndex() == 2 && s.capacity() == 4, "Snake: wrap-around sin resize");
        s.addHead(new Position(0, 6));                                // resize: 2 3 4 5 6 _ _ _
        boolean order = true;
        for (int i = 0; i < 5; i++) order &= s.get(i).column() == i + 2;
        check(order && s.tailIndex() == 0 && s.capacity() == 8, "Snake: resize conserva orden logico");
        expectThrows(IndexOutOfBoundsException.class, () -> s.get(5), "Snake: get(size)");
        Snake empty = new Snake(1);
        expectThrows(NoSuchElementException.class, empty::removeTail, "Snake: removeTail vacia");
        expectThrows(IllegalArgumentException.class, () -> new Snake(0), "Snake: capacidad 0");

        // --- Colision: la cola actual cuenta como ocupada
        Snake c = new Snake(new Position(0, 0), new Position(0, 1));
        check(!c.move(new Position(0, 0), false), "Snake: moverse a la cola actual es GAME_OVER");

        System.out.println("Pruebas superadas: " + passed + ", fallidas: " + failed);
        System.out.println();
    }

    // ------------------------------------------------------------ experimento P4
    static void experiment(int m, int g, long seed) {
        System.out.println("===== EXPERIMENTO P4: m=" + m + " movimientos, g=" + g + " manzanas =====");
        Random rnd = new Random(seed);
        boolean[] eats = new boolean[m];
        int placed = 0;
        while (placed < g) { int j = rnd.nextInt(m); if (!eats[j]) { eats[j] = true; placed++; } }

        // Recorrido en serpentina por un tablero ancho: nunca revisita una celda.
        Snake s = new Snake(new Position(0, 0), new Position(0, 1));
        int maxSize = 2;
        for (int t = 0; t < m; t++) {
            s.addHead(new Position(t + 2, 0));     // celdas distintas: se omite la colision aqui
            maxSize = Math.max(maxSize, s.size());
            if (!eats[t]) s.removeTail();
        }
        System.out.printf("Longitud final=%d  maxima transitoria=%d  capacity=%d%n", s.size(), maxSize, s.capacity());
        System.out.printf("Resizes=%d  copias=%d  cota 2g+4=%d  escrituras=%d%n",
                s.resizeCount(), s.copyCount(), 2L * g + 4, m + 2);
        System.out.printf("Costo total (escrituras+copias)=%d  -> por movimiento=%.3f%n",
                m + 2 + s.copyCount(), (double) (m + 2 + s.copyCount()) / m);
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            long seed = args.length >= 3 ? Long.parseLong(args[2]) : 42;
            experiment(Integer.parseInt(args[0]), Integer.parseInt(args[1]), seed);
            return;
        }
        trace();
        tests();
        experiment(1_000_000, 0, 42);
        experiment(1_000_000, 1_000, 42);
        experiment(1_000_000, 500_000, 42);
    }
}
