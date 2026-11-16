package triage;

import java.util.List;
import java.util.Scanner;

/**
 * Menu de consola del sistema de triage.
 */
public class Main {

    public static void main(String[] args) {
        TriageSystem system = new TriageSystem();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("=== Sistema de Priorizacion de Atencion ===");
        while (!exit) {
            showMenu();
            if (!scanner.hasNextLine()) {
                break;
            }
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    register(system, scanner);
                    break;
                case "2":
                    serve(system);
                    break;
                case "3":
                    find(system, scanner);
                    break;
                case "4":
                    cancel(system, scanner);
                    break;
                case "5":
                    undo(system);
                    break;
                case "6":
                    printList("Historial (mas reciente primero):", system.getHistory(),
                            "El historial esta vacio.");
                    break;
                case "7":
                    showPending(system);
                    break;
                case "0":
                    exit = true;
                    break;
                default:
                    System.out.println("Opcion no valida.");
                    break;
            }
        }
        scanner.close();
        System.out.println("Fin del programa.");
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("1) Registrar solicitud");
        System.out.println("2) Atender siguiente");
        System.out.println("3) Buscar por ID");
        System.out.println("4) Cancelar solicitud");
        System.out.println("5) Deshacer ultima atencion");
        System.out.println("6) Ver historial de atenciones");
        System.out.println("7) Ver pendientes en orden de atencion");
        System.out.println("0) Salir");
        System.out.print("Opcion: ");
    }

    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }

    private static void register(TriageSystem system, Scanner scanner) {
        String id = ask(scanner, "ID: ");
        String name = ask(scanner, "Nombre: ");
        String urgencyText = ask(scanner, "Urgencia (1 = mas urgente, 5 = menos urgente): ");
        try {
            int urgency = Integer.parseInt(urgencyText);
            Ticket t = system.registerRequest(id, name, urgency);
            System.out.println("Solicitud registrada: " + t);
        } catch (NumberFormatException e) {
            System.out.println("Error: la urgencia debe ser un numero entre 1 y 5.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void serve(TriageSystem system) {
        Ticket served = system.serveNext();
        if (served != null) {
            System.out.println("Atendiendo: " + served);
        } else {
            System.out.println("No hay solicitudes pendientes.");
        }
    }

    private static void find(TriageSystem system, Scanner scanner) {
        Ticket found = system.findById(ask(scanner, "ID a buscar: "));
        if (found != null) {
            System.out.println("Encontrado: " + found);
        } else {
            System.out.println("No existe una solicitud pendiente con ese ID.");
        }
    }

    private static void cancel(TriageSystem system, Scanner scanner) {
        if (system.cancelRequest(ask(scanner, "ID a cancelar: "))) {
            System.out.println("Solicitud cancelada.");
        } else {
            System.out.println("No existe una solicitud pendiente con ese ID.");
        }
    }

    private static void undo(TriageSystem system) {
        try {
            Ticket restored = system.undoLastService();
            if (restored != null) {
                System.out.println("Atencion deshecha; vuelve a pendientes: " + restored);
            } else {
                System.out.println("No hay atenciones para deshacer.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showPending(TriageSystem system) {
        System.out.print("Pendientes: " + system.pendingCount() + " (por urgencia:");
        for (int u = TriageSystem.MOST_URGENT; u <= TriageSystem.LEAST_URGENT; u++) {
            System.out.print(" " + u + "=" + system.pendingCountByUrgency(u));
        }
        System.out.println(")");
        printList("Orden de atencion:", system.getPendingInOrder(), "No hay solicitudes pendientes.");
    }

    private static void printList(String title, List<Ticket> tickets, String emptyMessage) {
        if (tickets.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        System.out.println(title);
        int position = 1;
        for (Ticket t : tickets) {
            System.out.println("  " + position++ + ". " + t);
        }
    }
}
