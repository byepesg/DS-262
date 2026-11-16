import java.util.Scanner;

public class Menu
{
    private final RegistrationSystem system;
    private final Scanner in;

    public Menu(RegistrationSystem system, Scanner in) {
        this.system = system;
        this.in = in;
    }

    public static void main(String[] args) {
        new Menu(SampleData.create(), new Scanner(System.in)).run();
    }

    public void run() {
        System.out.println("SISO-UNAL — estudiantes de ejemplo: 2024101 a 2024105");
        while (true) {
            printOptions();
            String option = ask("Opción");
            if (option == null || option.equals("0")) return;
            try {
                handle(option);
            } catch (NumberFormatException e) {
                System.out.println("Error: debe escribir un número.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printOptions() {
        System.out.println();
        System.out.println("Fase actual: " + describe(system.getPhase()));
        System.out.println("1. Ver grupos           2. Ver un grupo        3. Buscar estudiante");
        System.out.println("4. Pedir un grupo       5. Cancelar un grupo   6. Salir de una lista de espera");
        System.out.println("7. Sugerir otro grupo   8. Grupos con más demanda");
        System.out.println("9. Cambiar de fase      0. Terminar");
    }

    private void handle(String option) {
        switch (option) {
            case "1":
                for (CourseGroup group : system.getAllGroups()) printGroup(group);
                break;
            case "2":
                printGroup(group(ask("Clave del grupo (ej. 1000004-Gr-1)")));
                break;
            case "3":
                showStudent(ask("Código del estudiante"));
                break;
            case "4":
                String code = ask("Código del estudiante");
                String key = ask("Clave del grupo");
                boolean accepts = ask("¿Acepta la asignación automática si no hay cupo? (s/n)").equalsIgnoreCase("s");
                System.out.println(describe(system.request(code, key, accepts)));
                break;
            case "5":
                Student promoted = system.cancel(ask("Código del estudiante"), ask("Clave del grupo"));
                System.out.println(promoted == null ? "Cancelado. El cupo queda libre." : "Cancelado. El cupo pasó a " + promoted + ".");
                break;
            case "6":
                boolean left = system.leaveWaitlist(ask("Código del estudiante"), ask("Clave del grupo"));
                System.out.println(left ? "Salió de la lista de espera." : "No estaba en esa lista de espera.");
                break;
            case "7":
                LinkedList<CourseGroup> options = system.suggestAlternatives(ask("Código del estudiante"), ask("Clave del grupo lleno"));
                System.out.println(options.isEmpty() ? "No hay otro grupo con cupo y sin cruce." : "Puede pedir: " + keys(options));
                break;
            case "8":
                LinkedList<CourseGroup> top = system.topDemand(Integer.parseInt(ask("¿Cuántos grupos?")));
                if (top.isEmpty()) System.out.println("Ningún grupo tiene lista de espera.");
                for (CourseGroup group : top)
                    System.out.println(group.getKey() + " " + group + ": " + group.getWaitlist().size() + " en espera");
                break;
            case "9":
                changePhase();
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private void changePhase() {
        System.out.println("1. Citas   2. Adiciones y cancelaciones   3. Solo cancelaciones");
        switch (ask("Fase")) {
            case "1": system.setPhase(Phase.APPOINTMENTS); break;
            case "2": system.setPhase(Phase.ADD_AND_DROP); break;
            case "3": system.setPhase(Phase.DROP_ONLY); break;
            default: System.out.println("Fase no válida.");
        }
    }

    private void showStudent(String code) {
        Student student = system.findStudent(code);
        if (student == null) throw new IllegalArgumentException("No existe el estudiante " + code);
        System.out.println(student + " | inscrito en: " + keys(system.getEnrolledGroups(code))
                + " | en espera en: " + keys(system.getWaitingGroups(code)));
    }

    private CourseGroup group(String key) {
        CourseGroup group = system.findGroup(key);
        if (group == null) throw new IllegalArgumentException("No existe el grupo " + key);
        return group;
    }

    private void printGroup(CourseGroup group) {
        System.out.println(group.getKey() + " " + group + " " + group.getSchedule() + " | Inscritos: " + group.getEnrolled()
                + " | Libres: " + group.getAvailableSeats() + " | Espera: " + group.getWaitlist());
    }

    private String keys(LinkedList<CourseGroup> groups) {
        StringBuilder sb = new StringBuilder("[");
        for (CourseGroup group : groups) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(group.getKey());
        }
        return sb.append("]").toString();
    }

    private String ask(String prompt) {
        System.out.print(prompt + ": ");
        if (!in.hasNextLine()) return null;
        return in.nextLine().trim();
    }

    private static String describe(Phase phase) {
        switch (phase) {
            case APPOINTMENTS: return "citas";
            case ADD_AND_DROP: return "adiciones y cancelaciones";
            default:           return "solo cancelaciones";
        }
    }

    private static String describe(RequestResult result) {
        switch (result) {
            case ENROLLED:          return "Inscrito.";
            case WAITLISTED:        return "Sin cupo: entra a la lista de espera.";
            case REJECTED:          return "Sin cupo y sin aceptar la asignación automática: no entra a la lista.";
            case SCHEDULE_CLASH:    return "Rechazado: se cruza con su horario.";
            case ALREADY_IN_COURSE: return "Rechazado: ya tiene un grupo de esa asignatura.";
            default:                return "Rechazado: la fase actual no permite inscribir.";
        }
    }
}
