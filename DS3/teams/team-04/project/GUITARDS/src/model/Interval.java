package model;

import java.util.HashMap;
import java.util.Map;

public class Interval {
    private static final Map<Integer, String> NAMES = new HashMap<>();

    static {
        NAMES.put(0, "Unísono");
        NAMES.put(1, "Segunda menor");
        NAMES.put(2, "Segunda mayor");
        NAMES.put(3, "Tercera menor");
        NAMES.put(4, "Tercera mayor");
        NAMES.put(5, "Cuarta justa");
        NAMES.put(6, "Tritono");
        NAMES.put(7, "Quinta justa");
        NAMES.put(8, "Sexta menor");
        NAMES.put(9, "Sexta mayor");
        NAMES.put(10, "Séptima menor");
        NAMES.put(11, "Séptima mayor");
    }

    /** Intervalo en semitonos entre dos notas (0-11). */
    public static int between(Note from, Note to) {
        return Math.floorMod(to.getPitchClass() - from.getPitchClass(), 12);
    }

    /** Nombre del intervalo en función del número de semitonos. */
    public static String nameOf(int semitones) {
        return NAMES.getOrDefault(Math.floorMod(semitones, 12), "?");
    }
}