package triage;

import java.util.List;

/**
 * Fachada del sistema: registra solicitudes de atencion, las atiende por
 * orden de prioridad y mantiene consistentes las tres estructuras internas.
 *
 * <ul>
 *   <li>{@link LevelPriorityQueue}: solicitudes pendientes en orden de
 *       atencion.</li>
 *   <li>{@link HashTable} (indice): identificador de cada pendiente &rarr;
 *       localizador de su nodo en la cola de su nivel.</li>
 *   <li>{@link ServiceHistory}: pila de atenciones realizadas.</li>
 * </ul>
 *
 * <p>Invariante: un identificador esta en el indice si y solo si hay una
 * solicitud pendiente con ese identificador, y el localizador asociado
 * apunta al nodo de esa solicitud.</p>
 */
public class TriageSystem {

    public static final int MOST_URGENT = 1;
    public static final int LEAST_URGENT = 5;

    private final LevelPriorityQueue queue;
    private final HashTable<String, LinkedQueue.Node<Ticket>> index;
    private final ServiceHistory history;
    private long arrivalCounter;

    public TriageSystem() {
        this.queue = new LevelPriorityQueue(LEAST_URGENT - MOST_URGENT + 1);
        this.index = new HashTable<>();
        this.history = new ServiceHistory();
        this.arrivalCounter = 0;
    }

    /**
     * Registra una solicitud (RF1). {@code O(1)} esperado.
     *
     * @throws IllegalArgumentException si el id o el nombre estan vacios, la
     *         urgencia esta fuera de rango o el id ya pertenece a una
     *         solicitud pendiente
     */
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
        String key = id.trim();
        if (index.containsKey(key)) {
            throw new IllegalArgumentException("Ya existe una solicitud pendiente con el id: " + key);
        }
        Ticket t = new Ticket(key, name.trim(), urgency, arrivalCounter++);
        index.put(key, queue.insert(t));
        return t;
    }

    /**
     * Atiende la solicitud pendiente mas prioritaria (RF2). {@code O(1)}
     * esperado.
     *
     * @return la solicitud atendida, o null si no hay pendientes
     */
    public Ticket serveNext() {
        if (queue.isEmpty()) {
            return null;
        }
        Ticket next = queue.removeNext();
        index.remove(next.getId());
        history.recordService(next);
        return next;
    }

    /**
     * Siguiente solicitud que seria atendida, sin atenderla. {@code O(1)}.
     *
     * @return la solicitud, o null si no hay pendientes
     */
    public Ticket peekNext() {
        return queue.isEmpty() ? null : queue.peekNext();
    }

    /**
     * Busca una solicitud pendiente (RF3). {@code O(1)} esperado.
     *
     * @return la solicitud, o null si no hay una pendiente con ese id
     */
    public Ticket findById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        LinkedQueue.Node<Ticket> node = index.get(id.trim());
        return node == null ? null : node.value();
    }

    /**
     * Cancela una solicitud pendiente para que nunca sea atendida (RF4).
     * {@code O(1)} esperado.
     *
     * @return true si la solicitud existia y fue cancelada
     */
    public boolean cancelRequest(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        LinkedQueue.Node<Ticket> node = index.remove(id.trim());
        if (node == null) {
            return false;
        }
        queue.remove(node);
        return true;
    }

    /**
     * Deshace la atencion mas reciente (RF5): la solicitud vuelve a estar
     * pendiente en la misma posicion que ocupaba, es decir, al frente de su
     * nivel, conservando su orden de llegada original. {@code O(1)} esperado.
     *
     * @return la solicitud devuelta a pendientes, o null si el historial
     *         esta vacio
     * @throws IllegalStateException si, despues de atenderla, se registro
     *         otra solicitud pendiente con el mismo id; en ese caso no se
     *         modifica nada
     */
    public Ticket undoLastService() {
        Ticket last = history.lastServed();
        if (last == null) {
            return null;
        }
        String id = last.getId();
        if (index.containsKey(id)) {
            throw new IllegalStateException("No se puede deshacer: el id " + id
                    + " pertenece ahora a otra solicitud pendiente");
        }
        history.removeLast();
        index.put(id, queue.insertFront(last));
        return last;
    }

    /**
     * Solicitudes atendidas, de la mas reciente a la mas antigua (RF6).
     * {@code O(h)}.
     */
    public List<Ticket> getHistory() {
        return history.getHistory();
    }

    /**
     * Solicitudes pendientes en el orden en que serian atendidas (RF7).
     * {@code O(n)}.
     */
    public List<Ticket> getPendingInOrder() {
        return queue.toListInServiceOrder();
    }

    public int pendingCount() {
        return queue.size();
    }

    public int pendingCountByUrgency(int urgency) {
        if (urgency < MOST_URGENT || urgency > LEAST_URGENT) {
            throw new IllegalArgumentException("La urgencia debe estar entre "
                    + MOST_URGENT + " y " + LEAST_URGENT);
        }
        return queue.sizeOfLevel(urgency);
    }

    public int servedCount() {
        return history.size();
    }

    /**
     * Comprueba el invariante entre la cola y el indice recorriendo todos
     * los pendientes. {@code O(n)}; solo para pruebas.
     */
    boolean checkInvariant() {
        if (index.size() != queue.size()) {
            return false;
        }
        for (Ticket t : queue.toListInServiceOrder()) {
            LinkedQueue.Node<Ticket> node = index.get(t.getId());
            if (node == null || node.value() != t) {
                return false;
            }
        }
        return true;
    }
}
