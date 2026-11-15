/**
 * Grupo de una asignatura con cupo máximo, horario, lista de inscritos y lista
 * de espera ordenada por la prioridad oficial de los estudiantes.
 */
public class CourseGroup
{
    private String courseCode;
    private String courseName;
    private String groupNumber;
    private int capacity;
    private LinkedList<TimeSlot> schedule;
    private LinkedList<Student> enrolled;
    private PriorityQueueADT<Student> waitlist;

    public CourseGroup(String courseCode, String courseName, String groupNumber, int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("El cupo no puede ser negativo");
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.groupNumber = groupNumber;
        this.capacity = capacity;
        this.schedule = new LinkedList<>();
        this.enrolled = new LinkedList<>();
        this.waitlist = new BinaryHeapPriorityQueue<>();
    }

    public void addTimeSlot(TimeSlot slot) {
        schedule.addLast(slot);
    }

    public boolean clashesWith(CourseGroup other) {
        for (TimeSlot mine : schedule)
            for (TimeSlot theirs : other.schedule)
                if (mine.overlaps(theirs)) return true;
        return false;
    }

    /** O(1). */
    public boolean hasSeat() {
        return enrolled.size() < capacity;
    }

    public boolean isEnrolled(Student student) {
        return enrolled.contains(student);
    }

    public boolean isWaiting(Student student) {
        return waitlist.contains(student);
    }

    public void enroll(Student student) {
        if (!hasSeat()) throw new IllegalStateException(this + " no tiene cupo");
        enrolled.addLast(student);
    }

    public void addToWaitlist(Student student) {
        waitlist.insert(student);
    }

    /** El estudiante retira su aceptación y sale de la lista de espera. O(w). */
    public boolean leaveWaitlist(Student student) {
        return waitlist.remove(student);
    }

    public Student nextInWaitlist() {
        return waitlist.isEmpty() ? null : waitlist.removeHighest();
    }

    public boolean drop(Student student) {
        return enrolled.remove(student);
    }

    public String getKey() {
        return courseCode + "-" + groupNumber;
    }

    public String getCourseCode() {
        return courseCode;
    }
    public String getCourseName() {
        return courseName;
    }
    public String getGroupNumber() {
        return groupNumber;
    }
    public int getCapacity() {
        return capacity;
    }
    public int getAvailableSeats() {
        return capacity - enrolled.size();
    }
    public LinkedList<TimeSlot> getSchedule() {
        return schedule;
    }
    public LinkedList<Student> getEnrolled() {
        return enrolled;
    }
    public PriorityQueueADT<Student> getWaitlist() {
        return waitlist;
    }

    @Override
    public String toString() {
        return courseName + " " + groupNumber;
    }
}
