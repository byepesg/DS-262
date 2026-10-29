package com.raven.ds.modules.avl;

import com.raven.ds.core.AnimationEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Visual panel for AVL Tree operations
 */
public class AVLTreePanel extends JPanel {
    private AVLTreeAlgorithm avl;
    private AnimationEngine animationEngine;
    
    private Font nodeFont = new Font("SansSerif", Font.BOLD, 14);
    
    public AVLTreePanel(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.avl = new AVLTreeAlgorithm(animationEngine);
        
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createTitledBorder("AVL Tree Visualization"));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (avl.getRoot() != null) {
            drawTree(g2d, avl.getRoot(), getWidth() / 2, 50, getWidth() / 4);
        } else {
            g2d.drawString("Tree is empty", getWidth() / 2 - 40, getHeight() / 2);
        }
        
        drawStepExplanation(g2d);
    }
    
    private void drawStepExplanation(Graphics2D g2d) {
        String text = animationEngine.getCurrentStepDescription();
        if (text == null || text.isEmpty()) return;
        
        // Use a JLabel to render HTML
        JLabel renderer = new JLabel("<html><body style='width: 280px'>" + text + "</body></html>");
        renderer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        renderer.setForeground(new Color(52, 73, 94));
        renderer.setVerticalAlignment(SwingConstants.TOP);
        
        Dimension size = renderer.getPreferredSize();
        int x = getWidth() - size.width - 20;
        int y = 20;
        
        // Draw background
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(x - 10, y - 10, size.width + 20, size.height + 20, 10, 10);
        g2d.setColor(new Color(52, 73, 94));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x - 10, y - 10, size.width + 20, size.height + 20, 10, 10);
        
        // Translate and draw
        g2d.translate(x, y);
        renderer.setSize(size);
        renderer.paint(g2d);
        g2d.translate(-x, -y);
    }
    
    private void drawTree(Graphics2D g2d, AVLNode node, int x, int y, int xOffset) {
        if (node == null) return;
        
        node.x = x;
        node.y = y;
        
        // Draw edges
        if (node.left != null) {
            int childX = x - xOffset;
            int childY = y + 60;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y, childX, childY);
            drawTree(g2d, node.left, childX, childY, xOffset / 2);
        }
        
        if (node.right != null) {
            int childX = x + xOffset;
            int childY = y + 60;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y, childX, childY);
            drawTree(g2d, node.right, childX, childY, xOffset / 2);
        }
        
        // Draw node
        node.draw(g2d);
    }
    
    public void insert(int value) {
        avl.insert(value);
        repaint();
    }
    
    public void delete(int value) {
        avl.delete(value);
        repaint();
    }
    
    public void clear() {
        avl.clear();
        repaint();
    }
    
    public void loadSample() {
        avl.loadSample();
        repaint();
    }
}
