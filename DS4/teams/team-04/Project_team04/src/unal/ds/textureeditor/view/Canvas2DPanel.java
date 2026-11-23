package unal.ds.textureeditor.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import unal.ds.textureeditor.EditorState;
import unal.ds.textureeditor.model.BlockModel;
import unal.ds.textureeditor.model.Face;

/** Lienzo 2D: las seis caras desplegadas en cruz, con zoom y pan. */
public class Canvas2DPanel extends JComponent {
    private final EditorState state;
    private double zoom = 1, panX, panY;   // zoom = píxeles de pantalla por texel
    private boolean needFit = true;
    private boolean panning;
    private int lastMx, lastMy;
    private boolean selecting;
    private int selFace, selX0, selY0, selX1, selY1;

    public Canvas2DPanel(EditorState state) {
        this.state = state;
        setPreferredSize(new Dimension(640, 520));
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                lastMx = e.getX();
                lastMy = e.getY();
                panning = SwingUtilities.isMiddleMouseButton(e) || e.isAltDown();
                if (panning) return;
                if (state.tool == EditorState.Tool.RECT_SELECT) beginSelect(e);
                else handle(e);
            }

            @Override public void mouseDragged(MouseEvent e) {
                if (panning) {
                    panX += e.getX() - lastMx;
                    panY += e.getY() - lastMy;
                    lastMx = e.getX();
                    lastMy = e.getY();
                    repaint();
                } else if (state.tool == EditorState.Tool.RECT_SELECT) {
                    updateSelect(e);
                } else {
                    handle(e);
                }
            }

            @Override public void mouseReleased(MouseEvent e) {
                if (selecting) finishSelect(e);
                panning = false;
            }
            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                zoomAt(e.getX(), e.getY(), Math.pow(1.15, -e.getPreciseWheelRotation()));
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    // ---- zoom y encuadre ----
    private void zoomAt(double mx, double my, double factor) {
        double nz = Math.max(0.1, Math.min(80, zoom * factor));
        panX = mx - (mx - panX) * nz / zoom;
        panY = my - (my - panY) * nz / zoom;
        zoom = nz;
        repaint();
    }

    public void zoomBy(double factor) { zoomAt(getWidth() / 2.0, getHeight() / 2.0, factor); }

    public void fit() {
        int n = state.block.n;
        if (getWidth() <= 0 || getHeight() <= 0) { needFit = true; return; }
        double z = Math.min((getWidth() - 30.0) / (4 * n), (getHeight() - 30.0) / (3 * n));
        z = Math.max(0.1, Math.min(80, z));
        zoom = z >= 1 ? Math.floor(z) : z;
        panX = (getWidth() - 4 * n * zoom) / 2;
        panY = (getHeight() - 3 * n * zoom) / 2;
        repaint();
    }

    // ---- de pantalla a (cara, x, y): búsqueda directa en la tabla NET_CELL, O(1) ----
    private int[] hit(int mx, int my) {
        int n = state.block.n;
        double wx = (mx - panX) / zoom, wy = (my - panY) / zoom;
        int col = (int) Math.floor(wx / n), row = (int) Math.floor(wy / n);
        if (row < 0 || row > 2 || col < 0 || col > 3) return null;
        int f = Face.NET_CELL[row][col];
        if (f < 0) return null;
        int x = (int) Math.min(n - 1, Math.floor(wx - col * n));
        int y = (int) Math.min(n - 1, Math.floor(wy - row * n));
        return new int[]{f, x, y};
    }

    private void handle(MouseEvent e) {
        int[] h = hit(e.getX(), e.getY());
        if (h == null) return;
        state.currentFace = h[0];
        // TEMPORAL: pinta directo. En el paso de herramientas se reemplaza por state.tool.
        if (SwingUtilities.isLeftMouseButton(e)) state.block.set(h[0], h[1], h[2], state.colorArgb());
        state.fireChanged();
    }

    // ---- rectangular selection ----
    private void beginSelect(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        int[] h = hit(e.getX(), e.getY());
        if (h == null) return;
        selecting = true;
        selFace = h[0];
        selX0 = selX1 = h[1];
        selY0 = selY1 = h[2];
        state.currentFace = selFace;
        state.fireChanged();
    }

    /** Texel under the mouse, clamped to the face where the drag began (so dragging outside still works). */
    private int[] clampedTexel(int mx, int my) {
        int n = state.block.n;
        double wx = (mx - panX) / zoom - Face.NET_COL[selFace] * n;
        double wy = (my - panY) / zoom - Face.NET_ROW[selFace] * n;
        int x = (int) Math.max(0, Math.min(n - 1, Math.floor(wx)));
        int y = (int) Math.max(0, Math.min(n - 1, Math.floor(wy)));
        return new int[]{x, y};
    }

    private void updateSelect(MouseEvent e) {
        if (!selecting) return;
        int[] t = clampedTexel(e.getX(), e.getY());
        selX1 = t[0];
        selY1 = t[1];
        repaint();
    }

    private void finishSelect(MouseEvent e) {
        int[] t = clampedTexel(e.getX(), e.getY());
        selX1 = t[0];
        selY1 = t[1];
        boolean subtract = e.isControlDown();
        boolean add = e.isShiftDown();
        if (!add && !subtract) state.selection.clear();      // no modifier: replace
        state.selection.selectRect(selFace, state.block.n, selX0, selY0, selX1, selY1, !subtract);
        selecting = false;
        state.fireChanged();                                  // repaints and refreshes the status bar
    }

    private static int edge(int o, int len, int n, int i) {
        return o + (int) Math.round(i * (double) len / n);
    }

