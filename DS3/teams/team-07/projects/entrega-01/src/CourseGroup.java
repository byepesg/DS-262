/**
 * Grupo de una asignatura con cupo máximo, lista de inscritos y lista de
 * espera ordenada por la prioridad oficial de los estudiantes.
 */
public class CourseGroup
{
    private String courseCode;
    private String courseName;
    private String groupNumber;
    private int capacity;
    private LinkedList<Student> enrolled;
    private PriorityQueueADT<Student> waitlist;

    public CourseGroup(String courseCode, String courseName, String groupNumber, int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("El cupo no puede ser negativo");
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.groupNumber = groupNumber;
        this.capacity = capacity;
        this.enrolled = new LinkedList<>();
        this.waitlist = new SortedLinkedPriorityQueue<>();
    }

    /** O(1). */
    public boolean hasSeat() {
        return enrolled.size() < capacity;
    }

    /**
     * Solicitud de inscripción. Si hay cupo, inscribe. Si no, el estudiante
     * entra a la lista de espera solo si confirmó que acepta que el cupo se le
     * asigne automáticamente cuando alguien cancele.
     * Costo: O(c + w) por la verificación de duplicados; insertar en espera es O(w).
     *
     * @param acceptsAutoAssignment true si el estudiante acepta la asignación automática
     */
    public RequestResult request(Student student, boolean acceptsAutoAssignment) {
        if (enrolled.contains(student) || waitlist.contains(student))
            throw new IllegalStateException(student.getName() + " ya está en " + this);
        if (hasSeat()) {
            enrolled.addLast(student);
            return RequestResult.ENROLLED;
        }
        if (!acceptsAutoAssignment) return RequestResult.REJECTED;
        waitlist.insert(student);
        return RequestResult.WAITLISTED;
    }

    /** El estudiante retira su aceptación y sale de la lista de espera. O(w). */
    public boolean leaveWaitlist(Student student) {
        return waitlist.remove(student);
    }

    /**
     * Cancela la inscripción de un estudiante. Si había alguien en espera,
     * el cupo pasa automáticamente al de mayor prioridad.
     *
     * @return el estudiante que recibió el cupo, o null si no había nadie en espera
     */
    public Student cancel(Student student) {
        if (!enrolled.remove(student))
            throw new IllegalStateException(student.getName() + " no está inscrito en " + this);
        if (waitlist.isEmpty()) return null;
        Student promoted = waitlist.removeHighest();
        enrolled.addLast(promoted);
        return promoted;
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
