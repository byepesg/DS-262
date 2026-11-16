package triage;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Mediciones de tiempo de ejecucion para distintos tamanos de entrada.
 *
 * <p>Cada medicion se repite {@link #REPS} veces despues de un calentamiento
 * (para que el compilador JIT optimice el codigo) y se reporta la mediana,
 * en nanosegundos por operacion. Los datos son pseudoaleatorios con semilla
 * fija, de modo que las ejecuciones son reproducibles.</p>
 *
 * <p>Uso: {@code java -cp out triage.Benchmark [archivo.csv]}; con
 * {@code --quick} se usan tamanos pequenos (para comprobar que funciona).</p>
 */
public final class Benchmark {

    private static final int REPS = 5;
    private static final long SEED = 2026L;
    private static long blackhole = 0;

    private Benchmark() {
    }

    /** Una medicion: ejecuta la operacion y devuelve la duracion en ns. */
    private interface Trial {
        long runOnce();
    }

    public static void main(String[] args) throws IOException {
        boolean quick = Arrays.asList(args).contains("--quick");
        String csvPath = null;
        for (String a : args) {
            if (!a.startsWith("--")) {
                csvPath = a;
            }
        }
        int[] sizes = quick ? new int[] {1_000, 10_000} : new int[] {1_000, 10_000, 100_000, 1_000_000};
        int[] quadraticSizes = quick ? new int[] {1_000, 5_000} : new int[] {1_000, 10_000, 30_000};

        StringBuilder csv = new StringBuilder("experimento,variante,n,ns_por_op\n");
        System.out.printf(Locale.ROOT, "Java %s, %d repeticiones, mediana en ns/op%n%n",
                System.getProperty("java.version"), REPS);

        System.out.println("[1] Operaciones de TriageSystem (Entrega 2)");
        System.out.printf("%10s %10s %10s %10s %10s %10s%n",
                "n", "registrar", "buscar", "cancelar", "atender", "deshacer");
        for (int n : sizes) {
            double reg = median(() -> timeRegister(n));
            double find = median(() -> timeFind(n, 10_000));
            double cancel = median(() -> timeCancel(n, Math.min(n / 2, 10_000)));
            double serve = median(() -> timeServeAll(n));
            double undo = median(() -> timeUndoAll(n));
            System.out.printf(Locale.ROOT, "%10d %10.1f %10.1f %10.1f %10.1f %10.1f%n",
                    n, reg, find, cancel, serve, undo);
            row(csv, "sistema", "registrar", n, reg);
            row(csv, "sistema", "buscar", n, find);
            row(csv, "sistema", "cancelar", n, cancel);
            row(csv, "sistema", "atender", n, serve);
            row(csv, "sistema", "deshacer", n, undo);
        }

        System.out.println();
        System.out.println("[2] Busqueda por id: recorrido lineal (Entrega 1) vs. indice hash (Entrega 2)");
        System.out.printf("%10s %14s %14s %10s%n", "n", "lineal", "hash", "speedup");
        for (int n : sizes) {
            double linear = median(() -> timeLinearFind(n, 1_000));
            double hashed = median(() -> timeFind(n, 1_000));
            System.out.printf(Locale.ROOT, "%10d %14.1f %14.1f %9.0fx%n", n, linear, hashed, linear / hashed);
            row(csv, "busqueda", "lineal", n, linear);
            row(csv, "busqueda", "hash", n, hashed);
        }

        System.out.println();
        System.out.println("[3] Registrar n solicitudes: verificacion lineal de duplicados (Entrega 1) vs. indice hash");
        System.out.printf("%10s %14s %14s%n", "n", "lineal", "hash");
        for (int n : quadraticSizes) {
            double linear = median(() -> timeRegisterLinearCheck(n));
            double hashed = median(() -> timeRegister(n));
            System.out.printf(Locale.ROOT, "%10d %14.1f %14.1f%n", n, linear, hashed);
            row(csv, "registro", "lineal", n, linear);
            row(csv, "registro", "hash", n, hashed);
        }

        System.out.println();
        System.out.println("[4] Cola de prioridad: insertar n y extraer n (ns por par insertar+extraer)");
        System.out.printf("%10s %14s %14s %14s%n", "n", "por niveles", "heap binario", "lista ordenada");
        for (int n : sizes) {
            double level = median(() -> timeLevelQueue(n));
            double heap = median(() -> timeHeap(n));
            boolean small = n <= quadraticSizes[quadraticSizes.length - 1];
            double sorted = small ? median(() -> timeSortedList(n)) : Double.NaN;
            System.out.printf(Locale.ROOT, "%10d %14.1f %14.1f %14s%n", n, level, heap,
                    small ? String.format(Locale.ROOT, "%.1f", sorted) : "-");
            row(csv, "cola", "niveles", n, level);
            row(csv, "cola", "heap", n, heap);
            if (small) {
                row(csv, "cola", "lista_ordenada", n, sorted);
            }
        }
        for (int n : quadraticSizes) {
            if (Arrays.binarySearch(sizes, n) < 0) {
                double sorted = median(() -> timeSortedList(n));
                row(csv, "cola", "lista_ordenada", n, sorted);
                System.out.printf(Locale.ROOT, "%10d %14s %14s %14.1f%n", n, "", "", sorted);
            }
        }

        System.out.println();
        int memN = quick ? 10_000 : 1_000_000;
        double bytes = bytesPerPending(memN);
        System.out.printf(Locale.ROOT, "[5] Memoria aproximada: %.0f bytes por solicitud pendiente (n = %d)%n",
                bytes, memN);
        row(csv, "memoria", "bytes_por_pendiente", memN, bytes);

        System.out.println();
        System.out.println("(control: " + blackhole + ")");
        if (csvPath != null) {
            Path path = Paths.get(csvPath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
                out.print(csv);
            }
            System.out.println("Resultados guardados en " + path);
        }
    }

    // ------------------------------------------------------------------
    // Datos de entrada

    private static String[] ids(int n) {
        String[] ids = new String[n];
        for (int i = 0; i < n; i++) {
            ids[i] = "T" + i;
        }
        return ids;
    }

    private static int[] urgencies(int n, Random rnd) {
        int[] u = new int[n];
        for (int i = 0; i < n; i++) {
            u[i] = 1 + rnd.nextInt(5);
        }
        return u;
    }

    private static TriageSystem filledSystem(String[] ids, int[] urgencies) {
        TriageSystem sys = new TriageSystem();
        for (int i = 0; i < ids.length; i++) {
            sys.registerRequest(ids[i], "Paciente", urgencies[i]);
        }
        return sys;
    }

    private static String[] randomExistingIds(String[] ids, int q, Random rnd) {
        String[] queries = new String[q];
        for (int i = 0; i < q; i++) {
            queries[i] = ids[rnd.nextInt(ids.length)];
        }
        return queries;
    }

    private static Ticket[] tickets(int n, Random rnd) {
        Ticket[] t = new Ticket[n];
        for (int i = 0; i < n; i++) {
            t[i] = new Ticket("T" + i, "Paciente", 1 + rnd.nextInt(5), i);
        }
        return t;
    }

    // ------------------------------------------------------------------
    // Experimentos (cada uno devuelve ns por operacion)

    private static long timeRegister(int n) {
        Random rnd = new Random(SEED);
        String[] ids = ids(n);
        int[] u = urgencies(n, rnd);
        TriageSystem sys = new TriageSystem();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            sys.registerRequest(ids[i], "Paciente", u[i]);
        }
        long elapsed = System.nanoTime() - start;
        blackhole += sys.pendingCount();
        return elapsed / n;
    }

    private static long timeRegisterLinearCheck(int n) {
        Random rnd = new Random(SEED);
        String[] ids = ids(n);
        int[] u = urgencies(n, rnd);
        LevelPriorityQueue pq = new LevelPriorityQueue(5);
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            if (pq.linearFindById(ids[i]) != null) {
                throw new IllegalStateException("id repetido");
            }
            pq.insert(new Ticket(ids[i], "Paciente", u[i], i));
        }
        long elapsed = System.nanoTime() - start;
        blackhole += pq.size();
        return elapsed / n;
    }

    private static long timeFind(int n, int q) {
        Random rnd = new Random(SEED);
        String[] ids = ids(n);
        TriageSystem sys = filledSystem(ids, urgencies(n, rnd));
        String[] queries = randomExistingIds(ids, q, rnd);
        long start = System.nanoTime();
        for (String id : queries) {
            blackhole += sys.findById(id).getUrgency();
        }
        return (System.nanoTime() - start) / q;
    }

    private static long timeLinearFind(int n, int q) {
        Random rnd = new Random(SEED);
        String[] ids = ids(n);
        int[] u = urgencies(n, rnd);
        LevelPriorityQueue pq = new LevelPriorityQueue(5);
        for (int i = 0; i < n; i++) {
            pq.insert(new Ticket(ids[i], "Paciente", u[i], i));
        }
        String[] queries = randomExistingIds(ids, q, rnd);
        long start = System.nanoTime();
        for (String id : queries) {
            blackhole += pq.linearFindById(id).getUrgency();
        }
        return (System.nanoTime() - start) / q;
    }

    private static long timeCancel(int n, int q) {
        Random rnd = new Random(SEED);
        String[] ids = ids(n);
        TriageSystem sys = filledSystem(ids, urgencies(n, rnd));
        // q identificadores distintos elegidos al azar (barajado parcial).
        String[] shuffled = ids.clone();
        for (int i = 0; i < q; i++) {
            int j = i + rnd.nextInt(n - i);
            String tmp = shuffled[i];
            shuffled[i] = shuffled[j];
            shuffled[j] = tmp;
        }
        long start = System.nanoTime();
        for (int i = 0; i < q; i++) {
            if (sys.cancelRequest(shuffled[i])) {
                blackhole++;
            }
        }
        return (System.nanoTime() - start) / q;
    }

    private static long timeServeAll(int n) {
        Random rnd = new Random(SEED);
        TriageSystem sys = filledSystem(ids(n), urgencies(n, rnd));
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            blackhole += sys.serveNext().getUrgency();
        }
        return (System.nanoTime() - start) / n;
    }

    private static long timeUndoAll(int n) {
        Random rnd = new Random(SEED);
        TriageSystem sys = filledSystem(ids(n), urgencies(n, rnd));
        for (int i = 0; i < n; i++) {
            sys.serveNext();
        }
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            blackhole += sys.undoLastService().getUrgency();
        }
        return (System.nanoTime() - start) / n;
    }

    private static long timeLevelQueue(int n) {
        Ticket[] t = tickets(n, new Random(SEED));
        long start = System.nanoTime();
        LevelPriorityQueue pq = new LevelPriorityQueue(5);
        for (Ticket x : t) {
            pq.insert(x);
        }
        while (!pq.isEmpty()) {
            blackhole += pq.removeNext().getUrgency();
        }
        return (System.nanoTime() - start) / n;
    }

    private static long timeHeap(int n) {
        Ticket[] t = tickets(n, new Random(SEED));
        long start = System.nanoTime();
        PriorityQueue<Ticket> pq = new PriorityQueue<>(Comparator.comparingInt(Ticket::getUrgency)
                .thenComparingLong(Ticket::getArrivalOrder));
        for (Ticket x : t) {
            pq.add(x);
        }
        while (!pq.isEmpty()) {
            blackhole += pq.poll().getUrgency();
        }
        return (System.nanoTime() - start) / n;
    }

    private static long timeSortedList(int n) {
        Ticket[] t = tickets(n, new Random(SEED));
        long start = System.nanoTime();
        SortedListPriorityQueue pq = new SortedListPriorityQueue();
        for (Ticket x : t) {
            pq.insert(x);
        }
        while (!pq.isEmpty()) {
            blackhole += pq.removeNext().getUrgency();
        }
        return (System.nanoTime() - start) / n;
    }

    private static double bytesPerPending(int n) {
        String[] ids = ids(n);
        int[] u = urgencies(n, new Random(SEED));
        Runtime rt = Runtime.getRuntime();
        long before = usedMemory(rt);
        TriageSystem sys = filledSystem(ids, u);
        long after = usedMemory(rt);
        blackhole += sys.pendingCount();
        // Se descuentan los ids (ya existian antes de medir) pero no el resto:
        // Ticket, nodo de la cola, entrada de la tabla hash y cubetas.
        return (after - before) / (double) n;
    }

    private static long usedMemory(Runtime rt) {
        for (int i = 0; i < 4; i++) {
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return rt.totalMemory() - rt.freeMemory();
    }

    // ------------------------------------------------------------------

    private static double median(Trial trial) {
        trial.runOnce(); // calentamiento
        trial.runOnce();
        long[] samples = new long[REPS];
        for (int i = 0; i < REPS; i++) {
            samples[i] = trial.runOnce();
        }
        Arrays.sort(samples);
        return samples[REPS / 2];
    }

    private static void row(StringBuilder csv, String experiment, String variant, int n, double value) {
        csv.append(String.format(Locale.ROOT, "%s,%s,%d,%.2f%n", experiment, variant, n, value));
    }
}
