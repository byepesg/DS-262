package com.raven.ds.modules.hashtable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bucket in the hash table for visualization
 */
public class HashBucket {
    private int index;
    private List<HashEntry> entries;
    private boolean highlighted;
    private Color backgroundColor;
    
    public static final Color DEFAULT_COLOR = new Color(240, 240, 240);
    public static final Color HIGHLIGHTED_COLOR = new Color(255, 255, 0);
    public static final Color COLLISION_COLOR = new Color(255, 200, 200);
    
    public HashBucket(int index) {
        this.index = index;
        this.entries = new ArrayList<>();
        this.highlighted = false;
        this.backgroundColor = DEFAULT_COLOR;
    }
    
    public void addEntry(String key, String value) {
        // Check if key already exists and update
        for (HashEntry entry : entries) {
            if (entry.getKey().equals(key)) {
                entry.setValue(value);
                return;
            }
        }
        // Add new entry
        entries.add(new HashEntry(key, value));
        updateBackgroundColor();
    }
    
    public boolean removeEntry(String key) {
        boolean removed = entries.removeIf(entry -> entry.getKey().equals(key));
        updateBackgroundColor();
        return removed;
    }
    
    public HashEntry findEntry(String key) {
        for (HashEntry entry : entries) {
            if (entry.getKey().equals(key)) {
                return entry;
            }
        }
        return null;
    }
    
    public void draw(Graphics2D g2d, int x, int y, int width, int height) {
        // Draw bucket background
        g2d.setColor(highlighted ? HIGHLIGHTED_COLOR : backgroundColor);
        g2d.fillRect(x, y, width, height);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, width, height);
        
        // Draw index number
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString(String.valueOf(index), x + 5, y + 15);
        
        // Draw entries
        int entryY = y + 25;
        for (HashEntry entry : entries) {
            if (entryY + 15 > y + height) break; // Don't draw if it goes beyond bucket
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String text = entry.getKey() + ":" + entry.getValue();
            if (text.length() > 15) {
                text = text.substring(0, 12) + "...";
            }
            g2d.drawString(text, x + 5, entryY);
            entryY += 12;
        }
        
        // Draw collision indicator
        if (entries.size() > 1) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
            g2d.drawString("COLLISION", x + width - 45, y + 12);
        }
    }
    
    private void updateBackgroundColor() {
        if (entries.size() > 1) {
            backgroundColor = COLLISION_COLOR;
        } else {
            backgroundColor = DEFAULT_COLOR;
        }
    }
    
    public void clear() {
        entries.clear();
        backgroundColor = DEFAULT_COLOR;
    }
    
    // Getters and setters
    public int getIndex() { return index; }
    public List<HashEntry> getEntries() { return entries; }
    public boolean isEmpty() { return entries.isEmpty(); }
    public int getSize() { return entries.size(); }
    
    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean highlighted) { this.highlighted = highlighted; }
    
    public boolean hasCollision() { return entries.size() > 1; }
}

/**
 * Represents a key-value pair in the hash table
 */
class HashEntry {
    private String key;
    private String value;
    
    public HashEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey() { return key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    @Override
    public String toString() {
        return key + ":" + value;
    }
}
