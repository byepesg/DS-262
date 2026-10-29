package com.raven.ds.modules.graph;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.PDFDocumentGenerator;
import com.raven.swing.Button;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Control panel for Graph Dijkstra algorithm operations
 */
public class GraphControls extends JPanel {
    private GraphPanel visualizer;
    private AnimationEngine animationEngine;
    
    private Button runDijkstraBtn;
    private Button runBellmanFordBtn;
    private Button clearBtn;
    private Button loadSampleBtn;
    private Button addNodeBtn;
    private Button addEdgeBtn;
    private Button customGraphBtn;
    private Button exportHtmlBtn;
    private Button playBtn;
    private Button pauseBtn;
    private Button stepBtn;
    private Button resetBtn;
    private Button downloadDocsBtn;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JLabel stepLabel;
    
    public GraphControls(GraphPanel visualizer, AnimationEngine animationEngine) {
        this.visualizer = visualizer;
        this.animationEngine = animationEngine;
        
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        // Algorithm control buttons (50% bigger)
        runDijkstraBtn = new Button();
        runDijkstraBtn.setText("Run Dijkstra");
        runDijkstraBtn.setBackground(new Color(53, 106, 230)); // #356AE6 - Primary blue
        runDijkstraBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        runDijkstraBtn.setForeground(Color.WHITE);
        runDijkstraBtn.setPreferredSize(new Dimension(120, 40));
        runDijkstraBtn.setToolTipText("Find shortest path using Dijkstra's algorithm");
        
        runBellmanFordBtn = new Button();
        runBellmanFordBtn.setText("Run Bellman-Ford");
        runBellmanFordBtn.setBackground(new Color(55, 53, 230)); // #3735E6 - Purple-blue variant
        runBellmanFordBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        runBellmanFordBtn.setForeground(Color.WHITE);
        runBellmanFordBtn.setPreferredSize(new Dimension(140, 40));
        runBellmanFordBtn.setToolTipText("Find shortest path using Bellman-Ford algorithm");
        
        clearBtn = new Button();
        clearBtn.setText("Clear Graph");
        clearBtn.setBackground(new Color(231, 76, 60)); // Red
        clearBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(120, 40));
        clearBtn.setToolTipText("Clear all nodes and edges");
        
        loadSampleBtn = new Button();
        loadSampleBtn.setText("Load Sample");
        loadSampleBtn.setBackground(new Color(53, 162, 230)); // #35A2E6 - Lighter blue variant
        loadSampleBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        loadSampleBtn.setForeground(Color.WHITE);
        loadSampleBtn.setPreferredSize(new Dimension(120, 40));
        loadSampleBtn.setToolTipText("Load a sample graph for demonstration");
        
        // Graph customization buttons
        addNodeBtn = new Button();
        addNodeBtn.setText("Add Node Mode");
        addNodeBtn.setBackground(new Color(46, 204, 113)); // Green
        addNodeBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        addNodeBtn.setForeground(Color.WHITE);
        addNodeBtn.setPreferredSize(new Dimension(130, 40));
        addNodeBtn.setToolTipText("Click on empty space to add nodes");
        
        addNodeBtn.setToolTipText("Click on empty space to add nodes");
        
        addEdgeBtn = new Button();
        addEdgeBtn.setText("Add Edge Mode");
        addEdgeBtn.setBackground(new Color(155, 89, 182)); // Purple
        addEdgeBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        addEdgeBtn.setForeground(Color.WHITE);
        addEdgeBtn.setPreferredSize(new Dimension(130, 40));
        addEdgeBtn.setToolTipText("Click two nodes to connect them with an edge");
        
        customGraphBtn = new Button();
        customGraphBtn.setText("Custom Graph");
        customGraphBtn.setBackground(new Color(241, 196, 15)); // Yellow
        customGraphBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        customGraphBtn.setForeground(Color.BLACK);
        customGraphBtn.setPreferredSize(new Dimension(130, 40));
        customGraphBtn.setToolTipText("Generate a custom graph with specified parameters");
        
        exportHtmlBtn = new Button();
        exportHtmlBtn.setText("Export Analysis");
        exportHtmlBtn.setBackground(new Color(53, 106, 230)); // #356AE6 - Primary blue
        exportHtmlBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        exportHtmlBtn.setForeground(Color.WHITE);
        exportHtmlBtn.setPreferredSize(new Dimension(130, 40));
        exportHtmlBtn.setToolTipText("Export detailed algorithm analysis");
        
