package pathlab;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

/** Benchmark sencillo: tiempo depende del sistema y no debe interpretarse como valor universal. */
public final class Benchmark {
    private Benchmark() { }
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Uso: java -cp out pathlab.Benchmark maps/ejemplo.txt 20");
            System.exit(1);
        }
        GridMap map = GridMap.load(Path.of(args[0]));
        int repetitions = Integer.parseInt(args[1]);
        if (repetitions < 1 || repetitions > 100000) {
            throw new IllegalArgumentException("Repeticiones entre 1 y 100000.");
        }
        System.out.println("algoritmo,filas,columnas,repeticiones,ruta,pasos,costo,explorados,tiempo_medio_ms");
        for (String name : new String[]{"BFS", "Dijkstra", "A*"}) {
            try {
                for (int warmup = 0; warmup < Math.min(repetitions, 10); warmup++) run(map, name);
                long nanos = 0;
                SearchResult last = null;
                for (int i = 0; i < repetitions; i++) {
                    last = run(map, name);
                    nanos += last.elapsedNanos();
                }
                System.out.printf(Locale.US, "%s,%d,%d,%d,%s,%d,%d,%d,%.6f%n",
                        name, map.rows(), map.columns(), repetitions, last.found(),
                        last.steps(), last.totalCost(), last.explored(), nanos / repetitions / 1e6);
            } catch (IllegalArgumentException error) {
                System.err.println("Se omite " + name + ": " + error.getMessage());
            }
        }
    }

    private static SearchResult run(GridMap map, String name) {
        return switch (name) {
            case "BFS" -> PathFinder.bfs(map);
            case "Dijkstra" -> PathFinder.dijkstra(map);
            default -> PathFinder.astar(map);
        };
    }
}
