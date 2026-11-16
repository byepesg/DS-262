package triage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Cola de prioridad de tickets con una cola FIFO por nivel de urgencia
 * (bucket queue). Se atiende primero el nivel mas urgente y, dentro de cada
 * nivel, el ticket que llego primero.
 *
 * <p>Las inserciones devuelven el localizador del nodo en la cola de su
 * nivel, que permite retirar ese ticket en {@code O(1)} con
 * {@link #remove(LinkedQueue.Node)}.</p>
 */
public class LevelPriorityQueue {

    private final LinkedQueue<Ticket>[] levels;
    private int size;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public LevelPriorityQueue(int levelCount) {
        if (levelCount < 1) {
            throw new IllegalArgumentException("Debe haber al menos un nivel");
        }
        this.levels = (LinkedQueue<Ticket>[]) new LinkedQueue[levelCount];
        for (int i = 0; i < levelCount; i++) {
            levels[i] = new LinkedQueue<>();
        }
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int levelCount() {
        return levels.length;
    }

    /**
     * Encola el ticket al final del nivel de su urgencia (1 = primer nivel).
     * {@code O(1)}.
     */
    public LinkedQueue.Node<Ticket> insert(Ticket t) {
        LinkedQueue.Node<Ticket> node = levelOf(t).enqueue(t);
        size++;
        return node;
    }

    /**
     * Encola el ticket al frente del nivel de su urgencia. {@code O(1)}.
     * Lo usa "deshacer": el ticket recien atendido era el frente de su nivel.
     */
    public LinkedQueue.Node<Ticket> insertFront(Ticket t) {
        LinkedQueue.Node<Ticket> node = levelOf(t).enqueueFront(t);
        size++;
        return node;
    }

    /**
     * Retira el ticket mas prioritario. {@code O(k)} con {@code k} niveles.
     */
    public Ticket removeNext() {
        for (LinkedQueue<Ticket> level : levels) {
            if (!level.isEmpty()) {
                size--;
                return level.dequeue();
            }
        }
        throw new NoSuchElementException("La cola de prioridad esta vacia");
    }

    public Ticket peekNext() {
        for (LinkedQueue<Ticket> level : levels) {
            if (!level.isEmpty()) {
                return level.peek();
            }
        }
        throw new NoSuchElementException("La cola de prioridad esta vacia");
    }

    /**
     * Retira el ticket cuyo localizador se recibe. {@code O(1)}.
     */
    public Ticket remove(LinkedQueue.Node<Ticket> node) {
        Ticket t = levelOf(node.value()).remove(node);
        size--;
        return t;
    }

    /**
     * Busqueda lineal por identificador recorriendo todos los niveles,
     * {@code O(n)}. Era la busqueda de la Entrega 1; el sistema ya no la usa
     * (usa el indice hash), pero se conserva como linea base del benchmark y
     * para verificar en las pruebas que el indice y la cola coinciden.
     *
     * @return el ticket encontrado o null si no existe
     */
    public Ticket linearFindById(String id) {
        for (LinkedQueue<Ticket> level : levels) {
            for (Ticket t : level) {
                if (t.getId().equals(id)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Pendientes en el orden exacto en que serian atendidos. {@code O(n)}.
     */
    public List<Ticket> toListInServiceOrder() {
        List<Ticket> result = new ArrayList<>(size);
        for (LinkedQueue<Ticket> level : levels) {
            for (Ticket t : level) {
                result.add(t);
            }
        }
        return result;
    }

    /** Cantidad de pendientes del nivel de urgencia dado (1..k). */
    public int sizeOfLevel(int urgency) {
        return levels[urgency - 1].size();
    }

    private LinkedQueue<Ticket> levelOf(Ticket t) {
        int u = t.getUrgency();
        if (u < 1 || u > levels.length) {
            throw new IllegalArgumentException("Urgencia fuera de rango: " + u);
        }
        return levels[u - 1];
    }
}