        // Animation control buttons with enhanced styling and symbols
        playBtn = new Button();
        playBtn.setText("▶ Play");
        playBtn.setBackground(new Color(46, 204, 113)); // Green
        playBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        playBtn.setForeground(Color.WHITE);
        playBtn.setPreferredSize(new Dimension(80, 40));
        playBtn.setToolTipText("Start algorithm animation");
        
        pauseBtn = new Button();
        pauseBtn.setText("⏸ Pause");
        pauseBtn.setBackground(new Color(241, 196, 15)); // Yellow
        pauseBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        pauseBtn.setForeground(Color.BLACK);
        pauseBtn.setPreferredSize(new Dimension(80, 40));
        pauseBtn.setToolTipText("Pause current animation");
        
        stepBtn = new Button();
        stepBtn.setText("⏭ Step");
        stepBtn.setBackground(new Color(53, 106, 230)); // #356AE6 - Primary blue
        stepBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        stepBtn.setForeground(Color.WHITE);
        stepBtn.setPreferredSize(new Dimension(80, 40));
        stepBtn.setToolTipText("Execute next step in animation");
        
        resetBtn = new Button();
        resetBtn.setText("🔄 Reset");
        resetBtn.setBackground(new Color(149, 165, 166)); // Gray
        resetBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setPreferredSize(new Dimension(80, 40));
        resetBtn.setToolTipText("Reset animation to beginning");
        
        // Download Documentation button
        downloadDocsBtn = new Button();
        downloadDocsBtn.setText("📚 Docs");
        downloadDocsBtn.setBackground(new Color(52, 73, 94));
        downloadDocsBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        downloadDocsBtn.setForeground(Color.WHITE);
        downloadDocsBtn.setPreferredSize(new Dimension(80, 40));
        downloadDocsBtn.setToolTipText("Download module documentation");
        
