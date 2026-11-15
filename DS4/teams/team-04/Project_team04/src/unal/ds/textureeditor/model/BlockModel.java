package unal.ds.textureeditor.model;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/** Bloque de N x N píxeles por cara. Un píxel se identifica por un índice plano: cara*n*n + y*n + x. */
public class BlockModel {
    public final int n;
    public final int pixelsPerFace;   // n*n
    public final int totalPixels;     // 6*n*n
    public final Selection selection;

    private final BufferedImage[] images = new BufferedImage[Face.COUNT];
    private final int[][] px = new int[Face.COUNT][];   // arreglos ARGB compartidos con las imágenes

    public BlockModel(int n) {
        if (n < 1) throw new IllegalArgumentException("n debe ser >= 1");
        this.n = n;
        this.pixelsPerFace = n * n;
        this.totalPixels = Face.COUNT * pixelsPerFace;
        this.selection = new Selection(totalPixels);
        for (int f = 0; f < Face.COUNT; f++) {
            images[f] = new BufferedImage(n, n, BufferedImage.TYPE_INT_ARGB);
            px[f] = ((DataBufferInt) images[f].getRaster().getDataBuffer()).getData();
        }
    }

    /** Imagen de la cara, para dibujarla. No la modifiques directamente: usa set(). */
    public BufferedImage image(int f) { return images[f]; }

    /** Arreglo ARGB vivo de la cara (solo lectura): lo comparte la imagen. */
    public int[] pixels(int f) { return px[f]; }

    // ---- índices ----
    public int index(int f, int x, int y) { return f * pixelsPerFace + y * n + x; }
    public int faceOf(int idx) { return idx / pixelsPerFace; }
    public int xOf(int idx) { return idx % pixelsPerFace % n; }
    public int yOf(int idx) { return idx % pixelsPerFace / n; }
    public boolean inside(int x, int y) { return x >= 0 && y >= 0 && x < n && y < n; }

    // ---- acceso a píxeles, todos O(1) ----
    public int get(int idx) { return px[idx / pixelsPerFace][idx % pixelsPerFace]; }
    public void set(int idx, int argb) { px[idx / pixelsPerFace][idx % pixelsPerFace] = argb; }
    public int get(int f, int x, int y) { return px[f][y * n + x]; }
    public void set(int f, int x, int y, int argb) { px[f][y * n + x] = argb; }

    /** Copia n*n valores ARGB sobre una cara (importar texturas). */
    public void setFace(int f, int[] argb) { System.arraycopy(argb, 0, px[f], 0, pixelsPerFace); }
}