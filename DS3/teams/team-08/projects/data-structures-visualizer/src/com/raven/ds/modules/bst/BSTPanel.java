package com.raven.ds.modules.bst;

import com.raven.ds.core.AnimationEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Visual panel for Binary Search Tree operations
 * Features improved contrast, detailed step explanations, and better animations
 */
public class BSTPanel extends JPanel {
    private BSTAlgorithm bst;
    private AnimationEngine animationEngine;
    
    // Enhanced color scheme for better contrast
    private Color nodeColor = new Color(52, 73, 94);           // Dark blue-gray
    private Color highlightColor = new Color(231, 76, 60);     // Bright red
    private Color visitedColor = new Color(241, 196, 15);      // Golden yellow
    private Color successColor = new Color(39, 174, 96);       // Green
    private Color shadowColor = new Color(127, 140, 141, 100); // Light gray with transparency
    private Color textColor = Color.WHITE;
    private Color edgeColor = new Color(44, 62, 80);           // Darker blue-gray
    private Font nodeFont = new Font("SansSerif", Font.BOLD, 16);
    private Font explanationFont = new Font("SansSerif", Font.PLAIN, 14);
    
    private BSTNode highlightedNode = null;
    private BSTNode visitedNode = null;
    private String currentOperation = "";
    private String stepExplanation = "";
    private List<String> operationHistory = new ArrayList<>();
    
    public BSTPanel(AnimationEngine animationEngine) {
        this.animationEngine = animationEngine;
        this.bst = new BSTAlgorithm();
        
        setBackground(new Color(248, 249, 250)); // Light gray background
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
            "Binary Search Tree Visualization",
            0, 0, new Font("SansSerif", Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (bst.getRoot() != null) {
            drawTree(g2d, bst.getRoot(), getWidth() / 2, 70, getWidth() / 4);
        } else {
            // Draw empty tree message
            g2d.setColor(new Color(108, 117, 125));
            g2d.setFont(new Font("SansSerif", Font.ITALIC, 16));
            FontMetrics fm = g2d.getFontMetrics();
            String emptyMsg = "BST is empty - Insert values to begin";
            int x = (getWidth() - fm.stringWidth(emptyMsg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(emptyMsg, x, y);
        }
        
        // Draw enhanced operation status with background
        if (!currentOperation.isEmpty()) {
            drawOperationStatus(g2d);
        }
        
        // Draw step explanation
        if (!stepExplanation.isEmpty()) {
            drawStepExplanation(g2d);
        }
    }
    
    private void drawOperationStatus(Graphics2D g2d) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        
        String statusText = "Operation: " + currentOperation;
        int textWidth = fm.stringWidth(statusText);
        int textHeight = fm.getHeight();
        
        // Draw background rectangle
        g2d.setColor(new Color(52, 73, 94, 200));
        g2d.fillRoundRect(10, getHeight() - 60, textWidth + 20, textHeight + 10, 10, 10);
        
        // Draw border
        g2d.setColor(new Color(52, 73, 94));
        g2d.drawRoundRect(10, getHeight() - 60, textWidth + 20, textHeight + 10, 10, 10);
        
        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.drawString(statusText, 20, getHeight() - 45);
    }
    
    private void drawStepExplanation(Graphics2D g2d) {
        g2d.setFont(explanationFont);
        FontMetrics fm = g2d.getFontMetrics();
        
        String[] lines = stepExplanation.split("\n");
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(line));
        }
        
        int boxHeight = lines.length * fm.getHeight() + 20;
        int x = getWidth() - maxWidth - 40;
        int y = 20;
        
        // Draw background
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(x, y, maxWidth + 20, boxHeight, 10, 10);
        
        // Draw border
        g2d.setColor(new Color(52, 73, 94));
        g2d.drawRoundRect(x, y, maxWidth + 20, boxHeight, 10, 10);
        
        // Draw text
        g2d.setColor(new Color(52, 73, 94));
        for (int i = 0; i < lines.length; i++) {
            g2d.drawString(lines[i], x + 10, y + 15 + (i + 1) * fm.getHeight());
        }
    }
    
