public class RegistrationSystemTest
{
    public static void run() {
        priorityDecidesReassignment();
        studentLeavesOtherWaitlists();
        scheduleClashIsRejected();
        skipsCandidateWithClash();
        dropOnlyPhase();
        invalidInputs();
        suggestsAlternatives();
        reportsTopDemand();
    }

    private static RegistrationSystem system(CourseGroup... groups) {
        RegistrationSystem system = new RegistrationSystem();
        for (CourseGroup g : groups) system.addGroup(g);
        return system;
    }

    private static CourseGroup group(String code, String number, int capacity, String day, int start, int end) {
        CourseGroup g = new CourseGroup(code, "Asignatura " + code, number, capacity);
        g.addTimeSlot(new TimeSlot(day, start, end));
        return g;
    }

    private static Student student(String code, double progress, double ipa) {
        return new Student(code, "E" + code, false, 4.0, false, progress, ipa);
    }

    private static void priorityDecidesReassignment() {
        CourseGroup g = group("100", "Gr-1", 1, "LUN", 7, 9);
        RegistrationSystem system = system(g);
        Student first = student("1", 50, 5.0);
        Student low = student("2", 50, 6.0);
        Student high = student("3", 90, 0.0);
        system.addStudent(first);
        system.addStudent(low);
        system.addStudent(high);
        system.request("1", g.getKey(), true);
        TestRunner.check(system.request("2", g.getKey(), true) == RequestResult.WAITLISTED, "sin cupo y con aceptación entra a la lista de espera");
        system.request("3", g.getKey(), true);
        TestRunner.check(system.request("1", g.getKey(), true) == RequestResult.ALREADY_IN_COURSE, "no se puede inscribir dos veces la misma asignatura");
        Student promoted = system.cancel("1", g.getKey());
        TestRunner.check(promoted == high, "el cupo liberado va al de mayor prioridad aunque llegó después");
        TestRunner.check(g.getWaitlist().size() == 1 && g.isWaiting(low), "el otro estudiante sigue en espera");
    }

    private static void studentLeavesOtherWaitlists() {
        CourseGroup a = group("200", "Gr-1", 0, "LUN", 7, 9);
        CourseGroup b = group("200", "Gr-2", 1, "MAR", 7, 9);
        RegistrationSystem system = system(a, b);
        Student s = student("10", 50, 5.0);
        system.addStudent(s);
        system.request("10", a.getKey(), true);
        TestRunner.check(a.isWaiting(s), "queda en espera en el grupo 1");
        system.request("10", b.getKey(), true);
        TestRunner.check(b.isEnrolled(s) && !a.isWaiting(s), "al inscribir el grupo 2 sale de la espera del grupo 1");
        TestRunner.check(system.getWaitingGroups("10").isEmpty(), "su registro de esperas queda vacío");
    }

    private static void scheduleClashIsRejected() {
        CourseGroup a = group("300", "Gr-1", 5, "LUN", 7, 9);
        CourseGroup b = group("301", "Gr-1", 5, "LUN", 8, 10);
        RegistrationSystem system = system(a, b);
        system.addStudent(student("20", 50, 5.0));
        system.request("20", a.getKey(), true);
        TestRunner.check(system.request("20", b.getKey(), true) == RequestResult.SCHEDULE_CLASH, "se rechaza un grupo que se cruza con el horario");
        TestRunner.check(b.getEnrolled().isEmpty(), "el grupo con cruce no lo inscribe");
    }

    private static void skipsCandidateWithClash() {
        CourseGroup full = group("400", "Gr-1", 1, "MIE", 7, 9);
        CourseGroup other = group("401", "Gr-1", 5, "MIE", 8, 10);
        RegistrationSystem system = system(full, other);
        Student holder = student("30", 50, 1.0);
        Student top = student("31", 95, 0.0);
        Student second = student("32", 50, 4.0);
        system.addStudent(holder);
        system.addStudent(top);
        system.addStudent(second);
        system.request("30", full.getKey(), true);
        system.request("31", full.getKey(), true);
        system.request("32", full.getKey(), true);
        system.request("31", other.getKey(), true);
        Student promoted = system.cancel("30", full.getKey());
        TestRunner.check(promoted == second, "si el primero de la lista ya tiene cruce, el cupo pasa al siguiente");
        TestRunner.check(!full.isWaiting(top), "el candidato con cruce sale de la lista");
    }

