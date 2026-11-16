import model.*;
import util.MusicConstants;
import java.util.List;

public class Main_proyect {
    public static void main(String[] args) {
     
        // 1.  Modelado del Mástil y Notas
       
        Tuning tuning = Tuning.standard();
        Fretboard board = new Fretboard(tuning, 12);

        System.out.println("=== 1. Consultas de Notas (Fase 1) ===");
        System.out.println("Cuerda 6 (índice 0), traste 5 -> " + board.noteAt(0, 5)); // A
        System.out.println("Cuerda 1 (índice 5), traste 3 -> " + board.noteAt(5, 3)); // G

        /
        // 2.  Escalas y Acordes (Sets)

        Note rootC = new Note(0); // C
        
        System.out.println("\n=== 2. Escalas (Fase 2) ===");
        Scale cMajor = Scale.major(rootC);
        System.out.println("Escala de C Mayor: " + cMajor);
        
        // Buscar todas las posiciones de la escala en el mástil usando findAllIn
        List<int[]> posicionesEscala = board.findAllIn(cMajor.getNotes());
        System.out.println("Se encontraron " + posicionesEscala.size() + " notas de C Mayor en el mástil.");

        System.out.println("\n=== 3. Acordes (Fase 2) ===");
        Chord cMaj7 = Chord.major7(rootC);
        System.out.println("Acorde de C Maj7: " + cMaj7);
        
        List<int[]> posicionesAcorde = board.findAllIn(cMaj7.getNotes());
        System.out.println("Se encontraron " + posicionesAcorde.size() + " notas del acorde en el mástil.");

        // ==========================================
        // 3. FASE 2/3: Motor de Voicings (Developer Tooling)
        // ==========================================
        System.out.println("\n=== 4. Top 5 Voicings para C Mayor ===");
        
        // Creamos un acorde de C Mayor simple (tríada) para buscar voicings
        Chord cMaj = Chord.major(rootC);
        
        // Instanciamos el buscador con un límite físico de 4 trastes de estiramiento
        VoicingFinder finder = new VoicingFinder(board, 4);
        
        // Buscamos los 5 mejores voicings según nuestro sistema de puntuación
        List<VoicingScore> topVoicings = finder.findVoicings(cMaj, 5);

        // Imprimimos los resultados
        if (topVoicings.isEmpty()) {
            System.out.println("No se encontraron voicings válidos.");
        } else {
            for (int i = 0; i < topVoicings.size(); i++) {
                System.out.println((i + 1) + ". " + topVoicings.get(i));
            }
        }
    }
}