    private void drawTree(Graphics2D g2d, BSTNode node, int x, int y, int xOffset) {
        if (node == null) return;
        
        // Draw edges to children first (so they appear behind nodes)
        if (node.left != null) {
            int childX = x - xOffset;
            int childY = y + 90; // Increased spacing for better visibility
            
            // Draw edge shadow
            g2d.setColor(shadowColor);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(x + 1, y + 16, childX + 1, childY - 14);
            
            // Draw main edge
            g2d.setColor(edgeColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y + 15, childX, childY - 15);
            
            drawTree(g2d, node.left, childX, childY, xOffset / 2);
        }
        
        if (node.right != null) {
            int childX = x + xOffset;
            int childY = y + 90; // Increased spacing for better visibility
            
            // Draw edge shadow
            g2d.setColor(shadowColor);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(x + 1, y + 16, childX + 1, childY - 14);
            
            // Draw main edge
            g2d.setColor(edgeColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y + 15, childX, childY - 15);
            
            drawTree(g2d, node.right, childX, childY, xOffset / 2);
        }
        
        // Determine node color based on state
        Color currentNodeColor = nodeColor;
        if (node == highlightedNode) {
            currentNodeColor = highlightColor;
        } else if (node == visitedNode) {
            currentNodeColor = visitedColor;
        }
        
        // Draw node shadow
        g2d.setColor(shadowColor);
        Ellipse2D.Double shadowCircle = new Ellipse2D.Double(x - 17, y - 13, 34, 34);
        g2d.fill(shadowCircle);
        
        // Draw the main node circle
        g2d.setColor(currentNodeColor);
        Ellipse2D.Double circle = new Ellipse2D.Double(x - 18, y - 18, 36, 36);
        g2d.fill(circle);
        
        // Draw node border with enhanced thickness
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(circle);
        
        // Draw node value with better contrast
        g2d.setColor(textColor);
        g2d.setFont(nodeFont);
        FontMetrics fm = g2d.getFontMetrics();
        String valueStr = String.valueOf(node.value);
        int textX = x - fm.stringWidth(valueStr) / 2;
        int textY = y + fm.getAscent() / 2 - 2;
        
        // Draw text shadow for better readability
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(valueStr, textX + 1, textY + 1);
        
        // Draw main text
        g2d.setColor(textColor);
        g2d.drawString(valueStr, textX, textY);
    }
    
    public void insertValue(int value) {
        animationEngine.clearSteps();
        operationHistory.add("Insert " + value);
        
        // Check if value already exists
        if (bst.contains(value)) {
            JOptionPane.showMessageDialog(this, 
                "Value " + value + " already exists in the tree.\nBST does not allow duplicate values.",
                "Duplicate Value", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Step 1: Introduction step
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                currentOperation = "Preparing to insert " + value;
                stepExplanation = "BST Insertion Process:\n" +
                    "1. Start at root node\n" +
                    "2. Compare with current node\n" +
                    "3. Go left if smaller, right if larger\n" +
                    "4. Insert at empty position";
                highlightedNode = null;
                visitedNode = null;
                repaint();
            }
            
            @Override
            public void reset() {
                currentOperation = "";
                stepExplanation = "";
                highlightedNode = null;
                visitedNode = null;
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Initializing insertion of " + value + " into Binary Search Tree";
            }
        });
        
        List<BSTNode> path = bst.getInsertionPath(value);
        
        // If tree is empty
        if (path.isEmpty()) {
            animationEngine.addStep(new AnimationEngine.AnimationStep() {
                @Override
                public void execute() {
                    currentOperation = "Tree is empty - inserting as root";
                    stepExplanation = "Empty Tree Insertion:\n" +
                        "• Tree has no nodes\n" +
                        "• " + value + " becomes the root node\n" +
                        "• This is the simplest case";
                    repaint();
                }
                
                @Override
                public void reset() {
                    currentOperation = "";
                    stepExplanation = "";
                    repaint();
                }
                
                @Override
                public String getDescription() {
                    return "Inserting " + value + " as root of empty tree";
                }
            });
        } else {
            // Step through the insertion path
            for (int i = 0; i < path.size(); i++) {
                final BSTNode node = path.get(i);
                final int stepNum = i + 1;
                final int currentIndex = i;
                final boolean isLast = (i == path.size() - 1);
                
                animationEngine.addStep(new AnimationEngine.AnimationStep() {
                    @Override
                    public void execute() {
                        highlightedNode = node;
                        if (currentIndex > 0) visitedNode = path.get(currentIndex - 1);
                        
                        currentOperation = "Step " + stepNum + ": Comparing " + value + " with " + node.value;
                        
                        if (value < node.value) {
                            stepExplanation = "Comparison Analysis:\n" +
                                "• Current node: " + node.value + "\n" +
                                "• Inserting: " + value + "\n" +
                                "• " + value + " < " + node.value + " → Go LEFT\n" +
                                (isLast ? "• Left child is null → Insert here" : "• Continue to left child");
                        } else {
                            stepExplanation = "Comparison Analysis:\n" +
                                "• Current node: " + node.value + "\n" +
                                "• Inserting: " + value + "\n" +
                                "• " + value + " > " + node.value + " → Go RIGHT\n" +
                                (isLast ? "• Right child is null → Insert here" : "• Continue to right child");
                        }
                        repaint();
                    }
                    
                    @Override
                    public void reset() {
                        highlightedNode = null;
                        visitedNode = null;
                        currentOperation = "";
                        stepExplanation = "";
                        repaint();
                    }
                    
                    @Override
                    public String getDescription() {
                        return "Comparing " + value + " with node " + node.value + 
                               (value < node.value ? " (go left)" : " (go right)");
                    }
                });
            }
        }
        
