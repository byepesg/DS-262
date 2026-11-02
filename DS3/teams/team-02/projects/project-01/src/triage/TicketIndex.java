package triage;

import java.util.List;

/**
 * Indice de las solicitudes pendientes por identificador. Responde a la
 * pregunta que la cola de prioridad no sabe responder: donde esta la
 * solicitud con el identificador X.
 *
 * Representaciones: {@link BstIndex} (BST ordinario, usado como linea base
 * para comparar) y {@link AvlIndex} (AVL, el que usa el sistema).
 */
public interface TicketIndex {

    /** @return true si se inserto; false si el identificador ya existia */
    boolean insert(Ticket t);

    /** @return el ticket o null si no existe */
    Ticket find(String id);

    /** @return true si existia y fue eliminado */
    boolean delete(String id);

    int size();

    /** Altura en aristas (vacio = -1, hoja = 0). */
    int height();

    /** Tickets en orden creciente de identificador (recorrido inorder). */
    List<Ticket> inorder();

    /** Verifica todos los invariantes de la representacion. */
    boolean isValid();
}
