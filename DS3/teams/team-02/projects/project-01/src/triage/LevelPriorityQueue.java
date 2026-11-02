package triage;

import java.util.Comparator;

/**
 * Cola de prioridad de tickets con una cola FIFO por nivel de urgencia.
 * Se atiende primero el nivel mas urgente y, dentro de cada nivel, el
 * ticket que llego primero.
 *
 * Costos (k = numero de niveles, constante igual a 5): insertar Theta(1);
 * retirar Theta(k) = Theta(1) mas el descarte de los cancelados que esten al
 * frente, que se paga una sola vez por ticket cancelado (costo amortizado
 * Theta(1)); restaurar Theta(1).
 */
public class LevelPriorityQueue implements PendingQueue {

    private static final Comparator<Ticket> ORDER =
            Comparator.comparingInt(Ticket::getUrgency)
                    .thenComparingLong(Ticket::getArrivalOrder);

    private final LinkedQueue<Ticket>[] levels;
    private int stored;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public LevelPriorityQueue(int levelCount) {
        if (levelCount <= 0) {
            throw new IllegalArgumentException("Debe existir al menos un nivel");
        }
        this.levels = (LinkedQueue<Ticket>[]) new LinkedQueue[levelCount];
        for (int i = 0; i < levelCount; i++) {
            levels[i] = new LinkedQueue<>();
        }
        this.stored = 0;
    }

    /** Encola el ticket al final del nivel de su urgencia (1 = primer nivel). */
    @Override
    public void insert(Ticket t) {
        levels[t.getUrgency() - 1].enqueue(t);
        stored++;
    }

    @Override
    public Ticket removeNext() {
        for (LinkedQueue<Ticket> level : levels) {
            while (!level.isEmpty()) {
                Ticket t = level.dequeue();
                stored--;
                if (!t.isCancelled()) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * El ticket que se deshace fue el ultimo atendido, es decir, el frente de
     * su nivel en ese momento; por eso vuelve al frente y no al final.
     */
    @Override
    public void restore(Ticket t) {
        levels[t.getUrgency() - 1].addFirst(t);
        stored++;
    }

    @Override
    public Comparator<Ticket> attentionOrder() {
        return ORDER;
    }

    @Override
    public int storedCount() {
        return stored;
    }

    @Override
    public String policyName() {
        return "Por niveles (urgencia y luego orden de llegada)";
    }
}
