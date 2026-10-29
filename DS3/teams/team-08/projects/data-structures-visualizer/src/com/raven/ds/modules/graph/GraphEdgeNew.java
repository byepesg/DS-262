package com.raven.ds.modules.graph;

import java.awt.*;

public class GraphEdgeNew {
    private GraphNode source;
    private GraphNode target;
    private int weight;
    private boolean highlighted;
    private Color color;
    
    public static final Color DEFAULT_COLOR = new Color(120, 120, 120);
    public static final Color HIGHLIGHTED_COLOR = new Color(33, 84, 124);
    public static final Color PATH_COLOR = new Color(255, 215, 0);
    
    public GraphEdgeNew(GraphNode source, GraphNode target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.highlighted = false;
        this.color = DEFAULT_COLOR;
    }
    
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(highlighted ? 3 : 2));
        
        int dx = target.getX() - source.getX();
        int dy = target.getY() - source.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance == 0) return;
        
        double unitX = dx / distance;
        double unitY = dy / distance;
        
        int startX = source.getX() + (int)(unitX * GraphNode.NODE_RADIUS);
        int startY = source.getY() + (int)(unitY * GraphNode.NODE_RADIUS);
        int endX = target.getX() - (int)(unitX * GraphNode.NODE_RADIUS);
        int endY = target.getY() - (int)(unitY * GraphNode.NODE_RADIUS);
        
        g2d.drawLine(startX, startY, endX, endY);
        
        drawArrowhead(g2d, startX, startY, endX, endY);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;
        
        midX += 10;
        midY -= 5;
        
        g2d.fillRect(midX - 8, midY - 8, 16, 16);
        g2d.setColor(Color.WHITE);
        String weightStr = String.valueOf(weight);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(weightStr);
        g2d.drawString(weightStr, midX - textWidth/2, midY + 4);
    }
    
    private void drawArrowhead(Graphics2D g2d, int startX, int startY, int endX, int endY) {
        double angle = Math.atan2(endY - startY, endX - startX);
        int arrowLength = 10;
        double arrowAngle = Math.PI / 6;
        
        int x1 = endX - (int)(arrowLength * Math.cos(angle - arrowAngle));
        int y1 = endY - (int)(arrowLength * Math.sin(angle - arrowAngle));
        int x2 = endX - (int)(arrowLength * Math.cos(angle + arrowAngle));
        int y2 = endY - (int)(arrowLength * Math.sin(angle + arrowAngle));
        
        g2d.fillPolygon(new int[]{endX, x1, x2}, new int[]{endY, y1, y2}, 3);
    }
    
    public GraphNode getSource() { return source; }
    public GraphNode getTarget() { return target; }
    public int getWeight() { return weight; }
    
    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean highlighted) { 
        this.highlighted = highlighted;
        color = highlighted ? HIGHLIGHTED_COLOR : DEFAULT_COLOR;
    }
    
    public void setPathColor() {
        this.color = PATH_COLOR;
    }
    
    public void reset() {
        highlighted = false;
        color = DEFAULT_COLOR;
    }
}
