package com.raven.ds.core;

import com.raven.ds.modules.bst.BSTPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enhanced PDF Exporter for data structure visualizations
 * Generates comprehensive HTML analysis reports with detailed explanations
 */
public class PDFExporter {
    private static final String EXPORT_DIR = "exported";
    
    public static boolean exportToPDF(JPanel panel, String title, String content) {
        try {
            // Create export directory if it doesn't exist
            Files.createDirectories(Paths.get(EXPORT_DIR));
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = title.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".html";
            String filepath = EXPORT_DIR + File.separator + filename;
            
            // Generate comprehensive HTML report
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head>");
            html.append("<meta charset=\"UTF-8\">");
            html.append("<title>").append(title).append(" - Analysis Report</title>");
            html.append(generateCSS());
            html.append("</head><body>");
            
            // Generate content based on panel type
            if (panel instanceof BSTPanel) {
                html.append(generateBSTReport((BSTPanel) panel, title));
            } else {
                html.append(generateGenericReport(panel, title, content));
            }
            
            html.append("</body></html>");
            
            // Write to file
            try (FileWriter writer = new FileWriter(filepath)) {
                writer.write(html.toString());
            }
            
            // Show success message with option to open file
            int result = JOptionPane.showOptionDialog(null,
                "Analysis report generated successfully!\n\n" +
                "File: " + filename + "\n" +
                "Location: " + new File(filepath).getAbsolutePath() + "\n\n" +
                "Would you like to open the report?",
                "Export Complete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Open Report", "Close"},
                "Open Report");
            
            if (result == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new File(filepath));
            }
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Export failed: " + e.getMessage(),
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    private static String generateCSS() {
        return "<style>" +
            "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: #f8f9fa; }" +
            ".container { max-width: 1200px; margin: 0 auto; padding: 20px; }" +
            ".header { background: linear-gradient(135deg, #2c3e50, #3498db); color: white; padding: 30px; text-align: center; margin-bottom: 30px; border-radius: 10px; }" +
            ".header h1 { margin: 0; font-size: 2.5em; font-weight: 300; }" +
            ".header .subtitle { margin: 10px 0 0 0; font-size: 1.1em; opacity: 0.9; }" +
            ".section { background: white; margin: 20px 0; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
            ".section h2 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; margin-top: 0; }" +
            ".section h3 { color: #34495e; margin-top: 25px; }" +
            ".visualization { text-align: center; margin: 20px 0; }" +
            ".visualization img { max-width: 100%; border: 2px solid #bdc3c7; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }" +
            ".complexity-table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
            ".complexity-table th, .complexity-table td { border: 1px solid #bdc3c7; padding: 12px; text-align: left; }" +
            ".complexity-table th { background: #ecf0f1; font-weight: bold; color: #2c3e50; }" +
            ".complexity-table tr:nth-child(even) { background: #f8f9fa; }" +
            ".algorithm-box { background: #f8f9fa; border-left: 5px solid #3498db; padding: 20px; margin: 15px 0; }" +
            ".code-block { background: #2c3e50; color: #ecf0f1; padding: 20px; border-radius: 5px; overflow-x: auto; font-family: 'Courier New', monospace; }" +
            ".highlight { background: #f39c12; color: white; padding: 2px 6px; border-radius: 3px; }" +
            ".operation-history { background: #e8f5e8; border: 1px solid #27ae60; border-radius: 5px; padding: 15px; }" +
            ".step-explanation { background: #fff3cd; border: 1px solid #ffc107; border-radius: 5px; padding: 15px; margin: 10px 0; }" +
            ".footer { text-align: center; margin-top: 40px; padding: 20px; color: #7f8c8d; border-top: 1px solid #bdc3c7; }" +
            "</style>";
    }
    
