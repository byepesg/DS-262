package com.raven.ds.modules.bst;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.PDFExporter;
import com.raven.ds.core.PDFDocumentGenerator;
import com.raven.swing.Button;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Enhanced Control panel for Binary Search Tree operations
 * Features comprehensive controls with export functionality
 */
public class BSTControls extends JPanel {
    private BSTPanel visualizer;
    private AnimationEngine animationEngine;
    
    private JTextField inputField;
    private Button insertBtn;
    private Button deleteBtn;
    private Button findBtn;
    private Button clearBtn;
    private Button playBtn;
    private Button pauseBtn;
    private Button stepBtn;
    private Button resetBtn;
    private Button downloadBtn;
    private Button downloadDocsBtn;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JComboBox<String> traversalCombo;
    
    public BSTControls(BSTPanel visualizer, AnimationEngine animationEngine) {
        this.visualizer = visualizer;
        this.animationEngine = animationEngine;
        
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        // Input field with enhanced styling
        inputField = new JTextField(10);
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        inputField.setToolTipText("Enter a number to insert, delete, or search");
        
        // Operation buttons with enhanced styling (50% bigger)
        insertBtn = new Button();
        insertBtn.setText("Insert");
        insertBtn.setBackground(new Color(39, 174, 96)); // Green
        insertBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        insertBtn.setForeground(Color.WHITE);
        insertBtn.setPreferredSize(new Dimension(120, 40));
        insertBtn.setToolTipText("Insert value into BST");
        
        deleteBtn = new Button();
        deleteBtn.setText("Delete");
        deleteBtn.setBackground(new Color(231, 76, 60)); // Red
        deleteBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setPreferredSize(new Dimension(120, 40));
        deleteBtn.setToolTipText("Delete value from BST");
        
        findBtn = new Button();
        findBtn.setText("Find");
        findBtn.setBackground(new Color(52, 152, 219)); // Blue
        findBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        findBtn.setForeground(Color.WHITE);
        findBtn.setPreferredSize(new Dimension(120, 40));
        findBtn.setToolTipText("Search for value in BST");
        
        clearBtn = new Button();
        clearBtn.setText("Clear");
        clearBtn.setBackground(new Color(149, 165, 166)); // Gray
        clearBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(120, 40));
        clearBtn.setToolTipText("Clear entire BST");
        
        // Animation controls with enhanced styling
        playBtn = new Button();
        playBtn.setText("▶ Play");
        playBtn.setBackground(new Color(46, 204, 113)); // Green
        playBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        playBtn.setForeground(Color.WHITE);
        playBtn.setPreferredSize(new Dimension(80, 40));
        playBtn.setToolTipText("Play animation sequence");
        
        pauseBtn = new Button();
        pauseBtn.setText("⏸ Pause");
        pauseBtn.setBackground(new Color(241, 196, 15)); // Yellow
        pauseBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        pauseBtn.setForeground(Color.BLACK);
        pauseBtn.setPreferredSize(new Dimension(80, 40));
        pauseBtn.setToolTipText("Pause current animation");
        
        stepBtn = new Button();
        stepBtn.setText("⏭ Step");
        stepBtn.setBackground(new Color(52, 152, 219)); // Blue
        stepBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        stepBtn.setForeground(Color.WHITE);
        stepBtn.setPreferredSize(new Dimension(80, 40));
        stepBtn.setToolTipText("Execute next animation step");
        
        resetBtn = new Button();
        resetBtn.setText("🔄 Reset");
        resetBtn.setBackground(new Color(149, 165, 166)); // Gray
        resetBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setPreferredSize(new Dimension(80, 40));
        resetBtn.setToolTipText("Reset animation to beginning");
        
        // Download/Export button
        downloadBtn = new Button();
        downloadBtn.setText("📁 Export");
        downloadBtn.setBackground(new Color(155, 89, 182)); // Purple
        downloadBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setPreferredSize(new Dimension(120, 40));
        downloadBtn.setToolTipText("Download detailed analysis report");
        
