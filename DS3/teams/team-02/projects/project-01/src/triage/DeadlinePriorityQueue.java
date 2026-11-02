package triage;

import java.util.Comparator;

/**
 * Cola de pendientes ordenada por plazo maximo de atencion.
 *
 * Cada urgencia tiene un tiempo maximo de espera (ver
 * {@link TriageSystem#MAX_WAIT_MINUTES}); el plazo de un ticket es su minuto
 * de llegada mas ese tiempo. Se atiende primero el plazo mas cercano y, en
 * empate, la urgencia mayor y luego el orden de llegada. Asi una solicitud
 * poco urgente que lleva mucho tiempo esperando termina pasando por delante
 * de solicitudes mas urgentes recien llegadas, lo que evita la inanicion que
 * sufre la politica por niveles cuando el sistema esta sobrecargado.
 *
 * Como la clave ya no toma solo cinco valores, la representacion es un
 * min-heap binario: insertar y retirar cuestan O(log n).
 */
public class DeadlinePriorityQueue implements PendingQueue {

    private static final Comparator<Ticket> ORDER =
            Comparator.comparingLong(Ticket::getDeadline)
                    .thenComparingInt(Ticket::getUrgency)
                    .thenComparingLong(Ticket::getArrivalOrder);

    private final MinHeap<Ticket> heap = new MinHeap<>(ORDER);

    @Override
    public void insert(Ticket t) {
        heap.insert(t);
    }

    @Override
    public Ticket removeNext() {
        while (!heap.isEmpty()) {
            Ticket t = heap.removeMin();
            if (!t.isCancelled()) {
                return t;
            }
        }
        return null;
    }

    /** La clave del ticket no cambia, asi que basta con reinsertarlo. */
    @Override
    public void restore(Ticket t) {
        heap.insert(t);
    }

    @Override
    public Comparator<Ticket> attentionOrder() {
        return ORDER;
    }

    @Override
    public int storedCount() {
        return heap.size();
    }

    @Override
    public String policyName() {
        return "Por plazo maximo (min-heap)";
    }

    boolean heapIsValid() {
        return heap.isValid();
    }
}
