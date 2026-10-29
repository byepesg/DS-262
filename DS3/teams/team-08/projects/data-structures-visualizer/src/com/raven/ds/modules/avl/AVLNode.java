package com.raven.ds.modules.avl;

import java.awt.*;

public class AVLNode {
    public int value;
    public int height;
    public AVLNode left;
    public AVLNode right;
    
    // Visualization properties
    public int x, y;
    public boolean highlighted;
    public Color color;
    
    public static final int NODE_RADIUS = 20;
    public static final Color DEFAULT_COLOR = new Color(52, 152, 219); // Blue
    public static final Color HIGHLIGHTED_COLOR = new Color(231, 76, 60); // Red
    
    public AVLNode(int value) {
        this.value = value;
        this.height = 1;
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
        
        // Draw height (optional, maybe small text next to node)
        /*
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.drawString("h:" + height, x + NODE_RADIUS + 2, y);
        */
    }
    
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        this.color = highlighted ? HIGHLIGHTED_COLOR : DEFAULT_COLOR;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
}
