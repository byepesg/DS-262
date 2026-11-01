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
}