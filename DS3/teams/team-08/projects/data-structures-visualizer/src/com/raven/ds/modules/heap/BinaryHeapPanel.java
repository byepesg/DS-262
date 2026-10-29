package com.raven.ds.modules.heap;

import com.raven.ds.core.AnimationEngine;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Visual panel for Binary Heap operations
 */
public class BinaryHeapPanel extends JPanel {
    private BinaryHeapAlgorithm heap;
    private AnimationEngine animationEngine;
    
    private Font titleFont = new Font("SansSerif", Font.BOLD, 16);
    private Font infoFont = new Font("SansSerif", Font.PLAIN, 12);
    
    public BinaryHeapPanel(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.heap = new BinaryHeapAlgorithm(animationEngine, true); // Start with max heap
        
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createTitledBorder("Binary Heap Visualization"));
    }
    private void drawTitle(Graphics2D g2d) {
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(titleFont);
        String title = (heap.isMaxHeap() ? "Max" : "Min") + " Heap - Tree View";
        g2d.drawString(title, 20, 30);
    }
    
    private void drawHeap(Graphics2D g2d) {
        List<HeapNode> nodes = heap.getNodes();
        if (nodes.isEmpty()) return;
        
        int startX = getWidth() / 2;
        int startY = 80;
        int levelHeight = 60;
        
        // Calculate positions for all nodes
        for (int i = 0; i < nodes.size(); i++) {
            int level = (int) (Math.log(i + 1) / Math.log(2));
            int positionInLevel = i - (int) (Math.pow(2, level) - 1);
            int totalNodesInLevel = (int) Math.pow(2, level);
            
            int x = startX + (positionInLevel * 2 - totalNodesInLevel + 1) * (80 / (level + 1));
            int y = startY + level * levelHeight;
            
            nodes.get(i).setPosition(x, y);
        }
        
        // Draw edges first
        for (int i = 0; i < nodes.size(); i++) {
            int leftChild = 2 * i + 1;
            int rightChild = 2 * i + 2;
            
            HeapNode parent = nodes.get(i);
            
            if (leftChild < nodes.size()) {
                HeapNode left = nodes.get(leftChild);
                drawEdge(g2d, parent, left);
            }
            
            if (rightChild < nodes.size()) {
                HeapNode right = nodes.get(rightChild);
                drawEdge(g2d, parent, right);
            }
        }
        
        // Draw nodes
        for (HeapNode node : nodes) {
            node.draw(g2d);
        }
    }
    
    private void drawEdge(Graphics2D g2d, HeapNode parent, HeapNode child) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(parent.getX(), parent.getY(), child.getX(), child.getY());
    }
    
    private void drawArrayRepresentation(Graphics2D g2d) {
        List<HeapNode> nodes = heap.getNodes();
        if (nodes.isEmpty()) return;
        
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString("Array Representation:", 20, 350);
        
        int startX = 20;
        int startY = 370;
        int cellWidth = 40;
        int cellHeight = 30;
        
        for (int i = 0; i < Math.min(nodes.size(), 15); i++) { // Show max 15 elements
            int x = startX + i * cellWidth;
            
            // Draw cell background
            g2d.setColor(nodes.get(i).isHighlighted() ? 
                         HeapNode.HIGHLIGHTED_COLOR : 
                         nodes.get(i).isBeingSwapped() ? 
                         HeapNode.SWAP_COLOR : 
                         Color.LIGHT_GRAY);
            g2d.fillRect(x, startY, cellWidth, cellHeight);
            
            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, startY, cellWidth, cellHeight);
            
            // Draw value
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            String value = String.valueOf(nodes.get(i).getValue());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(value);
            g2d.drawString(value, x + (cellWidth - textWidth) / 2, startY + 20);
            
            // Draw index
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.drawString(String.valueOf(i), x + 2, startY + cellHeight + 12);
        }
        
        if (nodes.size() > 15) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2d.drawString("... (" + (nodes.size() - 15) + " more)", startX + 15 * cellWidth + 10, startY + 20);
        }
    }
    
    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(infoFont);
        
        String[] instructions = {
            "• Insert: Add new element and maintain heap property",
            "• Extract: Remove root and restore heap property",
            "• Build: Create heap from array of values",
            "• Toggle between Min and Max heap modes"
        };
        
        int instrY = 450;
        for (int i = 0; i < instructions.length; i++) {
            g2d.drawString(instructions[i], 20, instrY + i * 20);
        }
        
        // Draw heap property explanation
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        String property = heap.isMaxHeap() ? 
            "Max Heap: Parent ≥ Children" : 
            "Min Heap: Parent ≤ Children";
        g2d.drawString("Heap Property: " + property, 20, 540);
    }
    
    public void insert(int value) {
        heap.insert(value);
    }
    
    public void extractRoot() {
        heap.extractRoot();
    }
    
    public void buildHeap(int[] values) {
        heap.buildHeap(values);
    }
    
    public void clear() {
        heap.clear();
        repaint();
    }
    
    public void loadSample() {
        heap.loadSampleData();
        repaint();
    }
    
    public void setMaxHeap(boolean isMaxHeap) {
        heap.setMaxHeap(isMaxHeap);
        repaint();
    }
    
    public BinaryHeapAlgorithm getHeap() {
        return heap;
    }
}
