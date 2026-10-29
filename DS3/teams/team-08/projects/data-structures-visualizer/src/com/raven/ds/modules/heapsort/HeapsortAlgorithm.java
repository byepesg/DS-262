package com.raven.ds.modules.heapsort;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.SimpleAnimationStep;
import com.raven.ds.modules.heap.HeapNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Heapsort implementation with animation support
 */
public class HeapsortAlgorithm {
    private List<HeapNode> heap;
    private List<HeapNode> sortedArray;
    private AnimationEngine animationEngine;
    private int heapSize;
    
    public HeapsortAlgorithm(AnimationEngine animationEngine) {
        this.heap = new ArrayList<>();
        this.sortedArray = new ArrayList<>();
        this.animationEngine = animationEngine;
    }
    
    public void loadData(int[] values) {
        heap.clear();
        sortedArray.clear();
        animationEngine.clearSteps();
        
        for (int value : values) {
            heap.add(new HeapNode(value));
        }
        heapSize = heap.size();
    }
    
    public void sort() {
        if (heap.isEmpty()) return;
        
        animationEngine.clearSteps();
        heapSize = heap.size();
        sortedArray.clear();
        
        // Step 1: Build Max Heap
        animationEngine.addStep(new SimpleAnimationStep("Build Heap", () -> {
            // Visual indication
        }, "🏗️ <b>Phase 1: Build Max Heap</b><br>Converting the input array into a Max Heap structure so that the largest element is at the root."));
        
        for (int i = heapSize / 2 - 1; i >= 0; i--) {
            heapifyDown(i, heapSize);
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Heap Built", () -> {
            clearHighlights();
        }, "✅ <b>Max Heap Built!</b><br>The root now contains the maximum element. We can now proceed to the sorting phase."));
        
        // Step 2: Extract elements
        int originalSize = heapSize;
        for (int i = originalSize - 1; i > 0; i--) {
            final int currentIndex = i;
            
            animationEngine.addStep(new SimpleAnimationStep("Swap Root", () -> {
                clearHighlights();
                heap.get(0).setBeingSwapped(true);
                heap.get(currentIndex).setBeingSwapped(true);
                
                // Swap root with last element
                int temp = heap.get(0).getValue();
                heap.get(0).setValue(heap.get(currentIndex).getValue());
                heap.get(currentIndex).setValue(temp);
                
            }, "🔄 <b>Extract Max:</b> Swapping root (max value) with the last unsorted element at index " + currentIndex + "."));
            
            animationEngine.addStep(new SimpleAnimationStep("Lock Element", () -> {
                heap.get(0).setBeingSwapped(false);
                heap.get(currentIndex).setBeingSwapped(false);
                heap.get(currentIndex).setSorted(true); // Mark as sorted
                heapSize--;
            }, "🔒 <b>Lock Element:</b> Value <b>" + heap.get(currentIndex).getValue() + "</b> is now in its final sorted position."));
            
            heapifyDown(0, i);
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            if (!heap.isEmpty()) {
                heap.get(0).setSorted(true);
            }
            clearHighlights();
        }, "🏁 <b>Sorting Complete!</b><br>The entire array is now sorted in ascending order."));
    }
    
    private void heapifyDown(int index, int size) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int largest = index;
        
        if (leftChild < size) {
            final int l = leftChild;
            animationEngine.addStep(new SimpleAnimationStep("Compare", () -> {
                clearHighlights();
                heap.get(index).setHighlighted(true);
                heap.get(l).setHighlighted(true);
            }, "🔍 <b>Compare:</b> Checking if left child <b>" + heap.get(l).getValue() + "</b> is greater than parent <b>" + heap.get(index).getValue() + "</b>."));
            
            if (heap.get(leftChild).getValue() > heap.get(largest).getValue()) {
                largest = leftChild;
            }
        }
        
        if (rightChild < size) {
            final int r = rightChild;
            animationEngine.addStep(new SimpleAnimationStep("Compare", () -> {
                clearHighlights();
                heap.get(index).setHighlighted(true);
                heap.get(r).setHighlighted(true);
            }, "🔍 <b>Compare:</b> Checking if right child <b>" + heap.get(r).getValue() + "</b> is greater than current largest <b>" + heap.get(largest).getValue() + "</b>."));
            
            if (heap.get(rightChild).getValue() > heap.get(largest).getValue()) {
                largest = rightChild;
            }
        }
        
        if (largest != index) {
            final int target = largest;
            animationEngine.addStep(new SimpleAnimationStep("Swap", () -> {
                heap.get(index).setBeingSwapped(true);
                heap.get(target).setBeingSwapped(true);
                
                int temp = heap.get(index).getValue();
                heap.get(index).setValue(heap.get(target).getValue());
                heap.get(target).setValue(temp);
            }, "🔀 <b>Swap:</b> Moving smaller parent <b>" + heap.get(target).getValue() + "</b> down to restore heap property."));
            
            animationEngine.addStep(new SimpleAnimationStep("Continue", () -> {
                heap.get(index).setBeingSwapped(false);
                heap.get(target).setBeingSwapped(false);
                clearHighlights();
            }, "⬇️ <b>Continue:</b> Moving down to check the new position."));
            
            heapifyDown(largest, size);
        }
    }
    
    private void clearHighlights() {
        for (HeapNode node : heap) {
            node.setHighlighted(false);
            node.setBeingSwapped(false);
        }
    }
    
    public List<HeapNode> getNodes() {
        return heap;
    }
    
    public int getHeapSize() {
        return heapSize;
    }
}
