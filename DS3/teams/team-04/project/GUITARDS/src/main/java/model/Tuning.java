package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Afinación: lista de notas al aire, de cuerda grave a aguda.
 */
public class Tuning {
    private final List<Note> openStrings;

    public Tuning(int[] pitchClasses) {
        this.openStrings = new ArrayList<>();
        for (int pc : pitchClasses) {
            openStrings.add(new Note(pc));
        }
    }

    public static Tuning standard() {
        return new Tuning(util.MusicConstants.STANDARD_TUNING);
    }

    // devuelve el número de cuerdas de la afinación
    public int stringCount() { return openStrings.size(); }

    // devuelve la nota al aire de la cuerda indicada (0 = grave, 1 = siguiente, ...)
    public Note openString(int stringIndex) {
        return openStrings.get(stringIndex);
    }
}