    private static String generateBSTReport(BSTPanel bstPanel, String title) {
        StringBuilder html = new StringBuilder();
        
        // Header
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>Binary Search Tree Analysis</h1>");
        html.append("<div class=\"subtitle\">Interactive Data Structures Learning Suite</div>");
        html.append("<div class=\"subtitle\">Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"))).append("</div>");
        html.append("</div>");
        
        // Overview Section
        html.append("<div class=\"section\">");
        html.append("<h2>📊 Tree Overview</h2>");
        html.append("<div class=\"algorithm-box\">");
        html.append("<h3>Current Tree Statistics</h3>");
        html.append("<ul>");
        html.append("<li><strong>Total Nodes:</strong> ").append(bstPanel.getBSTAlgorithm().size()).append("</li>");
        html.append("<li><strong>Tree Height:</strong> ").append(bstPanel.getBSTAlgorithm().height()).append("</li>");
        html.append("<li><strong>Is Empty:</strong> ").append(bstPanel.getBSTAlgorithm().getRoot() == null ? "Yes" : "No").append("</li>");
        html.append("</ul>");
        html.append("</div>");
        html.append("</div>");
        
        // Visualization Section
        html.append("<div class=\"section\">");
        html.append("<h2>🌳 Tree Visualization</h2>");
        String imageFile = capturePanel(bstPanel, title);
        if (imageFile != null) {
            html.append("<div class=\"visualization\">");
            html.append("<img src=\"").append(imageFile).append("\" alt=\"BST Visualization\" />");
            html.append("<p><em>Current state of the Binary Search Tree</em></p>");
            html.append("</div>");
        }
        html.append("</div>");
        
        // Operation History
        List<String> history = bstPanel.getOperationHistory();
        if (!history.isEmpty()) {
            html.append("<div class=\"section\">");
            html.append("<h2>📋 Operation History</h2>");
            html.append("<div class=\"operation-history\">");
            html.append("<h3>Performed Operations:</h3>");
            html.append("<ol>");
            for (String operation : history) {
                html.append("<li>").append(operation).append("</li>");
            }
            html.append("</ol>");
            html.append("</div>");
            html.append("</div>");
        }
        
        // Current Step Explanation
        String explanation = bstPanel.getCurrentExplanation();
        if (!explanation.isEmpty()) {
            html.append("<div class=\"section\">");
            html.append("<h2>💡 Current Step Analysis</h2>");
            html.append("<div class=\"step-explanation\">");
            html.append("<pre>").append(explanation.replace("\n", "<br>")).append("</pre>");
            html.append("</div>");
            html.append("</div>");
        }
        
        // Algorithm Complexity Analysis
        html.append("<div class=\"section\">");
        html.append("<h2>⚡ Complexity Analysis</h2>");
        html.append("<table class=\"complexity-table\">");
        html.append("<thead>");
        html.append("<tr><th>Operation</th><th>Best Case</th><th>Average Case</th><th>Worst Case</th><th>Space Complexity</th></tr>");
        html.append("</thead>");
        html.append("<tbody>");
        html.append("<tr><td>Search</td><td>O(log n)</td><td>O(log n)</td><td>O(n)</td><td>O(1)</td></tr>");
        html.append("<tr><td>Insertion</td><td>O(log n)</td><td>O(log n)</td><td>O(n)</td><td>O(1)</td></tr>");
        html.append("<tr><td>Deletion</td><td>O(log n)</td><td>O(log n)</td><td>O(n)</td><td>O(1)</td></tr>");
        html.append("<tr><td>Traversal</td><td>O(n)</td><td>O(n)</td><td>O(n)</td><td>O(n)</td></tr>");
        html.append("</tbody>");
        html.append("</table>");
        html.append("<p><strong>Note:</strong> Worst case occurs when the tree becomes skewed (like a linked list). Best and average cases assume a balanced tree.</p>");
        html.append("</div>");
        
        // BST Properties and Theory
        html.append("<div class=\"section\">");
        html.append("<h2>📚 BST Properties & Theory</h2>");
        html.append("<div class=\"algorithm-box\">");
        html.append("<h3>Binary Search Tree Properties:</h3>");
        html.append("<ul>");
        html.append("<li><strong>Left Subtree Property:</strong> All nodes in the left subtree have values less than the root</li>");
        html.append("<li><strong>Right Subtree Property:</strong> All nodes in the right subtree have values greater than the root</li>");
        html.append("<li><strong>Recursive Property:</strong> Both left and right subtrees are also binary search trees</li>");
        html.append("<li><strong>No Duplicates:</strong> Traditional BST implementation does not allow duplicate values</li>");
        html.append("<li><strong>In-Order Traversal:</strong> Produces values in sorted (ascending) order</li>");
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<h3>Common Operations Explained:</h3>");
        html.append("<div class=\"algorithm-box\">");
        html.append("<h4>🔍 Search Operation:</h4>");
        html.append("<p>Starting from root, compare target with current node. Go left if target is smaller, right if larger. Continue until found or reach null.</p>");
        html.append("</div>");
        
        html.append("<div class=\"algorithm-box\">");
        html.append("<h4>➕ Insertion Operation:</h4>");
        html.append("<p>Find the correct position using search logic, then create a new node at the empty position. Maintains BST property automatically.</p>");
        html.append("</div>");
        
        html.append("<div class=\"algorithm-box\">");
        html.append("<h4>❌ Deletion Operation:</h4>");
        html.append("<p>Three cases: (1) Leaf node - simply remove, (2) One child - replace with child, (3) Two children - replace with inorder successor.</p>");
        html.append("</div>");
        html.append("</div>");
        
        // Pseudocode Section
        html.append("<div class=\"section\">");
        html.append("<h2>💻 Algorithm Pseudocode</h2>");
        
        html.append("<h3>Search Algorithm:</h3>");
        html.append("<div class=\"code-block\">");
        html.append("function search(root, target):<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;if root is null or root.value == target:<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return root<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;if target &lt; root.value:<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return search(root.left, target)<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;else:<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return search(root.right, target)");
        html.append("</div>");
        
        html.append("<h3>Insertion Algorithm:</h3>");
        html.append("<div class=\"code-block\">");
        html.append("function insert(root, value):<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;if root is null:<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new Node(value)<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;if value &lt; root.value:<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;root.left = insert(root.left, value)<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;else if value &gt; root.value:<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;root.right = insert(root.right, value)<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;return root");
        html.append("</div>");
        html.append("</div>");
        
        // Applications Section
        html.append("<div class=\"section\">");
        html.append("<h2>🚀 Real-World Applications</h2>");
        html.append("<div class=\"algorithm-box\">");
        html.append("<ul>");
        html.append("<li><strong>Database Indexing:</strong> B-trees (variants of BST) are used in database systems for efficient data retrieval</li>");
        html.append("<li><strong>Expression Parsing:</strong> Syntax trees in compilers use BST-like structures</li>");
        html.append("<li><strong>File Systems:</strong> Directory structures often use tree-based organization</li>");
        html.append("<li><strong>Priority Queues:</strong> Heap data structures (specialized trees) implement priority queues</li>");
        html.append("<li><strong>Auto-complete:</strong> Trie data structures (prefix trees) power search suggestions</li>");
        html.append("<li><strong>Game AI:</strong> Decision trees and game trees use similar concepts</li>");
        html.append("</ul>");
        html.append("</div>");
        html.append("</div>");
        
        // Footer
        html.append("<div class=\"footer\">");
        html.append("<p>Report generated by Interactive Data Structures Learning Suite</p>");
        html.append("<p>College Edition - Educational Purpose</p>");
        html.append("</div>");
        
        html.append("</div>"); // Close container
        
        return html.toString();
    }
    
    private static String generateGenericReport(JPanel panel, String title, String content) {
        StringBuilder html = new StringBuilder();
        
        // Header
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>").append(title).append(" Analysis</h1>");
        html.append("<div class=\"subtitle\">Interactive Data Structures Learning Suite</div>");
        html.append("<div class=\"subtitle\">Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"))).append("</div>");
        html.append("</div>");
        
        // Overview Section
        html.append("<div class=\"section\">");
        html.append("<h2>📊 Overview</h2>");
        html.append("<p>").append(content).append("</p>");
        html.append("</div>");
        
        // Visualization Section
        html.append("<div class=\"section\">");
        html.append("<h2>🎯 Visualization</h2>");
        String imageFile = capturePanel(panel, title);
        if (imageFile != null) {
            html.append("<div class=\"visualization\">");
            html.append("<img src=\"").append(imageFile).append("\" alt=\"Data Structure Visualization\" />");
            html.append("<p><em>Current state of the data structure</em></p>");
            html.append("</div>");
        }
        html.append("</div>");
        
        // Complexity Section (Generic)
        html.append("<div class=\"section\">");
        html.append("<h2>⚡ Complexity Analysis</h2>");
        html.append("<div class=\"algorithm-box\">");
        html.append("<p>Complexity analysis varies by algorithm and implementation.</p>");
        html.append("<p>Refer to the specific algorithm documentation for detailed complexity information.</p>");
        html.append("</div>");
        html.append("</div>");
        
        // Footer
        html.append("<div class=\"footer\">");
        html.append("<p>Report generated by Interactive Data Structures Learning Suite</p>");
        html.append("<p>College Edition - Educational Purpose</p>");
        html.append("</div>");
        
        html.append("</div>"); // Close container
        
        return html.toString();
    }
    
    private static String capturePanel(JPanel panel, String title) {
        try {
            // Create buffered image of the panel
            BufferedImage image = new BufferedImage(
                panel.getWidth(), 
                panel.getHeight(), 
                BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            panel.paint(g2d);
            g2d.dispose();
            
            // Save as PNG
            String filename = title.replaceAll("[^a-zA-Z0-9]", "_") + "_screenshot.png";
            String filepath = EXPORT_DIR + File.separator + filename;
            
            javax.imageio.ImageIO.write(image, "PNG", new File(filepath));
            
            return filename; // Return relative path for HTML
            
        } catch (Exception e) {
            System.err.println("Failed to capture panel screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create a comprehensive guide PDF with all data structure topics
     */
    public static void createComprehensiveGuide() {
        try {
            Files.createDirectories(Paths.get("docs"));
            String filepath = "docs" + File.separator + "DataStructuresGuide.html";
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head>");
            html.append("<title>Interactive Data Structures Guide</title>");
            html.append(generateCSS());
            html.append("</head><body>");
            
            html.append("<div class=\"container\">");
            html.append("<div class=\"header\">");
            html.append("<h1>Interactive Data Structures Learning Guide</h1>");
            html.append("<div class=\"subtitle\">Comprehensive Educational Resource</div>");
            html.append("</div>");
            
            html.append("<div class=\"section\">");
            html.append("<h2>📚 Table of Contents</h2>");
            html.append("<ol>");
            html.append("<li>Binary Search Tree (BST)</li>");
            html.append("<li>Graph - Shortest Path (Dijkstra)</li>");
            html.append("<li>Hash Table & Hash Functions</li>");
            html.append("<li>Binary Heap</li>");
            html.append("<li>Heapsort Algorithm</li>");
            html.append("<li>AVL Tree (Self-Balancing BST)</li>");
            html.append("<li>Dynamic Array (ArrayList)</li>");
            html.append("</ol>");
            html.append("</div>");
            
            // Add detailed content for each data structure
            String[] topics = {
                "Binary Search Tree (BST)",
                "Graph - Shortest Path (Dijkstra)",
                "Hash Table & Hash Functions", 
                "Binary Heap",
                "Heapsort Algorithm",
                "AVL Tree (Self-Balancing BST)",
                "Dynamic Array (ArrayList)"
            };
            
            for (String topic : topics) {
                html.append("<div class=\"section\">");
                html.append("<h2>").append(topic).append("</h2>");
                html.append("<div class=\"algorithm-box\">");
                html.append("<p>This section covers the fundamental concepts, implementation details, ");
                html.append("and practical applications of ").append(topic).append(".</p>");
                html.append("<p>Students can explore this data structure interactively using the ");
                html.append("visualization tools provided in the learning suite.</p>");
                html.append("</div>");
                html.append("</div>");
            }
            
            html.append("<div class=\"footer\">");
            html.append("<p>Interactive Data Structures Learning Suite - College Edition</p>");
            html.append("</div>");
            html.append("</div>");
            html.append("</body></html>");
            
            try (FileWriter writer = new FileWriter(filepath)) {
                writer.write(html.toString());
            }
            
            JOptionPane.showMessageDialog(null, 
                "Comprehensive guide created: " + filepath,
                "Guide Generated", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to create guide: " + e.getMessage(),
                "Guide Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}