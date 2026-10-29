package com.raven.ds.modules.avl;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.SimpleAnimationStep;

/**
 * AVL Tree implementation with animation support
 */
public class AVLTreeAlgorithm {
    private AVLNode root;
    private AnimationEngine animationEngine;
    
    public AVLTreeAlgorithm(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
    }
    
    public void insert(int value) {
        animationEngine.clearSteps();
        
        animationEngine.addStep(new SimpleAnimationStep("Start Insert", () -> {
            // Visual preparation
        }, "🌳 <b>AVL Tree Insertion:</b> Inserting value <b>" + value + "</b>. Will maintain balance after insertion."));
        
        root = insertRecursive(root, value);
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "✅ <b>Insertion Complete:</b> Tree is balanced. All nodes have balance factor ∈ {-1, 0, 1}."));
    }

    public void delete(int value) {
        animationEngine.clearSteps();
        
        animationEngine.addStep(new SimpleAnimationStep("Start Delete", () -> {
            // Visual preparation
        }, "🗑️ <b>AVL Tree Deletion:</b> Deleting value <b>" + value + "</b>. Will maintain balance after deletion."));
        
        root = deleteRecursive(root, value);
        
        animationEngine.addStep(new SimpleAnimationStep("Complete", () -> {
            clearHighlights();
        }, "✅ <b>Deletion Complete:</b> Tree is balanced."));
    }
    
    private AVLNode insertRecursive(AVLNode node, int value) {
        if (node == null) {
            AVLNode newNode = new AVLNode(value);
            animationEngine.addStep(new SimpleAnimationStep("Create Node", () -> {
                newNode.setHighlighted(true);
            }, "➕ <b>Create Node:</b> Created new node with value <b>" + value + "</b>."));
            return newNode;
        }
        
        final AVLNode currentNode = node;
        
        if (value < node.value) {
            animationEngine.addStep(new SimpleAnimationStep("Go Left", () -> {
                currentNode.setHighlighted(true);
            }, "⬅️ <b>Navigate Left:</b> " + value + " < " + node.value + ", moving to left subtree."));
            
            node.left = insertRecursive(node.left, value);
        } else if (value > node.value) {
            animationEngine.addStep(new SimpleAnimationStep("Go Right", () -> {
                currentNode.setHighlighted(true);
            }, "➡️ <b>Navigate Right:</b> " + value + " > " + node.value + ", moving to right subtree."));
            
            node.right = insertRecursive(node.right, value);
        } else {
            return node; // Duplicate values not allowed
        }
        
        // Update height
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        
        // Get balance factor
        int balance = getBalance(node);
        
        animationEngine.addStep(new SimpleAnimationStep("Check Balance", () -> {
            currentNode.setHighlighted(true);
        }, "⚖️ <b>Check Balance:</b> Node <b>" + node.value + "</b> has balance factor = <b>" + balance + "</b>."));
        
        // Left Left Case
        if (balance > 1 && value < node.left.value) {
            animationEngine.addStep(new SimpleAnimationStep("LL Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Left-Left Case:</b> Performing right rotation on node <b>" + node.value + "</b>."));
            return rightRotate(node);
        }
        
        // Right Right Case
        if (balance < -1 && value > node.right.value) {
            animationEngine.addStep(new SimpleAnimationStep("RR Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Right-Right Case:</b> Performing left rotation on node <b>" + node.value + "</b>."));
            return leftRotate(node);
        }
        
        // Left Right Case
        if (balance > 1 && value > node.left.value) {
            animationEngine.addStep(new SimpleAnimationStep("LR Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Left-Right Case:</b> Performing left rotation on left child, then right rotation on node <b>" + node.value + "</b>."));
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        
        // Right Left Case
        if (balance < -1 && value < node.right.value) {
            animationEngine.addStep(new SimpleAnimationStep("RL Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Right-Left Case:</b> Performing right rotation on right child, then left rotation on node <b>" + node.value + "</b>."));
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        
        return node;
    }

    private AVLNode deleteRecursive(AVLNode node, int value) {
        if (node == null) {
            animationEngine.addStep(new SimpleAnimationStep("Not Found", () -> {
            }, "❌ <b>Not Found:</b> Value <b>" + value + "</b> not found in the tree."));
            return node;
        }
        
        final AVLNode currentNode = node;

        if (value < node.value) {
            animationEngine.addStep(new SimpleAnimationStep("Go Left", () -> {
                currentNode.setHighlighted(true);
            }, "⬅️ <b>Navigate Left:</b> " + value + " < " + node.value + ", moving to left subtree."));
            node.left = deleteRecursive(node.left, value);
        } else if (value > node.value) {
            animationEngine.addStep(new SimpleAnimationStep("Go Right", () -> {
                currentNode.setHighlighted(true);
            }, "➡️ <b>Navigate Right:</b> " + value + " > " + node.value + ", moving to right subtree."));
            node.right = deleteRecursive(node.right, value);
        } else {
            // Node found
            animationEngine.addStep(new SimpleAnimationStep("Found", () -> {
                currentNode.setHighlighted(true);
                currentNode.setColor(new java.awt.Color(231, 76, 60)); // Red for deletion
            }, "🎯 <b>Found:</b> Node <b>" + value + "</b> found. Deleting..."));

            if ((node.left == null) || (node.right == null)) {
                AVLNode temp = null;
                if (temp == node.left)
                    temp = node.right;
                else
                    temp = node.left;

                if (temp == null) {
                    temp = node;
                    node = null;
                } else
                    node = temp;
            } else {
                // Node with two children: Get the inorder successor (smallest in the right subtree)
                AVLNode temp = minValueNode(node.right);
                
                animationEngine.addStep(new SimpleAnimationStep("Successor", () -> {
                    // Highlight logic if needed
                }, "🔄 <b>Successor:</b> Replacing with inorder successor <b>" + temp.value + "</b>."));

                node.value = temp.value;
                node.right = deleteRecursive(node.right, temp.value);
            }
        }

        if (node == null)
            return node;

        // Update height
        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;

        // Get balance factor
        int balance = getBalance(node);
        
        animationEngine.addStep(new SimpleAnimationStep("Check Balance", () -> {
            currentNode.setHighlighted(true);
        }, "⚖️ <b>Check Balance:</b> Node <b>" + node.value + "</b> has balance factor = <b>" + balance + "</b>."));

        // Left Left Case
        if (balance > 1 && getBalance(node.left) >= 0) {
             animationEngine.addStep(new SimpleAnimationStep("LL Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Left-Left Case:</b> Performing right rotation on node <b>" + node.value + "</b>."));
            return rightRotate(node);
        }

        // Left Right Case
        if (balance > 1 && getBalance(node.left) < 0) {
            animationEngine.addStep(new SimpleAnimationStep("LR Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Left-Right Case:</b> Performing left rotation on left child, then right rotation on node <b>" + node.value + "</b>."));
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && getBalance(node.right) <= 0) {
             animationEngine.addStep(new SimpleAnimationStep("RR Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Right-Right Case:</b> Performing left rotation on node <b>" + node.value + "</b>."));
            return leftRotate(node);
        }

        // Right Left Case
        if (balance < -1 && getBalance(node.right) > 0) {
             animationEngine.addStep(new SimpleAnimationStep("RL Rotation", () -> {
                currentNode.setColor(new java.awt.Color(231, 76, 60));
            }, "🔄 <b>Right-Left Case:</b> Performing right rotation on right child, then left rotation on node <b>" + node.value + "</b>."));
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }
    
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;
        
        // Perform rotation
        x.right = y;
        y.left = T2;
        
        // Update heights
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        
        return x;
    }
    
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;
        
        // Perform rotation
        y.left = x;
        x.right = T2;
        
        // Update heights
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        
        return y;
    }
    
    private int getHeight(AVLNode node) {
        return node == null ? 0 : node.height;
    }
    
    private int getBalance(AVLNode node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }
    
    private void clearHighlights() {
        clearHighlightsRecursive(root);
    }
    
    private void clearHighlightsRecursive(AVLNode node) {
        if (node == null) return;
        node.setHighlighted(false);
        node.setColor(new java.awt.Color(52, 152, 219));
        clearHighlightsRecursive(node.left);
        clearHighlightsRecursive(node.right);
    }
    
    public void clear() {
        root = null;
        animationEngine.clearSteps();
    }
    
    public void loadSample() {
        clear();
        int[] values = {10, 20, 30, 40, 50, 25};
        for (int v : values) {
            root = insertRecursive(root, v);
        }
    }
    
    public AVLNode getRoot() {
        return root;
    }
}
