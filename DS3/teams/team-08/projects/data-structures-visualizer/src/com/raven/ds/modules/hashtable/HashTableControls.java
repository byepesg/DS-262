package com.raven.ds.modules.hashtable;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.PDFDocumentGenerator;
import com.raven.swing.Button;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Control panel for Hash Table operations
 */
public class HashTableControls extends JPanel {
    private HashTablePanel visualizer;
    private AnimationEngine animationEngine;
    
    private JTextField keyField;
    private JTextField valueField;
    private Button insertBtn;
    private Button searchBtn;
    private Button removeBtn;
    private Button clearBtn;
    private Button loadSampleBtn;
    private Button playBtn;
    private Button pauseBtn;
    private Button stepBtn;
    private Button resetBtn;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JLabel stepLabel;
    private JComboBox<HashTableAlgorithm.HashFunction> hashFunctionCombo;
    private JSpinner capacitySpinner;
    private Button resizeBtn;
    private Button downloadDocsBtn;
    
    public HashTableControls(HashTablePanel visualizer, AnimationEngine animationEngine) {
        this.visualizer = visualizer;
        this.animationEngine = animationEngine;
        
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        // Input fields
        keyField = new JTextField(10);
        keyField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        keyField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        keyField.setToolTipText("Enter key");
        
        valueField = new JTextField(10);
        valueField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        valueField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        valueField.setToolTipText("Enter value");
        
        // Operation buttons (50% bigger)
        insertBtn = new Button();
        insertBtn.setText("Insert");
        insertBtn.setBackground(new Color(7, 164, 121)); // Raven green
        insertBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        insertBtn.setForeground(Color.WHITE);
        insertBtn.setPreferredSize(new Dimension(120, 40));
        insertBtn.setToolTipText("Insert key-value pair");
        
        searchBtn = new Button();
        searchBtn.setText("Search");
        searchBtn.setBackground(new Color(52, 152, 219)); // Blue
        searchBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.setToolTipText("Search by key");
        
        removeBtn = new Button();
        removeBtn.setText("Remove");
        removeBtn.setBackground(new Color(231, 76, 60)); // Red
        removeBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setPreferredSize(new Dimension(120, 40));
        removeBtn.setToolTipText("Remove by key");
        
        clearBtn = new Button();
        clearBtn.setText("Clear");
        clearBtn.setBackground(new Color(231, 76, 60)); // Red
        clearBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(120, 40));
        clearBtn.setToolTipText("Clear hash table");
        
        loadSampleBtn = new Button();
        loadSampleBtn.setText("Sample");
        loadSampleBtn.setBackground(new Color(53, 162, 230)); // Lighter blue
        loadSampleBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        loadSampleBtn.setForeground(Color.WHITE);
        loadSampleBtn.setPreferredSize(new Dimension(120, 40));
        loadSampleBtn.setToolTipText("Load sample data");
        
        // Animation control buttons
        playBtn = new Button();
        playBtn.setText("▶ Play");
        playBtn.setBackground(new Color(46, 204, 113)); // Green
        playBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        playBtn.setForeground(Color.WHITE);
        playBtn.setPreferredSize(new Dimension(80, 40));
        
        pauseBtn = new Button();
        pauseBtn.setText("⏸ Pause");
        pauseBtn.setBackground(new Color(241, 196, 15)); // Yellow
        pauseBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        pauseBtn.setForeground(Color.BLACK);
        pauseBtn.setPreferredSize(new Dimension(80, 40));
        
        stepBtn = new Button();
        stepBtn.setText("⏭ Step");
        stepBtn.setBackground(new Color(53, 106, 230)); // Primary blue
        stepBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        stepBtn.setForeground(Color.WHITE);
        stepBtn.setPreferredSize(new Dimension(80, 40));
        
        resetBtn = new Button();
        resetBtn.setText("🔄 Reset");
        resetBtn.setBackground(new Color(149, 165, 166)); // Gray
        resetBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setPreferredSize(new Dimension(80, 40));
        
        // Speed control (8 times bigger)
        speedSlider = new JSlider(50, 3000, 1000);
        speedSlider.setInverted(true); // Lower values = faster
        speedSlider.setPreferredSize(new Dimension(400, 60)); // 8x bigger
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setBackground(Color.WHITE);
        
        // Hash function selector
        hashFunctionCombo = new JComboBox<>(HashTableAlgorithm.HashFunction.values());
        hashFunctionCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hashFunctionCombo.setToolTipText("Select hash function");
        
        // Capacity control
        capacitySpinner = new JSpinner(new SpinnerNumberModel(16, 4, 64, 4));
        capacitySpinner.setFont(new Font("SansSerif", Font.PLAIN, 12));
        capacitySpinner.setToolTipText("Initial capacity");
        
        resizeBtn = new Button();
        resizeBtn.setText("Resize");
        resizeBtn.setBackground(new Color(241, 196, 15)); // Yellow
        resizeBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        resizeBtn.setForeground(Color.BLACK);
        resizeBtn.setPreferredSize(new Dimension(120, 40));
        resizeBtn.setToolTipText("Resize table");
        
        // Download Documentation button
        downloadDocsBtn = new Button();
        downloadDocsBtn.setText("📚 Docs");
        downloadDocsBtn.setBackground(new Color(52, 73, 94));
        downloadDocsBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        downloadDocsBtn.setForeground(Color.WHITE);
        downloadDocsBtn.setPreferredSize(new Dimension(80, 40));
        downloadDocsBtn.setToolTipText("Download module documentation");
        
