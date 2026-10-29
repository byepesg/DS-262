package com.raven.ds.modules.hashtable;

import com.raven.ds.core.AnimationEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Visual panel for Hash Table operations
 */
public class HashTablePanel extends JPanel {
    private HashTableAlgorithm hashTable;
    private AnimationEngine animationEngine;
    
    private static final int BUCKET_WIDTH = 80;
    private static final int BUCKET_HEIGHT = 80;
    private static final int MARGIN = 10;
    private static final int COLUMNS = 8;
    
    private Font titleFont = new Font("SansSerif", Font.BOLD, 16);
    private Font infoFont = new Font("SansSerif", Font.PLAIN, 12);
    
    public HashTablePanel(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.hashTable = new HashTableAlgorithm(16, animationEngine);
        
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createTitledBorder("Hash Table Visualization"));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawTitle(g2d);
        drawHashTable(g2d);
        drawStatistics(g2d);
        drawHashFunction(g2d);
        drawInstructions(g2d);
    }
    
    private void drawTitle(Graphics2D g2d) {
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(titleFont);
        String title = "Hash Table - " + hashTable.getHashFunction().name().replace("_", " ");
        g2d.drawString(title, 20, 40);
    }
    
    private void drawHashTable(Graphics2D g2d) {
        int startY = 60;
        int bucketCount = hashTable.getCapacity();
        
        for (int i = 0; i < bucketCount; i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            
            int x = MARGIN + col * (BUCKET_WIDTH + 5);
            int y = startY + row * (BUCKET_HEIGHT + 5);
            
            hashTable.getBuckets()[i].draw(g2d, x, y, BUCKET_WIDTH, BUCKET_HEIGHT);
        }
    }
    
    private void drawStatistics(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(infoFont);
        
        int statsY = 350;
        
        g2d.drawString("Statistics:", 20, statsY);
        g2d.drawString("Capacity: " + hashTable.getCapacity(), 20, statsY + 20);
        g2d.drawString("Size: " + hashTable.getSize(), 20, statsY + 40);
        g2d.drawString("Load Factor: " + String.format("%.2f", hashTable.getLoadFactor()), 20, statsY + 60);
        g2d.drawString("Collisions: " + hashTable.getCollisionCount(), 20, statsY + 80);
        
        // Draw load factor bar
        drawLoadFactorBar(g2d, 150, statsY + 55);
    }
    
    private void drawLoadFactorBar(Graphics2D g2d, int x, int y) {
        int barWidth = 200;
        int barHeight = 15;
        
        // Background
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(x, y, barWidth, barHeight);
        
        // Load factor fill
        double loadFactor = hashTable.getLoadFactor();
        int fillWidth = (int) (barWidth * Math.min(loadFactor, 1.0));
        
        if (loadFactor < 0.5) {
            g2d.setColor(new Color(46, 204, 113)); // Green
        } else if (loadFactor < 0.75) {
            g2d.setColor(new Color(241, 196, 15)); // Yellow
        } else {
            g2d.setColor(new Color(231, 76, 60)); // Red
        }
        
        g2d.fillRect(x, y, fillWidth, barHeight);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, barWidth, barHeight);
        
        // Labels
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.drawString("0", x - 5, y + 25);
        g2d.drawString("0.5", x + barWidth/2 - 10, y + 25);
        g2d.drawString("1.0", x + barWidth - 10, y + 25);
    }
    
    private void drawHashFunction(Graphics2D g2d) {
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(infoFont);
        
        int funcY = 460;
        g2d.drawString("Hash Function:", 20, funcY);
        g2d.drawString(hashTable.getHashFunction().getDescription(), 20, funcY + 20);
        
        // Draw example
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.ITALIC, 11));
        g2d.drawString("Example: \"apple\" → hash(\"apple\") → bucket index", 20, funcY + 40);
    }
    
    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        String[] instructions = {
            "• Use controls below to insert, search, or remove entries",
            "• Red buckets indicate collisions (multiple entries)",
            "• Try different hash functions to see collision patterns"
        };
        
        int instrY = 520;
        for (int i = 0; i < instructions.length; i++) {
            g2d.drawString(instructions[i], 20, instrY + i * 15);
        }
    }
    
    public void insert(String key, String value) {
        if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
            hashTable.put(key.trim(), value.trim());
        }
    }
    
    public void search(String key) {
        if (key != null && !key.trim().isEmpty()) {
            hashTable.get(key.trim());
        }
    }
    
    public void remove(String key) {
        if (key != null && !key.trim().isEmpty()) {
            hashTable.remove(key.trim());
        }
    }
    
    public void clear() {
        hashTable.clear();
        repaint();
    }
    
    public void loadSample() {
        hashTable.loadSampleData();
        repaint();
    }
    
    public void setHashFunction(HashTableAlgorithm.HashFunction function) {
        // Save current data
        java.util.List<String[]> currentData = new java.util.ArrayList<>();
        for (HashBucket bucket : hashTable.getBuckets()) {
            for (HashEntry entry : bucket.getEntries()) {
                currentData.add(new String[]{entry.getKey(), entry.getValue()});
            }
        }
        
        // Clear and set new function
        hashTable.clear();
        hashTable.setHashFunction(function);
        
        // Re-insert data with new hash function
        for (String[] entry : currentData) {
            hashTable.put(entry[0], entry[1]);
        }
        
        repaint();
    }
    
    public void resize(int newCapacity) {
        hashTable.resize(newCapacity);
        repaint();
    }
    
    public HashTableAlgorithm getHashTable() {
        return hashTable;
    }
}
