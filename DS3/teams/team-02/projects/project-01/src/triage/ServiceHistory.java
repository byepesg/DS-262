package triage;

import java.util.ArrayList;
import java.util.List;

/**
 * Historial de solicitudes atendidas, de la mas reciente a la mas antigua.
 */
public class ServiceHistory {

    private final LinkedStack<Ticket> stack;

    public ServiceHistory() {
        this.stack = new LinkedStack<>();
    }

    public void recordService(Ticket t) {
        stack.push(t);
    }

    public int size() {
        return stack.size();
    }

    /**
     * Devuelve el historial sin modificar la pila.
     */
    public List<Ticket> getHistory() {
        List<Ticket> result = new ArrayList<>();
        LinkedStack<Ticket> temp = new LinkedStack<>();
        while (!stack.isEmpty()) {
            Ticket t = stack.pop();
            result.add(t);
            temp.push(t);
        }
        while (!temp.isEmpty()) {
            stack.push(temp.pop());
        }
        return result;
    }
}