        // Status labels
        statusLabel = new JLabel("Ready for hash table operations");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        stepLabel = new JLabel("Step: 0/0");
        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Hash Table Controls"));
    }
    
    private void setupLayout() {
        setLayout(new MigLayout("", "[][grow][]", "[]5[]5[]5[]5[]5[]"));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Hash Table Controls"));
        
        // Operations row
        add(new JLabel("Operations:"), "cell 0 0");
        add(keyField, "cell 1 0, split 4");
        add(valueField, "cell 1 0");
        add(insertBtn, "cell 1 0");
        add(searchBtn, "cell 1 0");
        add(removeBtn, "cell 2 0");
        
        // Hash function row
        add(new JLabel("Hash Function:"), "cell 0 1");
        add(hashFunctionCombo, "cell 1 1");
        add(clearBtn, "cell 2 1");
        
        // Resize row
        add(new JLabel("Capacity:"), "cell 0 2");
        add(capacitySpinner, "cell 1 2, split 2");
        add(resizeBtn, "cell 1 2");
        add(loadSampleBtn, "cell 2 2");
        
        // Animation controls row
        add(new JLabel("Animation:"), "cell 0 3");
        add(playBtn, "cell 1 3");
        add(pauseBtn, "cell 1 3");
        add(stepBtn, "cell 1 3");
        add(resetBtn, "cell 1 3");
        add(downloadDocsBtn, "cell 2 3");
        
        // Speed control row
        add(new JLabel("Speed:"), "cell 0 4");
        add(speedSlider, "cell 1 4, span 2, grow");
        
        // Status row
        add(statusLabel, "cell 0 5, span 2");
        add(stepLabel, "cell 2 5");
    }
    
    private void setupListeners() {
        insertBtn.addActionListener(e -> {
            String key = keyField.getText().trim();
            String value = valueField.getText().trim();
            
            if (key.isEmpty() || value.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter both key and value", 
                    "Input Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            visualizer.insert(key, value);
            statusLabel.setText("Inserting: " + key + " → " + value);
            updateAnimationControls();
            
            // Clear fields after successful insert
            keyField.setText("");
            valueField.setText("");
        });
        
        searchBtn.addActionListener(e -> {
            String key = keyField.getText().trim();
            
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a key to search", 
                    "Input Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            visualizer.search(key);
            statusLabel.setText("Searching for key: " + key);
            updateAnimationControls();
        });
        
        removeBtn.addActionListener(e -> {
            String key = keyField.getText().trim();
            
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a key to remove", 
                    "Input Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            visualizer.remove(key);
            statusLabel.setText("Removing key: " + key);
            updateAnimationControls();
            
            keyField.setText("");
        });
        
        clearBtn.addActionListener(e -> {
            visualizer.clear();
            animationEngine.clearSteps();
            statusLabel.setText("Hash table cleared");
            updateAnimationControls();
        });
        
        loadSampleBtn.addActionListener(e -> {
            visualizer.loadSample();
            statusLabel.setText("Sample data loaded");
            updateAnimationControls();
        });
        
        hashFunctionCombo.addActionListener(e -> {
            HashTableAlgorithm.HashFunction selected = 
                (HashTableAlgorithm.HashFunction) hashFunctionCombo.getSelectedItem();
            visualizer.setHashFunction(selected);
            statusLabel.setText("Hash function changed to: " + selected.name());
        });
        
        resizeBtn.addActionListener(e -> {
            int newCapacity = (Integer) capacitySpinner.getValue();
            visualizer.resize(newCapacity);
            statusLabel.setText("Resized to capacity: " + newCapacity);
        });
        
        // Animation controls
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
            updateAnimationControls();
        });
        
        speedSlider.addChangeListener(e -> {
            animationEngine.setSpeed(speedSlider.getValue());
        });
        
        downloadDocsBtn.addActionListener(e -> {
            try {
                String content = PDFDocumentGenerator.generateHashTableDocumentation();
                PDFDocumentGenerator.generateModuleDocumentation(
                    "HashTable",
                    "Hash Table",
                    content
                );
                statusLabel.setText("Hash Table documentation generated");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to generate documentation: " + ex.getMessage(),
                    "Documentation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Enter key support for input fields
        keyField.addActionListener(e -> {
            if (!valueField.getText().trim().isEmpty()) {
                insertBtn.doClick();
            } else {
                valueField.requestFocus();
            }
        });
        
        valueField.addActionListener(e -> insertBtn.doClick());
        
        // Animation engine listeners
        animationEngine.addListener(new AnimationEngine.AnimationListener() {
            @Override
            public void onStepChanged(int currentStep, int totalSteps) {
                stepLabel.setText("Step: " + currentStep + "/" + totalSteps);
                visualizer.repaint();
            }
            
            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                playBtn.setEnabled(!isPlaying);
                pauseBtn.setEnabled(isPlaying);
            }
            
            @Override
            public void onAnimationComplete() {
                statusLabel.setText("Operation completed!");
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
    }
    
    public void loadDemo() {
        visualizer.loadSample();
        statusLabel.setText("Demo data loaded - try different operations!");
    }
}
