package triage;

import java.util.Random;

/**
 * Experimentos reproducibles (semillas fijas) que respaldan el analisis del
 * documento tecnico:
 * 1. BST ordinario frente a AVL con identificadores ordenados y aleatorios.
 * 2. Cola por niveles frente a min-heap por plazo: tiempo de n operaciones.
 * 3. Simulacion de sobrecarga: cumplimiento de plazos por politica.
 *
 * Se ejecuta en un hilo con pila grande porque el BST degenerado puede
 * tener miles de niveles.
 */
public class Experiments {

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(null, Experiments::runAll, "experimentos", 512L * 1024 * 1024);
        worker.start();
        worker.join();
    }

    private static void runAll() {
        indexExperiment();
        queueExperiment();
        starvationExperiment();
    }

    // ------------------------------------------------------------------
    // 1. BST frente a AVL
    // ------------------------------------------------------------------

    private static void indexExperiment() {
        System.out.println("=== 1. Indice por identificador: BST frente a AVL ===");
        System.out.println("n inserciones seguidas de n busquedas exitosas.");
        System.out.printf("%-10s %-8s %10s %10s %12s %12s %10s%n",
                "ids", "n", "h(BST)", "h(AVL)", "ms(BST)", "ms(AVL)", "rot(AVL)");
        int[] sizes = {1000, 5000, 20000};
        for (String mode : new String[]{"ordenados", "aleatorios"}) {
            for (int n : sizes) {
                Ticket[] tickets = makeTickets(n, mode.equals("aleatorios"));
                // calentamiento de la JVM
                runIndex(new AvlIndex(), tickets);
                runIndex(new BstIndex(), tickets);
                BstIndex bst = new BstIndex();
                AvlIndex avl = new AvlIndex();
                double msBst = runIndex(bst, tickets);
                double msAvl = runIndex(avl, tickets);
                System.out.printf("%-10s %-8d %10d %10d %12.2f %12.2f %10d%n",
                        mode, n, bst.height(), avl.height(), msBst, msAvl, avl.rotations());
            }
        }
        System.out.println();
    }

    private static Ticket[] makeTickets(int n, boolean shuffled) {
        Ticket[] tickets = new Ticket[n];
        for (int i = 0; i < n; i++) {
            tickets[i] = new Ticket(String.format("T%07d", i), "x", 3, i);
        }
        if (shuffled) {
            Random random = new Random(11);
            for (int i = n - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Ticket tmp = tickets[i];
                tickets[i] = tickets[j];
                tickets[j] = tmp;
            }
        }
        return tickets;
    }

    private static double runIndex(TicketIndex index, Ticket[] tickets) {
        long start = System.nanoTime();
        for (Ticket t : tickets) {
            index.insert(t);
        }
        for (Ticket t : tickets) {
            if (index.find(t.getId()) == null) {
                throw new IllegalStateException("busqueda fallida");
            }
        }
        return (System.nanoTime() - start) / 1e6;
    }

    // ------------------------------------------------------------------
    // 2. Niveles frente a heap
    // ------------------------------------------------------------------

    private static void queueExperiment() {
        System.out.println("=== 2. Cola de pendientes: niveles frente a min-heap ===");
        System.out.println("n inserciones seguidas de n extracciones, urgencias aleatorias.");
        System.out.printf("%-10s %14s %14s%n", "n", "ms(niveles)", "ms(heap)");
        for (int n : new int[]{10_000, 100_000, 1_000_000}) {
            Ticket[] tickets = new Ticket[n];
            Random random = new Random(5);
            for (int i = 0; i < n; i++) {
                int urgency = 1 + random.nextInt(5);
                long minute = i / 2;
                tickets[i] = new Ticket("Q" + i, "x", urgency, i, minute,
                        minute + TriageSystem.MAX_WAIT_MINUTES[urgency - 1]);
            }
            runQueue(new LevelPriorityQueue(5), tickets);
            runQueue(new DeadlinePriorityQueue(), tickets);
            double levels = runQueue(new LevelPriorityQueue(5), tickets);
            double heap = runQueue(new DeadlinePriorityQueue(), tickets);
            System.out.printf("%-10d %14.2f %14.2f%n", n, levels, heap);
        }
        System.out.println();
    }

    private static double runQueue(PendingQueue queue, Ticket[] tickets) {
        long start = System.nanoTime();
        for (Ticket t : tickets) {
            queue.insert(t);
        }
        for (int i = 0; i < tickets.length; i++) {
            queue.removeNext();
        }
        return (System.nanoTime() - start) / 1e6;
    }

    // ------------------------------------------------------------------
    // 3. Inanicion bajo sobrecarga
    // ------------------------------------------------------------------

    /**
     * Turno de 8 horas en que llegan en promedio 1.25 solicitudes por minuto
     * y se atiende 1 por minuto. Se cuenta, por urgencia, que porcentaje de
     * las solicitudes atendidas lo fue dentro de su plazo, la espera maxima
     * y cuantas quedaron sin atender al final.
     */
    private static void starvationExperiment() {
        System.out.println("=== 3. Sobrecarga: 480 minutos, 1.25 llegadas y 1 atencion por minuto ===");
        for (TriageSystem.Policy policy : TriageSystem.Policy.values()) {
            simulate(policy);
        }
    }

    private static void simulate(TriageSystem.Policy policy) {
        TriageSystem system = new TriageSystem(policy);
        Random random = new Random(42);
        int[] arrived = new int[5];
        int[] served = new int[5];
        int[] onTime = new int[5];
        long[] maxWait = new long[5];
        int counter = 0;
        for (int minute = 0; minute < 480; minute++) {
            int arrivals = random.nextDouble() < 0.25 ? 2 : 1;
            for (int a = 0; a < arrivals; a++) {
                int urgency = pickUrgency(random.nextDouble());
                system.registerRequest("S" + counter++, "x", urgency);
                arrived[urgency - 1]++;
            }
            Ticket t = system.serveNext();
            if (t != null) {
                int u = t.getUrgency() - 1;
                long wait = minute - t.getArrivalMinute();
                served[u]++;
                if (minute <= t.getDeadline()) {
                    onTime[u]++;
                }
                maxWait[u] = Math.max(maxWait[u], wait);
            }
            system.advanceTime(1);
        }
        int[] left = new int[5];
        for (Ticket t : system.pendingById()) {
            int u = t.getUrgency() - 1;
            left[u]++;
            maxWait[u] = Math.max(maxWait[u], system.currentMinute() - t.getArrivalMinute());
        }
        System.out.println("Politica: " + system.policyName());
        System.out.printf("%-9s %8s %9s %12s %12s %10s%n",
                "urgencia", "llegan", "atendidas", "% a tiempo", "espera max", "sin atender");
        for (int u = 0; u < 5; u++) {
            double pct = served[u] == 0 ? 0 : 100.0 * onTime[u] / served[u];
            System.out.printf("%-9d %8d %9d %11.1f%% %12d %10d%n",
                    u + 1, arrived[u], served[u], pct, maxWait[u], left[u]);
        }
        System.out.println();
    }

    /** Distribucion de urgencias: 5 %, 15 %, 30 %, 30 %, 20 %. */
    private static int pickUrgency(double p) {
        if (p < 0.05) {
            return 1;
        }
        if (p < 0.20) {
            return 2;
        }
        if (p < 0.50) {
            return 3;
        }
        if (p < 0.80) {
            return 4;
        }
        return 5;
    }
}
