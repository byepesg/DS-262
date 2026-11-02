package triage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fachada del sistema de una sede: registra solicitudes y las atiende por
 * orden de prioridad.
 *
 * Coordina tres estructuras y mantiene el invariante de consistencia:
 * el indice AVL contiene exactamente las solicitudes pendientes no
 * canceladas, y cada una de ellas esta tambien en la cola de pendientes.
 * La cola puede contener ademas tickets cancelados que aun no han llegado
 * al frente (eliminacion perezosa).
 *
 * Costos (n = pendientes, h = atenciones registradas):
 * registrar O(log n); atender O(log n) (niveles: Theta(1) amortizado en la
 * cola mas O(log n) en el indice); buscar O(log n); cancelar O(log n);
 * deshacer O(log n); historial Theta(h); pendientes ordenados Theta(n log n).
 */
public class TriageSystem {

    public static final int MOST_URGENT = 1;
    public static final int LEAST_URGENT = 5;

    /**
     * Tiempo maximo de espera en minutos por urgencia (indice 0 = urgencia 1),
     * inspirado en las escalas de triage de cinco niveles.
     */
    public static final long[] MAX_WAIT_MINUTES = {0, 10, 60, 120, 240};

    /** Politica que decide el orden de atencion. */
    public enum Policy {
        LEVELS, DEADLINE
    }

    private final PendingQueue queue;
    private final TicketIndex index;
    private final ServiceHistory history;
    private long arrivalCounter;
    private long currentMinute;

    public TriageSystem() {
        this(Policy.LEVELS);
    }

    public TriageSystem(Policy policy) {
        this.queue = policy == Policy.DEADLINE
                ? new DeadlinePriorityQueue()
                : new LevelPriorityQueue(LEAST_URGENT - MOST_URGENT + 1);
        this.index = new AvlIndex();
        this.history = new ServiceHistory();
        this.arrivalCounter = 0;
        this.currentMinute = 0;
    }

    // ------------------------------------------------------------------
    // Operaciones principales
    // ------------------------------------------------------------------

    /** RF1. Valida antes de modificar: si falla, el sistema queda intacto. */
    public Ticket registerRequest(String id, String name, int urgency) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id no puede estar vacio");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (urgency < MOST_URGENT || urgency > LEAST_URGENT) {
            throw new IllegalArgumentException("La urgencia debe estar entre "
                    + MOST_URGENT + " y " + LEAST_URGENT);
        }
        String cleanId = id.trim();
        if (index.find(cleanId) != null) {
            throw new IllegalArgumentException("Ya existe una solicitud con el id: " + cleanId);
        }
        long deadline = currentMinute + MAX_WAIT_MINUTES[urgency - 1];
        Ticket t = new Ticket(cleanId, name.trim(), urgency, arrivalCounter++,
                currentMinute, deadline);
        index.insert(t);
        queue.insert(t);
        return t;
    }

    /** RF2. Atiende la siguiente solicitud segun la politica; null si no hay. */
    public Ticket serveNext() {
        Ticket next = queue.removeNext();
        if (next == null) {
            return null;
        }
        index.delete(next.getId());
        history.recordService(next);
        return next;
    }

    /** RF3. */
    public Ticket findById(String id) {
        return id == null ? null : index.find(id.trim());
    }

    /**
     * RF4. El ticket sale del indice de inmediato y queda marcado para que
     * la cola lo descarte cuando llegue al frente.
     *
     * @return true si la solicitud existia y fue cancelada
     */
    public boolean cancelRequest(String id) {
        Ticket t = findById(id);
        if (t == null) {
            return false;
        }
        t.markCancelled();
        index.delete(t.getId());
        return true;
    }

    /**
     * RF5. Deshace la atencion mas reciente y devuelve la solicitud a
     * pendientes en la misma posicion que tenia.
     *
     * @return la solicitud restaurada o null si el historial esta vacio
     * @throws IllegalStateException si el identificador ya fue reutilizado
     */
    public Ticket undoLastService() {
        Ticket last = history.peekLast();
        if (last == null) {
            return null;
        }
        if (index.find(last.getId()) != null) {
            throw new IllegalStateException("No se puede deshacer: ya hay una solicitud"
                    + " pendiente con el id " + last.getId());
        }
        history.undoLast();
        index.insert(last);
        queue.restore(last);
        return last;
    }

    /** RF6. Solicitudes atendidas, de la mas reciente a la mas antigua. */
    public List<Ticket> getHistory() {
        return history.getHistory();
    }

    /**
     * RF7. Pendientes en el orden en que seran atendidos, sin modificar la
     * cola: se copian del indice y se ordenan con Heap-Sort.
     */
    public List<Ticket> pendingInAttentionOrder() {
        Ticket[] pending = index.inorder().toArray(new Ticket[0]);
        MinHeap.heapSort(pending, queue.attentionOrder());
        return new ArrayList<>(Arrays.asList(pending));
    }

    /** RF8. Pendientes ordenados por identificador (recorrido inorder del AVL). */
    public List<Ticket> pendingById() {
        return index.inorder();
    }

    /** RF9. Pendientes cuyo plazo maximo ya vencio en el minuto actual. */
    public List<Ticket> overduePending() {
        List<Ticket> out = new ArrayList<>();
        for (Ticket t : index.inorder()) {
            if (t.getDeadline() < currentMinute) {
                out.add(t);
            }
        }
        return out;
    }

    // ------------------------------------------------------------------
    // Reloj y consultas
    // ------------------------------------------------------------------

    public void advanceTime(long minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("El tiempo no puede retroceder");
        }
        currentMinute += minutes;
    }

    public long currentMinute() {
        return currentMinute;
    }

    public int pendingCount() {
        return index.size();
    }

    public int indexHeight() {
        return index.height();
    }

    public String policyName() {
        return queue.policyName();
    }

    /** Verifica el invariante de consistencia; se usa en las pruebas. */
    boolean invariantHolds() {
        return index.isValid() && queue.storedCount() >= index.size();
    }
}
