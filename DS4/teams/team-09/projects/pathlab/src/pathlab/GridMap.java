package pathlab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Mapa de cuadrícula: S = inicio, E = destino, # = obstáculo,
 * . = terreno normal y 1..9 = costo de entrar a la celda.
 */
public final class GridMap {
    private final char[][] cells;
    private final Position start;
    private final Position end;

    private GridMap(char[][] cells, Position start, Position end) {
        this.cells = cells;
        this.start = start;
        this.end = end;
    }

    public static GridMap load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || lines.get(0).isEmpty()) {
            throw new IllegalArgumentException("El mapa no puede estar vacío.");
        }

        int columns = lines.get(0).length();
        char[][] cells = new char[lines.size()][columns];
        Position start = null;
        Position end = null;

        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            if (line.length() != columns) {
                throw new IllegalArgumentException("La fila " + (row + 1)
                        + " no tiene " + columns + " columnas.");
            }

            for (int col = 0; col < columns; col++) {
                char symbol = line.charAt(col);
                if (symbol != 'S' && symbol != 'E' && symbol != '.'
                        && symbol != '#' && (symbol < '1' || symbol > '9')) {
                    throw new IllegalArgumentException("Símbolo no válido '" + symbol
                            + "' en fila " + (row + 1) + ", columna " + (col + 1) + ".");
                }
                if (symbol == 'S') {
                    if (start != null) {
                        throw new IllegalArgumentException("Debe existir un único inicio S.");
                    }
                    start = new Position(row, col);
                } else if (symbol == 'E') {
                    if (end != null) {
                        throw new IllegalArgumentException("Debe existir un único destino E.");
                    }
                    end = new Position(row, col);
                }
                cells[row][col] = symbol;
            }
        }

        if (start == null || end == null) {
            throw new IllegalArgumentException("El mapa necesita un inicio S y un destino E.");
        }
        return new GridMap(cells, start, end);
    }

    public int rows() { return cells.length; }
    public int columns() { return cells[0].length; }
    public Position start() { return start; }
    public Position end() { return end; }

    public boolean isInside(Position p) {
        return p.row() >= 0 && p.row() < rows()
                && p.col() >= 0 && p.col() < columns();
    }

    public boolean isWalkable(Position p) {
        return isInside(p) && cells[p.row()][p.col()] != '#';
    }

    /** Retorna el costo de entrar a una celda transitable. */
    public int costToEnter(Position p) {
        if (!isWalkable(p)) {
            throw new IllegalArgumentException("La celda no es transitable.");
        }
        char symbol = cells[p.row()][p.col()];
        return symbol >= '1' && symbol <= '9' ? symbol - '0' : 1;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (char[] row : cells) {
            result.append(row).append(System.lineSeparator());
        }
        return result.toString();
    }
}
