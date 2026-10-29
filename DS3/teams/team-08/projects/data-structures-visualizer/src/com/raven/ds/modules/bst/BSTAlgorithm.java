package com.raven.ds.modules.bst;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree Algorithm Implementation
 * Pure logic without GUI dependencies
 */
public class BSTAlgorithm {
    private BSTNode root;
    
    public BSTAlgorithm() {
        this.root = null;
    }
    
    public BSTNode getRoot() {
        return root;
    }
    
    public void insert(int value) {
        root = insertRec(root, value);
    }
    
    private BSTNode insertRec(BSTNode root, int value) {
        if (root == null) {
            return new BSTNode(value);
        }
        
        if (value < root.value) {
            root.left = insertRec(root.left, value);
        } else if (value > root.value) {
            root.right = insertRec(root.right, value);
        }
        // Duplicate values are ignored
        
        return root;
    }
    
    public boolean delete(int value) {
        if (!contains(value)) {
            return false;
        }
        root = deleteRec(root, value);
        return true;
    }
    
    private BSTNode deleteRec(BSTNode root, int value) {
        if (root == null) {
            return null;
        }
        
        if (value < root.value) {
            root.left = deleteRec(root.left, value);
        } else if (value > root.value) {
            root.right = deleteRec(root.right, value);
        } else {
            // Node to delete found
            
            // Case 1: Node has no children (leaf)
            if (root.left == null && root.right == null) {
                return null;
            }
            
            // Case 2: Node has one child
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            
            // Case 3: Node has two children
            // Find inorder successor (smallest in right subtree)
            BSTNode successor = findMin(root.right);
            root.value = successor.value;
            root.right = deleteRec(root.right, successor.value);
        }
        
        return root;
    }
    
    private BSTNode findMin(BSTNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    public boolean contains(int value) {
        return containsRec(root, value);
    }
    
    private boolean containsRec(BSTNode root, int value) {
        if (root == null) {
            return false;
        }
        
        if (value == root.value) {
            return true;
        }
        
        return value < root.value 
            ? containsRec(root.left, value)
            : containsRec(root.right, value);
    }
    
    public BSTNode findNode(int value) {
        return findNodeRec(root, value);
    }
    
    private BSTNode findNodeRec(BSTNode root, int value) {
        if (root == null || root.value == value) {
            return root;
        }
        
        return value < root.value 
            ? findNodeRec(root.left, value)
            : findNodeRec(root.right, value);
    }
    
    /**
     * Get the path taken during insertion (for animation)
     */
    public List<BSTNode> getInsertionPath(int value) {
        List<BSTNode> path = new ArrayList<>();
        getInsertionPathRec(root, value, path);
        return path;
    }
    
    private void getInsertionPathRec(BSTNode root, int value, List<BSTNode> path) {
        if (root == null) {
            return;
        }
        
        path.add(root);
        
        if (value < root.value && root.left != null) {
            getInsertionPathRec(root.left, value, path);
        } else if (value > root.value && root.right != null) {
            getInsertionPathRec(root.right, value, path);
        }
    }
    
    /**
     * Get the path taken during search (for animation)
     */
    public List<BSTNode> getSearchPath(int value) {
        List<BSTNode> path = new ArrayList<>();
        getSearchPathRec(root, value, path);
        return path;
    }
    
    private void getSearchPathRec(BSTNode root, int value, List<BSTNode> path) {
        if (root == null) {
            return;
        }
        
        path.add(root);
        
        if (value < root.value) {
            getSearchPathRec(root.left, value, path);
        } else if (value > root.value) {
            getSearchPathRec(root.right, value, path);
        }
        // Found the value, stop here
    }
    
    // Traversal methods for animation
    public void inOrderTraversal(BSTNode root, List<BSTNode> result) {
        if (root != null) {
            inOrderTraversal(root.left, result);
            result.add(root);
            inOrderTraversal(root.right, result);
        }
    }
    
    public void preOrderTraversal(BSTNode root, List<BSTNode> result) {
        if (root != null) {
            result.add(root);
            preOrderTraversal(root.left, result);
            preOrderTraversal(root.right, result);
        }
    }
    
    public void postOrderTraversal(BSTNode root, List<BSTNode> result) {
        if (root != null) {
            postOrderTraversal(root.left, result);
            postOrderTraversal(root.right, result);
            result.add(root);
        }
    }
    
    public void clear() {
        root = null;
    }
    
    public int size() {
        return sizeRec(root);
    }
    
    private int sizeRec(BSTNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + sizeRec(root.left) + sizeRec(root.right);
    }
    
    public int height() {
        return heightRec(root);
    }
    
    private int heightRec(BSTNode root) {
        if (root == null) {
            return -1;
        }
        return 1 + Math.max(heightRec(root.left), heightRec(root.right));
    }
}
