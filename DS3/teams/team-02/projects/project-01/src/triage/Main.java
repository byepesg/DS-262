package triage;

import java.util.List;
import java.util.Scanner;

/**
 * Menu de consola del Sistema de Priorizacion de Atencion.
 * Trabaja sobre la sede seleccionada dentro de la red de sedes.
 */
public class Main {

    private static Scanner scanner;
    private static SiteNetwork network;
    private static DecisionTree protocol;
    private static int currentSite;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        System.out.println("=== Sistema de Priorizacion de Atencion ===");
        TriageSystem.Policy policy = choosePolicy();
        network = SiteNetwork.defaultNetwork(policy);
        protocol = DecisionTree.defaultProtocol();
        currentSite = 0;

        boolean exit = false;
        while (!exit) {
            showMenu();
            String option = readLine();
            if (option == null) {
                break;
            }
            try {
                exit = handle(option.trim());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Fin del programa.");
    }

    private static TriageSystem.Policy choosePolicy() {
        System.out.println("Politica de atencion:");
        System.out.println("  1) Por niveles: siempre primero la urgencia mayor");
        System.out.println("  2) Por plazo maximo: la que vence primero (evita esperas infinitas)");
        System.out.print("Opcion [1]: ");
        String line = readLine();
        return "2".equals(line == null ? "" : line.trim())
                ? TriageSystem.Policy.DEADLINE : TriageSystem.Policy.LEVELS;
    }

    private static void showMenu() {
        SiteNetwork.Site site = network.site(currentSite);
        System.out.println();
        System.out.printf("--- Sede %s | minuto %d | pendientes %d/%d | %s ---%n",
                site.name(), system().currentMinute(), system().pendingCount(),
                site.capacity(), system().policyName());
        System.out.println(" 1) Registrar solicitud (urgencia manual)");
        System.out.println(" 2) Registrar con protocolo de triage (arbol de decision)");
        System.out.println(" 3) Atender siguiente");
        System.out.println(" 4) Buscar por ID");
        System.out.println(" 5) Cancelar solicitud");
        System.out.println(" 6) Deshacer ultima atencion");
        System.out.println(" 7) Ver historial de atenciones");
        System.out.println(" 8) Ver pendientes en orden de atencion");
        System.out.println(" 9) Ver pendientes ordenados por ID");
        System.out.println("10) Avanzar el reloj / ver vencidas");
        System.out.println("11) Ver protocolo de triage");
        System.out.println("12) Red de sedes: ver estado y cambiar de sede");
        System.out.println("13) Sede mas cercana con cupo (Dijkstra)");
        System.out.println("14) Ruta minima a otra sede (Dijkstra)");
        System.out.println("15) Sedes por numero de saltos (BFS)");
        System.out.println(" 0) Salir");
        System.out.print("Opcion: ");
    }

    private static boolean handle(String option) {
        switch (option) {
            case "1":
                register(false);
                return false;
            case "2":
                register(true);
                return false;
            case "3": {
                Ticket served = system().serveNext();
                System.out.println(served != null ? "Atendiendo: " + served
                        : "No hay solicitudes pendientes.");
                return false;
            }
            case "4": {
                System.out.print("ID a buscar: ");
                Ticket found = system().findById(readLine());
                System.out.println(found != null ? "Encontrado: " + found
                        : "No existe una solicitud pendiente con ese ID.");
                return false;
            }
            case "5": {
                System.out.print("ID a cancelar: ");
                System.out.println(system().cancelRequest(readLine()) ? "Solicitud cancelada."
                        : "No existe una solicitud pendiente con ese ID.");
                return false;
            }
            case "6": {
                Ticket restored = system().undoLastService();
                System.out.println(restored != null ? "Atencion deshecha, vuelve a pendientes: "
                        + restored : "No hay atenciones para deshacer.");
                return false;
            }
            case "7":
                printList("Historial (mas reciente primero):", system().getHistory(),
                        "El historial esta vacio.");
                return false;
            case "8":
                printList("Pendientes en orden de atencion:", system().pendingInAttentionOrder(),
                        "No hay solicitudes pendientes.");
                return false;
            case "9":
                printList("Pendientes por ID (altura del AVL: " + system().indexHeight() + "):",
                        system().pendingById(), "No hay solicitudes pendientes.");
                return false;
            case "10":
                advanceClock();
                return false;
            case "11":
                System.out.printf("Protocolo: %d nodos, %d hojas, altura %d%n",
                        protocol.countNodes(), protocol.countLeaves(), protocol.height());
                for (String line : protocol.protocolLines()) {
                    System.out.println("  " + line);
                }
                return false;
            case "12":
                showNetwork();
                return false;
            case "13":
                printRoute(network.nearestAvailable(currentSite),
                        "Ninguna sede alcanzable tiene cupo.");
                return false;
            case "14": {
                System.out.print("Sede destino: ");
                int target = network.indexOf(readLine());
                if (target < 0) {
                    System.out.println("No existe esa sede.");
                } else {
                    printRoute(network.shortestRoute(currentSite, target),
                            "No hay camino hasta esa sede.");
                }
                return false;
            }
            case "15": {
                int[] hops = network.bfsHops(currentSite);
                System.out.println("Saltos desde " + network.site(currentSite).name() + ":");
                for (int i = 0; i < network.siteCount(); i++) {
                    System.out.println("  " + network.site(i).name() + ": "
                            + (hops[i] < 0 ? "no alcanzable" : hops[i]));
                }
                return false;
            }
            case "0":
                return true;
            default:
                System.out.println("Opcion no valida.");
                return false;
        }
    }

    private static void register(boolean useProtocol) {
        System.out.print("ID: ");
        String id = readLine();
        System.out.print("Nombre: ");
        String name = readLine();
        int urgency;
        if (useProtocol) {
            urgency = protocol.classify(question -> {
                System.out.print(question + " (s/n): ");
                String answer = readLine();
                return answer != null && answer.trim().toLowerCase().startsWith("s");
            });
            System.out.println("Nivel asignado por el protocolo: " + urgency);
        } else {
            System.out.print("Urgencia (1 = mas urgente, 5 = menos urgente): ");
            try {
                urgency = Integer.parseInt(readLine().trim());
            } catch (NumberFormatException | NullPointerException e) {
                System.out.println("Error: la urgencia debe ser un numero entre 1 y 5.");
                return;
            }
        }
        Ticket t = system().registerRequest(id, name, urgency);
        System.out.println("Solicitud registrada: " + t);
        if (network.site(currentSite).isSaturated()) {
            System.out.println("Aviso: la sede alcanzo su capacidad.");
            printRoute(network.nearestAvailable(currentSite),
                    "Ninguna sede alcanzable tiene cupo para derivar.");
        }
    }

    private static void advanceClock() {
        System.out.print("Minutos a avanzar (0 para solo consultar): ");
        try {
            system().advanceTime(Long.parseLong(readLine().trim()));
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Error: escriba un numero entero.");
            return;
        }
        printList("Pendientes con el plazo vencido en el minuto " + system().currentMinute() + ":",
                system().overduePending(), "Ninguna solicitud tiene el plazo vencido.");
    }

    private static void showNetwork() {
        System.out.println("Sedes (" + network.siteCount() + ") y vias (" + network.roadCount() + "):");
        for (int i = 0; i < network.siteCount(); i++) {
            SiteNetwork.Site s = network.site(i);
            System.out.printf("  %s%s: %d/%d pendientes%s -> %s%n",
                    s.name(), i == currentSite ? " (actual)" : "",
                    s.system().pendingCount(), s.capacity(),
                    s.isSaturated() ? " SATURADA" : "", network.neighbors(i));
        }
        System.out.print("Nombre de la sede a la que desea cambiar (Enter para quedarse): ");
        String name = readLine();
        if (name != null && !name.isBlank()) {
            int i = network.indexOf(name);
            if (i < 0) {
                System.out.println("No existe esa sede.");
            } else {
                currentSite = i;
                System.out.println("Ahora trabaja en la sede " + network.site(i).name() + ".");
            }
        }
    }

    private static void printRoute(SiteNetwork.Route route, String ifNull) {
        if (route == null) {
            System.out.println(ifNull);
            return;
        }
        System.out.printf("Sede %s a %d min. Camino: %s%n",
                network.site(route.site()).name(), route.minutes(),
                String.join(" -> ", route.path()));
    }

    private static void printList(String title, List<Ticket> items, String ifEmpty) {
        if (items.isEmpty()) {
            System.out.println(ifEmpty);
            return;
        }
        System.out.println(title);
        for (Ticket t : items) {
            System.out.println("  " + t);
        }
    }

    private static TriageSystem system() {
        return network.site(currentSite).system();
    }

    private static String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}
