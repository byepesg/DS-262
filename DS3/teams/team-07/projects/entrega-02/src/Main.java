public class Main
{
    public static void main(String[] args) {
        RegistrationSystem system = new RegistrationSystem();

        CourseGroup calculo1 = group("1000004", "Cálculo Diferencial", "Gr-1", 2, "LUN", 7, 9);
        CourseGroup calculo2 = group("1000004", "Cálculo Diferencial", "Gr-2", 1, "MAR", 9, 11);
        CourseGroup calculo3 = group("1000004", "Cálculo Diferencial", "Gr-3", 3, "JUE", 14, 16);
        CourseGroup algebra = group("1000003", "Álgebra Lineal", "Gr-1", 3, "LUN", 8, 10);
        system.addGroup(calculo1);
        system.addGroup(calculo2);
        system.addGroup(calculo3);
        system.addGroup(algebra);

        Student ana    = new Student("2024101", "Ana",    true,  3.8, false, 40.0, 0.0);
        Student carla  = new Student("2024102", "Carla",  false, 4.1, false, 92.0, 0.0);
        Student daniel = new Student("2024103", "Daniel", false, 3.9, false, 85.0, 0.0);
        Student elena  = new Student("2024104", "Elena",  false, 4.3, false, 55.0, 7.5);
        Student felipe = new Student("2024105", "Felipe", false, 4.0, false, 35.0, 6.0);
        for (Student s : new Student[] {ana, carla, daniel, elena, felipe}) system.addStudent(s);

        PriorityQueueADT<Student> order = new BinaryHeapPriorityQueue<>();
        order.insert(felipe);
        order.insert(elena);
        order.insert(carla);
        order.insert(ana);
        System.out.println("Orden de prioridad para las citas: " + order);

        LinkedQueue<Student> appointment = new LinkedQueue<>();
        while (!order.isEmpty()) appointment.enqueue(order.removeHighest());

        title("1. Citas: todos piden " + calculo1 + " (cupo " + calculo1.getCapacity() + ")");
        while (!appointment.isEmpty()) {
            Student current = appointment.dequeue();
            boolean accepts = current != felipe;
            show(current, system.request(current.getCode(), calculo1.getKey(), accepts));
        }
        printState(calculo1);

        title("2. Adiciones: Daniel pide " + calculo1 + " y " + calculo2);
        system.setPhase(Phase.ADD_AND_DROP);
        show(daniel, system.request(daniel.getCode(), calculo1.getKey(), true));
        show(elena, system.request(elena.getCode(), calculo2.getKey(), true));
        show(daniel, system.request(daniel.getCode(), calculo2.getKey(), true));
        printState(calculo1);
        printState(calculo2);
        System.out.println("Grupos con más demanda: " + system.topDemand(2));
        System.out.println("Otros grupos para Felipe en lugar de " + calculo1 + ": "
                + system.suggestAlternatives(felipe.getCode(), calculo1.getKey()));

        title("3. Ana pide " + algebra + ", que se cruza con " + calculo1);
        show(ana, system.request(ana.getCode(), algebra.getKey(), true));

        title("4. Carla cancela " + calculo1);
        Student promoted = system.cancel(carla.getCode(), calculo1.getKey());
        System.out.println("El cupo se asignó automáticamente a: " + promoted);
        System.out.println("Listas de espera de Daniel después de recibir cupo: " + system.getWaitingGroups(daniel.getCode()));
        printState(calculo1);
        printState(calculo2);

        title("5. Fase de solo cancelaciones: Elena cancela " + calculo2);
        system.setPhase(Phase.DROP_ONLY);
        system.cancel(elena.getCode(), calculo2.getKey());
        System.out.println("El cupo queda libre: en esta fase no se reasigna");
        show(felipe, system.request(felipe.getCode(), calculo2.getKey(), true));
        printState(calculo2);

        title("6. Búsqueda por código y carrito con deshacer");
        System.out.println("Estudiante 2024105: " + system.findStudent("2024105"));
        System.out.println("Grupo 1000004-Gr-2: " + system.findGroup("1000004-Gr-2"));
        LinkedStack<CourseGroup> cart = new LinkedStack<>();
        cart.push(calculo1);
        cart.push(algebra);
        System.out.println("Deshacer -> se quita: " + cart.pop() + "; queda: " + cart.peek());
    }

    private static CourseGroup group(String code, String name, String number, int capacity,
                                     String day, int start, int end) {
        CourseGroup group = new CourseGroup(code, name, number, capacity);
        group.addTimeSlot(new TimeSlot(day, start, end));
        return group;
    }

    private static void title(String text) {
        System.out.println("\n--- " + text + " ---");
    }

    private static void show(Student student, RequestResult result) {
        System.out.println(student + " -> " + describe(result));
    }

    private static String describe(RequestResult result) {
        switch (result) {
            case ENROLLED:          return "inscrito";
            case WAITLISTED:        return "sin cupo; aceptó la asignación automática, entra a lista de espera";
            case REJECTED:          return "sin cupo; no aceptó la asignación automática, no entra a la lista";
            case SCHEDULE_CLASH:    return "rechazado: se cruza con su horario";
            case ALREADY_IN_COURSE: return "rechazado: ya tiene un grupo de esa asignatura";
            default:                return "rechazado: la fase actual no permite inscribir";
        }
    }

    private static void printState(CourseGroup group) {
        System.out.println("   " + group + " " + group.getSchedule() + " | Inscritos: " + group.getEnrolled()
                + " | Libres: " + group.getAvailableSeats() + " | Espera: " + group.getWaitlist());
    }
}
