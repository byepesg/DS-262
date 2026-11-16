package model;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void transposeAvancaDentroDeLaOctava() {
        assertEquals(5, new Note(4).transpose(1).getPitchClass());  // E -> F
        assertEquals(7, new Note(4).transpose(3).getPitchClass());  // E -> G
    }

    @Test
    void transposeCruzaDeB_aC() {
        assertEquals(0, new Note(11).transpose(1).getPitchClass()); // B -> C
    }

    @Test
    void transposeCruzaDeC_aB_retrocediendo() {
        assertEquals(11, new Note(0).transpose(-1).getPitchClass()); // C -> B
    }

    @Test
    void transposeConMultiplosDe12EsIdentidad() {
        assertEquals(4, new Note(4).transpose(12).getPitchClass());
        assertEquals(4, new Note(4).transpose(-12).getPitchClass());
        assertEquals(4, new Note(4).transpose(24).getPitchClass());
    }

    @Test
    void equalsSeBaseEnPitchClass() {
        assertEquals(new Note(0), new Note(0));
        assertNotEquals(new Note(0), new Note(1));
    }

    @Test
    void hashCodeConsistenteConEquals() {
        Set<Note> set = new HashSet<>();
        set.add(new Note(0));
        set.add(new Note(0));  // duplicado lógico
        assertEquals(1, set.size());
    }

    @Test
    void pitchClassFueraDeRangoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Note(-1));
        assertThrows(IllegalArgumentException.class, () -> new Note(12));
    }
}
