package util;
public final class MusicConstants {
    private MusicConstants() {}

    // Nombres en sostenidos (simplificación para Fase 1)
    public static final String[] NOTE_NAMES = {
        "C","C#","D","D#","E","F","F#","G","G#","A","A#","B"
    };

    public static String nameOf(int pitchClass) {
        return NOTE_NAMES[Math.floorMod(pitchClass, 12)];
    }

    // Afinación estándar EADGBE, de la 6ª (grave) a la 1ª (aguda)
    public static final int[] STANDARD_TUNING = {4, 9, 2, 7, 11, 4}; // E A D G B E

    // Fórmulas de intervalos (en semitonos desde la raíz)
    public static final int[] SCALE_MAJOR = {0, 2, 4, 5, 7, 9, 11};
    public static final int[] SCALE_MINOR_NATURAL = {0, 2, 3, 5, 7, 8, 10};
    // Puedes agregar más: Dórico, Mixolidio, Pentatónica, etc.

    // Fórmulas de acordes
    public static final int[] CHORD_MAJOR = {0, 4, 7};
    public static final int[] CHORD_MINOR = {0, 3, 7};
    public static final int[] CHORD_DIM = {0, 3, 6};
    public static final int[] CHORD_MAJ7 = {0, 4, 7, 11};
    public static final int[] CHORD_MIN7 = {0, 3, 7, 10};
}