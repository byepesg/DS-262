package com.raven.ds.modules.dynamicarray;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.PDFDocumentGenerator;
import com.raven.swing.Button;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Control panel for Dynamic Array operations
 */
public class DynamicArrayControls extends JPanel {
    private DynamicArrayPanel visualizer;
    private AnimationEngine animationEngine;
    
    private JTextField valueField;
    private JTextField indexField;
    private Button addBtn;
    private Button removeBtn;
    private Button clearBtn;
    private Button loadSampleBtn;
    private Button playBtn;
    private Button pauseBtn;
    private Button stepBtn;
    private Button resetBtn;
    private Button downloadDocsBtn;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JLabel stepLabel;
    
    public DynamicArrayControls(DynamicArrayPanel visualizer, AnimationEngine animationEngine) {
        this.visualizer = visualizer;
        this.animationEngine = animationEngine;
        
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        // Input fields
        valueField = new JTextField(10);
        valueField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        valueField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        valueField.setToolTipText("Enter value to add");
        
        indexField = new JTextField(5);
        indexField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        indexField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        indexField.setToolTipText("Enter index to remove");
        
        // Operation buttons (50% bigger)
        addBtn = new Button();
        addBtn.setText("Add");
        addBtn.setBackground(new Color(39, 174, 96)); // Green
        addBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        addBtn.setForeground(Color.WHITE);
        addBtn.setPreferredSize(new Dimension(120, 40));
        addBtn.setToolTipText("Add value to array");
        
        removeBtn = new Button();
        removeBtn.setText("Remove");
        removeBtn.setBackground(new Color(231, 76, 60)); // Red
        removeBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setPreferredSize(new Dimension(120, 40));
        removeBtn.setToolTipText("Remove value at index");
        
        clearBtn = new Button();
        clearBtn.setText("Clear");
        clearBtn.setBackground(new Color(231, 76, 60)); // Red
        clearBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(120, 40));
        clearBtn.setToolTipText("Clear entire array");
        
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
        stepBtn.setBackground(new Color(53, 106, 230)); // Primary blue
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
        speedSlider.setBackground(Color.WHITE);
        
        // Status labels
        statusLabel = new JLabel("Ready for array operations");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        stepLabel = new JLabel("Step: 0/0");
        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Dynamic Array Controls"));
    }
    
    private void setupLayout() {
        setLayout(new MigLayout("", "[][grow][]", "[]5[]5[]5[]5[]"));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Dynamic Array Controls"));
        
        // Operations row
        add(new JLabel("Operations:"), "cell 0 0");
        add(valueField, "cell 1 0, split 3");
        add(indexField, "cell 1 0");
        add(addBtn, "cell 1 0");
        add(removeBtn, "cell 2 0");
        
        // Sample row
        add(new JLabel("Sample:"), "cell 0 1");
        add(loadSampleBtn, "cell 1 1");
        add(clearBtn, "cell 2 1");
        
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
        addBtn.addActionListener(e -> {
            String valueStr = valueField.getText().trim();
            if (valueStr.isEmpty()) return;
            
            try {
                int value = Integer.parseInt(valueStr);
                visualizer.add(value);
                statusLabel.setText("Adding: " + value);
                updateAnimationControls();
                valueField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid integer");
            }
        });
        
        removeBtn.addActionListener(e -> {
            String indexStr = indexField.getText().trim();
            if (indexStr.isEmpty()) return;
            
            try {
                int index = Integer.parseInt(indexStr);
                if (index < 0 || index >= visualizer.getArraySize()) {
                     JOptionPane.showMessageDialog(this, "Index out of bounds");
                     return;
                }
                visualizer.remove(index);
                statusLabel.setText("Removing at index: " + index);
                updateAnimationControls();
                indexField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid index");
            }
        });
        
        clearBtn.addActionListener(e -> {
            visualizer.clear();
            animationEngine.clearSteps();
            statusLabel.setText("Array cleared");
            updateAnimationControls();
        });
        
        loadSampleBtn.addActionListener(e -> {
            visualizer.loadSample();
            statusLabel.setText("Sample loaded");
            updateAnimationControls();
        });
        
        // Animation controls
        playBtn.addActionListener(e -> {
            animationEngine.play();
            statusLabel.setText("Playing...");
        });
        
        pauseBtn.addActionListener(e -> {
            animationEngine.pause();
            statusLabel.setText("Paused");
        });
        
        stepBtn.addActionListener(e -> {
            animationEngine.nextStep();
            statusLabel.setText("Step executed");
        });
        
        resetBtn.addActionListener(e -> {
            animationEngine.reset();
            statusLabel.setText("Reset");
            updateAnimationControls();
        });
        
        speedSlider.addChangeListener(e -> {
            animationEngine.setSpeed(speedSlider.getValue());
        });
        
        downloadDocsBtn.addActionListener(e -> {
            try {
                String content = PDFDocumentGenerator.generateDynamicArrayDocumentation();
                PDFDocumentGenerator.generateModuleDocumentation(
                    "DynamicArray",
                    "Dynamic Array",
                    content
                );
                statusLabel.setText("Dynamic Array documentation generated");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to generate documentation: " + ex.getMessage(),
                    "Documentation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        valueField.addActionListener(e -> addBtn.doClick());
        
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
                stepBtn.setEnabled(!isPlaying);
                
                addBtn.setEnabled(!isPlaying);
                removeBtn.setEnabled(!isPlaying);
                clearBtn.setEnabled(!isPlaying);
                loadSampleBtn.setEnabled(!isPlaying);
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
        statusLabel.setText("Demo data loaded");
    }
}
