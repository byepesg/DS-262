package model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class VoicingFinder {
    private final Fretboard board;
    private final int maxFretSpan; // Máximo estiramiento de dedos (ej. 4 trastes)

    public VoicingFinder(Fretboard board, int maxFretSpan) {
        this.board = board;
        this.maxFretSpan = maxFretSpan;
    }

    /**
     * Encuentra los mejores voicings para un acorde, limitando el estiramiento
     * y devolviendo solo los top N resultados basados en un sistema de puntuación.
     */
    public List<VoicingScore> findVoicings(Chord chord, int maxResults) {
        // Usamos una PriorityQueue para mantener solo los mejores en memoria.
        // El compareTo de VoicingScore está diseñado para que los mejores puntajes queden arriba.
        PriorityQueue<VoicingScore> bestVoicings = new PriorityQueue<>();
        List<int[]> currentVoicing = new ArrayList<>();
        Set<Note> targetNotes = chord.getNotes();
        
        // Empezamos la búsqueda desde la 6ª cuerda (índice 0)
        backtrack(0, targetNotes, currentVoicing, bestVoicings, chord, maxResults);
        
        // Extraemos los resultados de la cola y los ordenamos de mejor a peor
        List<VoicingScore> result = new ArrayList<>(bestVoicings);
        result.sort(java.util.Comparator.naturalOrder()); 
        return result;
    }

    private void backtrack(int stringIndex, Set<Note> targetNotes, List<int[]> currentVoicing, 
                           PriorityQueue<VoicingScore> bestVoicings, Chord chord, int maxResults) {
        
        // Caso base: llegamos al final del mástil (después de la 1ª cuerda)
        if (stringIndex == board.stringCount()) {
            if (currentVoicing.size() >= 3) { // Un acorde debe tener al menos 3 notas
                int score = calculateScore(currentVoicing, chord);
                bestVoicings.add(new VoicingScore(new ArrayList<>(currentVoicing), score));
                
                // Si la cola supera el límite, sacamos el PEOR voicing (el de menor puntaje).
                // Como nuestro compareTo invierte el orden (mayor puntaje = menor valor de comparación),
                // poll() sacará el elemento con el puntaje más bajo.
                if (bestVoicings.size() > maxResults) {
                    bestVoicings.poll(); 
                }
            }
            return;
        }

        // Opción 1: Silenciar esta cuerda (mute)
        // Solo permitimos saltar cuerdas si ya hemos empezado a pisar notas
        if (!currentVoicing.isEmpty()) {
            backtrack(stringIndex + 1, targetNotes, currentVoicing, bestVoicings, chord, maxResults);
        }

        // Opción 2: Tocar una nota del acorde en esta cuerda
        for (int fret = 0; fret <= board.fretCount(); fret++) {
            Note noteAtFret = board.noteAt(stringIndex, fret);
            
            if (targetNotes.contains(noteAtFret)) {
                // Validar que el estiramiento no supere el límite físico
                if (isValidSpan(fret, currentVoicing)) {
                    currentVoicing.add(new int[]{stringIndex, fret});
                    backtrack(stringIndex + 1, targetNotes, currentVoicing, bestVoicings, chord, maxResults);
                    currentVoicing.remove(currentVoicing.size() - 1); // Backtrack
                }
            }
        }
    }

    /**
     * Verifica si agregar un traste específico excede la ventana máxima permitida.
     */
    private boolean isValidSpan(int fret, List<int[]> currentVoicing) {
        if (fret == 0) return true; // Cuerdas al aire siempre son válidas
        
        int minFret = fret;
        int maxFret = fret;
        
        for (int[] pos : currentVoicing) {
            if (pos[1] != 0) { // Ignoramos cuerdas al aire para el cálculo
                minFret = Math.min(minFret, pos[1]);
                maxFret = Math.max(maxFret, pos[1]);
            }
        }
        
        return (maxFret - minFret) <= maxFretSpan;
    }

    /**
     * Calcula un puntaje heurístico para un voicing.
     * Reglas: +10 por raíz, +5 por cuerda al aire, -20 si falta la 3ª, -2 por traste de estiramiento.
     */
    private int calculateScore(List<int[]> voicing, Chord chord) {
        int score = 0;
        boolean hasThird = false;
        boolean hasRoot = false;

        int rootPc = chord.getRoot().getPitchClass();
        int thirdMajorPc = (rootPc + 4) % 12;
        int thirdMinorPc = (rootPc + 3) % 12;

        int maxFret = 0;
        int minFret = 12;

        for (int[] pos : voicing) {
            int pc = board.noteAt(pos[0], pos[1]).getPitchClass();
        
            // Bono por cuerdas al aire (muy resonantes y fáciles)
            if (pos[1] == 0) {
                score += 15;
            } else {
                // Bono por trastes bajos (1, 2, 3): zona cómoda
                if (pos[1] >= 1 && pos[1] <= 3) {
                    score += 10;
                }
                // Actualizar los trastes máximo y mínimo
                maxFret = Math.max(maxFret, pos[1]);
                minFret = Math.min(minFret, pos[1]);
            }
        
            // Bono grande por nota raíz
            if (pc == rootPc) {
                hasRoot = true;
                score += 30;
            }
        
        
            // Verificar 3ª
            if (pc == thirdMajorPc || pc == thirdMinorPc) {
                hasThird = true;
            }
        }

        // Castigos
        if (!hasThird) {
            score -= 25; // Castigo severo por no definir la calidad
        }
    
        if (voicing.size() < 4) {
            score -= 15; // Acordes muy chicos suenan vacíos
        }

    
        // Castigo por estiramiento (más suave)
        if (maxFret > 0) {
            score -= (maxFret - minFret) * 5;
        }


        // Castigo por cuerdas saltadas en el medio
        for (int i = 1; i < voicing.size() - 1; i++) {
            int currentString = voicing.get(i)[0];
            int prevString = voicing.get(i - 1)[0];
            if (currentString - prevString > 1) {
                score -= 15;
            }
        }
    

        return score;
    }
}