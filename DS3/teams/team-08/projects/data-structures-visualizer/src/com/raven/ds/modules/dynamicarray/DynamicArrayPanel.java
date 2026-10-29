package com.raven.ds.modules.dynamicarray;

import com.raven.ds.core.AnimationEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Visual panel for Dynamic Array operations
 */
public class DynamicArrayPanel extends JPanel {
    private DynamicArrayAlgorithm dynamicArray;
    private AnimationEngine animationEngine;
    
    private Font titleFont = new Font("SansSerif", Font.BOLD, 16);
    
    public DynamicArrayPanel(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.dynamicArray = new DynamicArrayAlgorithm(animationEngine);
        
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createTitledBorder("Dynamic Array Visualization"));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawTitle(g2d);
        drawArray(g2d);
        drawStats(g2d);
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
    
    private void drawTitle(Graphics2D g2d) {
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(titleFont);
        g2d.drawString("Dynamic Array - Resizing Visualization", 20, 40);
    }
    
    private void drawArray(Graphics2D g2d) {
        int startX = 50;
        int startY = 100;
        int cellWidth = 50;
        int cellHeight = 50;
        
        if (dynamicArray.isResizing()) {
            // Draw old array
            g2d.drawString("Old Array (Full):", startX, startY - 10);
            Integer[] oldArray = dynamicArray.getOldArray();
            if (oldArray != null) {
                for (int i = 0; i < oldArray.length; i++) {
                    drawCell(g2d, startX + i * cellWidth, startY, cellWidth, cellHeight, oldArray[i], i, false);
                }
            }
            
            // Draw new array below
            startY += 150;
            g2d.drawString("New Array (Copying...):", startX, startY - 10);
        }
        
        // Draw current array
        Integer[] array = dynamicArray.getArray();
        for (int i = 0; i < dynamicArray.getCapacity(); i++) {
            Integer value = array[i];
            boolean isHighlighted = (i == dynamicArray.getHighlightedIndex());
            drawCell(g2d, startX + i * cellWidth, startY, cellWidth, cellHeight, value, i, isHighlighted);
        }
    }
    
    private void drawCell(Graphics2D g2d, int x, int y, int w, int h, Integer value, int index, boolean highlighted) {
        // Fill
        if (highlighted) {
            g2d.setColor(new Color(231, 76, 60)); // Red
        } else if (value != null) {
            g2d.setColor(new Color(52, 152, 219)); // Blue
        } else {
            g2d.setColor(new Color(236, 240, 241)); // Light Gray
        }
        g2d.fillRect(x, y, w, h);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, w, h);
        
        // Value
        if (value != null) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            String s = String.valueOf(value);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(s, x + (w - fm.stringWidth(s))/2, y + (h + fm.getAscent())/2 - 2);
        }
        
        // Index
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.drawString(String.valueOf(index), x + 2, y + h + 12);
    }
    
    private void drawStats(Graphics2D g2d) {
        int x = 50;
        int y = 400;
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        g2d.drawString("Size: " + dynamicArray.getSize(), x, y);
        g2d.drawString("Capacity: " + dynamicArray.getCapacity(), x, y + 20);
        
        if (dynamicArray.isResizing()) {
            g2d.setColor(new Color(231, 76, 60));
            g2d.drawString("Status: RESIZING...", x, y + 50);
        }
    }
    
    public void add(int value) {
        dynamicArray.add(value);
        repaint();
    }
    
    public void remove(int index) {
        dynamicArray.remove(index);
        repaint();
    }
    
    public void clear() {
        dynamicArray.clear();
        repaint();
    }
    
    public void loadSample() {
        dynamicArray.loadSample();
        repaint();
    }
    
    public int getArraySize() {
        return dynamicArray.getSize();
    }
}
