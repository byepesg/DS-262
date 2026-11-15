package unal.ds.textureeditor.view;

import unal.ds.textureeditor.EditorState;
import unal.ds.textureeditor.model.Face;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

/** Ventana principal: barra superior, lienzo 2D y barra de estado. */
public class MainFrame extends JFrame {
    private final EditorState state;
    private final Canvas2DPanel canvas;
    private final JLabel status = new JLabel(" ");

    public MainFrame(EditorState state) {
        super("Minecraft Block Texture Editor");
        this.state = state;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        canvas = new Canvas2DPanel(state);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        top.add(button("Nuevo bloque...", e -> newBlock()));
        top.add(button("Color...", e -> chooseColor()));   // provisional, luego el selector OKLab
        top.add(button("Ajustar", e -> canvas.fit()));
        top.add(button("-", e -> canvas.zoomBy(1 / 1.25)));
        top.add(button("+", e -> canvas.zoomBy(1.25)));

        status.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        state.addListener(canvas::repaint);
        state.addListener(this::refreshStatus);
        refreshStatus();
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private static JButton button(String text, ActionListener l) {
        JButton b = new JButton(text);
        b.setMargin(new Insets(2, 8, 2, 8));
        b.addActionListener(l);
        return b;
    }

    private void refreshStatus() {
        status.setText(Face.NAMES[state.currentFace] + "   |   " + state.block.n + " x " + state.block.n
                + " px por cara   |   color #" + String.format("%06X", state.rgb & 0xFFFFFF));
    }

    private void newBlock() {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(state.block.n, 1, 512, 1));
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p.add(new JLabel("Resolución (N x N):"));
        p.add(sp);
        for (int r : new int[]{8, 16, 32, 64}) {
            JButton b = new JButton(String.valueOf(r));
            b.addActionListener(e -> sp.setValue(r));
            p.add(b);
        }
        if (JOptionPane.showConfirmDialog(this, p, "Nuevo bloque", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        state.newBlock((Integer) sp.getValue());
        canvas.fit();
    }

    private void chooseColor() {
        Color c = JColorChooser.showDialog(this, "Color", new Color(state.rgb));
        if (c != null) {
            state.rgb = c.getRGB() & 0xFFFFFF;
            refreshStatus();
        }
    }
}