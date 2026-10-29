package com.raven.ds.modules.hashtable;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.SimpleAnimationStep;

/**
 * Hash Table implementation with animation support
 */
public class HashTableAlgorithm {
    private HashBucket[] buckets;
    private int capacity;
    private int size;
    private HashFunction hashFunction;
    private AnimationEngine animationEngine;
    
    public enum HashFunction {
        DIVISION("h(k) = k mod m"),
        MULTIPLICATION("h(k) = ⌊m(kA mod 1)⌋"),
        DJBX33A("DJBX33A Hash");
        
        private final String description;
        
        HashFunction(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public HashTableAlgorithm(int capacity, AnimationEngine animationEngine) {
        this.capacity = capacity;
        this.animationEngine = animationEngine;
        this.buckets = new HashBucket[capacity];
        this.size = 0;
        this.hashFunction = HashFunction.DIVISION;
        
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new HashBucket(i);
        }
    }
    
    public void setHashFunction(HashFunction function) {
        this.hashFunction = function;
    }
    
    private int hash(String key) {
        switch (hashFunction) {
            case DIVISION:
                return Math.abs(key.hashCode()) % capacity;
            case MULTIPLICATION:
                double A = 0.6180339887; // (√5 - 1)/2
                double kA = Math.abs(key.hashCode()) * A;
                return (int) Math.floor(capacity * (kA - Math.floor(kA)));
            case DJBX33A:
                int hash = 5381;
                for (char c : key.toCharArray()) {
                    hash = ((hash << 5) + hash) + c;
                }
                return Math.abs(hash) % capacity;
            default:
                return Math.abs(key.hashCode()) % capacity;
        }
    }
    
    public void put(String key, String value) {
        animationEngine.clearSteps();
        
        final int index = hash(key);
        
        animationEngine.addStep(new SimpleAnimationStep("Hash Key", () -> {
            clearHighlights();
            buckets[index].setHighlighted(true);
        }, "Hash function: " + hashFunction.getDescription() + 
           ". Key '" + key + "' hashes to index " + index));
        
        final boolean isUpdate = buckets[index].findEntry(key) != null;
        
        animationEngine.addStep(new SimpleAnimationStep("Insert/Update", () -> {
            buckets[index].addEntry(key, value);
            if (!isUpdate) {
                size++;
            }
        }, isUpdate ? 
           "Updated existing key '" + key + "' with value '" + value + "'" :
           "Inserted new entry: " + key + " → " + value + 
           (buckets[index].getSize() > 0 ? " (Collision detected!)" : "")));
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "Operation completed. Load factor: " + String.format("%.2f", getLoadFactor())));
    }
    
    public void get(String key) {
        animationEngine.clearSteps();
        
        final int index = hash(key);
        
        animationEngine.addStep(new SimpleAnimationStep("Hash Key", () -> {
            clearHighlights();
            buckets[index].setHighlighted(true);
        }, "Searching for key '" + key + "'. Hash to index " + index));
        
        final HashEntry entry = buckets[index].findEntry(key);
        
        animationEngine.addStep(new SimpleAnimationStep("Search Bucket", () -> {
            // Keep bucket highlighted
        }, entry != null ? 
           "Found key '" + key + "' with value '" + entry.getValue() + "'" :
           "Key '" + key + "' not found in bucket " + index));
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "Search completed"));
    }
    
    public void remove(String key) {
        animationEngine.clearSteps();
        
        final int index = hash(key);
        
        animationEngine.addStep(new SimpleAnimationStep("Hash Key", () -> {
            clearHighlights();
            buckets[index].setHighlighted(true);
        }, "Removing key '" + key + "'. Hash to index " + index));
        
        final boolean found = buckets[index].findEntry(key) != null;
        
        animationEngine.addStep(new SimpleAnimationStep("Remove Entry", () -> {
            if (buckets[index].removeEntry(key)) {
                size--;
            }
        }, found ? 
           "Removed key '" + key + "' from bucket " + index :
           "Key '" + key + "' not found in bucket " + index));
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "Removal completed. Load factor: " + String.format("%.2f", getLoadFactor())));
    }
    
    public void clear() {
        for (HashBucket bucket : buckets) {
            bucket.clear();
        }
        size = 0;
        animationEngine.clearSteps();
    }
    
    public void loadSampleData() {
        clear();
        
        // Add some sample data
        String[] keys = {"apple", "banana", "cherry", "date", "elderberry", "fig", "grape"};
        String[] values = {"red", "yellow", "red", "brown", "purple", "purple", "green"};
        
        for (int i = 0; i < keys.length; i++) {
            int index = hash(keys[i]);
            buckets[index].addEntry(keys[i], values[i]);
            size++;
        }
    }
    
    private void clearHighlights() {
        for (HashBucket bucket : buckets) {
            bucket.setHighlighted(false);
        }
    }
    
    public double getLoadFactor() {
        return (double) size / capacity;
    }
    
    public int getCollisionCount() {
        int collisions = 0;
        for (HashBucket bucket : buckets) {
            if (bucket.hasCollision()) {
                collisions += bucket.getSize() - 1;
            }
        }
        return collisions;
    }
    
    public void resize(int newCapacity) {
        if (newCapacity <= 0) return;
        
        // Save old data
        HashBucket[] oldBuckets = buckets;
        
        // Reinitialize with new capacity
        this.capacity = newCapacity;
        this.buckets = new HashBucket[capacity];
        this.size = 0;
        
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new HashBucket(i);
        }
        
        // Rehash all entries
        for (HashBucket oldBucket : oldBuckets) {
            for (HashEntry entry : oldBucket.getEntries()) {
                int newIndex = hash(entry.getKey());
                buckets[newIndex].addEntry(entry.getKey(), entry.getValue());
                size++;
            }
        }
    }
    
    // Getters
    public HashBucket[] getBuckets() { return buckets; }
    public int getCapacity() { return capacity; }
    public int getSize() { return size; }
    public HashFunction getHashFunction() { return hashFunction; }
}
