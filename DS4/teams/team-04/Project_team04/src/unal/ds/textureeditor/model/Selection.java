package unal.ds.textureeditor.model;

import java.util.Arrays;

/** Conjunto de píxeles seleccionados (índice plano sobre las 6 caras). Mantiene su tamaño al día en O(1). */
public class Selection {
    private final boolean[] sel;
    private int count;

    public Selection(int totalPixels) { sel = new boolean[totalPixels]; }

    public boolean isSelected(int idx) { return sel[idx]; }
    public boolean hasAny() { return count > 0; }
    public int count() { return count; }

    public void set(int idx, boolean value) {
        if (sel[idx] == value) return;
        sel[idx] = value;
        count += value ? 1 : -1;
    }

    /** Aplica un resultado de RegionFinder: lo agrega o lo quita de la selección. */
    public void apply(boolean[] region, boolean add) {
        for (int i = 0; i < region.length; i++) if (region[i]) set(i, add);
    }

    public void selectRange(int from, int toExclusive) {
        for (int i = from; i < toExclusive; i++) set(i, true);
    }

    public void clear() {
        Arrays.fill(sel, false);
        count = 0;
    }
}