        // Final step: actually insert the node
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                bst.insert(value);
                highlightedNode = bst.findNode(value);
                visitedNode = null;
                currentOperation = "Successfully inserted " + value;
                stepExplanation = "Insertion Complete!\n" +
                    "• New node " + value + " created\n" +
                    "• Attached to correct parent\n" +
                    "• BST property maintained\n" +
                    "• Tree size increased by 1";
                repaint();
            }
            
            @Override
            public void reset() {
                bst.delete(value);
                highlightedNode = null;
                visitedNode = null;
                currentOperation = "";
                stepExplanation = "";
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Creating new node with value " + value + " at correct position";
            }
        });
        
        // Final cleanup step
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                highlightedNode = null;
                visitedNode = null;
                currentOperation = "Insertion of " + value + " completed";
                stepExplanation = "";
                repaint();
            }
            
            @Override
            public void reset() {
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Insertion animation completed";
            }
        });
    }
    
    public void insertValueInstant(int value) {
        bst.insert(value);
        repaint();
    }
    
    public void deleteValue(int value) {
        if (!bst.contains(value)) {
            JOptionPane.showMessageDialog(this, 
                "Value " + value + " not found in tree.\nCannot delete a non-existent value.",
                "Value Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        animationEngine.clearSteps();
        operationHistory.add("Delete " + value);
        
        // Step 1: Find the node to delete
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                currentOperation = "Locating node " + value + " for deletion";
                stepExplanation = "BST Deletion Process:\n" +
                    "1. Find the node to delete\n" +
                    "2. Determine deletion case:\n" +
                    "   • Leaf node (no children)\n" +
                    "   • Node with one child\n" +
                    "   • Node with two children";
                highlightedNode = bst.findNode(value);
                repaint();
            }
            
            @Override
            public void reset() {
                bst.insert(value);
                currentOperation = "";
                stepExplanation = "";
                highlightedNode = null;
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Locating node " + value + " for deletion";
            }
        });
        
        // Step 2: Analyze the deletion case
        BSTNode nodeToDelete = bst.findNode(value);
        String deletionCase;
        if (nodeToDelete.left == null && nodeToDelete.right == null) {
            deletionCase = "Leaf Node";
        } else if (nodeToDelete.left == null || nodeToDelete.right == null) {
            deletionCase = "One Child";
        } else {
            deletionCase = "Two Children";
        }
        
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                highlightedNode = nodeToDelete;
                currentOperation = "Analyzing deletion case: " + deletionCase;
                
                if (deletionCase.equals("Leaf Node")) {
                    stepExplanation = "Leaf Node Deletion:\n" +
                        "• Node " + value + " has no children\n" +
                        "• Simply remove the node\n" +
                        "• Update parent's pointer to null\n" +
                        "• This is the simplest case";
                } else if (deletionCase.equals("One Child")) {
                    stepExplanation = "One Child Deletion:\n" +
                        "• Node " + value + " has one child\n" +
                        "• Replace node with its child\n" +
                        "• Parent points to grandchild\n" +
                        "• BST property maintained";
                } else {
                    stepExplanation = "Two Children Deletion:\n" +
                        "• Node " + value + " has two children\n" +
                        "• Find inorder successor\n" +
                        "• Replace value with successor\n" +
                        "• Delete successor node";
                }
                repaint();
            }
            
            @Override
            public void reset() {
                highlightedNode = null;
                currentOperation = "";
                stepExplanation = "";
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Analyzing deletion case for node " + value + ": " + deletionCase;
            }
        });
        
        // Step 3: Perform the deletion
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                bst.delete(value);
                highlightedNode = null;
                currentOperation = "Successfully deleted " + value;
                stepExplanation = "Deletion Complete!\n" +
                    "• Node " + value + " removed\n" +
                    "• BST property maintained\n" +
                    "• Tree structure updated\n" +
                    "• All invariants preserved";
                repaint();
            }
            
            @Override
            public void reset() {
                bst.insert(value);
                currentOperation = "";
                stepExplanation = "";
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Executing deletion of node " + value;
            }
        });
    }
    
    public boolean findValue(int value) {
        boolean found = bst.contains(value);
        animationEngine.clearSteps();
        operationHistory.add("Search " + value + " -> " + (found ? "Found" : "Not Found"));
        
        // Introduction step
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                currentOperation = "Searching for " + value;
                stepExplanation = "BST Search Process:\n" +
                    "1. Start at root node\n" +
                    "2. Compare target with current node\n" +
                    "3. Go left if smaller, right if larger\n" +
                    "4. Stop when found or reach null";
                highlightedNode = null;
                visitedNode = null;
                repaint();
            }
            
            @Override
            public void reset() {
                currentOperation = "";
                stepExplanation = "";
                highlightedNode = null;
                visitedNode = null;
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Initializing search for " + value;
            }
        });
        
        List<BSTNode> path = bst.getSearchPath(value);
        for (int i = 0; i < path.size(); i++) {
            final BSTNode node = path.get(i);
            final int stepNum = i + 1;
            final int currentIndex = i;
            final boolean isLast = (i == path.size() - 1);
            
            animationEngine.addStep(new AnimationEngine.AnimationStep() {
                @Override
                public void execute() {
                    highlightedNode = node;
                    if (currentIndex > 0) visitedNode = path.get(currentIndex - 1);
                    
                    if (isLast && node.value == value) {
                        currentOperation = "Found " + value + "!";
                        stepExplanation = "Search Successful!\n" +
                            "• Target value: " + value + "\n" +
                            "• Current node: " + node.value + "\n" +
                            "• " + value + " = " + node.value + " ✓\n" +
                            "• Search completed successfully";
                    } else {
                        currentOperation = "Step " + stepNum + ": Searching at " + node.value;
                        
                        if (value < node.value) {
                            stepExplanation = "Search Navigation:\n" +
                                "• Target: " + value + "\n" +
                                "• Current: " + node.value + "\n" +
                                "• " + value + " < " + node.value + " → Go LEFT\n" +
                                "• Continue search in left subtree";
                        } else if (value > node.value) {
                            stepExplanation = "Search Navigation:\n" +
                                "• Target: " + value + "\n" +
                                "• Current: " + node.value + "\n" +
                                "• " + value + " > " + node.value + " → Go RIGHT\n" +
                                "• Continue search in right subtree";
                        }
                    }
                    repaint();
                }
                
                @Override
                public void reset() {
                    highlightedNode = null;
                    visitedNode = null;
                    currentOperation = "";
                    stepExplanation = "";
                    repaint();
                }
                
                @Override
                public String getDescription() {
                    return "Searching for " + value + " at node " + node.value;
                }
            });
        }
        
        if (!found) {
            animationEngine.addStep(new AnimationEngine.AnimationStep() {
                @Override
                public void execute() {
                    highlightedNode = null;
                    visitedNode = null;
                    currentOperation = value + " not found in tree";
                    stepExplanation = "Search Failed!\n" +
                        "• Reached null pointer\n" +
                        "• " + value + " does not exist in tree\n" +
                        "• Search path exhausted\n" +
                        "• Value would be inserted here";
                    repaint();
                }
                
                @Override
                public void reset() {
                    currentOperation = "";
                    stepExplanation = "";
                    repaint();
                }
                
                @Override
                public String getDescription() {
                    return value + " not found - search unsuccessful";
                }
            });
        }
        
        return found;
    }
    
    public void performTraversal(String traversalType) {
        animationEngine.clearSteps();
        operationHistory.add("Traversal: " + traversalType);
        
        // Introduction step
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                currentOperation = "Starting " + traversalType + " traversal";
                stepExplanation = getTraversalExplanation(traversalType);
                highlightedNode = null;
                visitedNode = null;
                repaint();
            }
            
            @Override
            public void reset() {
                currentOperation = "";
                stepExplanation = "";
                highlightedNode = null;
                visitedNode = null;
                repaint();
            }
            
            @Override
            public String getDescription() {
                return "Initializing " + traversalType + " traversal";
            }
        });
        
        List<BSTNode> traversalOrder = new ArrayList<>();
        switch (traversalType) {
            case "In-Order":
                bst.inOrderTraversal(bst.getRoot(), traversalOrder);
                break;
            case "Pre-Order":
                bst.preOrderTraversal(bst.getRoot(), traversalOrder);
                break;
            case "Post-Order":
                bst.postOrderTraversal(bst.getRoot(), traversalOrder);
                break;
        }
        
        for (int i = 0; i < traversalOrder.size(); i++) {
            final BSTNode node = traversalOrder.get(i);
            final int stepNum = i + 1;
            final int currentIndex = i;
            
            animationEngine.addStep(new AnimationEngine.AnimationStep() {
                @Override
                public void execute() {
                    highlightedNode = node;
                    if (currentIndex > 0) visitedNode = traversalOrder.get(currentIndex - 1);
                    
                    currentOperation = traversalType + " Step " + stepNum + ": Visit " + node.value;
                    stepExplanation = "Traversal Progress:\n" +
                        "• Visiting node: " + node.value + "\n" +
                        "• Order: " + getTraversalSequence(traversalOrder, currentIndex) + "\n" +
                        "• Progress: " + stepNum + "/" + traversalOrder.size();
                    repaint();
                }
                
                @Override
                public void reset() {
                    highlightedNode = null;
                    visitedNode = null;
                    currentOperation = "";
                    stepExplanation = "";
                    repaint();
                }
                
                @Override
                public String getDescription() {
                    return traversalType + " traversal: visit " + node.value;
                }
            });
        }
        
        // Final step
        animationEngine.addStep(new AnimationEngine.AnimationStep() {
            @Override
            public void execute() {
                highlightedNode = null;
                visitedNode = null;
                currentOperation = traversalType + " traversal completed";
                stepExplanation = "Traversal Complete!\n" +
                    "• Final sequence: " + getTraversalSequence(traversalOrder, traversalOrder.size()) + "\n" +
                    "• Total nodes visited: " + traversalOrder.size() + "\n" +
                    "• Algorithm complexity: O(n)";
                repaint();
            }
            
            @Override
            public void reset() {
                currentOperation = "";
                stepExplanation = "";
                repaint();
            }
            
            @Override
            public String getDescription() {
                return traversalType + " traversal completed";
            }
        });
    }
    
    private String getTraversalExplanation(String traversalType) {
        switch (traversalType) {
            case "In-Order":
                return "In-Order Traversal:\n" +
                    "• Visit Left → Root → Right\n" +
                    "• Produces sorted sequence\n" +
                    "• Most common traversal\n" +
                    "• Useful for printing sorted data";
            case "Pre-Order":
                return "Pre-Order Traversal:\n" +
                    "• Visit Root → Left → Right\n" +
                    "• Root processed first\n" +
                    "• Good for tree copying\n" +
                    "• Creates prefix expression";
            case "Post-Order":
                return "Post-Order Traversal:\n" +
                    "• Visit Left → Right → Root\n" +
                    "• Root processed last\n" +
                    "• Good for tree deletion\n" +
                    "• Creates postfix expression";
            default:
                return "Unknown traversal type";
        }
    }
    
    private String getTraversalSequence(List<BSTNode> nodes, int upToIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(upToIndex, nodes.size()); i++) {
            if (i > 0) sb.append(", ");
            sb.append(nodes.get(i).value);
        }
        return sb.toString();
    }
    
    public void clear() {
        bst.clear();
        highlightedNode = null;
        visitedNode = null;
        currentOperation = "";
        stepExplanation = "";
        operationHistory.clear();
        animationEngine.clearSteps();
        repaint();
    }
    
    // Getter methods for enhanced functionality
    public List<String> getOperationHistory() {
        return new ArrayList<>(operationHistory);
    }
    
    public String getCurrentExplanation() {
        return stepExplanation;
    }
    
    public BSTAlgorithm getBSTAlgorithm() {
        return bst;
    }
}