        // Download Documentation button
        downloadDocsBtn = new Button();
        downloadDocsBtn.setText("📚 Docs");
        downloadDocsBtn.setBackground(new Color(52, 73, 94)); // Dark blue
        downloadDocsBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        downloadDocsBtn.setForeground(Color.WHITE);
        downloadDocsBtn.setPreferredSize(new Dimension(80, 40));
        downloadDocsBtn.setToolTipText("Download complete module documentation PDF");
        
        // Speed control (8 times bigger)
        speedSlider = new JSlider(100, 3000, 1000);
        speedSlider.setInverted(true); // Lower values = faster
        speedSlider.setPreferredSize(new Dimension(400, 60)); // 8x bigger
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setBackground(Color.WHITE);
        speedSlider.setToolTipText("Adjust animation speed");
        
        // Traversal options
        traversalCombo = new JComboBox<>(new String[]{
            "In-Order", "Pre-Order", "Post-Order"
        });
        traversalCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        traversalCombo.setToolTipText("Select traversal type");
        
        // Status label with enhanced styling
        statusLabel = new JLabel("Enter a number and select an operation");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(52, 73, 94));
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("BST Controls"));
    }
    
    private void setupLayout() {
        setLayout(new MigLayout("", "[][grow][]", "[]5[]5[]5[]5[]"));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("BST Controls"));
        
        // Operations row
        add(new JLabel("Operations:"), "cell 0 0");
        add(inputField, "cell 1 0, split 4");
        add(insertBtn, "cell 1 0");
        add(deleteBtn, "cell 1 0");
        add(findBtn, "cell 1 0");
        add(clearBtn, "cell 2 0");
        
        // Traversal row
        add(new JLabel("Traversal:"), "cell 0 1");
        add(traversalCombo, "cell 1 1");
        add(downloadBtn, "cell 2 1");
        
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
        add(statusLabel, "cell 0 4, span 3");
    }
    
    private void setupListeners() {
        insertBtn.addActionListener(e -> insertValue());
        deleteBtn.addActionListener(e -> deleteValue());
        findBtn.addActionListener(e -> findValue());
        clearBtn.addActionListener(e -> clearTree());
        
        playBtn.addActionListener(e -> animationEngine.play());
        pauseBtn.addActionListener(e -> animationEngine.pause());
        stepBtn.addActionListener(e -> animationEngine.nextStep());
        resetBtn.addActionListener(e -> animationEngine.reset());
        
        // Enhanced download functionality
        downloadBtn.addActionListener(e -> downloadAnalysis());
        downloadDocsBtn.addActionListener(e -> downloadDocumentation());
        
        speedSlider.addChangeListener(e -> {
            int speed = 3100 - speedSlider.getValue(); // Invert so right = faster
            animationEngine.setSpeed(speed);
        });
        
        traversalCombo.addActionListener(e -> performTraversal());
        
        // Enhanced animation engine listener
        animationEngine.addListener(new AnimationEngine.AnimationListener() {
            @Override
            public void onStepChanged(int currentStep, int totalSteps) {
                updateStatus("Step " + (currentStep + 1) + " of " + totalSteps);
            }
            
            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                playBtn.setEnabled(!isPlaying);
                pauseBtn.setEnabled(isPlaying);
                stepBtn.setEnabled(!isPlaying);
                
                // Disable operation buttons during animation for safety
                insertBtn.setEnabled(!isPlaying);
                deleteBtn.setEnabled(!isPlaying);
                findBtn.setEnabled(!isPlaying);
                clearBtn.setEnabled(!isPlaying);
                traversalCombo.setEnabled(!isPlaying);
            }
            
            @Override
            public void onAnimationComplete() {
                updateStatus("Animation complete");
            }
            
            @Override
            public void onReset() {
                updateStatus("Animation reset");
            }
        });
        
        // Input field enter key
        inputField.addActionListener(e -> insertValue());
        
        // Add input validation
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume(); // Ignore non-numeric input
                }
            }
        });
    }
    
    private void insertValue() {
        try {
            int value = Integer.parseInt(inputField.getText().trim());
            if (value < -999 || value > 999) {
                updateStatus("Please enter a value between -999 and 999");
                return;
            }
            visualizer.insertValue(value);
            inputField.setText("");
            updateStatus("Initiating insertion of " + value);
        } catch (NumberFormatException e) {
            updateStatus("Please enter a valid integer");
            inputField.selectAll();
        }
    }
    
    private void deleteValue() {
        try {
            int value = Integer.parseInt(inputField.getText().trim());
            visualizer.deleteValue(value);
            inputField.setText("");
            updateStatus("Initiating deletion of " + value);
        } catch (NumberFormatException e) {
            updateStatus("Please enter a valid integer");
            inputField.selectAll();
        }
    }
    
    private void findValue() {
        try {
            int value = Integer.parseInt(inputField.getText().trim());
            boolean found = visualizer.findValue(value);
            updateStatus("Searching for " + value + " - " + (found ? "Found" : "Not found"));
        } catch (NumberFormatException e) {
            updateStatus("Please enter a valid integer");
            inputField.selectAll();
        }
    }
    
    private void clearTree() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear the entire tree?\nThis action cannot be undone.",
            "Clear Tree Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            visualizer.clear();
            animationEngine.clearSteps();
            updateStatus("Tree cleared successfully");
        }
    }
    
    private void performTraversal() {
        String traversalType = (String) traversalCombo.getSelectedItem();
        if (visualizer.getBSTAlgorithm().getRoot() == null) {
            updateStatus("Tree is empty - cannot perform traversal");
            return;
        }
        visualizer.performTraversal(traversalType);
        updateStatus("Initiating " + traversalType + " traversal");
    }
    
    private void downloadAnalysis() {
        try {
            // Check if tree has content
            if (visualizer.getBSTAlgorithm().getRoot() == null) {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "The BST is currently empty.\nWould you like to load a demo tree first?",
                    "Empty Tree",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    loadDemo();
                    // Wait a moment for the tree to render
                    SwingUtilities.invokeLater(() -> {
                        PDFExporter.exportToPDF(visualizer, "BST", 
                            "Binary Search Tree interactive learning visualization with comprehensive analysis and explanations.");
                    });
                } else {
                    return;
                }
            } else {
                PDFExporter.exportToPDF(visualizer, "BST", 
                    "Binary Search Tree interactive learning visualization with comprehensive analysis and explanations.");
            }
            
            updateStatus("Analysis report generation initiated");
            
        } catch (Exception e) {
            updateStatus("Failed to generate analysis report: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void downloadDocumentation() {
        try {
            String content = PDFDocumentGenerator.generateBSTDocumentation();
            boolean success = PDFDocumentGenerator.generateModuleDocumentation(
                "BST",
                "Binary Search Tree (BST)",
                content
            );
            
            if (success) {
                updateStatus("BST documentation generated successfully");
            }
            
        } catch (Exception e) {
            updateStatus("Failed to generate documentation: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Failed to generate documentation:\n" + e.getMessage(),
                "Documentation Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void updateStatus(String message) {
        statusLabel.setText(message);
        // Auto-clear status after 5 seconds
        Timer timer = new Timer(5000, e -> {
            if (statusLabel.getText().equals(message)) {
                statusLabel.setText("Ready for operation");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    // Enhanced demo with more comprehensive tree
    public void loadDemo() {
        visualizer.clear();
        
        // Create a more interesting demo tree
        int[] demoValues = {50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45, 55, 65, 75, 90};
        
        for (int value : demoValues) {
            visualizer.insertValueInstant(value);
        }
        
        updateStatus("Demo tree loaded with " + demoValues.length + " nodes");
    }
}
