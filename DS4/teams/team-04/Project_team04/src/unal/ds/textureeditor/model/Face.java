package unal.ds.textureeditor.model;

import java.util.Arrays;

/** Geometría del cubo: orden de caras, orientación de cada textura y despliegue en cruz. */
public final class Face {
    private Face() { }

    public static final int FRONT = 0, BACK = 1, LEFT = 2, RIGHT = 3, TOP = 4, BOTTOM = 5;
    public static final int COUNT = 6;

    public static final String[] NAMES = {"Face (Front)", "Rear (Back)", "Left Side", "Right Side", "Top", "Underside"};
    public static final String[] SHORT = {"Front", "Rear", "Left", "Right", "Top", "Under"};

    /** Normal de cada cara, y ejes u (derecha en la textura) y v (abajo en la textura). */
    public static final int[][] NORMAL = {{0, 0, 1}, {0, 0, -1}, {-1, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, -1, 0}};
    public static final int[][] UDIR = {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 0, 0}, {1, 0, 0}};
    public static final int[][] VDIR = {{0, -1, 0}, {0, -1, 0}, {0, -1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
    /** Esquina (0/1 por eje) donde está el texel (0,0) de cada cara. */
    public static final int[][] ORIG = {{0, 1, 1}, {1, 1, 0}, {0, 1, 0}, {1, 1, 1}, {0, 1, 0}, {0, 0, 1}};

    /** Sombreado por cara en la vista 3D. */
    public static final double[] SHADE = {0.8, 0.8, 0.6, 0.6, 1.0, 0.5};

    /** Despliegue en cruz: columna y fila de cada cara, y búsqueda inversa [fila][columna]. */
    public static final int[] NET_COL = {1, 3, 0, 2, 1, 1};
    public static final int[] NET_ROW = {1, 1, 1, 1, 0, 2};
    public static final int[][] NET_CELL = {{-1, TOP, -1, -1}, {LEFT, FRONT, RIGHT, BACK}, {-1, BOTTOM, -1, -1}};

    /** Cara cuya normal es nrm (se usa para cruzar las aristas del cubo). */
    public static int ofNormal(int[] nrm) {
        for (int f = 0; f < COUNT; f++) if (Arrays.equals(NORMAL[f], nrm)) return f;
        throw new IllegalArgumentException("Normal inválida: " + Arrays.toString(nrm));
    }
}