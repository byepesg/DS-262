import model.*;
import util.MusicConstants;

public class Main_proyect {
    public static void main(String[] args) {
        Tuning tuning = Tuning.standard();
        Fretboard board = new Fretboard(tuning, 12);

        System.out.println("=== Mástil (afinación estándar) ===");
        board.print();

        System.out.println("\n=== Consultas puntuales ===");
        System.out.println("Cuerda 6 (E), traste 5 -> " + board.noteAt(0, 5)); // A
        System.out.println("Cuerda 1 (E), traste 3 -> " + board.noteAt(5, 3)); // G

        System.out.println("\n=== Intervalos ===");
        Note a = new Note(9, "A");
        Note c = new Note(0, "C");
        int semis = Interval.between(a, c);
        System.out.println("De A a C: " + semis + " semitonos (" + Interval.nameOf(semis) + ")");
    }
}