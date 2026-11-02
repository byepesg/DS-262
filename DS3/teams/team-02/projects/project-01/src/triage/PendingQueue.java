package triage;

import java.util.Comparator;

/**
 * Tipo abstracto de la cola de solicitudes pendientes: decide cual es la
 * siguiente solicitud a atender. Tiene dos representaciones:
 * {@link LevelPriorityQueue} (una cola FIFO por nivel) y
 * {@link DeadlinePriorityQueue} (un min-heap ordenado por plazo maximo).
 *
 * Las solicitudes canceladas no se buscan dentro de la cola: se marcan en el
 * ticket y se descartan cuando llegan al frente (eliminacion perezosa).
 */
public interface PendingQueue {

    /** Agrega una solicitud nueva. */
    void insert(Ticket t);

    /**
     * Retira la siguiente solicitud no cancelada.
     *
     * @return la solicitud o null si no queda ninguna activa
     */
    Ticket removeNext();

    /**
     * Devuelve a la cola una solicitud que acaba de ser atendida (deshacer),
     * dejandola en la misma posicion que tenia antes de ser retirada.
     */
    void restore(Ticket t);

    /** Orden en que esta cola atiende las solicitudes. */
    Comparator<Ticket> attentionOrder();

    /** Numero de tickets guardados, incluidos los cancelados aun no descartados. */
    int storedCount();

    /** Nombre de la politica para mostrar al usuario. */
    String policyName();
}
