package com.raven.ds.modules.heap;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.SimpleAnimationStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Heap implementation with animation support
 */
public class BinaryHeapAlgorithm {
    private List<HeapNode> heap;
    private boolean isMaxHeap;
    private AnimationEngine animationEngine;
    
    public BinaryHeapAlgorithm(AnimationEngine animationEngine, boolean isMaxHeap) {
        this.heap = new ArrayList<>();
        this.animationEngine = animationEngine;
        this.isMaxHeap = isMaxHeap;
    }
    
    public void insert(int value) {
        animationEngine.clearSteps();
        
        HeapNode newNode = new HeapNode(value);
        
        animationEngine.addStep(new SimpleAnimationStep("Insert", () -> {
            heap.add(newNode);
            clearHighlights();
            newNode.setHighlighted(true);
        }, "➕ <b>Insert:</b> Adding value <b>" + value + "</b> at the end of the heap (first available leaf)."));
        
        if (heap.size() > 1) {
            heapifyUp(heap.size() - 1);
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "✅ <b>Insertion Complete:</b> Heap property is maintained. All nodes satisfy the " + (isMaxHeap ? "Max-Heap" : "Min-Heap") + " condition."));
    }
    
    public void extractRoot() {
        if (heap.isEmpty()) return;
        
        animationEngine.clearSteps();
        
        animationEngine.addStep(new SimpleAnimationStep("Extract Root", () -> {
            clearHighlights();
            if (!heap.isEmpty()) {
                heap.get(0).setHighlighted(true);
            }
        }, "📤 <b>Extract Root:</b> Removing the root element <b>" + (heap.isEmpty() ? "none" : heap.get(0).getValue()) + "</b> (" + (isMaxHeap ? "Maximum" : "Minimum") + ")."));
        
        if (heap.size() == 1) {
            animationEngine.addStep(new SimpleAnimationStep("Remove Last", () -> {
                heap.clear();
            }, "🗑️ <b>Remove Last:</b> The heap is now empty."));
            return;
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Move Last to Root", () -> {
            if (!heap.isEmpty()) {
                HeapNode lastNode = heap.get(heap.size() - 1);
                heap.get(0).setValue(lastNode.getValue());
                heap.remove(heap.size() - 1);
                clearHighlights();
                heap.get(0).setHighlighted(true);
            }
        }, "🔄 <b>Replace Root:</b> Moved the last element to the root position to maintain the complete tree structure."));
        
        if (!heap.isEmpty()) {
            heapifyDown(0);
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "✅ <b>Extraction Complete:</b> Heap property restored."));
    }
    
    private void heapifyUp(int index) {
        if (index == 0) return;
        
        int parentIndex = (index - 1) / 2;
        HeapNode current = heap.get(index);
        HeapNode parent = heap.get(parentIndex);
        
        boolean shouldSwap = isMaxHeap ? 
            current.getValue() > parent.getValue() : 
            current.getValue() < parent.getValue();
        
        animationEngine.addStep(new SimpleAnimationStep("Compare", () -> {
            clearHighlights();
            current.setHighlighted(true);
            parent.setHighlighted(true);
        }, "🔍 <b>Compare:</b> Checking if child <b>" + current.getValue() + "</b> violates heap property with parent <b>" + parent.getValue() + "</b>."));
        
        if (shouldSwap) {
            animationEngine.addStep(new SimpleAnimationStep("Swap", () -> {
                current.setBeingSwapped(true);
                parent.setBeingSwapped(true);
                
                // Swap values
                int temp = current.getValue();
                current.setValue(parent.getValue());
                parent.setValue(temp);
            }, "🔀 <b>Swap:</b> Child <b>" + current.getValue() + "</b> is " + (isMaxHeap ? "greater" : "smaller") + " than parent. Swapping to restore heap property."));
            
            animationEngine.addStep(new SimpleAnimationStep("Continue Heapify", () -> {
                current.setBeingSwapped(false);
                parent.setBeingSwapped(false);
                clearHighlights();
                parent.setHighlighted(true);
            }, "⬆️ <b>Continue:</b> Moving up to check the parent's new position."));
            
            heapifyUp(parentIndex);
        }
    }
    
    private void heapifyDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int targetIndex = index;
        
        if (leftChild >= heap.size()) return;
        
        animationEngine.addStep(new SimpleAnimationStep("Find Target", () -> {
            clearHighlights();
            heap.get(index).setHighlighted(true);
            if (leftChild < heap.size()) {
                heap.get(leftChild).setHighlighted(true);
            }
            if (rightChild < heap.size()) {
                heap.get(rightChild).setHighlighted(true);
            }
        }, "🔍 <b>Compare Children:</b> Comparing <b>" + heap.get(index).getValue() + "</b> with its children to find the " + (isMaxHeap ? "largest" : "smallest") + "."));
        
        // Find the target child to potentially swap with
        if (isMaxHeap) {
            if (leftChild < heap.size() && 
                heap.get(leftChild).getValue() > heap.get(targetIndex).getValue()) {
                targetIndex = leftChild;
            }
            if (rightChild < heap.size() && 
                heap.get(rightChild).getValue() > heap.get(targetIndex).getValue()) {
                targetIndex = rightChild;
            }
        } else {
            if (leftChild < heap.size() && 
                heap.get(leftChild).getValue() < heap.get(targetIndex).getValue()) {
                targetIndex = leftChild;
            }
            if (rightChild < heap.size() && 
                heap.get(rightChild).getValue() < heap.get(targetIndex).getValue()) {
                targetIndex = rightChild;
            }
        }
        
        if (targetIndex != index) {
            final int finalTarget = targetIndex;
            animationEngine.addStep(new SimpleAnimationStep("Swap", () -> {
                HeapNode current = heap.get(index);
                HeapNode target = heap.get(finalTarget);
                
                current.setBeingSwapped(true);
                target.setBeingSwapped(true);
                
                // Swap values
                int temp = current.getValue();
                current.setValue(target.getValue());
                target.setValue(temp);
            }, "🔀 <b>Swap:</b> Swapping <b>" + heap.get(index).getValue() + "</b> with <b>" + heap.get(targetIndex).getValue() + "</b> to restore heap property."));
            
            animationEngine.addStep(new SimpleAnimationStep("Continue Heapify", () -> {
                heap.get(index).setBeingSwapped(false);
                heap.get(finalTarget).setBeingSwapped(false);
                clearHighlights();
                heap.get(finalTarget).setHighlighted(true);
            }, "⬇️ <b>Continue:</b> Moving down to check the child's new position."));
            
            heapifyDown(targetIndex);
        }
    }
    
    public void buildHeap(int[] values) {
        clear();
        animationEngine.clearSteps();
        
        animationEngine.addStep(new SimpleAnimationStep("Initialize", () -> {
            for (int value : values) {
                heap.add(new HeapNode(value));
            }
        }, "🏗️ <b>Build Heap:</b> Initialized heap with " + values.length + " elements. Starting bottom-up construction."));
        
        // Build heap from bottom up
        for (int i = heap.size() / 2 - 1; i >= 0; i--) {
            heapifyDown(i);
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "✅ <b>Build Complete:</b> The array has been transformed into a valid " + (isMaxHeap ? "Max-Heap" : "Min-Heap") + "."));
    }
    
    public void clear() {
        heap.clear();
        animationEngine.clearSteps();
    }
    
    public void loadSampleData() {
        int[] sampleData = {10, 20, 15, 30, 40, 50, 100, 25, 45};
        buildHeap(sampleData);
    }
    
    private void clearHighlights() {
        for (HeapNode node : heap) {
            node.reset();
        }
    }
    
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    
    public int size() {
        return heap.size();
    }
    
    public HeapNode getRoot() {
        return heap.isEmpty() ? null : heap.get(0);
    }
    
    // Getters
    public List<HeapNode> getNodes() { return heap; }
    public boolean isMaxHeap() { return isMaxHeap; }
    public void setMaxHeap(boolean maxHeap) { this.isMaxHeap = maxHeap; }
}
