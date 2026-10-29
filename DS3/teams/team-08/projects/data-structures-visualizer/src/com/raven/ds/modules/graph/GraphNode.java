package com.raven.ds.modules.graph;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the graph for Dijkstra's algorithm visualization
 */
public class GraphNode {
    private String id;
    private int x, y;
    private int distance;
    private GraphNode predecessor;
    private boolean visited;
    private boolean highlighted;
    private boolean inPath;  // Added to track if node is part of the shortest path
    private Color color;
    private List<GraphEdge> edges;
    
    public static final int NODE_RADIUS = 25;
    // Enhanced high contrast color scheme for better visibility during animation
    public static final Color DEFAULT_COLOR = new Color(240, 240, 240);      // Light gray for unprocessed
    public static final Color VISITED_COLOR = new Color(100, 149, 237);      // Cornflower blue for visited
    public static final Color CURRENT_COLOR = new Color(255, 165, 0);        // Orange for current processing
    public static final Color PATH_COLOR = new Color(50, 205, 50);           // Lime green for final path
    public static final Color START_COLOR = new Color(34, 139, 34);          // Forest green for start
    public static final Color END_COLOR = new Color(220, 20, 60);            // Crimson red for end
    public static final Color RELAXING_COLOR = new Color(255, 255, 0);       // Bright yellow for edge relaxation
    // Additional colors for enhanced neighbor exploration animation
    public static final Color EXPLORING_COLOR = new Color(255, 255, 200);    // Light yellow for exploration
    public static final Color IMPROVEMENT_COLOR = new Color(144, 238, 144);  // Light green for improvement
    public static final Color NO_IMPROVEMENT_COLOR = new Color(255, 200, 200); // Light red for no improvement
    
    public GraphNode(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.distance = Integer.MAX_VALUE;
        this.predecessor = null;
        this.visited = false;
        this.highlighted = false;
        this.inPath = false;
        this.color = DEFAULT_COLOR;
        this.edges = new ArrayList<>();
    }
    
    public void addEdge(GraphNode target, int weight) {
        edges.add(new GraphEdge(this, target, weight));
    }
    
    public void draw(Graphics2D g2d) {
        // Draw enhanced shadow for better depth perception
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(x - NODE_RADIUS + 4, y - NODE_RADIUS + 4, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Draw node circle with enhanced colors
        g2d.setColor(color);
        g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Draw border with high contrast - thicker for better visibility
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3)); // Black border for definition
        g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        // Add white inner highlight for extra contrast
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(x - NODE_RADIUS + 2, y - NODE_RADIUS + 2, NODE_RADIUS * 2 - 4, NODE_RADIUS * 2 - 4);
        
        // Draw node ID with enhanced contrast
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18)); // Larger, bolder font
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(id);
        int textHeight = fm.getHeight();
        
        // Add black outline for better readability
        g2d.setColor(Color.BLACK);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) {
                    g2d.drawString(id, x - textWidth/2 + dx, y + textHeight/4 + dy);
                }
            }
        }
        
        // Draw white text
        g2d.setColor(Color.WHITE);
        g2d.drawString(id, x - textWidth/2, y + textHeight/4);
        
        // Draw distance label with enhanced contrast background
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        String distText = distance == Integer.MAX_VALUE ? "∞" : String.valueOf(distance);
        FontMetrics distFm = g2d.getFontMetrics();
        int distWidth = distFm.stringWidth(distText);
        
        // Enhanced background for distance label with border
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x - distWidth/2 - 4, y - NODE_RADIUS - 20, distWidth + 8, 16, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x - distWidth/2 - 4, y - NODE_RADIUS - 20, distWidth + 8, 16, 6, 6);
        g2d.drawString(distText, x - distWidth/2, y - NODE_RADIUS - 7);
    }
    
    public boolean contains(int px, int py) {
        return Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) <= NODE_RADIUS;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
    
    public GraphNode getPredecessor() { return predecessor; }
    public void setPredecessor(GraphNode predecessor) { this.predecessor = predecessor; }
    
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { 
        this.visited = visited;
        updateColor();
    }
    
    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean highlighted) { 
        this.highlighted = highlighted;
        updateColor();
    }
    
    public boolean isInPath() { return inPath; }
    public void setInPath(boolean inPath) {
        this.inPath = inPath;
        updateColor();
    }
    
    public List<GraphEdge> getEdges() { return edges; }
    
    public void updateColor() {
        if (highlighted) {
            color = CURRENT_COLOR;
        } else if (inPath) {
            color = PATH_COLOR;
        } else if (visited) {
            color = VISITED_COLOR;
        } else {
            color = DEFAULT_COLOR;
        }
    }
    
    public void setColor(Color newColor) {
        this.color = newColor;
    }
    
    public void setAsStartNode() {
        color = START_COLOR;
    }
    
    public void setAsEndNode() {
        color = END_COLOR;
    }
    
    public void reset() {
        distance = Integer.MAX_VALUE;
        predecessor = null;
        visited = false;
        highlighted = false;
        inPath = false;
        color = DEFAULT_COLOR;
    }
}
