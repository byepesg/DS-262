package triage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pruebas automaticas: cada verificacion imprime [OK] o [FALLA] y el
 * programa termina con codigo 1 si alguna falla.
 */
public class BasicTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        section("Escenario principal", BasicTests::mainScenario);
        section("Casos borde", BasicTests::edgeCases);
        section("Deshacer (pila)", BasicTests::undoTests);
        section("Indice AVL y BST", BasicTests::indexTests);
        section("Heap y Heap-Sort", BasicTests::heapTests);
        section("Politica por plazo maximo", BasicTests::deadlineTests);
        section("Arbol de decision", BasicTests::decisionTreeTests);
        section("Red de sedes (grafo)", BasicTests::graphTests);

        System.out.println();
        System.out.println("Resultado: " + passed + " correctas, " + failed + " fallidas");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void mainScenario() {
        TriageSystem s = new TriageSystem();
        s.registerRequest("A1", "Juan Perez", 3);
        s.registerRequest("A2", "Ana Gomez", 1);
        s.registerRequest("A3", "Luis Ruiz", 2);
        s.registerRequest("A4", "Eva Leon", 1);
        check(s.pendingCount() == 4, "4 solicitudes registradas");
        check(s.serveNext().getId().equals("A2"), "1ra atencion: A2 (urgencia 1)");
        check(s.serveNext().getId().equals("A4"), "2da atencion: A4 (urgencia 1, llego despues)");
        check(s.cancelRequest("A3"), "cancelar A3 devuelve true");
        check(s.findById("A3") == null, "A3 ya no se encuentra");
        check(s.pendingCount() == 1, "queda 1 pendiente");
        check(s.serveNext().getId().equals("A1"), "3ra atencion: A1 (A3 cancelada se descarta)");
        check(s.serveNext() == null, "sin pendientes devuelve null");
        check(ids(s.getHistory()).equals(List.of("A1", "A4", "A2")), "historial A1, A4, A2");
        check(s.invariantHolds(), "invariante de consistencia");
    }

    private static void edgeCases() {
        TriageSystem s = new TriageSystem();
        expectError(() -> s.registerRequest("B1", "Maria Diaz", 9), "urgencia 9 se rechaza");
        expectError(() -> s.registerRequest("B1", "Maria Diaz", 0), "urgencia 0 se rechaza");
        expectError(() -> s.registerRequest(" ", "Maria Diaz", 2), "id vacio se rechaza");
        expectError(() -> s.registerRequest("B1", "  ", 2), "nombre vacio se rechaza");
        check(s.pendingCount() == 0, "los errores no modifican el sistema");
        s.registerRequest("C1", "Pedro Rojas", 2);
        expectError(() -> s.registerRequest("C1", "Sofia Mora", 4), "id repetido se rechaza");
        check(!s.cancelRequest("ZZ"), "cancelar id inexistente devuelve false");
        s.serveNext();
        s.registerRequest("C1", "Sofia Mora", 4);
        check(s.findById("C1").getName().equals("Sofia Mora"), "se puede reusar un id ya atendido");
        check(s.cancelRequest("C1") && s.serveNext() == null, "cancelar la unica pendiente deja la cola vacia");
        s.registerRequest("C1", "Otra", 5);
        check(s.serveNext().getName().equals("Otra"), "reusar id cancelado no atiende el ticket viejo");
    }

    private static void undoTests() {
        TriageSystem s = new TriageSystem();
        check(s.undoLastService() == null, "deshacer sin historial devuelve null");
        s.registerRequest("D1", "Uno", 2);
        s.registerRequest("D2", "Dos", 2);
        s.registerRequest("D3", "Tres", 4);
        Ticket first = s.serveNext();
        check(s.undoLastService() == first, "deshacer devuelve D1");
        check(s.getHistory().isEmpty() && s.pendingCount() == 3, "historial vacio y 3 pendientes");
        check(s.serveNext().getId().equals("D1"), "D1 recupera su lugar al frente de su nivel");
        s.registerRequest("D0", "Cero", 1);
        s.undoLastService();
        check(ids(s.pendingInAttentionOrder()).equals(List.of("D0", "D1", "D2", "D3")),
                "orden tras deshacer con otra llegada: D0, D1, D2, D3");
        s.serveNext();
        s.registerRequest("D0", "Reusado", 3);
        expectState(s::undoLastService, "no se deshace si el id ya fue reutilizado");
        check(s.invariantHolds(), "invariante tras deshacer");
    }

    private static void indexTests() {
        int n = 1023;
        AvlIndex avl = new AvlIndex();
        BstIndex bst = new BstIndex();
        for (int i = 0; i < n; i++) {
            Ticket t = new Ticket(String.format("T%05d", i), "x", 3, i);
            avl.insert(t);
            bst.insert(t);
        }
        check(avl.isValid() && bst.isValid(), "ambos indices validos tras " + n + " ids ordenados");
        check(bst.height() == n - 1, "BST degenera en cadena: altura " + bst.height());
        check(avl.height() == 9, "AVL queda perfecto con 1023 ids ordenados: altura " + avl.height());
        check(!avl.insert(new Ticket("T00005", "y", 1, 0)), "AVL rechaza id repetido");
        check(avl.find("T00500") != null && avl.find("Z") == null, "busquedas exitosa y fallida");

        Random random = new Random(7);
        List<String> present = new ArrayList<>();
        AvlIndex mixed = new AvlIndex();
        boolean valid = true;
        for (int step = 0; step < 5000; step++) {
            String id = "K" + random.nextInt(2000);
            if (random.nextBoolean()) {
                boolean inserted = mixed.insert(new Ticket(id, "x", 1, step));
                if (inserted) {
                    present.add(id);
                }
            } else if (mixed.delete(id)) {
                present.remove(id);
            }
            if (step % 250 == 0) {
                valid &= mixed.isValid();
            }
        }
        valid &= mixed.isValid() && mixed.size() == present.size();
        check(valid, "5000 inserciones y eliminaciones aleatorias conservan el invariante AVL");
        double bound = 1.44 * Math.log(mixed.size() + 2) / Math.log(2);
        check(mixed.height() <= bound, "altura " + mixed.height() + " <= 1.44 log2(n+2)");
        check(new AvlIndex().height() == -1 && new AvlIndex().inorder().isEmpty(), "AVL vacio");
    }

    private static void heapTests() {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compare);
        int[] values = {40, 10, 90, 30, 90, 70, 5, 60};
        for (int v : values) {
            heap.insert(v);
        }
        check(heap.isValid() && heap.peek() == 5, "propiedad de heap y minimo en la raiz");
        List<Integer> out = new ArrayList<>();
        while (!heap.isEmpty()) {
            out.add(heap.removeMin());
        }
        check(out.equals(List.of(5, 10, 30, 40, 60, 70, 90, 90)), "extracciones en orden con duplicados");
        expectNoSuchElement(heap::removeMin, "extraer de un heap vacio lanza excepcion");

        Integer[] a = {4, 10, 3, 5, 1};
        MinHeap.heapSort(a, Integer::compare);
        check(List.of(a).equals(List.of(1, 3, 4, 5, 10)), "Heap-Sort de [4,10,3,5,1]");
        Integer[] b = {9, 4, 7, 1, 6, 3, 8};
        MinHeap.heapSort(b, Integer::compare);
        check(List.of(b).equals(List.of(1, 3, 4, 6, 7, 8, 9)), "Heap-Sort de [9,4,7,1,6,3,8]");
        Integer[] empty = {};
        MinHeap.heapSort(empty, Integer::compare);
        Integer[] one = {7};
        MinHeap.heapSort(one, Integer::compare);
        check(empty.length == 0 && one[0] == 7, "Heap-Sort de 0 y 1 elementos");

        Random random = new Random(3);
        Integer[] big = new Integer[2000];
        for (int i = 0; i < big.length; i++) {
            big[i] = random.nextInt(500);
        }
        MinHeap.heapSort(big, Integer::compare);
        boolean sorted = true;
        for (int i = 1; i < big.length; i++) {
            sorted &= big[i - 1] <= big[i];
        }
        check(sorted, "Heap-Sort de 2000 valores aleatorios queda ascendente");
    }

    private static void deadlineTests() {
        TriageSystem s = new TriageSystem(TriageSystem.Policy.DEADLINE);
        s.registerRequest("P5", "Poco urgente", 5);   // plazo 0 + 240 = 240
        s.advanceTime(200);
        s.registerRequest("P3", "Media", 3);          // plazo 200 + 60 = 260
        s.registerRequest("P2", "Urgente", 2);        // plazo 200 + 10 = 210
        check(ids(s.pendingInAttentionOrder()).equals(List.of("P2", "P5", "P3")),
                "orden por plazo: P2 (210), P5 (240), P3 (260)");
        check(s.serveNext().getId().equals("P2"), "primero P2");
        check(s.serveNext().getId().equals("P5"), "la urgencia 5 que espero mucho pasa antes que P3");
        s.advanceTime(100);
        check(ids(s.overduePending()).equals(List.of("P3")), "P3 aparece vencida en el minuto 300");

        TriageSystem levels = new TriageSystem(TriageSystem.Policy.LEVELS);
        levels.registerRequest("P5", "Poco urgente", 5);
        levels.advanceTime(200);
        levels.registerRequest("P3", "Media", 3);
        check(levels.serveNext().getId().equals("P3"), "por niveles, P3 pasa antes que P5");

        TriageSystem undo = new TriageSystem(TriageSystem.Policy.DEADLINE);
        undo.registerRequest("U1", "a", 2);
        undo.registerRequest("U2", "b", 2);
        undo.serveNext();
        undo.undoLastService();
        check(undo.serveNext().getId().equals("U1"), "deshacer en la politica por plazo");
        check(undo.cancelRequest("U2") && undo.serveNext() == null, "cancelacion perezosa en el heap");
    }

    private static void decisionTreeTests() {
        DecisionTree tree = DecisionTree.defaultProtocol();
        check(tree.countNodes() == 11 && tree.countLeaves() == 6, "11 nodos y 6 hojas");
        check(tree.height() == 5, "altura 5");
        check(tree.classify(q -> true) == 1, "si a todo: urgencia 1 (una sola pregunta)");
        check(tree.classify(q -> false) == 5, "no a todo: urgencia 5");
        check(tree.classify(q -> q.contains("sangrado")) == 2, "solo sangrado: urgencia 2");
        check(tree.classify(q -> q.contains("fiebre")) == 3, "solo fiebre: urgencia 3");
        int[] asked = {0};
        tree.classify(q -> {
            asked[0]++;
            return false;
        });
        check(asked[0] == tree.height(), "el peor caso hace h = 5 preguntas");
        check(tree.protocolLines().size() == 11, "el protocolo en preorder lista los 11 nodos");
    }

    private static void graphTests() {
        SiteNetwork net = SiteNetwork.defaultNetwork(TriageSystem.Policy.LEVELS);
        int central = net.indexOf("Central");
        int suba = net.indexOf("Suba");
        int norte = net.indexOf("norte");
        check(net.siteCount() == 6 && net.roadCount() == 8, "6 sedes y 8 vias");

        int[] hops = net.bfsHops(central);
        check(hops[central] == 0 && hops[suba] == 2 && hops[norte] == 2, "BFS: Suba y Norte a 2 saltos");

        long[] dist = net.dijkstra(central, null);
        check(dist[norte] == 35, "Dijkstra: Central-Norte 35 min (por Chapinero)");
        check(dist[suba] == 50, "Dijkstra: Central-Suba 50 min");
        SiteNetwork.Route route = net.shortestRoute(central, suba);
        check(route.path().equals(List.of("Central", "Chapinero", "Norte", "Suba"))
                || route.path().equals(List.of("Central", "Occidente", "Suba")),
                "ruta minima a Suba: " + route.path());

        check(net.nearestAvailable(central).site() == net.indexOf("Chapinero"),
                "con todo libre, la mas cercana es Chapinero (15 min)");
        TriageSystem chapinero = net.site(net.indexOf("Chapinero")).system();
        for (int i = 0; i < 4; i++) {
            chapinero.registerRequest("H" + i, "x", 3);
        }
        check(net.site(net.indexOf("Chapinero")).isSaturated(), "Chapinero saturada");
        check(net.nearestAvailable(central).site() == net.indexOf("Sur"),
                "con Chapinero llena, se deriva a Sur (20 min)");

        SiteNetwork isolated = new SiteNetwork();
        int a = isolated.addSite("A", 1, TriageSystem.Policy.LEVELS);
        int b = isolated.addSite("B", 1, TriageSystem.Policy.LEVELS);
        check(isolated.bfsHops(a)[b] == -1, "BFS: sede sin vias no es alcanzable");
        check(isolated.nearestAvailable(a) == null, "sin camino no hay derivacion");
        expectError(() -> isolated.addRoad(a, a, 5), "no se permite una via de una sede a si misma");
        expectError(() -> isolated.addRoad(a, b, 0), "no se permite tiempo 0");
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    private static void section(String name, Runnable body) {
        System.out.println();
        System.out.println("--- " + name + " ---");
        try {
            body.run();
        } catch (RuntimeException e) {
            check(false, "excepcion inesperada: " + e);
        }
    }

    private static void check(boolean condition, String description) {
        if (condition) {
            passed++;
        } else {
            failed++;
        }
        System.out.println((condition ? "[OK]    " : "[FALLA] ") + description);
    }

    private static void expectError(Runnable action, String description) {
        try {
            action.run();
            check(false, description);
        } catch (IllegalArgumentException e) {
            check(true, description + " (" + e.getMessage() + ")");
        }
    }

    private static void expectState(Runnable action, String description) {
        try {
            action.run();
            check(false, description);
        } catch (IllegalStateException e) {
            check(true, description);
        }
    }

    private static void expectNoSuchElement(Runnable action, String description) {
        try {
            action.run();
            check(false, description);
        } catch (java.util.NoSuchElementException e) {
            check(true, description);
        }
    }

    private static List<String> ids(List<Ticket> tickets) {
        List<String> out = new ArrayList<>();
        for (Ticket t : tickets) {
            out.add(t.getId());
        }
        return out;
    }
}
