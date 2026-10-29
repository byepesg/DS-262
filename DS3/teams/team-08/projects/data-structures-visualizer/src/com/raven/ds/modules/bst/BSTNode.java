package com.raven.ds.modules.bst;

/**
 * Binary Search Tree Node
 */
public class BSTNode {
    public int value;
    public BSTNode left;
    public BSTNode right;
    
    public BSTNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
    
    @Override
    public String toString() {
        return "Node(" + value + ")";
    }
}
