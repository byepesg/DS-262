package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntervalTest {

    @Test
    void betweenMismaNotaEsCero() {
        assertEquals(0, Interval.between(new Note(4), new Note(4)));
    }

    @Test
    void betweenA_aC_esTerceraMenor() {
        assertEquals(3, Interval.between(new Note(9), new Note(0)));
    }

    @Test
    void betweenC_aA_esSextaMayor() {
        assertEquals(9, Interval.between(new Note(0), new Note(9)));
    }

    @Test
    void betweenB_aC_esSegundaMenor() {
        assertEquals(1, Interval.between(new Note(11), new Note(0)));
    }

    @Test
    void betweenSiempreEstaEnRango0_11() {
        for (int a = 0; a < 12; a++) {
            for (int b = 0; b < 12; b++) {
                int d = Interval.between(new Note(a), new Note(b));
                assertTrue(d >= 0 && d <= 11, "d=" + d + " para a=" + a + ", b=" + b);
            }
        }
    }

    @Test
    void nameOfCubreLos12Semitonos() {
        assertEquals("Unísono", Interval.nameOf(0));
        assertEquals("Tercera menor", Interval.nameOf(3));
        assertEquals("Quinta justa", Interval.nameOf(7));
        assertEquals("Séptima mayor", Interval.nameOf(11));
    }

    @Test
    void nameOfManejaNegativosYMayoresQue12() {
        assertEquals("Tercera menor", Interval.nameOf(-9));  // -9 ≡ 3
        assertEquals("Tercera menor", Interval.nameOf(15));  // 15 ≡ 3
    }
}