    private static void dropOnlyPhase() {
        CourseGroup g = group("500", "Gr-1", 1, "JUE", 7, 9);
        RegistrationSystem system = system(g);
        system.addStudent(student("40", 50, 5.0));
        system.addStudent(student("41", 50, 6.0));
        system.request("40", g.getKey(), true);
        system.request("41", g.getKey(), true);
        system.setPhase(Phase.DROP_ONLY);
        TestRunner.check(system.cancel("40", g.getKey()) == null, "en solo cancelaciones el cupo no se reasigna");
        TestRunner.check(g.getAvailableSeats() == 1, "el cupo queda libre");
        TestRunner.check(system.request("41", g.getKey(), true) == RequestResult.PHASE_CLOSED, "en solo cancelaciones no se puede inscribir");
    }

    private static void invalidInputs() {
        CourseGroup g = group("600", "Gr-1", 1, "VIE", 7, 9);
        RegistrationSystem system = system(g);
        system.addStudent(student("50", 50, 5.0));
        TestRunner.checkThrows(() -> system.request("999", g.getKey(), true), "estudiante inexistente lanza excepción");
        TestRunner.checkThrows(() -> system.request("50", "no-existe", true), "grupo inexistente lanza excepción");
        TestRunner.checkThrows(() -> system.cancel("50", g.getKey()), "cancelar sin estar inscrito lanza excepción");
        TestRunner.checkThrows(() -> new CourseGroup("1", "x", "Gr-1", -1), "cupo negativo lanza excepción");
    }

    private static void suggestsAlternatives() {
        CourseGroup full = group("700", "Gr-1", 0, "LUN", 7, 9);
        CourseGroup clash = group("700", "Gr-2", 5, "MAR", 7, 9);
        CourseGroup noSeat = group("700", "Gr-3", 0, "MIE", 7, 9);
        CourseGroup good = group("700", "Gr-4", 5, "JUE", 7, 9);
        CourseGroup otherCourse = group("701", "Gr-1", 5, "MAR", 8, 10);
        RegistrationSystem system = system(full, clash, noSeat, good, otherCourse);
        system.addStudent(student("60", 50, 5.0));
        system.request("60", otherCourse.getKey(), true);
        LinkedList<CourseGroup> suggestions = system.suggestAlternatives("60", full.getKey());
        TestRunner.check(suggestions.size() == 1 && suggestions.contains(good), "sugiere solo grupos de la asignatura con cupo y sin cruce");
        TestRunner.check(system.suggestAlternatives("60", good.getKey()).isEmpty(), "sin alternativas válidas devuelve una lista vacía");
    }

    private static void reportsTopDemand() {
        CourseGroup a = group("800", "Gr-1", 0, "LUN", 7, 9);
        CourseGroup b = group("801", "Gr-1", 0, "MAR", 7, 9);
        CourseGroup c = group("802", "Gr-1", 0, "MIE", 7, 9);
        CourseGroup d = group("803", "Gr-1", 5, "JUE", 7, 9);
        RegistrationSystem system = system(a, b, c, d);
        for (int i = 0; i < 6; i++) system.addStudent(student("9" + i, 50, i));
        for (int i = 0; i < 3; i++) system.request("9" + i, b.getKey(), true);
        for (int i = 0; i < 5; i++) system.request("9" + i, c.getKey(), true);
        system.request("90", a.getKey(), true);
        system.request("95", d.getKey(), true);
        LinkedList<CourseGroup> top = system.topDemand(2);
        TestRunner.check(top.toString().equals("[" + c + ", " + b + "]"), "top-2 de demanda en orden: " + top);
        TestRunner.check(system.topDemand(10).size() == 3, "con k mayor que los grupos con espera devuelve solo esos grupos");
        TestRunner.checkThrows(() -> system.topDemand(0), "k = 0 lanza excepción");
    }
}
