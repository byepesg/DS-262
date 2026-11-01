package model;
import util.MusicConstants;
import java.util.Objects;

/**
 * Nota musical representada por su valor cromático (0-11).
 * 0=C, 1=C#, 2=D, ... 11=B
 */
public class Note {
    private final int pitchClass; // 0-11
    private final String name;

    public Note(int pitchClass, String name) {
        /** Invariante de pitchClass Notas desde 0 hasta 11*/
        if (pitchClass < 0 || pitchClass > 11)
            throw new IllegalArgumentException("pitchClass debe estar en 0-11");
        this.pitchClass = pitchClass;
        this.name = name;
    }

    public int getPitchClass() { return pitchClass; }
    public String getName() { return name; }

    /** Suma semitonos con aritmética modular (lista circular). Pasar de nota a nota*/
    public Note transpose(int semitones) {
        int newPc = Math.floorMod(pitchClass + semitones, 12);
        return new Note(newPc, MusicConstants.nameOf(newPc));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        return pitchClass == ((Note) o).pitchClass;
    }

    @Override
    public int hashCode() { return Objects.hash(pitchClass); }

    @Override
    public String toString() { return name; }
}