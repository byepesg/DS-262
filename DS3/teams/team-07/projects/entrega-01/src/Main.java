/**
 * Demostración guiada de la Entrega 1: citas en orden de prioridad, lista de
 * espera por prioridad y reasignación automática de un cupo liberado.
 */
public class Main {
    public static void main(String[] args) {
        CourseGroup calculo = new CourseGroup("1000004", "Cálculo Diferencial", "Gr-1", 2);
        CourseGroup calculo2 = new CourseGroup("1000004", "Cálculo Diferencial", "Gr-2", 3);

        // Student(código, nombre, discapacidad, PAPA, vulnerabilidad, avance %, IPA oficial)
        // El IPA es un dato de entrada: no se calcula.
        Student ana    = new Student("2024101", "Ana",    true,  3.8, false, 40.0, 0.0);
        Student carla  = new Student("2024102", "Carla",  false, 4.1, false, 92.0, 0.0);
        Student daniel = new Student("2024103", "Daniel", false, 3.9, false, 85.0, 0.0);
        Student elena  = new Student("2024104", "Elena",  false, 4.3, false, 55.0, 7.5);
        Student felipe = new Student("2024105", "Felipe", false, 4.0, false, 35.0, 6.0);

        // 1. Ordenar por prioridad oficial usando la cola de prioridad y armar
        //    la cola de la cita (FIFO) en ese orden. Daniel no alcanza a
        //    inscribir en su cita; llega en el periodo de adiciones.
        PriorityQueueADT<Student> orden = new SortedLinkedPriorityQueue<>();
        orden.insert(felipe);
        orden.insert(elena);
        orden.insert(carla);
        orden.insert(ana);
        System.out.println("Orden de prioridad para las citas: " + orden);

        LinkedQueue<Student> cita = new LinkedQueue<>();
        while (!orden.isEmpty())
            cita.enqueue(orden.removeHighest());

        // Cada estudiante decide si acepta que el cupo se le asigne
        // automáticamente si alguien cancela. Felipe no lo acepta.
        System.out.println("\n--- 1. Citas: todos piden " + calculo + " (cupo " + calculo.getCapacity() + ") ---");
        while (!cita.isEmpty()) {
            Student current = cita.dequeue();
            boolean accepts = current != felipe;
            RequestResult result = calculo.request(current, accepts);
            System.out.println(current + " -> " + describe(result));
        }
        printState(calculo);

        // 2. Adiciones: Daniel llega después, pero su prioridad (grupo 3) lo
        //    pone por delante de Elena (grupo 4).
        System.out.println("\n--- 2. Adiciones: Daniel pide " + calculo + " ---");
        System.out.println(daniel + " -> " + describe(calculo.request(daniel, true)));
        printState(calculo);

        // 3. Cancelación: el cupo pasa solo al primero de la lista de espera.
        System.out.println("\n--- 3. Carla cancela ---");
        Student promoted = calculo.cancel(carla);
        System.out.println("El cupo liberado se asignó automáticamente a: " + promoted);
        printState(calculo);

        // 4. Carrito de selección con deshacer (pila LIFO).
        System.out.println("\n--- 4. Carrito de Felipe con deshacer ---");
        LinkedStack<CourseGroup> carrito = new LinkedStack<>();
        carrito.push(calculo);
        carrito.push(calculo2);
        System.out.println("Último grupo añadido: " + carrito.peek());
        System.out.println("Deshacer -> se quita: " + carrito.pop());
        System.out.println("Queda en el tope del carrito: " + carrito.peek());
    }

    private static String describe(RequestResult result) {
        switch (result) {
            case ENROLLED:   return "inscrito";
            case WAITLISTED: return "sin cupo; aceptó la asignación automática, entra a lista de espera";
            default:         return "sin cupo; no aceptó la asignación automática, no entra a la lista";
        }
    }

    private static void printState(CourseGroup group) {
        System.out.println("   Inscritos en " + group + ": " + group.getEnrolled()
                + " | Cupos libres: " + group.getAvailableSeats());
        System.out.println("   Lista de espera: " + group.getWaitlist());
    }
}
