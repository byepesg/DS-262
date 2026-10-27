package triage;

/**
 * Pruebas manuales de las operaciones principales y de algunos casos borde.
 */
public class BasicTests {

    public static void main(String[] args) {
        System.out.println("--- Escenario principal ---");
        mainScenario();
        System.out.println();
        System.out.println("--- Casos borde ---");
        edgeCases();
    }

    private static void mainScenario() {
        TriageSystem system = new TriageSystem();

        system.registerRequest("A1", "Juan Perez", 3);
        system.registerRequest("A2", "Ana Gomez", 1);
        system.registerRequest("A3", "Luis Ruiz", 2);
        system.registerRequest("A4", "Eva Leon", 1);
        System.out.println("Pendientes tras registrar 4 solicitudes (esperado 4): "
                + system.pendingCount());

        Ticket first = system.serveNext();
        System.out.println("1ra atencion (esperado: A2, urgencia 1): " + first);

        Ticket second = system.serveNext();
        System.out.println("2da atencion (esperado: A4, urgencia 1, llego despues de A2): "
                + second);

        boolean cancelled = system.cancelRequest("A3");
        System.out.println("Cancelar A3 (esperado true): " + cancelled);
        System.out.println("Buscar A3 tras cancelar (esperado null): " + system.findById("A3"));
        System.out.println("Pendientes tras cancelar A3 (esperado 1): "
                + system.pendingCount());

        Ticket third = system.serveNext();
        System.out.println("3ra atencion (esperado: A1; A3 ya no esta): " + third);

        Ticket fourth = system.serveNext();
        System.out.println("4to intento (esperado: null, no quedan pendientes): " + fourth);

        System.out.println("Historial (esperado: A1, A4, A2):");
        for (Ticket t : system.getHistory()) {
            System.out.println("  " + t);
        }
    }

    private static void edgeCases() {
        TriageSystem system = new TriageSystem();

        try {
            system.registerRequest("B1", "Maria Diaz", 9);
            System.out.println("Urgencia 9 (esperado: error): se acepto");
        } catch (IllegalArgumentException e) {
            System.out.println("Urgencia 9 (esperado: error): " + e.getMessage());
        }

        system.registerRequest("C1", "Pedro Rojas", 2);
        try {
            system.registerRequest("C1", "Sofia Mora", 4);
            System.out.println("Id repetido pendiente (esperado: error): se acepto");
        } catch (IllegalArgumentException e) {
            System.out.println("Id repetido pendiente (esperado: error): " + e.getMessage());
        }

        System.out.println("Cancelar id inexistente (esperado false): "
                + system.cancelRequest("ZZ"));

        system.serveNext();
        system.registerRequest("C1", "Sofia Mora", 4);
        System.out.println("Reusar id ya atendido (esperado: Sofia Mora): "
                + system.findById("C1"));

        System.out.println("Atender dos veces (esperado: C1 y luego null): "
                + system.serveNext().getId() + ", " + system.serveNext());
    }
}
