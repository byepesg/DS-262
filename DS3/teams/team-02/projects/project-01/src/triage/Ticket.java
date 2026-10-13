package triage;

/**
 * Solicitud de atencion con urgencia de 1 (mas urgente) a 5 (menos urgente).
 */
public class Ticket {

    private final String id;
    private final String name;
    private final int urgency;
    private final long arrivalOrder;

    public Ticket(String id, String name, int urgency, long arrivalOrder) {
        this.id = id;
        this.name = name;
        this.urgency = urgency;
        this.arrivalOrder = arrivalOrder;
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

    @Override
    public String toString() {
        return String.format("Ticket{id=%s, nombre=%s, urgencia=%d, llegada=%d}",
                id, name, urgency, arrivalOrder);
    }
}
