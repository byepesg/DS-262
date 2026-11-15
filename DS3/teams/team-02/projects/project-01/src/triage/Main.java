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
                case "1": {
                    System.out.print("ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Nombre: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Urgencia (1 = mas urgente, 5 = menos urgente): ");
                    String urgencyText = scanner.nextLine().trim();
                    try {
                        int urgency = Integer.parseInt(urgencyText);
                        system.registerRequest(id, name, urgency);
                        System.out.println("Solicitud registrada.");
                    } catch (NumberFormatException e) {
                        System.out.println("Error: la urgencia debe ser un numero entre 1 y 5.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "2": {
                    Ticket served = system.serveNext();
                    if (served != null) {
                        System.out.println("Atendiendo: " + served);
                    } else {
                        System.out.println("No hay solicitudes pendientes.");
                    }
                    break;
                }
                case "3": {
                    System.out.print("ID a buscar: ");
                    Ticket found = system.findById(scanner.nextLine().trim());
                    if (found != null) {
                        System.out.println("Encontrado: " + found);
                    } else {
                        System.out.println("No existe una solicitud pendiente con ese ID.");
                    }
                    break;
                }
                case "4": {
                    System.out.print("ID a cancelar: ");
                    boolean cancelled = system.cancelRequest(scanner.nextLine().trim());
                    if (cancelled) {
                        System.out.println("Solicitud cancelada.");
                    } else {
                        System.out.println("No existe una solicitud pendiente con ese ID.");
                    }
                    break;
                }
                case "5": {
                    List<Ticket> history = system.getHistory();
                    if (history.isEmpty()) {
                        System.out.println("El historial esta vacio.");
                    } else {
                        System.out.println("Historial (mas reciente primero):");
                        for (Ticket t : history) {
                            System.out.println("  " + t);
                        }
                    }
                    break;
                }
                case "6": {
                    System.out.println("Solicitudes pendientes: " + system.pendingCount());
                    break;
                }
                case "0": {
                    exit = true;
                    break;
                }
                default: {
                    System.out.println("Opcion no valida.");
                    break;
                }
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
        System.out.println("5) Ver historial de atenciones");
        System.out.println("6) Ver cantidad de pendientes");
        System.out.println("0) Salir");
        System.out.print("Opcion: ");
    }
}
