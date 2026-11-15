public class TimeSlot
{
    private final String day;
    private final int startHour;
    private final int endHour;

    public TimeSlot(String day, int startHour, int endHour) {
        if (startHour >= endHour) throw new IllegalArgumentException("La hora de inicio debe ser menor que la de fin");
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public boolean overlaps(TimeSlot other) {
        return day.equals(other.day) && startHour < other.endHour && other.startHour < endHour;
    }

    @Override
    public String toString() {
        return day + " " + startHour + "-" + endHour;
    }
}
