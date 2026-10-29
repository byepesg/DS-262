package com.raven.ds.modules.dynamicarray;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.SimpleAnimationStep;

import java.util.Arrays;

/**
 * Dynamic Array implementation with animation support
 */
public class DynamicArrayAlgorithm {
    private Integer[] array;
    private int size;
    private int capacity;
    private AnimationEngine animationEngine;
    
    // Visualization state
    private int highlightedIndex = -1;
    private boolean isResizing = false;
    private Integer[] oldArray = null; // For resizing animation
    
    public DynamicArrayAlgorithm(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.capacity = 4;
        this.array = new Integer[capacity];
        this.size = 0;
    }
    
    public void add(int value) {
        animationEngine.clearSteps();
        
        if (size == capacity) {
            resize();
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Add", () -> {
            highlightedIndex = size;
            array[size] = value;
            size++;
        }, "➕ <b>Add Element:</b> Inserting <b>" + value + "</b> at index <b>" + size + "</b>. Current size: " + size + ", capacity: " + capacity + "."));
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            highlightedIndex = -1;
        }, "✅ <b>Added:</b> Element <b>" + value + "</b> successfully added. Array now has " + (size + 1) + " elements."));
    }
    
    private void resize() {
        int newCapacity = capacity * 2;
        
        animationEngine.addStep(new SimpleAnimationStep("Resize Start", () -> {
            isResizing = true;
            oldArray = array;
            array = new Integer[newCapacity];
            // Don't copy yet in visualization
        }, "📦 <b>Array Full!</b> Current capacity (" + capacity + ") reached. Creating new array with capacity <b>" + newCapacity + "</b>."));
        
        for (int i = 0; i < size; i++) {
            final int index = i;
            final int val = oldArray[i];
            animationEngine.addStep(new SimpleAnimationStep("Copy " + index, () -> {
                array[index] = val;
                highlightedIndex = index;
            }, "📋 <b>Copy Element:</b> Copying element <b>" + val + "</b> from old array[" + index + "] to new array[" + index + "]."));
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Resize End", () -> {
            isResizing = false;
            oldArray = null;
            capacity = newCapacity;
            highlightedIndex = -1;
        }, "✅ <b>Resize Complete:</b> Old array discarded. New capacity is <b>" + newCapacity + "</b>. Memory reallocated successfully."));
    }
    
    public void remove(int index) {
        if (index < 0 || index >= size) return;
        
        animationEngine.clearSteps();
        
        final int removedValue = array[index];
        
        animationEngine.addStep(new SimpleAnimationStep("Remove", () -> {
            highlightedIndex = index;
            array[index] = null;
        }, "🗑️ <b>Remove Element:</b> Removing element <b>" + removedValue + "</b> at index <b>" + index + "</b>."));
        
        for (int i = index; i < size - 1; i++) {
            final int curr = i;
            final int nextVal = array[curr + 1];
            animationEngine.addStep(new SimpleAnimationStep("Shift", () -> {
                array[curr] = array[curr + 1];
                array[curr + 1] = null;
                highlightedIndex = curr;
            }, "⬅️ <b>Shift Left:</b> Moving element <b>" + nextVal + "</b> from index " + (curr + 1) + " to index " + curr + "."));
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            size--;
            highlightedIndex = -1;
        }, "✅ <b>Removal Complete:</b> Element removed. Array now has <b>" + (size - 1) + "</b> elements."));
    }
    
    public void clear() {
        animationEngine.clearSteps();
        size = 0;
        capacity = 4;
        array = new Integer[capacity];
        highlightedIndex = -1;
        isResizing = false;
        oldArray = null;
    }
    
    public void loadSample() {
        clear();
        int[] sample = {1, 2, 3, 4, 5}; // Will trigger resize
        for (int v : sample) {
            if (size == capacity) {
                // Manual resize without animation for loadSample
                capacity *= 2;
                array = Arrays.copyOf(array, capacity);
            }
            array[size++] = v;
        }
    }
    
    // Getters
    public Integer[] getArray() { return array; }
    public int getSize() { return size; }
    public int getCapacity() { return capacity; }
    public int getHighlightedIndex() { return highlightedIndex; }
    public boolean isResizing() { return isResizing; }
    public Integer[] getOldArray() { return oldArray; }
}
