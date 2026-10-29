package com.raven.ds.modules.heap;

import java.awt.*;

/**
 * Represents a node in the binary heap for visualization
 */
public class HeapNode {
    private int value;
    private int x, y;
    private boolean highlighted;
    private boolean isBeingSwapped;
    private boolean isSorted;
    private Color color;
    
    public static final int NODE_RADIUS = 20;
    public static final Color DEFAULT_COLOR = new Color(7, 164, 121);
    public static final Color HIGHLIGHTED_COLOR = new Color(231, 76, 60);
    public static final Color SWAP_COLOR = new Color(241, 196, 15);
    public static final Color PARENT_COLOR = new Color(52, 152, 219);
    public static final Color SORTED_COLOR = new Color(46, 204, 113); // Lighter green or different shade
    
    public HeapNode(int value) {
        this.value = value;
        this.highlighted = false;
        this.isBeingSwapped = false;
        this.isSorted = false;
        this.color = DEFAULT_COLOR;
    }
    
    public void draw(Graphics2D g2d) {
        // Draw node circle
        g2d.setColor(color);
        g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Draw value
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String valueStr = String.valueOf(value);
        int textWidth = fm.stringWidth(valueStr);
        int textHeight = fm.getHeight();
        g2d.drawString(valueStr, x - textWidth/2, y + textHeight/4);
    }
    
    public void updateColor() {
        if (isSorted) {
            color = SORTED_COLOR;
        } else if (isBeingSwapped) {
            color = SWAP_COLOR;
        } else if (highlighted) {
            color = HIGHLIGHTED_COLOR;
        } else {
            color = DEFAULT_COLOR;
        }
    }
    
    // Getters and setters
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { 
        this.x = x; 
        this.y = y; 
    }
    
    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean highlighted) { 
        this.highlighted = highlighted;
        updateColor();
    }
    
    public boolean isBeingSwapped() { return isBeingSwapped; }
    public void setBeingSwapped(boolean beingSwapped) { 
        this.isBeingSwapped = beingSwapped;
        updateColor();
    }
    
    public boolean isSorted() { return isSorted; }
    public void setSorted(boolean sorted) {
        this.isSorted = sorted;
        updateColor();
    }
    
    public void reset() {
        highlighted = false;
        isBeingSwapped = false;
        // Do not reset sorted state here usually, unless full reset
        updateColor();
    }
}
