package pathlab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Algoritmos de búsqueda sobre el grafo implícito de una cuadrícula. */
public final class PathFinder {
    private PathFinder() { }
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    private static final long INF = Long.MAX_VALUE / 4;

    /** BFS encuentra el menor número de pasos, solo con costos unitarios. */
    public static SearchResult bfs(GridMap map) {
        if (hasNonUnitCosts(map)) {
            throw new IllegalArgumentException("BFS requiere costos unitarios (use Dijkstra o A*).");
        }
        long started = System.nanoTime();
        boolean[][] visited = new boolean[map.rows()][map.columns()];
        Position[][] parent = new Position[map.rows()][map.columns()];
        CircularQueue<Position> queue = new CircularQueue<>(map.rows() * map.columns());
        Position start = map.start();
        queue.add(start);
        visited[start.row()][start.col()] = true;
        int explored = 0;

        while (!queue.isEmpty()) {
            Position current = queue.remove();
            explored++;
            if (current.equals(map.end())) {
                List<Position> path = reconstruct(parent, map.start(), map.end());
                return new SearchResult("BFS", path, path.size() - 1, explored,
                        System.nanoTime() - started);
            }
            for (Position next : neighbors(map, current)) {
                if (!visited[next.row()][next.col()]) {
                    visited[next.row()][next.col()] = true;
                    parent[next.row()][next.col()] = current;
                    queue.add(next);
                }
            }
        }
        return new SearchResult("BFS", List.of(), -1, explored, System.nanoTime() - started);
    }

    public static SearchResult dijkstra(GridMap map) { return prioritySearch(map, false); }
    public static SearchResult astar(GridMap map) { return prioritySearch(map, true); }

    private record State(Position position, long distance, long priority, long insertion)
            implements Comparable<State> {
        @Override
        public int compareTo(State other) {
            int byPriority = Long.compare(priority, other.priority);
            return byPriority != 0 ? byPriority : Long.compare(insertion, other.insertion);
        }
    }

    private static SearchResult prioritySearch(GridMap map, boolean useHeuristic) {
        long started = System.nanoTime();
        String algorithm = useHeuristic ? "A*" : "Dijkstra";
        long[][] distance = new long[map.rows()][map.columns()];
        for (long[] row : distance) Arrays.fill(row, INF);
        Position[][] parent = new Position[map.rows()][map.columns()];
        boolean[][] closed = new boolean[map.rows()][map.columns()];
        BinaryMinHeap<State> frontier = new BinaryMinHeap<>();
        Position start = map.start();
        distance[start.row()][start.col()] = 0;
        long sequence = 0;
        frontier.add(new State(start, 0, heuristic(start, map.end(), useHeuristic), sequence++));
        int explored = 0;

        while (!frontier.isEmpty()) {
            State entry = frontier.removeMin();
            Position current = entry.position();
            if (closed[current.row()][current.col()]
                    || entry.distance() != distance[current.row()][current.col()]) continue;
            closed[current.row()][current.col()] = true;
            explored++;
            if (current.equals(map.end())) {
                return new SearchResult(algorithm, reconstruct(parent, start, map.end()),
                        entry.distance(), explored, System.nanoTime() - started);
            }
            for (Position next : neighbors(map, current)) {
                if (closed[next.row()][next.col()]) continue;
                long newCost = entry.distance() + map.costToEnter(next);
                if (newCost < distance[next.row()][next.col()]) {
                    distance[next.row()][next.col()] = newCost;
                    parent[next.row()][next.col()] = current;
                    long priority = newCost + heuristic(next, map.end(), useHeuristic);
                    frontier.add(new State(next, newCost, priority, sequence++));
                }
            }
        }
        return new SearchResult(algorithm, List.of(), -1, explored, System.nanoTime() - started);
    }

    private static long heuristic(Position position, Position target, boolean enabled) {
        return enabled
                ? Math.abs(position.row() - target.row()) + Math.abs(position.col() - target.col())
                : 0;
    }

    /** Grafo implícito: las aristas son hasta cuatro celdas adyacentes. */
    private static List<Position> neighbors(GridMap map, Position position) {
        List<Position> adjacent = new ArrayList<>(4);
        for (int[] direction : DIRECTIONS) {
            Position next = new Position(position.row() + direction[0], position.col() + direction[1]);
            if (map.isWalkable(next)) adjacent.add(next);
        }
        return adjacent;
    }

    private static List<Position> reconstruct(Position[][] parent, Position start, Position end) {
        List<Position> reverse = new ArrayList<>();
        for (Position p = end; p != null; p = parent[p.row()][p.col()]) {
            reverse.add(p);
            if (p.equals(start)) break;
        }
        Collections.reverse(reverse);
        if (reverse.isEmpty() || !reverse.get(0).equals(start)) return List.of();
        return reverse;
    }

    private static boolean hasNonUnitCosts(GridMap map) {
        for (int row = 0; row < map.rows(); row++) {
            for (int col = 0; col < map.columns(); col++) {
                Position p = new Position(row, col);
                if (map.isWalkable(p) && map.costToEnter(p) != 1) return true;
            }
        }
        return false;
    }
}
