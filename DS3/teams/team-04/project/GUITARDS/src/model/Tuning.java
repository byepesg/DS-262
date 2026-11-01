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
            openStrings.add(new Note(pc, util.MusicConstants.nameOf(pc)));
        }
    }

    public static Tuning standard() {
        return new Tuning(util.MusicConstants.STANDARD_TUNING);
    }

    public int stringCount() { return openStrings.size(); }

    public Note openString(int stringIndex) {
        return openStrings.get(stringIndex);
    }
}