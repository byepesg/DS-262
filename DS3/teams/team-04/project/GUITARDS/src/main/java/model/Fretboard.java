package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Mástil representado como matriz [cuerda][traste] de notas.
 * Fila 0 = cuerda 6 (grave), fila 5 = cuerda 1 (aguda).
 * Columna 0 = traste 0 (cuerda al aire).
 */
public class Fretboard {
    private final Tuning tuning;
    private final int frets;          // número de trastes (sin contar el 0)
    private final Note[][] grid;

    public Fretboard(Tuning tuning, int frets) {
        this.tuning = tuning;
        this.frets = frets;
        this.grid = new Note[tuning.stringCount()][frets + 1];

        for (int s = 0; s < tuning.stringCount(); s++) {
            Note open = tuning.openString(s);
            for (int f = 0; f <= frets; f++) {
                grid[s][f] = open.transpose(f);
            }
        }
    }

    public Note noteAt(int string, int fret) {
        if (string < 0 || string >= grid.length)
            throw new IllegalArgumentException("cuerda fuera de rango");
        if (fret < 0 || fret > frets)
            throw new IllegalArgumentException("traste fuera de rango");
        return grid[string][fret];
    }

    /** Devuelve todas las posiciones [cuerda, traste] donde aparece la nota dada. */
    public List<int[]> findAll(Note note) {
        List<int[]> positions = new ArrayList<>();
        for (int s = 0; s < stringCount(); s++) {
            for (int f = 0; f <= frets; f++) {
                if (grid[s][f].equals(note)) {
                    positions.add(new int[]{s, f});
                }
            }
        }
        return positions;
    }

    // devuelve todas las posiciones [cuerda, traste] donde aparece alguna de las notas dadas del conjunto
    public List<int[]> findAllIn(Set<Note> notes) {
        List<int[]> positions = new ArrayList<>();
        for (int s = 0; s < stringCount(); s++) {
            for (int f = 0; f <= frets; f++) {
                if (notes.contains(grid[s][f])) {  // O(1) por hashCode
                    positions.add(new int[]{s, f});
                }
            }
        }
        return positions;
    }

    public int stringCount() { return grid.length; }
    public int fretCount() { return frets; }
    public Tuning getTuning() { return tuning; }

    /** Imprime el mástil de forma legible. */
    public void print() {
        System.out.print("    ");
        for (int f = 0; f <= frets; f++) System.out.printf("%3d", f);
        System.out.println();
        for (int s = 0; s < stringCount(); s++) {
            System.out.printf("%2s |", tuning.openString(s).getName());
            for (int f = 0; f <= frets; f++)
                System.out.printf("%3s", grid[s][f].getName());
            System.out.println();
        }
    }
}