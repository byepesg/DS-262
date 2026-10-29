package com.raven.ds.modules.graph;

import com.raven.ds.core.AnimationEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Visual panel for Dijkstra's algorithm visualization
 */
public class GraphPanel extends JPanel {
    private DijkstraAlgorithm dijkstraAlgorithm;
    private BellmanFordAlgorithm bellmanFordAlgorithm;
    private AnimationEngine animationEngine;
    private GraphNode draggedNode;
    private GraphNode selectedNode;
    private boolean isAddingEdge;
    private GraphNode edgeStartNode;
    private String currentAlgorithm = "DIJKSTRA"; // "DIJKSTRA" or "BELLMAN_FORD"
    
    private Color backgroundColor = Color.WHITE;
    private Font infoFont = new Font("SansSerif", Font.PLAIN, 12);
    
    public GraphPanel(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.dijkstraAlgorithm = new DijkstraAlgorithm(animationEngine);
        this.bellmanFordAlgorithm = new BellmanFordAlgorithm(animationEngine);
        
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createTitledBorder("Graph Shortest Path Visualization"));
        
        setupMouseListeners();
    }
    
    
    private GraphNode getNodeAt(int x, int y) {
        for (GraphNode node : dijkstraAlgorithm.getNodes()) {
            if (node.contains(x, y)) {
                return node;
            }
        }
        return null;
    }
    
    public void addNode(GraphNode node) {
        dijkstraAlgorithm.addNode(node);
        bellmanFordAlgorithm.setNodes(dijkstraAlgorithm.getNodes());
    }
    
    private void removeNode(GraphNode node) {
        dijkstraAlgorithm.removeNode(node);
        bellmanFordAlgorithm.setNodes(dijkstraAlgorithm.getNodes());
    }
    
    public void addEdge(GraphNode source, GraphNode target, int weight) {
        dijkstraAlgorithm.addEdge(source, target, weight);
        bellmanFordAlgorithm.setNodes(dijkstraAlgorithm.getNodes());
    }
    
    public boolean hasEdge(GraphNode source, GraphNode target) {
        for (GraphEdge edge : source.getEdges()) {
            if (edge.getTarget() == target) {
                return true;
            }
        }
        return false;
    }
    
    private void setStartNode(GraphNode node) {
        // Clear previous start node
        for (GraphNode n : getNodes()) {
            if (n != node && isStartNode(n)) {
                n.updateColor();
            }
        }
        dijkstraAlgorithm.setStartNode(node);
        bellmanFordAlgorithm.setStartNode(node);
        node.setAsStartNode();
    }
    
    private void setEndNode(GraphNode node) {
        // Clear previous end node
        for (GraphNode n : getNodes()) {
            if (n != node && isEndNode(n)) {
                n.updateColor();
            }
        }
        dijkstraAlgorithm.setEndNode(node);
        bellmanFordAlgorithm.setEndNode(node);
        node.setAsEndNode();
    }
    
    private boolean isStartNode(GraphNode node) {
        return dijkstraAlgorithm.getStartNode() == node;
    }
    
    private boolean isEndNode(GraphNode node) {
        return dijkstraAlgorithm.getEndNode() == node;
    }
    
    private GraphNode getStartNode() {
        return dijkstraAlgorithm.getStartNode();
    }
    
    private GraphNode getEndNode() {
        return dijkstraAlgorithm.getEndNode();
    }
    
    private List<GraphNode> getNodes() {
        return dijkstraAlgorithm.getNodes();
    }
    
    public void setCurrentAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
        setBorder(BorderFactory.createTitledBorder(
            algorithm.equals("DIJKSTRA") ? 
            "Dijkstra's Shortest Path Visualization" : 
            "Bellman-Ford Shortest Path Visualization"));
    }
    
    public String getCurrentAlgorithm() {
        return currentAlgorithm;
    }
    
    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GraphNode clickedNode = getNodeAt(e.getX(), e.getY());
                
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isAddingEdge) {
                        if (clickedNode != null) {
                            if (edgeStartNode == null) {
                                edgeStartNode = clickedNode;
                                clickedNode.setHighlighted(true);
                            } else if (clickedNode != edgeStartNode) {
                                // Add edge dialog
                                String weightStr = JOptionPane.showInputDialog(
                                    GraphPanel.this,
                                    "Enter edge weight:",
                                    "Add Edge",
                                    JOptionPane.QUESTION_MESSAGE
                                );
                                
                                if (weightStr != null && !weightStr.trim().isEmpty()) {
                                    try {
                                        int weight = Integer.parseInt(weightStr.trim());
                                        addEdge(edgeStartNode, clickedNode, weight);
                                        repaint();
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(
                                            GraphPanel.this,
                                            "Invalid weight. Please enter a number.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                        );
                                    }
                                }
                                
                                edgeStartNode.setHighlighted(false);
                                edgeStartNode = null;
                                isAddingEdge = false;
                            }
                        }
                    } else {
                        draggedNode = clickedNode;
                        selectedNode = clickedNode;
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (clickedNode != null) {
                        showNodeContextMenu(clickedNode, e.getX(), e.getY());
                    } else {
                        showBackgroundContextMenu(e.getX(), e.getY());
                    }
                }
                
                repaint();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null && !isAddingEdge) {
                    draggedNode.setPosition(e.getX(), e.getY());
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }
        };
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
    
    private void showNodeContextMenu(GraphNode node, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem setStart = new JMenuItem("Set as Start Node");
        setStart.addActionListener(e -> {
            setStartNode(node);
            repaint();
        });
        
        JMenuItem setEnd = new JMenuItem("Set as End Node");
        setEnd.addActionListener(e -> {
            setEndNode(node);
            repaint();
        });
        
        JMenuItem delete = new JMenuItem("Delete Node");
        delete.addActionListener(e -> {
            removeNode(node);
            repaint();
        });
        
        menu.add(setStart);
        menu.add(setEnd);
        menu.addSeparator();
        menu.add(delete);
        
        menu.show(this, x, y);
    }
    
    private void showBackgroundContextMenu(int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem addNode = new JMenuItem("Add Node");
        addNode.addActionListener(e -> {
            String nodeId = JOptionPane.showInputDialog(
                this,
                "Enter node ID:",
                "Add Node",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (nodeId != null && !nodeId.trim().isEmpty()) {
                GraphNode newNode = new GraphNode(nodeId.trim(), x, y);
                addNode(newNode);
                repaint();
            }
        });
        
        menu.add(addNode);
        menu.show(this, x, y);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw edges first (so they appear behind nodes)
        for (GraphNode node : getNodes()) {
            for (GraphEdge edge : node.getEdges()) {
                edge.draw(g2d);
            }
        }
        
        // Draw nodes
        for (GraphNode node : getNodes()) {
            node.draw(g2d);
        }
        
        // Draw start/end node indicators
        drawNodeIndicators(g2d);
        
        // Draw instructions
        drawInstructions(g2d);
    }
    
    private void drawNodeIndicators(Graphics2D g2d) {
        if (getStartNode() != null) {
            GraphNode start = getStartNode();
            g2d.setColor(new Color(46, 204, 113));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            // Enhanced start node label with background
            String startText = "START";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(startText);
            
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(start.getX() - textWidth/2 - 3, start.getY() + GraphNode.NODE_RADIUS + 8, textWidth + 6, 16, 4, 4);
            g2d.setColor(new Color(46, 204, 113));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(start.getX() - textWidth/2 - 3, start.getY() + GraphNode.NODE_RADIUS + 8, textWidth + 6, 16, 4, 4);
            g2d.drawString(startText, start.getX() - textWidth/2, start.getY() + GraphNode.NODE_RADIUS + 20);
        }
        
        if (getEndNode() != null) {
            GraphNode end = getEndNode();
            g2d.setColor(new Color(231, 76, 60));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            // Enhanced end node label with background
            String endText = "END";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(endText);
            
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(end.getX() - textWidth/2 - 3, end.getY() + GraphNode.NODE_RADIUS + 8, textWidth + 6, 16, 4, 4);
            g2d.setColor(new Color(231, 76, 60));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(end.getX() - textWidth/2 - 3, end.getY() + GraphNode.NODE_RADIUS + 8, textWidth + 6, 16, 4, 4);
            g2d.drawString(endText, end.getX() - textWidth/2, end.getY() + GraphNode.NODE_RADIUS + 20);
        }
    }
    
    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
        
        String[] instructions = {
            "🖱️ Right-click: Node context menu (set start/end, delete)",
            "🖱️ Left-click: Select and drag nodes around",
            "🎮 Use controls below to run Dijkstra or Bellman-Ford algorithms",
            "📊 Watch the step-by-step algorithm animation with explanations"
        };
        
        // Enhanced instruction panel with background
        int maxWidth = 0;
        FontMetrics fm = g2d.getFontMetrics();
        for (String instruction : instructions) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(instruction));
        }
        
        int panelHeight = instructions.length * 18 + 20;
        int panelY = getHeight() - panelHeight - 10;
        
        // Draw instruction panel background
        g2d.setColor(new Color(248, 249, 250, 230));
        g2d.fillRoundRect(10, panelY, maxWidth + 20, panelHeight, 10, 10);
        
        g2d.setColor(new Color(52, 73, 94));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(10, panelY, maxWidth + 20, panelHeight, 10, 10);
        
        // Draw instructions
        for (int i = 0; i < instructions.length; i++) {
            g2d.drawString(instructions[i], 20, panelY + 25 + (i * 18));
        }
    }
    
    public void startAddingEdge() {
        isAddingEdge = true;
        edgeStartNode = null;
        // Clear any existing highlights
        for (GraphNode node : getNodes()) {
            node.setHighlighted(false);
        }
        repaint();
    }
    
    public void cancelAddingEdge() {
        isAddingEdge = false;
        if (edgeStartNode != null) {
            edgeStartNode.setHighlighted(false);
            edgeStartNode = null;
        }
        repaint();
    }
    
    public void loadSampleGraph() {
        dijkstraAlgorithm.loadSampleGraph();
        bellmanFordAlgorithm.setNodes(dijkstraAlgorithm.getNodes());
        repaint();
    }
    
    public void clearGraph() {
        dijkstraAlgorithm.reset();
        dijkstraAlgorithm.getNodes().clear();
        bellmanFordAlgorithm.setNodes(dijkstraAlgorithm.getNodes());
        repaint();
    }
    
    public void runDijkstra() {
        if (getStartNode() == null) {
            JOptionPane.showMessageDialog(this, 
                "Please set a start node first (right-click on a node)", 
                "No Start Node", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            dijkstraAlgorithm.runDijkstra();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error running algorithm: " + e.getMessage(), 
                "Algorithm Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void runBellmanFord() {
        if (getStartNode() == null) {
            JOptionPane.showMessageDialog(this, 
                "Please set a start node first (right-click on a node)", 
                "No Start Node", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            bellmanFordAlgorithm.setNodes(dijkstraAlgorithm.getNodes());
            bellmanFordAlgorithm.runBellmanFord();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error running algorithm: " + e.getMessage(), 
                "Algorithm Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public DijkstraAlgorithm getDijkstraAlgorithm() {
        return dijkstraAlgorithm;
    }
    
    public BellmanFordAlgorithm getBellmanFordAlgorithm() {
        return bellmanFordAlgorithm;
    }
}
