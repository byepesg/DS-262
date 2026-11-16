public class RegistrationSystem
{
    private Dictionary<String, Student> students;
    private Dictionary<String, CourseGroup> groups;
    private Dictionary<String, LinkedList<CourseGroup>> enrolledGroups;
    private Dictionary<String, LinkedList<CourseGroup>> waitingGroups;
    private Dictionary<String, LinkedList<CourseGroup>> groupsByCourse;
    private Phase phase;

    public RegistrationSystem() {
        this.students = new AVLTreeDictionary<>();
        this.groups = new AVLTreeDictionary<>();
        this.enrolledGroups = new AVLTreeDictionary<>();
        this.waitingGroups = new AVLTreeDictionary<>();
        this.groupsByCourse = new AVLTreeDictionary<>();
        this.phase = Phase.APPOINTMENTS;
    }

    public void addStudent(Student student) {
        students.put(student.getCode(), student);
        enrolledGroups.put(student.getCode(), new LinkedList<>());
        waitingGroups.put(student.getCode(), new LinkedList<>());
    }

    public void addGroup(CourseGroup group) {
        groups.put(group.getKey(), group);
        LinkedList<CourseGroup> sameCourse = groupsByCourse.get(group.getCourseCode());
        if (sameCourse == null) {
            sameCourse = new LinkedList<>();
            groupsByCourse.put(group.getCourseCode(), sameCourse);
        }
        sameCourse.addLast(group);
    }

    public Student findStudent(String code) {
        return students.get(code);
    }

    public CourseGroup findGroup(String key) {
        return groups.get(key);
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public Phase getPhase() {
        return phase;
    }

    public RequestResult request(String studentCode, String groupKey, boolean acceptsAutoAssignment) {
        Student student = require(students.get(studentCode), "estudiante " + studentCode);
        CourseGroup group = require(groups.get(groupKey), "grupo " + groupKey);
        if (phase == Phase.DROP_ONLY) return RequestResult.PHASE_CLOSED;
        if (group.isWaiting(student))
            throw new IllegalStateException(student.getName() + " ya está en espera en " + group);
        if (enrolledInCourse(student, group.getCourseCode()) != null) return RequestResult.ALREADY_IN_COURSE;
        if (clashesWithSchedule(student, group)) return RequestResult.SCHEDULE_CLASH;
        if (group.hasSeat()) {
            enrollAndClean(student, group);
            return RequestResult.ENROLLED;
        }
        if (!acceptsAutoAssignment) return RequestResult.REJECTED;
        group.addToWaitlist(student);
        waitingGroups.get(studentCode).addLast(group);
        return RequestResult.WAITLISTED;
    }

    public boolean leaveWaitlist(String studentCode, String groupKey) {
        Student student = require(students.get(studentCode), "estudiante " + studentCode);
        CourseGroup group = require(groups.get(groupKey), "grupo " + groupKey);
        waitingGroups.get(studentCode).remove(group);
        return group.leaveWaitlist(student);
    }

    public Student cancel(String studentCode, String groupKey) {
        Student student = require(students.get(studentCode), "estudiante " + studentCode);
        CourseGroup group = require(groups.get(groupKey), "grupo " + groupKey);
        if (!group.drop(student))
            throw new IllegalStateException(student.getName() + " no está inscrito en " + group);
        enrolledGroups.get(studentCode).remove(group);
        if (phase == Phase.DROP_ONLY) return null;
        return reassignSeat(group);
    }

    public LinkedList<CourseGroup> suggestAlternatives(String studentCode, String groupKey) {
        Student student = require(students.get(studentCode), "estudiante " + studentCode);
        CourseGroup group = require(groups.get(groupKey), "grupo " + groupKey);
        LinkedList<CourseGroup> suggestions = new LinkedList<>();
        for (CourseGroup other : groupsByCourse.get(group.getCourseCode()))
            if (other != group && other.hasSeat() && !clashesWithSchedule(student, other))
                suggestions.addLast(other);
        return suggestions;
    }

    public LinkedList<CourseGroup> getEnrolledGroups(String studentCode) {
        return enrolledGroups.get(studentCode);
    }

    public LinkedList<CourseGroup> getWaitingGroups(String studentCode) {
        return waitingGroups.get(studentCode);
    }

    // Se salta a quien ya tenga la asignatura o un cruce de horario.
    private Student reassignSeat(CourseGroup group) {
        Student candidate = group.nextInWaitlist();
        while (candidate != null) {
            waitingGroups.get(candidate.getCode()).remove(group);
            if (enrolledInCourse(candidate, group.getCourseCode()) == null && !clashesWithSchedule(candidate, group)) {
                enrollAndClean(candidate, group);
                return candidate;
            }
            candidate = group.nextInWaitlist();
        }
        return null;
    }

    private void enrollAndClean(Student student, CourseGroup group) {
        group.enroll(student);
        enrolledGroups.get(student.getCode()).addLast(group);
        LinkedList<CourseGroup> waiting = waitingGroups.get(student.getCode());
        LinkedList<CourseGroup> sameCourse = new LinkedList<>();
        for (CourseGroup other : waiting)
            if (other.getCourseCode().equals(group.getCourseCode())) sameCourse.addLast(other);
        for (CourseGroup other : sameCourse) {
            other.leaveWaitlist(student);
            waiting.remove(other);
        }
    }

    private CourseGroup enrolledInCourse(Student student, String courseCode) {
        for (CourseGroup enrolled : enrolledGroups.get(student.getCode()))
            if (enrolled.getCourseCode().equals(courseCode)) return enrolled;
        return null;
    }

    private boolean clashesWithSchedule(Student student, CourseGroup group) {
        for (CourseGroup enrolled : enrolledGroups.get(student.getCode()))
            if (enrolled.clashesWith(group)) return true;
        return false;
    }

    private static <T> T require(T value, String description) {
        if (value == null) throw new IllegalArgumentException("No existe el " + description);
        return value;
    }
}