    // ---- dibujo ----
    @Override protected void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr.create();
        int W = getWidth(), H = getHeight();
        g.setColor(new Color(0x3C3F41));
        g.fillRect(0, 0, W, H);
        if (needFit && W > 0) { fit(); needFit = false; }

        BlockModel b = state.block;
        int n = b.n;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        Shape oldClip = g.getClip();
        Rectangle viewRect = new Rectangle(0, 0, W, H);
        int[][] rects = new int[Face.COUNT][];

        for (int f = 0; f < Face.COUNT; f++) {
            int x0 = (int) Math.round(panX + Face.NET_COL[f] * n * zoom);
            int x1 = (int) Math.round(panX + (Face.NET_COL[f] + 1) * n * zoom);
            int y0 = (int) Math.round(panY + Face.NET_ROW[f] * n * zoom);
            int y1 = (int) Math.round(panY + (Face.NET_ROW[f] + 1) * n * zoom);
            rects[f] = new int[]{x0, y0, x1, y1};
            int fw = x1 - x0, fh = y1 - y0;
            if (fw <= 0 || fh <= 0) continue;
            Rectangle vis = new Rectangle(x0, y0, fw, fh).intersection(viewRect);
            if (vis.isEmpty()) continue;
            g.setClip(vis);

            // tablero de transparencia, alineado a la cara
            int cx0 = x0 + Math.floorDiv(vis.x - x0, 8) * 8, cy0 = y0 + Math.floorDiv(vis.y - y0, 8) * 8;
            for (int yy = cy0; yy < vis.y + vis.height; yy += 8)
                for (int xx = cx0; xx < vis.x + vis.width; xx += 8) {
                    g.setColor(((xx - x0) / 8 + (yy - y0) / 8) % 2 == 0 ? new Color(0xCCCCCC) : new Color(0x999999));
                    g.fillRect(xx, yy, 8, 8);
                }

            g.drawImage(b.image(f), x0, y0, fw, fh, null);

            if (zoom >= 8) {   // cuadrícula de texels
                g.setColor(new Color(0, 0, 0, 60));
                for (int i = 0; i <= n; i++) {
                    int xx = edge(x0, fw, n, i), yy = edge(y0, fh, n, i);
                    if (xx >= vis.x && xx <= vis.x + vis.width) g.drawLine(xx, vis.y, xx, vis.y + vis.height);
                    if (yy >= vis.y && yy <= vis.y + vis.height) g.drawLine(vis.x, yy, vis.x + vis.width, yy);
                }
            }

            if (state.selection.hasAny()) {
                int base = f * n * n;
                for (int ty = 0; ty < n; ty++)
                    for (int tx = 0; tx < n; tx++) {
                        if (!state.selection.isSelected(base + ty * n + tx)) continue;
                        int px0 = edge(x0, fw, n, tx), px1 = edge(x0, fw, n, tx + 1);
                        int py0 = edge(y0, fh, n, ty), py1 = edge(y0, fh, n, ty + 1);
                        g.setColor(new Color(60, 120, 255, 90));
                        g.fillRect(px0, py0, px1 - px0, py1 - py0);
                        // outline only where the neighbour is not selected
                        g.setColor(Color.WHITE);
                        if (ty == 0 || !state.selection.isSelected(base + (ty - 1) * n + tx)) g.drawLine(px0, py0, px1 - 1, py0);
                        if (ty == n - 1 || !state.selection.isSelected(base + (ty + 1) * n + tx)) g.drawLine(px0, py1 - 1, px1 - 1, py1 - 1);
                        if (tx == 0 || !state.selection.isSelected(base + ty * n + tx - 1)) g.drawLine(px0, py0, px0, py1 - 1);
                        if (tx == n - 1 || !state.selection.isSelected(base + ty * n + tx + 1)) g.drawLine(px1 - 1, py0, px1 - 1, py1 - 1);
                    }
            }

        }
        g.setClip(oldClip);

        g.setFont(getFont().deriveFont(11f));
        for (int f = 0; f < Face.COUNT; f++) {
            int[] r = rects[f];
            g.setColor(new Color(0, 0, 0, 150));
            g.drawRect(r[0], r[1], r[2] - r[0], r[3] - r[1]);
            if (r[2] - r[0] >= 60) {
                g.setColor(new Color(0, 0, 0, 160));
                g.drawString(Face.SHORT[f], r[0] + 5, r[1] + 15);
                g.setColor(new Color(255, 255, 255, 200));
                g.drawString(Face.SHORT[f], r[0] + 4, r[1] + 14);
            }
        }
        int[] r = rects[state.currentFace];
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(0xFFD33D));
        g.drawRect(r[0], r[1], r[2] - r[0], r[3] - r[1]);

        if (selecting) {
            int[] fr = rects[selFace];
            int fw = fr[2] - fr[0], fh = fr[3] - fr[1];
            int xa = Math.min(selX0, selX1), xb = Math.max(selX0, selX1);
            int ya = Math.min(selY0, selY1), yb = Math.max(selY0, selY1);
            int rx0 = edge(fr[0], fw, n, xa), rx1 = edge(fr[0], fw, n, xb + 1);
            int ry0 = edge(fr[1], fh, n, ya), ry1 = edge(fr[1], fh, n, yb + 1);
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
            g.setColor(Color.WHITE);
            g.drawRect(rx0, ry0, rx1 - rx0, ry1 - ry0);
            g.setStroke(new BasicStroke(1f));
        }

        g.setColor(new Color(255, 255, 255, 120));
        g.drawString("Rueda: zoom   Clic central o Alt+arrastrar: mover", 10, H - 10);
        g.dispose();
    }
}