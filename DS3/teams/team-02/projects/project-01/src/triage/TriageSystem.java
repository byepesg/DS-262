package triage;

import java.util.List;

/**
 * Registra solicitudes de atencion y las atiende por orden de prioridad.
 */
public class TriageSystem {

    public static final int MOST_URGENT = 1;
    public static final int LEAST_URGENT = 5;

    private final LevelPriorityQueue queue;
    private final ServiceHistory history;
    private long arrivalCounter;

    public TriageSystem() {
        this.queue = new LevelPriorityQueue(LEAST_URGENT - MOST_URGENT + 1);
        this.history = new ServiceHistory();
        this.arrivalCounter = 0;
    }

    public Ticket registerRequest(String id, String name, int urgency) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("El id no puede estar vacio");
        }
        if (urgency < MOST_URGENT || urgency > LEAST_URGENT) {
            throw new IllegalArgumentException("La urgencia debe estar entre "
                    + MOST_URGENT + " y " + LEAST_URGENT);
        }
        if (queue.findById(id) != null) {
            throw new IllegalArgumentException("Ya existe una solicitud con el id: " + id);
        }
        Ticket t = new Ticket(id, name, urgency, arrivalCounter++);
        queue.insert(t);
        return t;
    }

    public Ticket serveNext() {
        if (queue.isEmpty()) {
            return null;
        }
        Ticket next = queue.removeNext();
        history.recordService(next);
        return next;
    }

    public Ticket findById(String id) {
        return queue.findById(id);
    }

    /**
     * Cancela una solicitud pendiente para que nunca sea atendida.
     *
     * @return true si la solicitud existia y fue cancelada
     */
    public boolean cancelRequest(String id) {
        return queue.removeById(id) != null;
    }

    /**
     * Solicitudes atendidas, de la mas reciente a la mas antigua.
     */
    public List<Ticket> getHistory() {
        return history.getHistory();
    }

    public int pendingCount() {
        return queue.size();
    }
}
