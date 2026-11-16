package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FretboardTest {

    private Fretboard board;

    @BeforeEach
    void setUp() {
        board = new Fretboard(Tuning.standard(), 12);
    }

    @Test
    void notaAlAireCuerda6EsE() {
        assertEquals(new Note(4), board.noteAt(0, 0));
    }

    @Test
    void notaAlAireCuerda1EsE() {
        assertEquals(new Note(4), board.noteAt(5, 0));
    }

    @Test
    void cuerda6Traste5EsA() {
        assertEquals(new Note(9), board.noteAt(0, 5));
    }

    @Test
    void cuerda1Traste3EsG() {
        assertEquals(new Note(7), board.noteAt(5, 3));
    }

    @Test
    void cuerdaInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> board.noteAt(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.noteAt(6, 0));
    }

    @Test
    void trasteInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> board.noteAt(0, -1));
        assertThrows(IllegalArgumentException.class, () -> board.noteAt(0, 13));
    }

    @Test
    void findAllDeA_encuentraTodasLasPosiciones() {
        List<int[]> pos = board.findAll(new Note(9));  // A
        // En 12 trastes, A aparece en: 6ª/5, 5ª/0, 4ª/7, 3ª/2, 2ª/10, 1ª/5
        assertEquals(7, pos.size());
    }

    @Test
    void findAllDeNotaQueNoExiste_devuelveVacio() {
        // Con 0 trastes, solo hay notas al aire: E A D G B E
        Fretboard plano = new Fretboard(Tuning.standard(), 0);
        assertEquals(0, plano.findAll(new Note(0)).size());  // C no está al aire
    }
}