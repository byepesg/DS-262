package com.raven.ds.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import java.awt.Desktop;

/**
 * PDF Document Generator for Data Structure Module Documentation
 * Generates comprehensive educational PDF documents explaining each module's
 * implementation, theory, and usage.
 */
public class PDFDocumentGenerator {
    private static final String DOCS_DIR = "docs";
    
    /**
     * Generate module documentation PDF
     * @param moduleName Name of the module (e.g., "BST", "AVL", etc.)
     * @param moduleTitle Full title of the module
     * @param content Documentation content (HTML format)
     * @return true if successful, false otherwise
     */
    public static boolean generateModuleDocumentation(String moduleName, String moduleTitle, String content) {
        try {
            // Create docs directory if it doesn't exist
            Files.createDirectories(Paths.get(DOCS_DIR));
            
            // Generate filename
            String filename = moduleName + "_Documentation.html";
            String filepath = DOCS_DIR + File.separator + filename;
            
            // Generate HTML document
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head>");
            html.append("<meta charset=\"UTF-8\">");
            html.append("<title>").append(moduleTitle).append(" - Module Documentation</title>");
            html.append(generateCSS());
            html.append("</head><body>");
            html.append(content);
            html.append(generateFooter());
            html.append("</body></html>");
            
            // Write to file
            try (FileWriter writer = new FileWriter(filepath)) {
                writer.write(html.toString());
            }
            
            // Show success message
            int result = JOptionPane.showOptionDialog(null,
                "Module documentation generated successfully!\n\n" +
                "File: " + filename + "\n" +
                "Location: " + new File(filepath).getAbsolutePath() + "\n\n" +
                "Would you like to open the documentation?",
                "Documentation Generated",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Open Document", "Close"},
                "Open Document");
            
            if (result == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new File(filepath));
            }
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Documentation generation failed: " + e.getMessage(),
                "Generation Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    private static String generateCSS() {
        return "<style>" +
            "* { margin: 0; padding: 0; box-sizing: border-box; }" +
            "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background: #f5f5f5; }" +
            ".container { max-width: 1200px; margin: 0 auto; padding: 20px; }" +
            ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 60px 40px; text-align: center; margin-bottom: 40px; border-radius: 10px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }" +
            ".header h1 { font-size: 3em; font-weight: 700; margin-bottom: 15px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }" +
            ".header .subtitle { font-size: 1.3em; opacity: 0.95; font-weight: 300; }" +
            ".header .date { margin-top: 15px; font-size: 0.95em; opacity: 0.85; }" +
            ".section { background: white; margin: 30px 0; padding: 40px; border-radius: 8px; box-shadow: 0 2px 15px rgba(0,0,0,0.1); }" +
            ".section h2 { color: #667eea; font-size: 2em; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 3px solid #667eea; }" +
            ".section h3 { color: #764ba2; font-size: 1.5em; margin-top: 30px; margin-bottom: 15px; }" +
            ".section h4 { color: #555; font-size: 1.2em; margin-top: 20px; margin-bottom: 10px; }" +
            ".section p { margin: 15px 0; text-align: justify; }" +
            ".section ul, .section ol { margin: 15px 0 15px 30px; }" +
            ".section li { margin: 8px 0; }" +
            ".info-box { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 25px; border-radius: 8px; margin: 25px 0; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }" +
            ".info-box h3 { color: white; margin-top: 0; border-bottom: 2px solid rgba(255,255,255,0.3); padding-bottom: 10px; }" +
            ".algorithm-box { background: #f8f9fa; border-left: 5px solid #667eea; padding: 25px; margin: 25px 0; border-radius: 5px; }" +
            ".algorithm-box h3 { color: #667eea; margin-top: 0; }" +
            ".code-block { background: #2d3748; color: #e2e8f0; padding: 25px; border-radius: 8px; overflow-x: auto; font-family: 'Courier New', Consolas, monospace; font-size: 14px; line-height: 1.5; margin: 20px 0; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
            ".code-block .comment { color: #68d391; }" +
            ".code-block .keyword { color: #fc8181; font-weight: bold; }" +
            ".code-block .type { color: #90cdf4; }" +
            ".code-block .method { color: #fbd38d; }" +
            ".code-block .string { color: #f6ad55; }" +
            ".complexity-table { width: 100%; border-collapse: collapse; margin: 25px 0; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".complexity-table th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px; text-align: left; font-weight: 600; }" +
            ".complexity-table td { border: 1px solid #e2e8f0; padding: 12px 15px; }" +
            ".complexity-table tr:nth-child(even) { background: #f7fafc; }" +
            ".complexity-table tr:hover { background: #edf2f7; }" +
            ".highlight { background: #fbd38d; padding: 3px 8px; border-radius: 4px; font-weight: 600; }" +
            ".warning-box { background: #fff5f5; border-left: 5px solid #fc8181; padding: 20px; margin: 25px 0; border-radius: 5px; }" +
            ".success-box { background: #f0fff4; border-left: 5px solid #68d391; padding: 20px; margin: 25px 0; border-radius: 5px; }" +
            ".note-box { background: #ebf8ff; border-left: 5px solid #4299e1; padding: 20px; margin: 25px 0; border-radius: 5px; }" +
            ".example-box { background: #fffff0; border: 2px dashed #f6ad55; padding: 25px; margin: 25px 0; border-radius: 8px; }" +
            ".example-box h4 { color: #d69e2e; margin-top: 0; }" +
            ".steps-list { counter-reset: step-counter; list-style: none; padding-left: 0; }" +
            ".steps-list li { counter-increment: step-counter; margin: 20px 0; padding: 20px; background: #f7fafc; border-left: 4px solid #667eea; border-radius: 5px; position: relative; padding-left: 60px; }" +
            ".steps-list li::before { content: counter(step-counter); position: absolute; left: 15px; top: 50%; transform: translateY(-50%); background: #667eea; color: white; width: 35px; height: 35px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; }" +
            ".comparison-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin: 25px 0; }" +
            ".comparison-item { background: #f7fafc; padding: 20px; border-radius: 8px; border: 2px solid #e2e8f0; }" +
            ".comparison-item h4 { color: #667eea; margin-top: 0; }" +
            ".footer { text-align: center; margin-top: 60px; padding: 30px; color: #718096; border-top: 2px solid #e2e8f0; background: white; border-radius: 8px; }" +
            ".footer p { margin: 8px 0; }" +
            "@media print { body { background: white; } .section { box-shadow: none; page-break-inside: avoid; } }" +
            "</style>";
    }
    
    private static String generateFooter() {
        return "<div class=\"footer\">" +
            "<p><strong>Interactive Data Structures Learning Suite</strong></p>" +
            "<p>Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm:ss")) + "</p>" +
            "<p>© 2025 - Educational Resource for Computer Science Students</p>" +
            "<p style=\"margin-top: 15px; font-size: 0.9em; color: #a0aec0;\">This documentation can be saved as PDF using your browser's Print to PDF function</p>" +
            "</div>";
    }
    
    /**
     * Generate BST module documentation
     */
    public static String generateBSTDocumentation() {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class=\"container\">");
        
        // Header
        content.append("<div class=\"header\">");
        content.append("<h1>📊 Binary Search Tree (BST)</h1>");
        content.append("<div class=\"subtitle\">Complete Module Documentation</div>");
        content.append("<div class=\"date\">Interactive Data Structures Learning Suite</div>");
        content.append("</div>");
        
        // Introduction
        content.append("<div class=\"section\">");
        content.append("<h2>1. Introduction</h2>");
        content.append("<p>A <span class=\"highlight\">Binary Search Tree (BST)</span> is a hierarchical data structure where each node has at most two children (left and right). It follows a specific ordering property that makes searching, insertion, and deletion operations efficient.</p>");
        content.append("<div class=\"info-box\">");
        content.append("<h3>Key Characteristics</h3>");
        content.append("<ul>");
        content.append("<li><strong>Ordered Structure:</strong> All values in the left subtree are less than the root, and all values in the right subtree are greater</li>");
        content.append("<li><strong>Recursive Property:</strong> Both left and right subtrees are themselves binary search trees</li>");
        content.append("<li><strong>No Duplicates:</strong> Traditional BST implementations don't allow duplicate values</li>");
        content.append("<li><strong>Dynamic Size:</strong> Can grow and shrink as needed</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        // Implementation Details
        content.append("<div class=\"section\">");
        content.append("<h2>2. Implementation Details</h2>");
        content.append("<h3>2.1 Node Structure</h3>");
        content.append("<p>The fundamental building block of a BST is the node, which contains:</p>");
        content.append("<div class=\"code-block\">");
        content.append("<span class=\"keyword\">public class</span> <span class=\"type\">BSTNode</span> {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"comment\">// Data fields</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">private</span> <span class=\"type\">int</span> value;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"comment\">// The value stored in the node</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">private</span> <span class=\"type\">BSTNode</span> left;&nbsp;&nbsp;<span class=\"comment\">// Reference to left child</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">private</span> <span class=\"type\">BSTNode</span> right;&nbsp;<span class=\"comment\">// Reference to right child</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">private</span> <span class=\"type\">int</span> x, y;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"comment\">// Coordinates for visualization</span><br>");
        content.append("}");
        content.append("</div>");
        
        content.append("<h3>2.2 Core Operations</h3>");
        
        // Insert Operation
        content.append("<div class=\"algorithm-box\">");
        content.append("<h4>Insert Operation</h4>");
        content.append("<p>Insertion follows the BST property to find the correct position:</p>");
        content.append("<ol class=\"steps-list\">");
        content.append("<li>Start at the root node</li>");
        content.append("<li>If the value to insert is less than current node, go left; otherwise go right</li>");
        content.append("<li>Repeat until finding an empty position</li>");
        content.append("<li>Create a new node at that position</li>");
        content.append("</ol>");
        content.append("<div class=\"code-block\">");
        content.append("<span class=\"keyword\">public</span> <span class=\"type\">BSTNode</span> <span class=\"method\">insert</span>(<span class=\"type\">BSTNode</span> node, <span class=\"type\">int</span> value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (node == <span class=\"keyword\">null</span>) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">return new</span> <span class=\"type\">BSTNode</span>(value);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (value &lt; node.value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;node.left = <span class=\"method\">insert</span>(node.left, value);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;} <span class=\"keyword\">else if</span> (value &gt; node.value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;node.right = <span class=\"method\">insert</span>(node.right, value);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">return</span> node;<br>");
        content.append("}");
        content.append("</div>");
        content.append("</div>");
        
        // Search Operation
        content.append("<div class=\"algorithm-box\">");
        content.append("<h4>Search Operation</h4>");
        content.append("<p>Searching efficiently locates a value by following the BST property:</p>");
        content.append("<ol class=\"steps-list\">");
        content.append("<li>Start at the root node</li>");
        content.append("<li>Compare target value with current node</li>");
        content.append("<li>If equal, value is found</li>");
        content.append("<li>If less, search left subtree; if greater, search right subtree</li>");
        content.append("<li>If reach null, value doesn't exist</li>");
        content.append("</ol>");
        content.append("<div class=\"code-block\">");
        content.append("<span class=\"keyword\">public</span> <span class=\"type\">boolean</span> <span class=\"method\">search</span>(<span class=\"type\">BSTNode</span> node, <span class=\"type\">int</span> value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (node == <span class=\"keyword\">null</span>) <span class=\"keyword\">return false</span>;<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (value == node.value) <span class=\"keyword\">return true</span>;<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (value &lt; node.value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">return</span> <span class=\"method\">search</span>(node.left, value);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">return</span> <span class=\"method\">search</span>(node.right, value);<br>");
        content.append("}");
        content.append("</div>");
        content.append("</div>");
        
        // Delete Operation
        content.append("<div class=\"algorithm-box\">");
        content.append("<h4>Delete Operation</h4>");
        content.append("<p>Deletion is the most complex operation with three cases:</p>");
        content.append("<ol class=\"steps-list\">");
        content.append("<li><strong>Leaf Node:</strong> Simply remove the node</li>");
        content.append("<li><strong>One Child:</strong> Replace node with its child</li>");
        content.append("<li><strong>Two Children:</strong> Find inorder successor (smallest in right subtree), replace value, delete successor</li>");
        content.append("</ol>");
        content.append("<div class=\"code-block\">");
        content.append("<span class=\"keyword\">public</span> <span class=\"type\">BSTNode</span> <span class=\"method\">delete</span>(<span class=\"type\">BSTNode</span> node, <span class=\"type\">int</span> value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (node == <span class=\"keyword\">null</span>) <span class=\"keyword\">return null</span>;<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (value &lt; node.value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;node.left = <span class=\"method\">delete</span>(node.left, value);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;} <span class=\"keyword\">else if</span> (value &gt; node.value) {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;node.right = <span class=\"method\">delete</span>(node.right, value);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;} <span class=\"keyword\">else</span> {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"comment\">// Node with one or no child</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (node.left == <span class=\"keyword\">null</span>) <span class=\"keyword\">return</span> node.right;<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">if</span> (node.right == <span class=\"keyword\">null</span>) <span class=\"keyword\">return</span> node.left;<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"comment\">// Node with two children</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;node.value = <span class=\"method\">findMin</span>(node.right);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;node.right = <span class=\"method\">delete</span>(node.right, node.value);<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">return</span> node;<br>");
        content.append("}");
        content.append("</div>");
        content.append("</div>");
        content.append("</div>");
        
        // Complexity Analysis
        content.append("<div class=\"section\">");
        content.append("<h2>3. Time and Space Complexity</h2>");
        content.append("<table class=\"complexity-table\">");
        content.append("<thead>");
        content.append("<tr><th>Operation</th><th>Best Case</th><th>Average Case</th><th>Worst Case</th><th>Space Complexity</th></tr>");
        content.append("</thead>");
        content.append("<tbody>");
        content.append("<tr><td><strong>Search</strong></td><td>O(log n)</td><td>O(log n)</td><td>O(n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Insert</strong></td><td>O(log n)</td><td>O(log n)</td><td>O(n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Delete</strong></td><td>O(log n)</td><td>O(log n)</td><td>O(n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Inorder Traversal</strong></td><td>O(n)</td><td>O(n)</td><td>O(n)</td><td>O(h)</td></tr>");
        content.append("<tr><td><strong>Space (Tree)</strong></td><td>O(n)</td><td>O(n)</td><td>O(n)</td><td>-</td></tr>");
        content.append("</tbody>");
        content.append("</table>");
        content.append("<div class=\"note-box\">");
        content.append("<p><strong>Note:</strong> Best and average cases assume a balanced tree. Worst case O(n) occurs when the tree becomes skewed (degenerates into a linked list). The height h = log n for balanced trees and h = n for skewed trees.</p>");
        content.append("</div>");
        content.append("</div>");
        
        // Visualization Features
        content.append("<div class=\"section\">");
        content.append("<h2>4. Visualization Features in This Module</h2>");
        content.append("<div class=\"comparison-grid\">");
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>🎨 Visual Elements</h4>");
        content.append("<ul>");
        content.append("<li>Node highlighting during operations</li>");
        content.append("<li>Color-coded states (normal, visiting, found)</li>");
        content.append("<li>Smooth animations between steps</li>");
        content.append("<li>Clear edge connections</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>⚙️ Interactive Controls</h4>");
        content.append("<ul>");
        content.append("<li>Insert, delete, and search operations</li>");
        content.append("<li>Animation speed control</li>");
        content.append("<li>Step-by-step execution</li>");
        content.append("<li>Operation history tracking</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>📊 Analysis Tools</h4>");
        content.append("<ul>");
        content.append("<li>Real-time tree statistics</li>");
        content.append("<li>Step explanations</li>");
        content.append("<li>Multiple traversal modes</li>");
        content.append("<li>Export functionality</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        content.append("</div>");
        
        // Use Cases
        content.append("<div class=\"section\">");
        content.append("<h2>5. Real-World Applications</h2>");
        content.append("<div class=\"example-box\">");
        content.append("<h4>Common Use Cases:</h4>");
        content.append("<ul>");
        content.append("<li><strong>Database Indexing:</strong> BSTs are used in database systems for efficient data retrieval</li>");
        content.append("<li><strong>File Systems:</strong> Directory structures often use tree-based organization</li>");
        content.append("<li><strong>Expression Parsing:</strong> Compiler design uses BSTs for syntax trees</li>");
        content.append("<li><strong>Priority Queues:</strong> Can be implemented using BSTs</li>");
        content.append("<li><strong>Auto-complete:</strong> Dictionary implementations for word suggestions</li>");
        content.append("<li><strong>Router Tables:</strong> Network routing algorithms</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        // Advantages and Limitations
        content.append("<div class=\"section\">");
        content.append("<h2>6. Advantages and Limitations</h2>");
        content.append("<div class=\"comparison-grid\">");
        content.append("<div class=\"comparison-item\" style=\"border-color: #68d391;\">");
        content.append("<h4 style=\"color: #38a169;\">✅ Advantages</h4>");
        content.append("<ul>");
        content.append("<li>Efficient searching compared to arrays</li>");
        content.append("<li>Dynamic size (grows/shrinks as needed)</li>");
        content.append("<li>Inorder traversal gives sorted data</li>");
        content.append("<li>Simple to understand and implement</li>");
        content.append("<li>Flexible for various applications</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("<div class=\"comparison-item\" style=\"border-color: #fc8181;\">");
        content.append("<h4 style=\"color: #e53e3e;\">⚠️ Limitations</h4>");
        content.append("<ul>");
        content.append("<li>Can degenerate to O(n) if not balanced</li>");
        content.append("<li>No random access like arrays</li>");
        content.append("<li>Extra memory for pointers</li>");
        content.append("<li>Performance depends on insertion order</li>");
        content.append("<li>Not cache-friendly for modern CPUs</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        content.append("<div class=\"note-box\">");
        content.append("<p><strong>Solution:</strong> Use self-balancing trees like AVL or Red-Black trees to guarantee O(log n) performance.</p>");
        content.append("</div>");
        content.append("</div>");
        
        // Code Example
        content.append("<div class=\"section\">");
        content.append("<h2>7. Complete Code Example</h2>");
        content.append("<p>Here's how the BST module is implemented in our visualization tool:</p>");
        content.append("<div class=\"code-block\">");
        content.append("<span class=\"comment\">// BSTAlgorithm.java - Core BST implementation</span><br>");
        content.append("<span class=\"keyword\">public class</span> <span class=\"type\">BSTAlgorithm</span> {<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">private</span> <span class=\"type\">BSTNode</span> root;<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">private</span> <span class=\"type\">int</span> nodeCount = 0;<br><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"comment\">// Main operations with animation support</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">public void</span> <span class=\"method\">insert</span>(<span class=\"type\">int</span> value) { ... }<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">public boolean</span> <span class=\"method\">delete</span>(<span class=\"type\">int</span> value) { ... }<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">public boolean</span> <span class=\"method\">search</span>(<span class=\"type\">int</span> value) { ... }<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">public void</span> <span class=\"method\">clear</span>() { root = null; }<br><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"comment\">// Traversal methods</span><br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">public</span> <span class=\"type\">List&lt;Integer&gt;</span> <span class=\"method\">inorderTraversal</span>() { ... }<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">public</span> <span class=\"type\">List&lt;Integer&gt;</span> <span class=\"method\">preorderTraversal</span>() { ... }<br>");
        content.append("&nbsp;&nbsp;&nbsp;&nbsp;<span class=\"keyword\">public</span> <span class=\"type\">List&lt;Integer&gt;</span> <span class=\"method\">postorderTraversal</span>() { ... }<br>");
        content.append("}");
        content.append("</div>");
        content.append("<p>The visualization panel (<code>BSTPanel.java</code>) handles rendering, while the control panel (<code>BSTControls.java</code>) manages user interactions and animation flow.</p>");
        content.append("</div>");
        
        // Learning Tips
        content.append("<div class=\"section\">");
        content.append("<h2>8. Learning Tips & Best Practices</h2>");
        content.append("<div class=\"success-box\">");
        content.append("<h4>📚 Study Recommendations:</h4>");
        content.append("<ol>");
        content.append("<li><strong>Start Simple:</strong> Begin with insert and search operations before tackling deletion</li>");
        content.append("<li><strong>Visualize:</strong> Draw trees on paper while learning to understand structure</li>");
        content.append("<li><strong>Practice Traversals:</strong> Master inorder, preorder, and postorder traversals</li>");
        content.append("<li><strong>Understand Recursion:</strong> BST operations are naturally recursive</li>");
        content.append("<li><strong>Test Edge Cases:</strong> Empty tree, single node, skewed trees</li>");
        content.append("<li><strong>Compare with AVL:</strong> Understand why balanced trees are important</li>");
        content.append("</ol>");
        content.append("</div>");
        content.append("<div class=\"warning-box\">");
        content.append("<h4>⚠️ Common Pitfalls:</h4>");
        content.append("<ul>");
        content.append("<li>Forgetting to update parent references during deletion</li>");
        content.append("<li>Not handling null cases properly</li>");
        content.append("<li>Confusing left and right subtree rules</li>");
        content.append("<li>Ignoring tree balance (can lead to poor performance)</li>");
        content.append("<li>Memory leaks in languages without garbage collection</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("</div>"); // Close container
        
        return content.toString();
    }
    
    /**
     * Generate AVL Tree module documentation
     */
    public static String generateAVLDocumentation() {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class=\"container\">");
        
        // Header
        content.append("<div class=\"header\">");
        content.append("<h1>⚖️ AVL Tree (Self-Balancing BST)</h1>");
        content.append("<div class=\"subtitle\">Complete Module Documentation</div>");
        content.append("<div class=\"date\">Interactive Data Structures Learning Suite</div>");
        content.append("</div>");
        
        // Introduction
        content.append("<div class=\"section\">");
        content.append("<h2>1. Introduction</h2>");
        content.append("<p>An <span class=\"highlight\">AVL Tree</span> is a self-balancing Binary Search Tree where the heights of left and right subtrees of any node differ by at most one. Named after inventors Adelson-Velsky and Landis (1962), it guarantees O(log n) time complexity for search, insert, and delete operations.</p>");
        content.append("<div class=\"info-box\">");
        content.append("<h3>Key Characteristics</h3>");
        content.append("<ul>");
        content.append("<li><strong>Balance Factor:</strong> For every node, |height(left) - height(right)| ≤ 1</li>");
        content.append("<li><strong>Self-Balancing:</strong> Automatically rebalances after insertions and deletions</li>");
        content.append("<li><strong>Guaranteed Performance:</strong> O(log n) worst-case for all operations</li>");
        content.append("<li><strong>Rotation Operations:</strong> Uses single and double rotations to maintain balance</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        // Balance and Rotations
        content.append("<div class=\"section\">");
        content.append("<h2>2. Balance Factor and Rotations</h2>");
        content.append("<h3>2.1 Balance Factor</h3>");
        content.append("<p>The balance factor of a node is calculated as:</p>");
        content.append("<div class=\"code-block\">");
        content.append("Balance Factor = height(left subtree) - height(right subtree)");
        content.append("</div>");
        content.append("<p>Valid values: -1, 0, or +1. Any other value means the tree needs rebalancing.</p>");
        
        content.append("<h3>2.2 Rotation Types</h3>");
        content.append("<div class=\"comparison-grid\">");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Left Rotation (RR Case)</h4>");
        content.append("<p>Used when the right subtree of the right child is too heavy.</p>");
        content.append("<div class=\"code-block\">");
        content.append("   y          x<br>");
        content.append("  / \\   →    / \\<br>");
        content.append(" A   x      y   C<br>");
        content.append("    / \\    / \\<br>");
        content.append("   B   C  A   B");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Right Rotation (LL Case)</h4>");
        content.append("<p>Used when the left subtree of the left child is too heavy.</p>");
        content.append("<div class=\"code-block\">");
        content.append("     y        x<br>");
        content.append("    / \\  →   / \\<br>");
        content.append("   x   C    A   y<br>");
        content.append("  / \\          / \\<br>");
        content.append(" A   B        B   C");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Left-Right Rotation (LR Case)</h4>");
        content.append("<p>Left rotate on left child, then right rotate on node.</p>");
        content.append("</div>");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Right-Left Rotation (RL Case)</h4>");
        content.append("<p>Right rotate on right child, then left rotate on node.</p>");
        content.append("</div>");
        
        content.append("</div>");
        content.append("</div>");
        
        // Complexity
        content.append("<div class=\"section\">");
        content.append("<h2>3. Time and Space Complexity</h2>");
        content.append("<table class=\"complexity-table\">");
        content.append("<thead>");
        content.append("<tr><th>Operation</th><th>Average Case</th><th>Worst Case</th><th>Space</th></tr>");
        content.append("</thead>");
        content.append("<tbody>");
        content.append("<tr><td><strong>Search</strong></td><td>O(log n)</td><td>O(log n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Insert</strong></td><td>O(log n)</td><td>O(log n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Delete</strong></td><td>O(log n)</td><td>O(log n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Rotation</strong></td><td>O(1)</td><td>O(1)</td><td>O(1)</td></tr>");
        content.append("</tbody>");
        content.append("</table>");
        content.append("<div class=\"success-box\">");
        content.append("<p><strong>Advantage over BST:</strong> AVL trees guarantee O(log n) performance even in worst case, unlike regular BST which can degenerate to O(n).</p>");
        content.append("</div>");
        content.append("</div>");
        
        // Applications
        content.append("<div class=\"section\">");
        content.append("<h2>4. Real-World Applications</h2>");
        content.append("<ul>");
        content.append("<li><strong>Database Systems:</strong> Index structures requiring guaranteed performance</li>");
        content.append("<li><strong>Memory Management:</strong> Allocation and deallocation in operating systems</li>");
        content.append("<li><strong>File Systems:</strong> Directory structures with frequent lookups</li>");
        content.append("<li><strong>Network Routing:</strong> Routing tables in network devices</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>"); // Close container
        
        return content.toString();
    }
    
    /**
     * Generate Dynamic Array module documentation
     */
    public static String generateDynamicArrayDocumentation() {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class=\"container\">");
        
        content.append("<div class=\"header\">");
        content.append("<h1>📦 Dynamic Array</h1>");
        content.append("<div class=\"subtitle\">Complete Module Documentation</div>");
        content.append("<div class=\"date\">Interactive Data Structures Learning Suite</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>1. Introduction</h2>");
        content.append("<p>A <span class=\"highlight\">Dynamic Array</span> (also called ArrayList or Vector) is a resizable array that automatically grows when elements are added. It combines the benefits of arrays (random access) with dynamic sizing.</p>");
        content.append("<div class=\"info-box\">");
        content.append("<h3>Key Features</h3>");
        content.append("<ul>");
        content.append("<li><strong>Automatic Resizing:</strong> Grows as needed, typically doubling in size</li>");
        content.append("<li><strong>Random Access:</strong> O(1) access time by index</li>");
        content.append("<li><strong>Contiguous Memory:</strong> Elements stored in consecutive memory locations</li>");
        content.append("<li><strong>Cache Friendly:</strong> Better performance due to locality of reference</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>2. Resizing Strategy</h2>");
        content.append("<div class=\"algorithm-box\">");
        content.append("<h3>Growth Strategy</h3>");
        content.append("<p>When the array is full, it typically:</p>");
        content.append("<ol class=\"steps-list\">");
        content.append("<li>Allocates new array with 2× capacity</li>");
        content.append("<li>Copies all existing elements to new array</li>");
        content.append("<li>Adds the new element</li>");
        content.append("<li>Deallocates old array</li>");
        content.append("</ol>");
        content.append("<p><strong>Amortized Cost:</strong> While a single resize is O(n), the amortized cost of insertions is O(1).</p>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>3. Time Complexity</h2>");
        content.append("<table class=\"complexity-table\">");
        content.append("<thead>");
        content.append("<tr><th>Operation</th><th>Best Case</th><th>Average Case</th><th>Worst Case</th></tr>");
        content.append("</thead>");
        content.append("<tbody>");
        content.append("<tr><td><strong>Access by Index</strong></td><td>O(1)</td><td>O(1)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Append</strong></td><td>O(1)</td><td>O(1)</td><td>O(n)</td></tr>");
        content.append("<tr><td><strong>Insert at Position</strong></td><td>O(1)</td><td>O(n)</td><td>O(n)</td></tr>");
        content.append("<tr><td><strong>Delete</strong></td><td>O(1)</td><td>O(n)</td><td>O(n)</td></tr>");
        content.append("<tr><td><strong>Search</strong></td><td>O(1)</td><td>O(n)</td><td>O(n)</td></tr>");
        content.append("</tbody>");
        content.append("</table>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>4. Applications</h2>");
        content.append("<ul>");
        content.append("<li><strong>Collections Framework:</strong> Foundation for ArrayList in Java, vector in C++</li>");
        content.append("<li><strong>General Purpose Storage:</strong> Most common data structure for storing sequences</li>");
        content.append("<li><strong>Stack Implementation:</strong> Efficient stack with array backing</li>");
        content.append("<li><strong>Buffer Management:</strong> Dynamic buffers in I/O operations</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>");
        return content.toString();
    }
    
    /**
     * Generate Hash Table module documentation
     */
    public static String generateHashTableDocumentation() {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class=\"container\">");
        
        content.append("<div class=\"header\">");
        content.append("<h1>🗂️ Hash Table</h1>");
        content.append("<div class=\"subtitle\">Complete Module Documentation</div>");
        content.append("<div class=\"date\">Interactive Data Structures Learning Suite</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>1. Introduction</h2>");
        content.append("<p>A <span class=\"highlight\">Hash Table</span> is a data structure that implements an associative array, mapping keys to values using a hash function. It provides average O(1) time complexity for insertions, deletions, and lookups.</p>");
        content.append("<div class=\"info-box\">");
        content.append("<h3>Core Concepts</h3>");
        content.append("<ul>");
        content.append("<li><strong>Hash Function:</strong> Converts keys to array indices</li>");
        content.append("<li><strong>Collision Resolution:</strong> Handles multiple keys mapping to same index</li>");
        content.append("<li><strong>Load Factor:</strong> Ratio of filled slots to total capacity</li>");
        content.append("<li><strong>Rehashing:</strong> Resizing when load factor exceeds threshold</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>2. Collision Resolution Methods</h2>");
        content.append("<div class=\"comparison-grid\">");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Chaining</h4>");
        content.append("<p>Store multiple elements at same index using linked lists.</p>");
        content.append("<ul>");
        content.append("<li>Simple to implement</li>");
        content.append("<li>No clustering</li>");
        content.append("<li>Extra memory for pointers</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Open Addressing</h4>");
        content.append("<p>Find next available slot when collision occurs.</p>");
        content.append("<ul>");
        content.append("<li>Linear probing</li>");
        content.append("<li>Quadratic probing</li>");
        content.append("<li>Double hashing</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>3. Time Complexity</h2>");
        content.append("<table class=\"complexity-table\">");
        content.append("<thead>");
        content.append("<tr><th>Operation</th><th>Average Case</th><th>Worst Case</th></tr>");
        content.append("</thead>");
        content.append("<tbody>");
        content.append("<tr><td><strong>Insert</strong></td><td>O(1)</td><td>O(n)</td></tr>");
        content.append("<tr><td><strong>Delete</strong></td><td>O(1)</td><td>O(n)</td></tr>");
        content.append("<tr><td><strong>Search</strong></td><td>O(1)</td><td>O(n)</td></tr>");
        content.append("</tbody>");
        content.append("</table>");
        content.append("<div class=\"note-box\">");
        content.append("<p><strong>Note:</strong> Worst case occurs with many collisions. Good hash function and proper load factor management ensure average case performance.</p>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>4. Applications</h2>");
        content.append("<ul>");
        content.append("<li><strong>Databases:</strong> Fast record lookup by key</li>");
        content.append("<li><strong>Caching:</strong> Web browsers, CDNs, memoization</li>");
        content.append("<li><strong>Symbol Tables:</strong> Compiler implementations</li>");
        content.append("<li><strong>Dictionaries:</strong> HashMap, HashSet implementations</li>");
        content.append("<li><strong>Password Verification:</strong> Storing password hashes</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>");
        return content.toString();
    }
    
    /**
     * Generate Binary Heap module documentation
     */
    public static String generateBinaryHeapDocumentation() {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class=\"container\">");
        
        content.append("<div class=\"header\">");
        content.append("<h1>🏔️ Binary Heap</h1>");
        content.append("<div class=\"subtitle\">Complete Module Documentation</div>");
        content.append("<div class=\"date\">Interactive Data Structures Learning Suite</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>1. Introduction</h2>");
        content.append("<p>A <span class=\"highlight\">Binary Heap</span> is a complete binary tree that satisfies the heap property. It's commonly used to implement priority queues and in heap sort algorithm.</p>");
        content.append("<div class=\"info-box\">");
        content.append("<h3>Heap Properties</h3>");
        content.append("<ul>");
        content.append("<li><strong>Max Heap:</strong> Parent ≥ children (largest at root)</li>");
        content.append("<li><strong>Min Heap:</strong> Parent ≤ children (smallest at root)</li>");
        content.append("<li><strong>Complete Binary Tree:</strong> All levels filled except possibly last</li>");
        content.append("<li><strong>Array Representation:</strong> Efficient storage without pointers</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>2. Array Representation</h2>");
        content.append("<div class=\"algorithm-box\">");
        content.append("<h3>Index Calculations</h3>");
        content.append("<p>For a node at index i:</p>");
        content.append("<div class=\"code-block\">");
        content.append("Parent index = (i - 1) / 2<br>");
        content.append("Left child = 2 * i + 1<br>");
        content.append("Right child = 2 * i + 2");
        content.append("</div>");
        content.append("<p>This allows efficient heap operations without pointer manipulation.</p>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>3. Core Operations</h2>");
        content.append("<div class=\"algorithm-box\">");
        content.append("<h4>Heapify Up (Bubble Up)</h4>");
        content.append("<p>After insertion, restore heap property by moving element up.</p>");
        content.append("<h4>Heapify Down (Bubble Down)</h4>");
        content.append("<p>After deletion, restore heap property by moving element down.</p>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>4. Time Complexity</h2>");
        content.append("<table class=\"complexity-table\">");
        content.append("<thead>");
        content.append("<tr><th>Operation</th><th>Time Complexity</th></tr>");
        content.append("</thead>");
        content.append("<tbody>");
        content.append("<tr><td><strong>Insert</strong></td><td>O(log n)</td></tr>");
        content.append("<tr><td><strong>Delete Max/Min</strong></td><td>O(log n)</td></tr>");
        content.append("<tr><td><strong>Get Max/Min</strong></td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Build Heap</strong></td><td>O(n)</td></tr>");
        content.append("</tbody>");
        content.append("</table>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>5. Applications</h2>");
        content.append("<ul>");
        content.append("<li><strong>Priority Queues:</strong> Task scheduling, event simulation</li>");
        content.append("<li><strong>Heap Sort:</strong> Efficient O(n log n) sorting algorithm</li>");
        content.append("<li><strong>Graph Algorithms:</strong> Dijkstra's, Prim's algorithms</li>");
        content.append("<li><strong>Median Finding:</strong> Using two heaps</li>");
        content.append("<li><strong>Top K Problems:</strong> Finding k largest/smallest elements</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>");
        return content.toString();
    }
    
    /**
     * Generate Heapsort module documentation
     */
    public static String generateHeapsortDocumentation() {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class=\"container\">");
        
        content.append("<div class=\"header\">");
        content.append("<h1>🔄 Heapsort Algorithm</h1>");
        content.append("<div class=\"subtitle\">Complete Module Documentation</div>");
        content.append("<div class=\"date\">Interactive Data Structures Learning Suite</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>1. Introduction</h2>");
        content.append("<p><span class=\"highlight\">Heapsort</span> is a comparison-based sorting algorithm that uses a binary heap data structure. It's an in-place algorithm with O(n log n) time complexity in all cases.</p>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>2. Algorithm Steps</h2>");
        content.append("<ol class=\"steps-list\">");
        content.append("<li>Build a max heap from the input array (O(n))</li>");
        content.append("<li>Swap root (maximum) with last element</li>");
        content.append("<li>Reduce heap size by 1</li>");
        content.append("<li>Heapify the root to restore heap property</li>");
        content.append("<li>Repeat steps 2-4 until heap size is 1</li>");
        content.append("</ol>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>3. Complexity Analysis</h2>");
        content.append("<table class=\"complexity-table\">");
        content.append("<thead>");
        content.append("<tr><th>Case</th><th>Time Complexity</th><th>Space Complexity</th></tr>");
        content.append("</thead>");
        content.append("<tbody>");
        content.append("<tr><td><strong>Best Case</strong></td><td>O(n log n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Average Case</strong></td><td>O(n log n)</td><td>O(1)</td></tr>");
        content.append("<tr><td><strong>Worst Case</strong></td><td>O(n log n)</td><td>O(1)</td></tr>");
        content.append("</tbody>");
        content.append("</table>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>4. Advantages & Disadvantages</h2>");
        content.append("<div class=\"comparison-grid\">");
        content.append("<div class=\"comparison-item\" style=\"border-color: #68d391;\">");
        content.append("<h4>✅ Advantages</h4>");
        content.append("<ul>");
        content.append("<li>Guaranteed O(n log n) performance</li>");
        content.append("<li>In-place sorting (O(1) space)</li>");
        content.append("<li>No worst-case quadratic behavior</li>");
        content.append("<li>Not affected by input distribution</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("<div class=\"comparison-item\" style=\"border-color: #fc8181;\">");
        content.append("<h4>⚠️ Disadvantages</h4>");
        content.append("<ul>");
        content.append("<li>Not stable (relative order not preserved)</li>");
        content.append("<li>Slower than quicksort in practice</li>");
        content.append("<li>Poor cache performance</li>");
        content.append("<li>Not adaptive to partially sorted data</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("</div>");
        return content.toString();
    }
    
    /**
     * Generate Graph module documentation
     */
    public static String generateGraphDocumentation() {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class=\"container\">");
        
        content.append("<div class=\"header\">");
        content.append("<h1>🕸️ Graph Data Structure</h1>");
        content.append("<div class=\"subtitle\">Complete Module Documentation</div>");
        content.append("<div class=\"date\">Interactive Data Structures Learning Suite</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>1. Introduction</h2>");
        content.append("<p>A <span class=\"highlight\">Graph</span> is a non-linear data structure consisting of vertices (nodes) and edges (connections). Graphs can represent complex relationships and networks.</p>");
        content.append("<div class=\"info-box\">");
        content.append("<h3>Graph Types</h3>");
        content.append("<ul>");
        content.append("<li><strong>Directed vs Undirected:</strong> Edges have/don't have direction</li>");
        content.append("<li><strong>Weighted vs Unweighted:</strong> Edges have/don't have costs</li>");
        content.append("<li><strong>Cyclic vs Acyclic:</strong> Contains/doesn't contain cycles</li>");
        content.append("<li><strong>Connected vs Disconnected:</strong> All vertices reachable or not</li>");
        content.append("</ul>");
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>2. Graph Representations</h2>");
        content.append("<div class=\"comparison-grid\">");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Adjacency Matrix</h4>");
        content.append("<p>2D array where matrix[i][j] indicates edge from i to j.</p>");
        content.append("<ul>");
        content.append("<li>Space: O(V²)</li>");
        content.append("<li>Edge check: O(1)</li>");
        content.append("<li>Good for dense graphs</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("<div class=\"comparison-item\">");
        content.append("<h4>Adjacency List</h4>");
        content.append("<p>Array of lists, each containing neighbors of a vertex.</p>");
        content.append("<ul>");
        content.append("<li>Space: O(V + E)</li>");
        content.append("<li>Edge check: O(degree)</li>");
        content.append("<li>Good for sparse graphs</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>");
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>3. Graph Algorithms</h2>");
        
        content.append("<div class=\"algorithm-box\">");
        content.append("<h4>Dijkstra's Algorithm</h4>");
        content.append("<p>Finds shortest path from source to all vertices in weighted graph with non-negative edges.</p>");
        content.append("<ul>");
        content.append("<li>Time: O((V + E) log V) with min-heap</li>");
        content.append("<li>Uses greedy approach</li>");
        content.append("<li>Cannot handle negative weights</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("<div class=\"algorithm-box\">");
        content.append("<h4>Bellman-Ford Algorithm</h4>");
        content.append("<p>Finds shortest path from source, can handle negative edge weights.</p>");
        content.append("<ul>");
        content.append("<li>Time: O(VE)</li>");
        content.append("<li>Detects negative cycles</li>");
        content.append("<li>Uses dynamic programming</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>");
        
        content.append("<div class=\"section\">");
        content.append("<h2>4. Applications</h2>");
        content.append("<ul>");
        content.append("<li><strong>Social Networks:</strong> Friend connections, recommendations</li>");
        content.append("<li><strong>Navigation:</strong> GPS, route finding, maps</li>");
        content.append("<li><strong>Web Crawling:</strong> Link analysis, PageRank</li>");
        content.append("<li><strong>Network Routing:</strong> Internet packet routing</li>");
        content.append("<li><strong>Dependency Resolution:</strong> Build systems, package managers</li>");
        content.append("<li><strong>Recommendation Systems:</strong> Product/content suggestions</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("</div>");
        return content.toString();
    }
}
