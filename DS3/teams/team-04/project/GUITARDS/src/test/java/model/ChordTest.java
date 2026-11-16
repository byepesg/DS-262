package model;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ChordTest {

    @Test
    void cMajorTiene3NotasCorrectas() {
        Chord cMaj = Chord.major(new Note(0));
        Set<Note> notas = cMaj.getNotes();
        
        assertEquals(3, notas.size());
        assertTrue(notas.contains(new Note(0))); // C
        assertTrue(notas.contains(new Note(4))); // E
        assertTrue(notas.contains(new Note(7))); // G
    }

    @Test
    void gMajor7Tiene4NotasCorrectas() {
        Chord gMaj7 = Chord.major7(new Note(7)); // G
        Set<Note> notas = gMaj7.getNotes();
        
        assertEquals(4, notas.size());
        assertTrue(notas.contains(new Note(7)));  // G
        assertTrue(notas.contains(new Note(11))); // B
        assertTrue(notas.contains(new Note(2)));  // D
        assertTrue(notas.contains(new Note(6)));  // F#
    }
}