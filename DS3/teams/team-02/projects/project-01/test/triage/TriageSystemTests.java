package triage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

/**
 * Pruebas de integracion de la fachada TriageSystem: requerimientos RF1 a
 * RF7, casos borde, entradas invalidas y una prueba aleatoria contra un
 * modelo de referencia construido con la biblioteca estandar de Java.
 */
final class TriageSystemTests {

    private TriageSystemTests() {
    }

    static void runAll() {
        entrega1Scenario();
        validation();
        undo();
        randomizedAgainstModel();
    }

    private static List<String> ids(List<Ticket> tickets) {
        List<String> ids = new ArrayList<>();
        for (Ticket t : tickets) {
            ids.add(t.getId());
        }
        return ids;
    }

    // ------------------------------------------------------------------
    /** Escenario de BasicTests (Entrega 1), ahora con aserciones. */
    private static void entrega1Scenario() {
        String s = "TriageSystem (escenario Entrega 1)";

        Check.run(s, "prioridad, desempate, cancelacion e historial", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("A1", "Juan Perez", 3);
            sys.registerRequest("A2", "Ana Gomez", 1);
            sys.registerRequest("A3", "Luis Ruiz", 2);
            sys.registerRequest("A4", "Eva Leon", 1);
            Check.equal(4, sys.pendingCount(), "pendientes tras registrar");
            Check.equal("A2", sys.peekNext().getId(), "peek no atiende");
            Check.equal(4, sys.pendingCount(), "peek no cambia el tamano");
            Check.equal("A2", sys.serveNext().getId(), "1ra atencion: urgencia 1");
            Check.equal("A4", sys.serveNext().getId(), "2da atencion: desempate por llegada");
            Check.isTrue(sys.cancelRequest("A3"), "cancelar A3");
            Check.equal(null, sys.findById("A3"), "A3 ya no se encuentra");
            Check.equal(1, sys.pendingCount(), "pendientes tras cancelar");
            Check.equal("A1", sys.serveNext().getId(), "3ra atencion");
            Check.equal(null, sys.serveNext(), "sin pendientes");
            Check.equal(List.of("A1", "A4", "A2"), ids(sys.getHistory()), "historial");
            Check.isTrue(sys.checkInvariant(), "invariante cola-indice");
        });

