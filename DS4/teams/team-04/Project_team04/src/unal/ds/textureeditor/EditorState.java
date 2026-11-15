package unal.ds.textureeditor;

import java.util.ArrayList;
import java.util.List;
import unal.ds.textureeditor.model.BlockModel;

public class EditorState {
    public BlockModel block;
    public int currentFace = 0;        // índice de la cara (0 = Front)
    public int rgb = 0x5D8A3A;         // color actual sin alfa
    public int alpha = 255;
    public int tolerance = 10;         // 0..100
    public boolean contiguous = true;
    public boolean wholeBlock = false; // false = solo la cara actual
    public int brushSize = 1;

    private final List<Runnable> listeners = new ArrayList<>();

    public EditorState() { newBlock(16); }

    public void newBlock(int n) {
        block = new BlockModel(n);
        currentFace = 0;
        fireChanged();
    }

    public int colorArgb() { return (alpha << 24) | (rgb & 0xFFFFFF); }

    public void addListener(Runnable r) { listeners.add(r); }
    public void fireChanged() { for (Runnable r : listeners) r.run(); }
}