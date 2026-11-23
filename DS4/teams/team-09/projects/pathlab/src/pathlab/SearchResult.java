package pathlab;

import java.util.List;

/** Resultado reproducible de una búsqueda sobre un mapa. */
public record SearchResult(
        String algorithm,
        List<Position> path,
        long totalCost,
        int explored,
        long elapsedNanos) {

    public SearchResult {
        path = List.copyOf(path);
    }

    public boolean found() { return !path.isEmpty(); }
    public int steps() { return found() ? path.size() - 1 : -1; }
    public double elapsedMillis() { return elapsedNanos / 1_000_000.0; }
}
