package pathlab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PathLabTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        checkQueue();
        checkHeap();
        checkUnweighted();
        checkWeighted();
        checkNoRoute();
        checkInputErrors();
        System.out.println("OK: " + checks + " comprobaciones superadas.");
    }

    private static void checkQueue() {
        CircularQueue<Integer> queue = new CircularQueue<>(2);
        queue.add(10);
        queue.add(20);
        eq(queue.remove(), 10, "FIFO 1");
        queue.add(30);
        eq(queue.remove(), 20, "FIFO 2");
        eq(queue.remove(), 30, "FIFO con índice circular");
        yes(queue.isEmpty(), "Cola vacía");
    }

    private static void checkHeap() {
        BinaryMinHeap<Integer> heap = new BinaryMinHeap<>();
        for (int n : new int[]{8, 3, 11, 1, 7, 1}) heap.add(n);
        for (int expected : new int[]{1, 1, 3, 7, 8, 11}) eq(heap.removeMin(), expected, "Heap mínimo");
        yes(heap.isEmpty(), "Heap vacío");
    }

    private static void checkUnweighted() throws IOException {
        GridMap map = GridMap.load(Path.of("maps/simple.txt"));
        SearchResult bfs = PathFinder.bfs(map);
        SearchResult dijkstra = PathFinder.dijkstra(map);
        SearchResult astar = PathFinder.astar(map);
        yes(bfs.found(), "BFS encuentra ruta");
        eq(bfs.steps(), 9, "BFS menor número de pasos");
        eq(bfs.totalCost(), dijkstra.totalCost(), "BFS y Dijkstra mismo costo unitario");
        eq(astar.totalCost(), dijkstra.totalCost(), "A* óptimo en costos unitarios");
        validatePath(map, bfs);
        validatePath(map, dijkstra);
        validatePath(map, astar);
    }

    private static void checkWeighted() throws IOException {
        GridMap map = GridMap.load(Path.of("maps/ponderado.txt"));
        SearchResult dijkstra = PathFinder.dijkstra(map);
        SearchResult astar = PathFinder.astar(map);
        eq(dijkstra.totalCost(), 5L, "Dijkstra evita el peso 9");
        eq(astar.totalCost(), dijkstra.totalCost(), "A* da costo óptimo");
        eq(dijkstra.steps(), 5, "Camino ponderado más largo en pasos");
        validatePath(map, dijkstra);
        validatePath(map, astar);
        boolean rejected = false;
        try { PathFinder.bfs(map); } catch (IllegalArgumentException expected) { rejected = true; }
        yes(rejected, "BFS no se ofrece para pesos variables");
    }

    private static void checkNoRoute() throws IOException {
        GridMap map = GridMap.load(Path.of("maps/sin_ruta.txt"));
        yes(!PathFinder.bfs(map).found(), "BFS sin ruta");
        yes(!PathFinder.dijkstra(map).found(), "Dijkstra sin ruta");
        yes(!PathFinder.astar(map).found(), "A* sin ruta");
    }

    private static void checkInputErrors() throws IOException {
        Path file = Files.createTempFile("pathlab-invalid-", ".txt");
        try {
            for (String content : List.of("S..\n.E\n", "S..\n...\n", "SS.E\n", "S$E\n")) {
                Files.writeString(file, content, StandardCharsets.UTF_8);
                boolean failed = false;
                try { GridMap.load(file); } catch (IllegalArgumentException expected) { failed = true; }
                yes(failed, "Mapa inválido: " + content.replace('\n', ' '));
            }
        } finally { Files.deleteIfExists(file); }
    }

    private static void validatePath(GridMap map, SearchResult result) {
        List<Position> path = result.path();
        yes(!path.isEmpty(), "Camino no vacío");
        yes(path.get(0).equals(map.start()), "Camino comienza en S");
        yes(path.get(path.size() - 1).equals(map.end()), "Camino termina en E");
        long actualCost = 0;
        for (int i = 1; i < path.size(); i++) {
            Position before = path.get(i-1), after = path.get(i);
            yes(Math.abs(before.row()-after.row()) + Math.abs(before.col()-after.col()) == 1,
                    "Los movimientos son ortogonales");
            yes(map.isWalkable(after), "No atraviesa obstáculos");
            actualCost += map.costToEnter(after);
        }
        eq(actualCost, result.totalCost(), "Suma de costos del camino");
    }

    private static void yes(boolean condition, String name) {
        checks++;
        if (!condition) throw new AssertionError(name);
    }
    private static void eq(long actual, long expected, String name) {
        checks++;
        if (actual != expected) throw new AssertionError(name + ": esperado " + expected + ", recibido " + actual);
    }
}
