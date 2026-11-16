/**
 * Cola de prioridad: siempre se saca primero el elemento de MAYOR prioridad.
 * Convención: a tiene más prioridad que b si a.compareTo(b) < 0.
 *
 * Es una interfaz para poder cambiar la implementación (hoy una lista
 * enlazada ordenada; más adelante un heap) sin tocar el resto del sistema.
 *
 * @param <T> tipo de los elementos
 */
public interface PriorityQueueADT<T extends Comparable<T>>
{
    void insert(T item);

    T removeHighest();

    T peekHighest();

    /** Saca un elemento cualquiera (por ejemplo, alguien que deja la lista de espera). */
    boolean remove(T item);

    boolean contains(T item);

    boolean isEmpty();

    int size();
}
