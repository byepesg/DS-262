package triage;

/**
 * Solicitud de atencion con urgencia de 1 (mas urgente) a 5 (menos urgente).
 *
 * Los datos de la solicitud son inmutables. El unico estado que cambia es la
 * marca de cancelacion, que usan las colas de pendientes para descartar la
 * solicitud de forma perezosa cuando llega al frente.
 */
public class Ticket {

    private final String id;
    private final String name;
    private final int urgency;
    private final long arrivalOrder;
    private final long arrivalMinute;
    private final long deadline;
    private boolean cancelled;

    public Ticket(String id, String name, int urgency, long arrivalOrder,
            long arrivalMinute, long deadline) {
        this.id = id;
        this.name = name;
        this.urgency = urgency;
        this.arrivalOrder = arrivalOrder;
        this.arrivalMinute = arrivalMinute;
        this.deadline = deadline;
        this.cancelled = false;
    }

    /** Constructor corto: llegada en el minuto 0 y plazo igual a 0. */
    public Ticket(String id, String name, int urgency, long arrivalOrder) {
        this(id, name, urgency, arrivalOrder, 0, 0);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUrgency() {
        return urgency;
    }

    public long getArrivalOrder() {
        return arrivalOrder;
    }

    public long getArrivalMinute() {
        return arrivalMinute;
    }

    /** Minuto maximo en que deberia ser atendida segun su urgencia. */
    public long getDeadline() {
        return deadline;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    void markCancelled() {
        cancelled = true;
    }

    @Override
    public String toString() {
        return String.format("Ticket{id=%s, nombre=%s, urgencia=%d, llegada=%d, minuto=%d, plazo=%d}",
                id, name, urgency, arrivalOrder, arrivalMinute, deadline);
    }
}
