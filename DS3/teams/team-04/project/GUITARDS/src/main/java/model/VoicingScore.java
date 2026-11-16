package model;

import java.util.List;

public class VoicingScore implements Comparable<VoicingScore> {
    private final List<int[]> positions;
    private final int score;

    public VoicingScore(List<int[]> positions, int score) {
        this.positions = positions;
        this.score = score;
    }

    public List<int[]> getPositions() { return positions; }
    public int getScore() { return score; }

    // CORRECCIÓN: Orden ascendente. El puntaje más bajo (peor) se considera "menor".
    // Como usamos PriorityQueue, poll() sacará el "menor" (el peor), dejando los mejores.
    @Override
    public int compareTo(VoicingScore other) {
        return Integer.compare(this.score, other.score); 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Score: " + score + " -> ");
        for (int[] pos : positions) {
            // Imprimimos C6-T5 (asumiendo que 0 es la cuerda 6)
            sb.append("[C").append(6 - pos[0]).append("-T").append(pos[1]).append("] ");
        }
        return sb.toString();
    }
}