        Check.run(s, "reutilizar el id de una solicitud ya atendida", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("C1", "Pedro Rojas", 2);
            sys.serveNext();
            sys.registerRequest("C1", "Sofia Mora", 4);
            Check.equal("Sofia Mora", sys.findById("C1").getName(), "nuevo C1");
        });

        Check.run(s, "reutilizar el id de una solicitud cancelada", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("C1", "Pedro Rojas", 2);
            sys.cancelRequest("C1");
            sys.registerRequest("C1", "Sofia Mora", 4);
            Check.equal(4, sys.findById("C1").getUrgency(), "nuevo C1");
            Check.equal(1, sys.pendingCount(), "un pendiente");
        });

        Check.run(s, "pendientes en orden de atencion y conteo por nivel", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("P1", "a", 5);
            sys.registerRequest("P2", "b", 2);
            sys.registerRequest("P3", "c", 2);
            sys.registerRequest("P4", "d", 1);
            Check.equal(List.of("P4", "P2", "P3", "P1"), ids(sys.getPendingInOrder()), "orden");
            Check.equal(2, sys.pendingCountByUrgency(2), "nivel 2");
            Check.equal(0, sys.pendingCountByUrgency(3), "nivel 3");
        });
    }

    // ------------------------------------------------------------------
    private static void validation() {
        String s = "TriageSystem (entradas invalidas)";

        Check.run(s, "urgencia fuera de rango", () -> {
            TriageSystem sys = new TriageSystem();
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest("B1", "Maria", 9), "urgencia 9");
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest("B1", "Maria", 0), "urgencia 0");
            Check.equal(0, sys.pendingCount(), "no se registro");
        });

        Check.run(s, "id o nombre vacios o en blanco", () -> {
            TriageSystem sys = new TriageSystem();
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest("", "Maria", 2), "id vacio");
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest("   ", "Maria", 2), "id en blanco");
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest(null, "Maria", 2), "id null");
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest("B1", "  ", 2), "nombre en blanco");
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest("B1", null, 2), "nombre null");
            Check.equal(0, sys.pendingCount(), "no se registro nada");
        });

        Check.run(s, "id repetido entre pendientes (con espacios alrededor)", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("C1", "Pedro", 2);
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest("C1", "Sofia", 4), "repetido");
            Check.throwsException(IllegalArgumentException.class,
                    () -> sys.registerRequest(" C1 ", "Sofia", 4), "repetido con espacios");
            Check.equal(1, sys.pendingCount(), "solo el primero");
        });

        Check.run(s, "buscar o cancelar ids inexistentes, vacios o null", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("C1", "Pedro", 2);
            Check.isTrue(!sys.cancelRequest("ZZ"), "cancelar inexistente");
            Check.isTrue(!sys.cancelRequest(""), "cancelar vacio");
            Check.isTrue(!sys.cancelRequest(null), "cancelar null");
            Check.equal(null, sys.findById("ZZ"), "buscar inexistente");
            Check.equal(null, sys.findById(null), "buscar null");
            Check.isTrue(sys.cancelRequest("C1"), "cancelar existente");
            Check.isTrue(!sys.cancelRequest("C1"), "cancelar dos veces");
        });

        Check.run(s, "atender o mirar con la cola vacia", () -> {
            TriageSystem sys = new TriageSystem();
            Check.equal(null, sys.serveNext(), "serveNext");
            Check.equal(null, sys.peekNext(), "peekNext");
            Check.equal(0, sys.getHistory().size(), "historial vacio");
        });
    }

    // ------------------------------------------------------------------
    private static void undo() {
        String s = "TriageSystem (deshacer, RF5)";

        Check.run(s, "historial vacio: no hay nada que deshacer", () -> {
            TriageSystem sys = new TriageSystem();
            Check.equal(null, sys.undoLastService(), "undo");
        });

        Check.run(s, "la solicitud vuelve al frente de su nivel", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("A", "a", 2);
            sys.registerRequest("B", "b", 2);
            sys.registerRequest("C", "c", 1);
            Check.equal("C", sys.serveNext().getId(), "atiende C");
            Check.equal("A", sys.serveNext().getId(), "atiende A");
            Check.equal("A", sys.undoLastService().getId(), "deshace A");
            Check.equal(List.of("A", "B"), ids(sys.getPendingInOrder()), "A antes que B");
            Check.equal(List.of("C"), ids(sys.getHistory()), "historial sin A");
            Check.equal("A", sys.findById("A").getId(), "A se encuentra por id");
            Check.isTrue(sys.checkInvariant(), "invariante");
        });

        Check.run(s, "deshacer varias veces sigue el orden LIFO", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("A", "a", 3);
            sys.registerRequest("B", "b", 1);
            sys.registerRequest("C", "c", 3);
            sys.serveNext();
            sys.serveNext();
            sys.serveNext();
            Check.equal("C", sys.undoLastService().getId(), "1er undo");
            Check.equal("A", sys.undoLastService().getId(), "2do undo");
            Check.equal("B", sys.undoLastService().getId(), "3er undo");
            Check.equal(null, sys.undoLastService(), "nada mas");
            Check.equal(List.of("B", "A", "C"), ids(sys.getPendingInOrder()), "estado original");
        });

        Check.run(s, "deshacer con llegadas posteriores en el mismo nivel", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("A", "a", 2);
            sys.serveNext();
            sys.registerRequest("D", "d", 2);
            sys.undoLastService();
            Check.equal(List.of("A", "D"), ids(sys.getPendingInOrder()), "A llego antes que D");
        });

        Check.run(s, "conflicto: el id fue reutilizado por otra pendiente", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("A", "original", 2);
            sys.serveNext();
            sys.registerRequest("A", "nueva", 4);
            Check.throwsException(IllegalStateException.class, sys::undoLastService, "undo con conflicto");
            Check.equal("nueva", sys.findById("A").getName(), "no se modifico la pendiente");
            Check.equal(1, sys.getHistory().size(), "no se modifico el historial");
            sys.cancelRequest("A");
            Check.equal("original", sys.undoLastService().getName(), "tras cancelar ya se puede");
        });

        Check.run(s, "una solicitud deshecha puede cancelarse en O(1)", () -> {
            TriageSystem sys = new TriageSystem();
            sys.registerRequest("A", "a", 2);
            sys.registerRequest("B", "b", 2);
            sys.serveNext();
            sys.undoLastService();
            Check.isTrue(sys.cancelRequest("A"), "cancelar A");
            Check.equal(List.of("B"), ids(sys.getPendingInOrder()), "solo B");
        });
    }

    // ------------------------------------------------------------------
    /**
     * Ejecuta secuencias largas de operaciones aleatorias sobre TriageSystem
     * y sobre un modelo de referencia independiente (TreeSet ordenado por
     * urgencia y llegada, HashMap y ArrayDeque) y compara los resultados
     * despues de cada operacion.
     */
    private static void randomizedAgainstModel() {
        String s = "TriageSystem (prueba aleatoria vs. modelo)";
        long[] seeds = {1, 2, 3, 42, 2026};
        for (long seed : seeds) {
            Check.run(s, "semilla " + seed + ", 20000 operaciones", () -> runModel(seed, 20_000));
        }
    }

    private static void runModel(long seed, int steps) {
        Random rnd = new Random(seed);
        TriageSystem sys = new TriageSystem();
        Comparator<Ticket> byPriority = Comparator.comparingInt(Ticket::getUrgency)
                .thenComparingLong(Ticket::getArrivalOrder);
        TreeSet<Ticket> pending = new TreeSet<>(byPriority);
        Map<String, Ticket> byId = new HashMap<>();
        Deque<Ticket> served = new ArrayDeque<>();

        for (int step = 0; step < steps; step++) {
            String id = "T" + rnd.nextInt(300);
            int op = rnd.nextInt(10);
            String where = " (semilla " + seed + ", paso " + step + ")";
            if (op < 4) {
                int urgency = rnd.nextInt(7); // incluye 0 y 6, invalidos
                boolean valid = urgency >= 1 && urgency <= 5 && !byId.containsKey(id);
                try {
                    Ticket t = sys.registerRequest(id, "n" + step, urgency);
                    Check.isTrue(valid, "se acepto un registro invalido" + where);
                    pending.add(t);
                    byId.put(id, t);
                } catch (IllegalArgumentException e) {
                    Check.isTrue(!valid, "se rechazo un registro valido" + where);
                }
            } else if (op < 6) {
                Ticket expected = pending.pollFirst();
                Ticket actual = sys.serveNext();
                Check.isTrue(expected == actual, "serveNext" + where);
                if (expected != null) {
                    byId.remove(expected.getId());
                    served.push(expected);
                }
            } else if (op == 6) {
                Ticket expected = byId.remove(id);
                if (expected != null) {
                    pending.remove(expected);
                }
                Check.equal(expected != null, sys.cancelRequest(id), "cancelRequest" + where);
            } else if (op == 7) {
                Ticket last = served.peek();
                if (last != null && byId.containsKey(last.getId())) {
                    Check.throwsException(IllegalStateException.class, sys::undoLastService,
                            "undo con conflicto" + where);
                } else {
                    Ticket actual = sys.undoLastService();
                    Check.isTrue(actual == last, "undoLastService" + where);
                    if (last != null) {
                        served.pop();
                        pending.add(last);
                        byId.put(last.getId(), last);
                    }
                }
            } else {
                Check.isTrue(byId.get(id) == sys.findById(id), "findById" + where);
            }
            Check.equal(pending.size(), sys.pendingCount(), "pendingCount" + where);
        }
        Check.equal(new ArrayList<>(pending), sys.getPendingInOrder(), "orden final de pendientes");
        Check.equal(new ArrayList<>(served), sys.getHistory(), "historial final");
        Check.isTrue(sys.checkInvariant(), "invariante final");
    }
}
