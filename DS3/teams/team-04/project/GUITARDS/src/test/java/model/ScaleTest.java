package model;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ScaleTest {

    @Test
    void cMajorTiene7NotasCorrectas() {
        Scale cMajor = Scale.major(new Note(0)); // C
        Set<Note> notas = cMajor.getNotes();
        
        assertEquals(7, notas.size());
        
        // Verificamos que contenga las notas exactas (C, D, E, F, G, A, B)
        assertTrue(notas.contains(new Note(0)));  // C
        assertTrue(notas.contains(new Note(2)));  // D
        assertTrue(notas.contains(new Note(4)));  // E
        assertTrue(notas.contains(new Note(5)));  // F
        assertTrue(notas.contains(new Note(7)));  // G
        assertTrue(notas.contains(new Note(9)));  // A
        assertTrue(notas.contains(new Note(11))); // B
        
        // Verificamos que NO contenga notas fuera de la escala
        assertFalse(notas.contains(new Note(1))); // C#
    }

    @Test
    void aMinorTiene7NotasCorrectas() {
        Scale aMinor = Scale.minorNatural(new Note(9)); // A
        Set<Note> notas = aMinor.getNotes();
        
        assertEquals(7, notas.size());
        assertTrue(notas.contains(new Note(0)));  // C
        assertTrue(notas.contains(new Note(9)));  // A
        assertTrue(notas.contains(new Note(7)));  // G
    }
}