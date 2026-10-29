package com.raven.ds.modules.graph;

import java.awt.*;

public class GraphEdge {
    private GraphNode source;
    private GraphNode target;
    private int weight;
    private boolean highlighted;
    private Color color;
    
    // Enhanced color scheme for better contrast and visibility
    public static final Color DEFAULT_COLOR = new Color(100, 100, 100);       // Medium gray for default
    public static final Color HIGHLIGHTED_COLOR = new Color(255, 69, 0);      // Red-orange for highlighting
    public static final Color PATH_COLOR = new Color(50, 205, 50);            // Lime green for final path
    public static final Color RELAXING_COLOR = new Color(255, 215, 0);        // Gold for edge being relaxed
    // Additional colors for enhanced exploration animation
    public static final Color EXPLORING_COLOR = new Color(200, 200, 255);     // Light blue for exploration
    public static final Color IMPROVEMENT_COLOR = new Color(50, 205, 50);     // Green for improved path
    public static final Color NO_IMPROVEMENT_COLOR = new Color(255, 150, 150); // Light red for rejected path
    
    public GraphEdge(GraphNode source, GraphNode target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.highlighted = false;
        this.color = DEFAULT_COLOR;
    }
    
    public void draw(Graphics2D g2d) {
        // Enhanced stroke width for better visibility during animation
        g2d.setStroke(new BasicStroke(highlighted ? 8 : 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
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
        
        // Draw shadow for highlighted edges
        if (highlighted) {
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(startX + 3, startY + 3, endX + 3, endY + 3);
        }
        
        // Draw main edge line
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(highlighted ? 8 : 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(startX, startY, endX, endY);
        
        drawArrowhead(g2d, startX, startY, endX, endY);
        
        // Enhanced weight label with better background contrast
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;
        
        // Offset label to avoid overlapping with edge
        midX += 12;
        midY -= 8;
        
        String weightStr = String.valueOf(weight);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(weightStr);
        int textHeight = fm.getHeight();
        
        // Enhanced background with shadow and border
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(midX - textWidth/2 - 5 + 1, midY - textHeight/2 - 3 + 1, textWidth + 10, textHeight + 6, 8, 8);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(midX - textWidth/2 - 5, midY - textHeight/2 - 3, textWidth + 10, textHeight + 6, 8, 8);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(midX - textWidth/2 - 5, midY - textHeight/2 - 3, textWidth + 10, textHeight + 6, 8, 8);
        
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
    
    public void setColor(Color newColor) {
        this.color = newColor;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setPathColor() {
        this.color = PATH_COLOR;
    }
    
    public void reset() {
        highlighted = false;
        color = DEFAULT_COLOR;
    }
}
