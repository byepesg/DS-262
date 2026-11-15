package triage;

import java.util.NoSuchElementException;

/**
 * Cola de prioridad de tickets con una cola FIFO por nivel de urgencia.
 * Se atiende primero el nivel mas urgente y, dentro de cada nivel, el
 * ticket que llego primero.
 */
public class LevelPriorityQueue {

    private final LinkedQueue<Ticket>[] levels;
    private int size;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public LevelPriorityQueue(int levelCount) {
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

    /**
     * Encola el ticket en el nivel de su urgencia (1 = primer nivel).
     */
    public void insert(Ticket t) {
        levels[t.getUrgency() - 1].enqueue(t);
        size++;
    }

    public Ticket removeNext() {
        for (LinkedQueue<Ticket> level : levels) {
            if (!level.isEmpty()) {
                size--;
                return level.dequeue();
            }
        }
        throw new NoSuchElementException("La cola de prioridad esta vacia");
    }

    /**
     * Busca un ticket por su identificador recorriendo todos los niveles.
     *
     * @return el ticket encontrado o null si no existe
     */
    public Ticket findById(String id) {
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
     * Retira de la cola el ticket con el identificador dado.
     *
     * @return el ticket retirado o null si no existe
     */
    public Ticket removeById(String id) {
        Ticket t = findById(id);
        if (t == null) {
            return null;
        }
        levels[t.getUrgency() - 1].remove(t);
        size--;
        return t;
    }

    public Ticket peekNext() {
        for (LinkedQueue<Ticket> level : levels) {
            if (!level.isEmpty()) {
                return level.peek();
            }
        }
        throw new NoSuchElementException("La cola de prioridad esta vacia");
    }
}
