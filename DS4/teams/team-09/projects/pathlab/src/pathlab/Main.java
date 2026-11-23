package pathlab;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Uso: java -cp out pathlab.Main maps/ejemplo.txt [bfs|dijkstra|astar|all]");
            System.exit(1);
        }
        try {
            GridMap map = GridMap.load(Path.of(args[0]));
            String selected = args.length == 1 ? "all" : args[1].toLowerCase(Locale.ROOT);
            System.out.println("Mapa (" + map.rows() + "x" + map.columns() + "):");
            System.out.print(map);
            List<String> algorithms = selected.equals("all")
                    ? List.of("bfs", "dijkstra", "astar") : List.of(selected);
            for (String algorithm : algorithms) {
                try {
                    SearchResult result = switch (algorithm) {
                        case "bfs" -> PathFinder.bfs(map);
                        case "dijkstra" -> PathFinder.dijkstra(map);
                        case "astar" -> PathFinder.astar(map);
                        default -> throw new IllegalArgumentException("Algoritmo desconocido: " + algorithm);
                    };
                    printResult(map, result);
                } catch (IllegalArgumentException error) {
                    if (selected.equals("all") && algorithm.equals("bfs")) {
                        System.out.println("\nBFS omitido: " + error.getMessage());
                    } else throw error;
                }
            }
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }

    private static void printResult(GridMap map, SearchResult result) {
        System.out.printf(Locale.US, "\n%s | ruta: %s | pasos: %d | costo: %d | explorados: %d | tiempo: %.3f ms%n",
                result.algorithm(), result.found() ? "SI" : "NO", result.steps(),
                result.totalCost(), result.explored(), result.elapsedMillis());
        if (!result.found()) return;
        char[][] picture = new char[map.rows()][map.columns()];
        String[] lines = map.toString().stripTrailing().split("\\R");
        for (int i = 0; i < lines.length; i++) picture[i] = lines[i].toCharArray();
        for (Position position : result.path()) {
            if (!position.equals(map.start()) && !position.equals(map.end())) {
                picture[position.row()][position.col()] = '*';
            }
        }
        for (char[] row : picture) System.out.println(new String(row));
    }
}
