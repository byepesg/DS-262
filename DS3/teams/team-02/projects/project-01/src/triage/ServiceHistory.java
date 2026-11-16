package triage;

import java.util.ArrayList;
import java.util.List;

/**
 * Historial de solicitudes atendidas sobre una pila: el tope es la atencion
 * mas reciente, que es la unica que puede deshacerse.
 */
public class ServiceHistory {

    private final LinkedStack<Ticket> stack;

    public ServiceHistory() {
        this.stack = new LinkedStack<>();
    }

    /** Registra una atencion. {@code O(1)}. */
    public void recordService(Ticket t) {
        stack.push(t);
    }

    /** Atencion mas reciente sin retirarla, o null si no hay. {@code O(1)}. */
    public Ticket lastServed() {
        return stack.isEmpty() ? null : stack.peek();
    }

    /** Retira la atencion mas reciente para deshacerla. {@code O(1)}. */
    public Ticket removeLast() {
        return stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

    /**
     * Historial de la atencion mas reciente a la mas antigua, sin modificar
     * la pila. {@code O(h)}.
     */
    public List<Ticket> getHistory() {
        List<Ticket> result = new ArrayList<>(stack.size());
        for (Ticket t : stack) {
            result.add(t);
        }
        return result;
    }
}
