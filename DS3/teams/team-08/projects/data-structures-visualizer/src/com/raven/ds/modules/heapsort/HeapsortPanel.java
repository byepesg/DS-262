package com.raven.ds.modules.heapsort;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.modules.heap.HeapNode;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Visual panel for Heapsort operations
 */
public class HeapsortPanel extends JPanel {
    private HeapsortAlgorithm heapsort;
    private AnimationEngine animationEngine;
    
    private Font titleFont = new Font("SansSerif", Font.BOLD, 16);
    private Font infoFont = new Font("SansSerif", Font.PLAIN, 12);
    
    public HeapsortPanel(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.heapsort = new HeapsortAlgorithm(animationEngine);
        
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createTitledBorder("Heapsort Visualization"));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawTitle(g2d);
        drawHeap(g2d);
        drawArrayRepresentation(g2d);
        drawInstructions(g2d);
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
        g2d.drawString("Heapsort - Max Heap Visualization", 20, 40);
    }
    
    private void drawHeap(Graphics2D g2d) {
        List<HeapNode> nodes = heapsort.getNodes();
        if (nodes.isEmpty()) return;
        
        int heapSize = heapsort.getHeapSize();
        if (heapSize == 0) return; // Don't draw tree if heap is empty (all sorted)
        
        int startX = getWidth() / 2;
        int startY = 80;
        int levelHeight = 60;
        
        // Only draw nodes that are part of the active heap
        for (int i = 0; i < heapSize; i++) {
            int level = (int) (Math.log(i + 1) / Math.log(2));
            int positionInLevel = i - (int) (Math.pow(2, level) - 1);
            int totalNodesInLevel = (int) Math.pow(2, level);
            
            int x = startX + (positionInLevel * 2 - totalNodesInLevel + 1) * (80 / (level + 1));
            int y = startY + level * levelHeight;
            
            nodes.get(i).setPosition(x, y);
        }
        
        // Draw edges
        for (int i = 0; i < heapSize; i++) {
            int leftChild = 2 * i + 1;
            int rightChild = 2 * i + 2;
            
            HeapNode parent = nodes.get(i);
            
            if (leftChild < heapSize) {
                HeapNode left = nodes.get(leftChild);
                drawEdge(g2d, parent, left);
            }
            
            if (rightChild < heapSize) {
                HeapNode right = nodes.get(rightChild);
                drawEdge(g2d, parent, right);
            }
        }
        
        // Draw nodes
        for (int i = 0; i < heapSize; i++) {
            nodes.get(i).draw(g2d);
        }
    }
    
    private void drawEdge(Graphics2D g2d, HeapNode parent, HeapNode child) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(parent.getX(), parent.getY(), child.getX(), child.getY());
    }
    
    private void drawArrayRepresentation(Graphics2D g2d) {
        List<HeapNode> nodes = heapsort.getNodes();
        if (nodes.isEmpty()) return;
        
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString("Array Status:", 20, 350);
        
        int startX = 20;
        int startY = 370;
        int cellWidth = 40;
        int cellHeight = 30;
        
        for (int i = 0; i < Math.min(nodes.size(), 18); i++) {
            int x = startX + i * cellWidth;
            
            // Draw cell background
            HeapNode node = nodes.get(i);
            g2d.setColor(node.isSorted() ? HeapNode.SORTED_COLOR : 
                         node.isHighlighted() ? HeapNode.HIGHLIGHTED_COLOR : 
                         node.isBeingSwapped() ? HeapNode.SWAP_COLOR : 
                         Color.LIGHT_GRAY);
            
            // If it's part of the active heap (not sorted), maybe distinguish?
            // The isSorted flag handles it.
            
            g2d.fillRect(x, startY, cellWidth, cellHeight);
            
            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, startY, cellWidth, cellHeight);
            
            // Draw value
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            String value = String.valueOf(node.getValue());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(value);
            g2d.drawString(value, x + (cellWidth - textWidth) / 2, startY + 20);
            
            // Draw index
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.drawString(String.valueOf(i), x + 2, startY + cellHeight + 12);
        }
        
        // Legend
        int legendY = 420;
        drawLegendItem(g2d, "Active Heap", Color.LIGHT_GRAY, 20, legendY);
        drawLegendItem(g2d, "Sorted", HeapNode.SORTED_COLOR, 140, legendY);
        drawLegendItem(g2d, "Swapping", HeapNode.SWAP_COLOR, 240, legendY);
        drawLegendItem(g2d, "Comparing", HeapNode.HIGHLIGHTED_COLOR, 340, legendY);
    }
    
    private void drawLegendItem(Graphics2D g2d, String label, Color color, int x, int y) {
        g2d.setColor(color);
        g2d.fillRect(x, y, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, 20, 20);
        g2d.drawString(label, x + 25, y + 15);
    }
    
    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(infoFont);
        
        String[] instructions = {
            "1. Build Max Heap: Rearrange array so that parent ≥ children",
            "2. Swap Root: Move max element (root) to end of array",
            "3. Heapify Down: Restore heap property for remaining elements",
            "4. Repeat until array is sorted"
        };
        
        int instrY = 460;
        for (int i = 0; i < instructions.length; i++) {
            g2d.drawString(instructions[i], 20, instrY + i * 20);
        }
    }
    
    public void sort() {
        heapsort.sort();
    }
    
    public void loadData(int[] values) {
        heapsort.loadData(values);
        repaint();
    }
    
    public void loadSample() {
        int[] sampleData = {12, 11, 13, 5, 6, 7, 1, 9, 15, 4};
        loadData(sampleData);
    }
}