        // Speed control (8 times bigger)
        speedSlider = new JSlider(50, 3000, 1000);
        speedSlider.setInverted(true); // Lower values = faster
        speedSlider.setPreferredSize(new Dimension(400, 60)); // 8x bigger
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        
        // Status labels
        statusLabel = new JLabel("Ready to run algorithm");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        stepLabel = new JLabel("Step: 0/0");
        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Graph Controls"));
    }
    
    private void setupLayout() {
        setLayout(new MigLayout("", "[][grow][]", "[]5[]5[]5[]"));
        
        // Algorithm controls row
        add(new JLabel("Algorithms:"), "cell 0 0");
        add(runDijkstraBtn, "cell 1 0");
        add(runBellmanFordBtn, "cell 1 0");
        add(loadSampleBtn, "cell 1 0");
        add(clearBtn, "cell 2 0");
        
        // Graph customization row
        add(new JLabel("Customize:"), "cell 0 1");
        add(addNodeBtn, "cell 1 1");
        add(addEdgeBtn, "cell 1 1");
        add(customGraphBtn, "cell 1 1");
        add(exportHtmlBtn, "cell 2 1");
        
        // Animation controls row
        add(new JLabel("Animation:"), "cell 0 2");
        add(playBtn, "cell 1 2");
        add(pauseBtn, "cell 1 2");
        add(stepBtn, "cell 1 2");
        add(resetBtn, "cell 1 2");
        add(downloadDocsBtn, "cell 2 2");
        
        // Speed control row
        add(new JLabel("Speed:"), "cell 0 3");
        add(speedSlider, "cell 1 3, span 2, grow");
        
        // Status row
        add(statusLabel, "cell 0 4, span 2");
        add(stepLabel, "cell 2 4");
    }
    
    private void setupListeners() {
        runDijkstraBtn.addActionListener(e -> {
            runDijkstraBtn.setEnabled(false);
            runBellmanFordBtn.setEnabled(false);
            statusLabel.setText("Running Dijkstra's algorithm...");
            visualizer.setCurrentAlgorithm("DIJKSTRA");
            visualizer.runDijkstra();
            updateAnimationControls();
        });
        
        runBellmanFordBtn.addActionListener(e -> {
            runDijkstraBtn.setEnabled(false);
            runBellmanFordBtn.setEnabled(false);
            statusLabel.setText("Running Bellman-Ford algorithm...");
            visualizer.setCurrentAlgorithm("BELLMAN_FORD");
            visualizer.runBellmanFord();
            updateAnimationControls();
        });
        
        clearBtn.addActionListener(e -> {
            visualizer.clearGraph();
            animationEngine.clearSteps();
            statusLabel.setText("Graph cleared");
            updateAnimationControls();
        });
        
        loadSampleBtn.addActionListener(e -> {
            visualizer.loadSampleGraph();
            statusLabel.setText("Sample graph loaded");
            updateAnimationControls();
        });
        
        addNodeBtn.addActionListener(e -> {
            if (addNodeBtn.getText().equals("Add Node Mode")) {
                addNodeBtn.setText("Cancel Node");
                addNodeBtn.setBackground(new Color(231, 76, 60));
                statusLabel.setText("Right-click on empty space to add a node");
            } else {
                addNodeBtn.setText("Add Node Mode");
                addNodeBtn.setBackground(new Color(46, 204, 113));
                statusLabel.setText("Ready");
            }
        });
        
        addEdgeBtn.addActionListener(e -> {
            if (addEdgeBtn.getText().equals("Add Edge Mode")) {
                visualizer.startAddingEdge();
                addEdgeBtn.setText("Cancel Edge");
                addEdgeBtn.setBackground(new Color(231, 76, 60));
                statusLabel.setText("Click two nodes to add an edge");
            } else {
                visualizer.cancelAddingEdge();
                addEdgeBtn.setText("Add Edge Mode");
                addEdgeBtn.setBackground(new Color(155, 89, 182));
                statusLabel.setText("Ready");
            }
        });
        
        customGraphBtn.addActionListener(e -> {
            showCustomGraphDialog();
        });
        
        exportHtmlBtn.addActionListener(e -> {
            exportAnalysisToHtml();
        });
        
        playBtn.addActionListener(e -> {
            animationEngine.play();
            statusLabel.setText("Playing animation...");
        });
        
        pauseBtn.addActionListener(e -> {
            animationEngine.pause();
            statusLabel.setText("Animation paused");
        });
        
        stepBtn.addActionListener(e -> {
            animationEngine.nextStep();
            statusLabel.setText("Step executed");
        });
        
        resetBtn.addActionListener(e -> {
            animationEngine.reset();
            statusLabel.setText("Animation reset");
            runDijkstraBtn.setEnabled(true);
            runBellmanFordBtn.setEnabled(true);
            updateAnimationControls();
        });
        
        speedSlider.addChangeListener(e -> {
            animationEngine.setSpeed(speedSlider.getValue());
        });
        
        downloadDocsBtn.addActionListener(e -> {
            try {
                String content = PDFDocumentGenerator.generateGraphDocumentation();
                PDFDocumentGenerator.generateModuleDocumentation(
                    "Graph",
                    "Graph Data Structure & Algorithms",
                    content
                );
                statusLabel.setText("Graph documentation generated");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to generate documentation: " + ex.getMessage(),
                    "Documentation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Animation engine listeners
        animationEngine.addListener(new AnimationEngine.AnimationListener() {
            @Override
            public void onStepChanged(int currentStep, int totalSteps) {
                stepLabel.setText("Step: " + currentStep + "/" + totalSteps);
                if (currentStep < totalSteps) {
                    statusLabel.setText("Step " + currentStep + " executed");
                }
                visualizer.repaint();
            }
            
            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                playBtn.setEnabled(!isPlaying);
                pauseBtn.setEnabled(isPlaying);
            }
            
            @Override
            public void onAnimationComplete() {
                statusLabel.setText("Algorithm completed!");
                runDijkstraBtn.setEnabled(true);
                runBellmanFordBtn.setEnabled(true);
            }
            
            @Override
            public void onReset() {
                stepLabel.setText("Step: 0/0");
                visualizer.repaint();
            }
        });
    }
    
    private void updateAnimationControls() {
        boolean hasSteps = animationEngine.getTotalSteps() > 0;
        playBtn.setEnabled(hasSteps && !animationEngine.isPlaying());
        pauseBtn.setEnabled(hasSteps && animationEngine.isPlaying());
        stepBtn.setEnabled(hasSteps);
        resetBtn.setEnabled(hasSteps);
        
        if (!hasSteps) {
            runDijkstraBtn.setEnabled(true);
            runBellmanFordBtn.setEnabled(true);
        }
    }
    
    public void loadDemo() {
        visualizer.loadSampleGraph();
        statusLabel.setText("Demo graph loaded - ready to run algorithm");
    }
    
    private void showCustomGraphDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Custom Graph", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new MigLayout("fillx, wrap 2", "[right][left]"));
        
        // Number of nodes
        JLabel nodesLabel = new JLabel("Number of nodes:");
        JSpinner nodesSpinner = new JSpinner(new SpinnerNumberModel(5, 3, 20, 1));
        
        // Graph type
        JLabel typeLabel = new JLabel("Graph type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Random", "Grid", "Circle", "Complete"});
        
        // Directed/Undirected
        JLabel directedLabel = new JLabel("Graph direction:");
        JComboBox<String> directedCombo = new JComboBox<>(new String[]{"Undirected", "Directed"});
        
        // Weight range
        JLabel weightLabel = new JLabel("Weight range:");
        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JSpinner minWeightSpinner = new JSpinner(new SpinnerNumberModel(1, -100, 100, 1));
        weightPanel.add(minWeightSpinner);
        weightPanel.add(new JLabel(" to "));
        JSpinner maxWeightSpinner = new JSpinner(new SpinnerNumberModel(10, -100, 100, 1));
        weightPanel.add(maxWeightSpinner);
        
        // Density (for random graphs)
        JLabel densityLabel = new JLabel("Edge density (%):");
        JSpinner densitySpinner = new JSpinner(new SpinnerNumberModel(50, 10, 100, 5));
        
        mainPanel.add(nodesLabel);
        mainPanel.add(nodesSpinner);
        mainPanel.add(typeLabel);
        mainPanel.add(typeCombo);
        mainPanel.add(directedLabel);
        mainPanel.add(directedCombo);
        mainPanel.add(weightLabel);
        mainPanel.add(weightPanel);
        mainPanel.add(densityLabel);
        mainPanel.add(densitySpinner);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        Button createBtn = new Button();
        createBtn.setText("Create Graph");
        createBtn.setPreferredSize(new Dimension(150, 40));
        
        Button cancelBtn = new Button();
        cancelBtn.setText("Cancel");
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        
        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        createBtn.addActionListener(e -> {
            int numNodes = (Integer) nodesSpinner.getValue();
            String graphType = (String) typeCombo.getSelectedItem();
            boolean isDirected = "Directed".equals(directedCombo.getSelectedItem());
            int minWeight = (Integer) minWeightSpinner.getValue();
            int maxWeight = (Integer) maxWeightSpinner.getValue();
            int density = (Integer) densitySpinner.getValue();
            
            // Generate the graph based on parameters
            generateCustomGraph(numNodes, graphType, isDirected, minWeight, maxWeight, density);
            statusLabel.setText("Custom graph created with " + numNodes + " nodes");
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void generateCustomGraph(int numNodes, String graphType, boolean isDirected, 
                                   int minWeight, int maxWeight, int density) {
        visualizer.clearGraph();
        Random random = new Random();
        List<GraphNode> nodes = new ArrayList<>();
        
        // Create nodes in different patterns based on type
        switch (graphType) {
            case "Grid":
                int cols = (int) Math.ceil(Math.sqrt(numNodes));
                int rows = (int) Math.ceil((double) numNodes / cols);
                for (int i = 0; i < numNodes; i++) {
                    int row = i / cols;
                    int col = i % cols;
                    int x = 100 + col * 80;
                    int y = 100 + row * 80;
                    GraphNode node = new GraphNode(String.valueOf(i), x, y);
                    visualizer.addNode(node);
                    nodes.add(node);
                }
                break;
                
            case "Circle":
                double angleStep = 2 * Math.PI / numNodes;
                int radius = 150;
                int centerX = 300, centerY = 200;
                for (int i = 0; i < numNodes; i++) {
                    double angle = i * angleStep;
                    int x = centerX + (int) (radius * Math.cos(angle));
                    int y = centerY + (int) (radius * Math.sin(angle));
                    GraphNode node = new GraphNode(String.valueOf(i), x, y);
                    visualizer.addNode(node);
                    nodes.add(node);
                }
                break;
                
            case "Complete":
            case "Random":
            default:
                // Random positioning
                for (int i = 0; i < numNodes; i++) {
                    int x = 50 + random.nextInt(500);
                    int y = 50 + random.nextInt(300);
                    GraphNode node = new GraphNode(String.valueOf(i), x, y);
                    visualizer.addNode(node);
                    nodes.add(node);
                }
                break;
        }
        
        // Create edges based on type
        switch (graphType) {
            case "Grid":
                int cols = (int) Math.ceil(Math.sqrt(numNodes));
                for (int i = 0; i < numNodes; i++) {
                    int row = i / cols;
                    int col = i % cols;
                    
                    // Right neighbor
                    if (col + 1 < cols && i + 1 < numNodes) {
                        int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                        visualizer.addEdge(nodes.get(i), nodes.get(i + 1), weight);
                        if (isDirected) {
                            // For directed graphs, optionally add reverse edge
                            if (random.nextBoolean()) {
                                int reverseWeight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                                visualizer.addEdge(nodes.get(i + 1), nodes.get(i), reverseWeight);
                            }
                        }
                    }
                    
                    // Bottom neighbor
                    if (i + cols < numNodes) {
                        int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                        visualizer.addEdge(nodes.get(i), nodes.get(i + cols), weight);
                        if (isDirected) {
                            // For directed graphs, optionally add reverse edge
                            if (random.nextBoolean()) {
                                int reverseWeight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                                visualizer.addEdge(nodes.get(i + cols), nodes.get(i), reverseWeight);
                            }
                        }
                    }
                }
                break;
                
            case "Circle":
                for (int i = 0; i < numNodes; i++) {
                    int next = (i + 1) % numNodes;
                    int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                    visualizer.addEdge(nodes.get(i), nodes.get(next), weight);
                    if (!isDirected) {
                        // For undirected, ensure we don't add duplicate edges
                        continue;
                    }
                    // For directed graphs, optionally add reverse edge
                    if (random.nextBoolean()) {
                        int reverseWeight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                        visualizer.addEdge(nodes.get(next), nodes.get(i), reverseWeight);
                    }
                }
                break;
                
            case "Complete":
                for (int i = 0; i < numNodes; i++) {
                    for (int j = i + 1; j < numNodes; j++) {
                        int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                        visualizer.addEdge(nodes.get(i), nodes.get(j), weight);
                        if (isDirected) {
                            // For directed graphs, add reverse edge with different weight
                            int reverseWeight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                            visualizer.addEdge(nodes.get(j), nodes.get(i), reverseWeight);
                        }
                    }
                }
                break;
                
            case "Random":
            default:
                int maxEdges = isDirected ? numNodes * (numNodes - 1) : numNodes * (numNodes - 1) / 2;
                int targetEdges = Math.max(numNodes - 1, (maxEdges * density) / 100);
                
                // Ensure connectivity - create spanning tree first
                List<Integer> connected = new ArrayList<>();
                List<Integer> unconnected = new ArrayList<>();
                connected.add(0);
                for (int i = 1; i < numNodes; i++) {
                    unconnected.add(i);
                }
                
                while (!unconnected.isEmpty()) {
                    int connectedIdx = random.nextInt(connected.size());
                    int unconnectedIdx = random.nextInt(unconnected.size());
                    int from = connected.get(connectedIdx);
                    int to = unconnected.get(unconnectedIdx);
                    
                    int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                    visualizer.addEdge(nodes.get(from), nodes.get(to), weight);
                    
                    connected.add(to);
                    unconnected.remove(unconnectedIdx);
                }
                
                // Add remaining random edges
                int edgesAdded = numNodes - 1;
                Set<String> existingEdges = new HashSet<>();
                
                // Record existing edges
                for (int i = 0; i < numNodes - 1; i++) {
                    if (isDirected) {
                        existingEdges.add(i + "->" + (i + 1));
                    } else {
                        existingEdges.add(Math.min(i, i + 1) + "-" + Math.max(i, i + 1));
                    }
                }
                
                while (edgesAdded < targetEdges) {
                    int from = random.nextInt(numNodes);
                    int to = random.nextInt(numNodes);
                    
                    if (from != to) {
                        String edgeKey = isDirected ? (from + "->" + to) : 
                                       (Math.min(from, to) + "-" + Math.max(from, to));
                                       
                        if (!existingEdges.contains(edgeKey)) {
                            int weight = minWeight + random.nextInt(maxWeight - minWeight + 1);
                            visualizer.addEdge(nodes.get(from), nodes.get(to), weight);
                            existingEdges.add(edgeKey);
                            edgesAdded++;
                        }
                    }
                }
                break;
        }
        
        visualizer.repaint();
    }
    
    private void exportAnalysisToHtml() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Graph_Algorithm_Analysis.html"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("HTML files", "html"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".html")) {
                    file = new java.io.File(file.getAbsolutePath() + ".html");
                }
                
                String htmlContent = generateAnalysisHtml();
                
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write(htmlContent);
                }
                
                statusLabel.setText("Analysis exported to: " + file.getName());
                
                // Ask if user wants to open the file
                int choice = JOptionPane.showConfirmDialog(this, 
                    "Analysis exported successfully!\nWould you like to open the file?", 
                    "Export Complete", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().browse(file.toURI());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Could not open file: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error exporting analysis: " + e.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateAnalysisHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Graph Algorithm Analysis</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; background: #f5f5f5; }\n");
        html.append("        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n");
        html.append("        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }\n");
        html.append("        h2 { color: #34495e; margin-top: 30px; }\n");
        html.append("        h3 { color: #7f8c8d; }\n");
        html.append("        .algorithm-section { margin: 20px 0; padding: 20px; border: 1px solid #ecf0f1; border-radius: 5px; }\n");
        html.append("        .code-block { background: #2c3e50; color: #ecf0f1; padding: 15px; border-radius: 5px; overflow-x: auto; margin: 10px 0; }\n");
        html.append("        .complexity { background: #e8f8f5; padding: 10px; border-left: 4px solid #27ae60; margin: 10px 0; }\n");
        html.append("        .warning { background: #fdf2e9; padding: 10px; border-left: 4px solid #e67e22; margin: 10px 0; }\n");
        html.append("        .info { background: #eaf2ff; padding: 10px; border-left: 4px solid #3498db; margin: 10px 0; }\n");
        html.append("        table { width: 100%; border-collapse: collapse; margin: 15px 0; }\n");
        html.append("        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n");
        html.append("        th { background: #34495e; color: white; }\n");
        html.append("        .highlight { background: #fff3cd; padding: 2px 4px; border-radius: 3px; }\n");
        html.append("        .timestamp { color: #7f8c8d; font-size: 0.9em; }\n");
        html.append("    </style>\n</head>\n<body>\n");
        
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>📊 Graph Algorithm Analysis Report</h1>\n");
        html.append("        <p class=\"timestamp\">Generated on: ").append(new Date().toString()).append("</p>\n");
        
        // Overview Section
        html.append("        <h2>🔍 Overview</h2>\n");
        html.append("        <p>This report provides a comprehensive analysis of graph algorithms implemented in the Graph Visualizer module, ");
        html.append("including <strong>Dijkstra's Algorithm</strong> and <strong>Bellman-Ford Algorithm</strong> for shortest path finding.</p>\n");
        
        // Dijkstra's Algorithm Section
        html.append("        <div class=\"algorithm-section\">\n");
        html.append("            <h2>🚀 Dijkstra's Algorithm</h2>\n");
        html.append("            <h3>Description</h3>\n");
        html.append("            <p>Dijkstra's algorithm finds the shortest path from a source vertex to all other vertices in a weighted graph with non-negative edge weights.</p>\n");
        
        html.append("            <h3>Key Characteristics</h3>\n");
        html.append("            <ul>\n");
        html.append("                <li><strong>Greedy Algorithm:</strong> Makes locally optimal choices at each step</li>\n");
        html.append("                <li><strong>Non-negative weights only:</strong> Cannot handle negative edge weights</li>\n");
        html.append("                <li><strong>Single-source shortest path:</strong> Finds shortest paths from one source to all vertices</li>\n");
        html.append("                <li><strong>Uses Priority Queue:</strong> Efficiently selects the next closest vertex</li>\n");
        html.append("            </ul>\n");
        
        html.append("            <div class=\"complexity\">\n");
        html.append("                <h4>⏱️ Time Complexity</h4>\n");
        html.append("                <ul>\n");
        html.append("                    <li><strong>With Binary Heap:</strong> O((V + E) log V)</li>\n");
        html.append("                    <li><strong>With Array:</strong> O(V²)</li>\n");
        html.append("                    <li><strong>Space Complexity:</strong> O(V)</li>\n");
        html.append("                </ul>\n");
        html.append("            </div>\n");
        
        html.append("            <h3>Algorithm Steps</h3>\n");
        html.append("            <ol>\n");
        html.append("                <li>Initialize distances to all vertices as infinite, except source (distance = 0)</li>\n");
        html.append("                <li>Create a priority queue and add source vertex</li>\n");
        html.append("                <li>While priority queue is not empty:</li>\n");
        html.append("                <li>&nbsp;&nbsp;&nbsp;&nbsp;a. Extract vertex with minimum distance</li>\n");
        html.append("                <li>&nbsp;&nbsp;&nbsp;&nbsp;b. For each adjacent vertex, check if new path is shorter</li>\n");
        html.append("                <li>&nbsp;&nbsp;&nbsp;&nbsp;c. If shorter, update distance and add to queue</li>\n");
        html.append("            </ol>\n");
        
        html.append("            <h3>Implementation Code</h3>\n");
        html.append("            <div class=\"code-block\">\n");
        html.append("public void runDijkstra(GraphNode source) {<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;// Initialize distances<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;for (GraphNode node : nodes) {<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;distances.put(node, node == source ? 0 : Integer.MAX_VALUE);<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br><br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;// Priority queue for unvisited nodes<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;PriorityQueue&lt;GraphNode&gt; pq = new PriorityQueue&lt;&gt;(<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Comparator.comparingInt(distances::get));<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;pq.add(source);<br><br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;while (!pq.isEmpty()) {<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;GraphNode current = pq.poll();<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Process neighbors...<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br>\n");
        html.append("}\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // Bellman-Ford Algorithm Section
        html.append("        <div class=\"algorithm-section\">\n");
        html.append("            <h2>⚡ Bellman-Ford Algorithm</h2>\n");
        html.append("            <h3>Description</h3>\n");
        html.append("            <p>The Bellman-Ford algorithm finds shortest paths from a source vertex to all other vertices, and can handle negative edge weights. It also detects negative cycles.</p>\n");
        
        html.append("            <h3>Key Characteristics</h3>\n");
        html.append("            <ul>\n");
        html.append("                <li><strong>Handles negative weights:</strong> Can process graphs with negative edge weights</li>\n");
        html.append("                <li><strong>Negative cycle detection:</strong> Identifies if negative cycles exist</li>\n");
        html.append("                <li><strong>Dynamic Programming:</strong> Uses relaxation technique repeatedly</li>\n");
        html.append("                <li><strong>Slower than Dijkstra:</strong> But more versatile</li>\n");
        html.append("            </ul>\n");
        
        html.append("            <div class=\"complexity\">\n");
        html.append("                <h4>⏱️ Time Complexity</h4>\n");
        html.append("                <ul>\n");
        html.append("                    <li><strong>Time Complexity:</strong> O(V × E)</li>\n");
        html.append("                    <li><strong>Space Complexity:</strong> O(V)</li>\n");
        html.append("                </ul>\n");
        html.append("            </div>\n");
        
        html.append("            <div class=\"warning\">\n");
        html.append("                <h4>⚠️ When to Use Bellman-Ford</h4>\n");
        html.append("                <ul>\n");
        html.append("                    <li>Graph contains negative edge weights</li>\n");
        html.append("                    <li>Need to detect negative cycles</li>\n");
        html.append("                    <li>Graph is sparse (few edges)</li>\n");
        html.append("                </ul>\n");
        html.append("            </div>\n");
        
        html.append("            <h3>Algorithm Steps</h3>\n");
        html.append("            <ol>\n");
        html.append("                <li>Initialize distances to all vertices as infinite, except source (distance = 0)</li>\n");
        html.append("                <li>Repeat V-1 times:</li>\n");
        html.append("                <li>&nbsp;&nbsp;&nbsp;&nbsp;For each edge (u,v) with weight w:</li>\n");
        html.append("                <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If distance[u] + w < distance[v], update distance[v]</li>\n");
        html.append("                <li>Check for negative cycles by repeating relaxation once more</li>\n");
        html.append("            </ol>\n");
        
        html.append("            <h3>Implementation Code</h3>\n");
        html.append("            <div class=\"code-block\">\n");
        html.append("public boolean runBellmanFord(GraphNode source) {<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;// Initialize distances<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;for (GraphNode node : nodes) {<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;distances.put(node, node == source ? 0 : Integer.MAX_VALUE);<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br><br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;// Relax edges V-1 times<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;for (int i = 0; i < nodes.size() - 1; i++) {<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;for (GraphEdge edge : edges) {<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;relaxEdge(edge);<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;}<br><br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;// Check for negative cycles<br>\n");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;return checkForNegativeCycles();<br>\n");
        html.append("}\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // Comparison Table
        html.append("        <h2>📋 Algorithm Comparison</h2>\n");
        html.append("        <table>\n");
        html.append("            <tr><th>Aspect</th><th>Dijkstra's Algorithm</th><th>Bellman-Ford Algorithm</th></tr>\n");
        html.append("            <tr><td><strong>Time Complexity</strong></td><td>O((V + E) log V)</td><td>O(V × E)</td></tr>\n");
        html.append("            <tr><td><strong>Space Complexity</strong></td><td>O(V)</td><td>O(V)</td></tr>\n");
        html.append("            <tr><td><strong>Negative Weights</strong></td><td>❌ No</td><td>✅ Yes</td></tr>\n");
        html.append("            <tr><td><strong>Negative Cycle Detection</strong></td><td>❌ No</td><td>✅ Yes</td></tr>\n");
        html.append("            <tr><td><strong>Performance</strong></td><td>✅ Faster</td><td>⚠️ Slower</td></tr>\n");
        html.append("            <tr><td><strong>Implementation</strong></td><td>Priority Queue</td><td>Edge Relaxation</td></tr>\n");
        html.append("            <tr><td><strong>Best Use Case</strong></td><td>Non-negative weights, speed critical</td><td>Negative weights, cycle detection needed</td></tr>\n");
        html.append("        </table>\n");
        
        // Practical Applications
        html.append("        <h2>🌟 Practical Applications</h2>\n");
        html.append("        <div class=\"info\">\n");
        html.append("            <h3>Dijkstra's Algorithm Applications:</h3>\n");
        html.append("            <ul>\n");
        html.append("                <li><strong>GPS Navigation:</strong> Finding shortest routes between locations</li>\n");
        html.append("                <li><strong>Network Routing:</strong> Determining optimal data packet paths</li>\n");
        html.append("                <li><strong>Social Networks:</strong> Finding degrees of separation between users</li>\n");
        html.append("                <li><strong>Game Development:</strong> AI pathfinding for NPCs</li>\n");
        html.append("            </ul>\n");
        html.append("        </div>\n");
        
        html.append("        <div class=\"info\">\n");
        html.append("            <h3>Bellman-Ford Algorithm Applications:</h3>\n");
        html.append("            <ul>\n");
        html.append("                <li><strong>Currency Exchange:</strong> Detecting arbitrage opportunities</li>\n");
        html.append("                <li><strong>Network Analysis:</strong> Finding negative cycles in financial networks</li>\n");
        html.append("                <li><strong>Distance Vector Routing:</strong> Used in RIP protocol</li>\n");
        html.append("                <li><strong>Graph Analysis:</strong> Preprocessing for Johnson's algorithm</li>\n");
        html.append("            </ul>\n");
        html.append("        </div>\n");
        
        // Implementation Features
        html.append("        <h2>🔧 Implementation Features</h2>\n");
        html.append("        <h3>Visualization Capabilities</h3>\n");
        html.append("        <ul>\n");
        html.append("            <li><strong>Interactive Graph Creation:</strong> Click to add nodes, drag to create edges</li>\n");
        html.append("            <li><strong>Step-by-step Animation:</strong> Watch algorithms execute in real-time</li>\n");
        html.append("            <li><strong>Customizable Speed:</strong> Adjust animation speed from 1-100</li>\n");
        html.append("            <li><strong>Multiple Graph Types:</strong> Generate random, grid, circle, or complete graphs</li>\n");
        html.append("            <li><strong>Visual Feedback:</strong> Color-coded nodes and edges show algorithm progress</li>\n");
        html.append("        </ul>\n");
        
        html.append("        <h3>User Controls</h3>\n");
        html.append("        <ul>\n");
        html.append("            <li><strong>Algorithm Selection:</strong> Choose between Dijkstra and Bellman-Ford</li>\n");
        html.append("            <li><strong>Source Node Selection:</strong> Right-click any node to set as source</li>\n");
        html.append("            <li><strong>Animation Controls:</strong> Play, pause, step, and reset functionality</li>\n");
        html.append("            <li><strong>Graph Manipulation:</strong> Add/remove nodes and edges dynamically</li>\n");
        html.append("            <li><strong>Demo Graphs:</strong> Load pre-built example graphs</li>\n");
        html.append("        </ul>\n");
        
        // Educational Value
        html.append("        <h2>🎓 Educational Value</h2>\n");
        html.append("        <p>This implementation serves as an excellent educational tool for understanding graph algorithms:</p>\n");
        html.append("        <ul>\n");
        html.append("            <li><strong>Visual Learning:</strong> See how algorithms work step-by-step</li>\n");
        html.append("            <li><strong>Interactive Exploration:</strong> Create custom graphs to test different scenarios</li>\n");
        html.append("            <li><strong>Algorithm Comparison:</strong> Direct comparison of different approaches</li>\n");
        html.append("            <li><strong>Real-time Feedback:</strong> Immediate visualization of algorithm decisions</li>\n");
        html.append("        </ul>\n");
        
        html.append("        <h2>📝 Conclusion</h2>\n");
        html.append("        <p>Both Dijkstra's and Bellman-Ford algorithms are fundamental to computer science and have numerous ");
        html.append("practical applications. This visualization tool provides an intuitive way to understand their behavior, ");
        html.append("differences, and use cases. The interactive nature allows for experimentation and deeper learning about ");
        html.append("graph theory and algorithm design.</p>\n");
        
        html.append("        <div class=\"info\">\n");
        html.append("            <p><strong>💡 Tip:</strong> Try creating graphs with different structures and weights to see how ");
        html.append("each algorithm behaves in various scenarios. Pay special attention to how Bellman-Ford handles negative ");
        html.append("weights and detects negative cycles!</p>\n");
        html.append("        </div>\n");
        
        html.append("    </div>\n</body>\n</html>");
        
        return html.toString();
    }
}
