package pathlab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

/** Compara algoritmos propios con una referencia independiente sobre mapas aleatorios reproducibles. */
public final class DifferentialTest {
    private record Step(int row, int col, long cost) implements Comparable<Step> {
        @Override public int compareTo(Step other) { return Long.compare(cost, other.cost); }
    }

    private static long referenceCost(GridMap map) {
        long[][] dist = new long[map.rows()][map.columns()];
        for (long[] row : dist) Arrays.fill(row, Long.MAX_VALUE);
        Position start = map.start();
        dist[start.row()][start.col()] = 0;
        PriorityQueue<Step> pq = new PriorityQueue<>();
        pq.add(new Step(start.row(), start.col(), 0));
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
        while (!pq.isEmpty()) {
            Step s = pq.poll();
            if (s.cost != dist[s.row][s.col]) continue;
            if (s.row == map.end().row() && s.col == map.end().col()) return s.cost;
            for (int[] d : dirs) {
                Position next = new Position(s.row + d[0], s.col + d[1]);
                if (!map.isWalkable(next)) continue;
                long candidate = s.cost + map.costToEnter(next);
                if (candidate < dist[next.row()][next.col()]) {
                    dist[next.row()][next.col()] = candidate;
                    pq.add(new Step(next.row(), next.col(), candidate));
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) throws Exception {
        Random rnd = new Random(2622026L);
        int maps = 80;
        int checks = 0;
        Path tmp = Files.createTempFile("pathlab-random-", ".txt");
        try {
            for (int test = 0; test < maps; test++) {
                int rows = 3 + rnd.nextInt(10);
                int cols = 3 + rnd.nextInt(10);
                boolean weighted = test % 2 == 0;
                StringBuilder data = new StringBuilder();
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        char symbol;
                        if (r == 0 && c == 0) symbol = 'S';
                        else if (r == rows - 1 && c == cols - 1) symbol = 'E';
                        else if (rnd.nextDouble() < 0.24) symbol = '#';
                        else symbol = weighted ? (char)('1' + rnd.nextInt(9)) : '.';
                        data.append(symbol);
                    }
                    data.append('\n');
                }
                Files.writeString(tmp, data);
                GridMap map = GridMap.load(tmp);
                long expected = referenceCost(map);
                SearchResult dij = PathFinder.dijkstra(map);
                SearchResult ast = PathFinder.astar(map);
                if (dij.totalCost() != expected || ast.totalCost() != expected) {
                    throw new AssertionError("Costo incorrecto para mapa aleatorio " + test
                            + ": referencia=" + expected + ", Dijkstra=" + dij.totalCost()
                            + ", A*=" + ast.totalCost());
                }
                checks += 2;
                if (!weighted) {
                    SearchResult bfs = PathFinder.bfs(map);
                    if (bfs.totalCost() != expected) {
                        throw new AssertionError("BFS inconsistente para mapa aleatorio " + test);
                    }
                    checks++;
                }
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
        System.out.printf("OK: %d mapas aleatorios, %d comparaciones contra referencia.%n", maps, checks);
    }
}
