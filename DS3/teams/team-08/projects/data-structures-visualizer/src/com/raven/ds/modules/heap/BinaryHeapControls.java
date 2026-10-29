package com.raven.ds.modules.heap;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.PDFDocumentGenerator;
import com.raven.swing.Button;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Control panel for Binary Heap operations
 */
public class BinaryHeapControls extends JPanel {
    private BinaryHeapPanel visualizer;
    private AnimationEngine animationEngine;
    
    private JTextField valueField;
    private Button insertBtn;
    private Button extractBtn;
    private Button clearBtn;
    private Button loadSampleBtn;
    private Button playBtn;
    private Button pauseBtn;
    private Button stepBtn;
    private Button resetBtn;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JLabel stepLabel;
    private JComboBox<String> heapTypeCombo;
    private Button downloadDocsBtn;
    
    public BinaryHeapControls(BinaryHeapPanel visualizer, AnimationEngine animationEngine) {
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
        valueField.setToolTipText("Enter value to insert");
        
        // Operation buttons (50% bigger)
        insertBtn = new Button();
        insertBtn.setText("Insert");
        insertBtn.setBackground(new Color(39, 174, 96)); // Green
        insertBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        insertBtn.setForeground(Color.WHITE);
        insertBtn.setPreferredSize(new Dimension(120, 40));
        insertBtn.setToolTipText("Insert value into Heap");
        
        extractBtn = new Button();
        extractBtn.setText("Extract");
        extractBtn.setBackground(new Color(231, 76, 60)); // Red
        extractBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        extractBtn.setForeground(Color.WHITE);
        extractBtn.setPreferredSize(new Dimension(120, 40));
        extractBtn.setToolTipText("Extract root element");
        
        clearBtn = new Button();
        clearBtn.setText("Clear");
        clearBtn.setBackground(new Color(231, 76, 60)); // Red
        clearBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(120, 40));
        clearBtn.setToolTipText("Clear entire Heap");
        
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
        
        // Heap type selector
        heapTypeCombo = new JComboBox<>(new String[]{"Max Heap", "Min Heap"});
        heapTypeCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        heapTypeCombo.setToolTipText("Select heap type");
        
        // Status labels
        statusLabel = new JLabel("Ready for heap operations");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        stepLabel = new JLabel("Step: 0/0");
        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Binary Heap Controls"));
    }
    
    private void setupLayout() {
        setLayout(new MigLayout("", "[][grow][]", "[]5[]5[]5[]5[]"));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Binary Heap Controls"));
        
        // Operations row
        add(new JLabel("Operations:"), "cell 0 0");
        add(valueField, "cell 1 0, split 3");
        add(insertBtn, "cell 1 0");
        add(extractBtn, "cell 1 0");
        add(clearBtn, "cell 2 0");
        
        // Heap type and sample row
        add(new JLabel("Heap Type:"), "cell 0 1");
        add(heapTypeCombo, "cell 1 1");
        add(loadSampleBtn, "cell 2 1");
        
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
        insertBtn.addActionListener(e -> {
            String valueStr = valueField.getText().trim();
            if (valueStr.isEmpty()) return;
            
            try {
                int value = Integer.parseInt(valueStr);
                visualizer.insert(value);
                statusLabel.setText("Inserting: " + value);
                updateAnimationControls();
                valueField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid integer");
            }
        });
        
        extractBtn.addActionListener(e -> {
            if (visualizer.getHeap().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Heap is empty");
                return;
            }
            visualizer.extractRoot();
            statusLabel.setText("Extracting root");
            updateAnimationControls();
        });
        
        clearBtn.addActionListener(e -> {
            visualizer.clear();
            animationEngine.clearSteps();
            statusLabel.setText("Heap cleared");
            updateAnimationControls();
        });
        
        loadSampleBtn.addActionListener(e -> {
            visualizer.loadSample();
            statusLabel.setText("Sample loaded");
            updateAnimationControls();
        });
        
        heapTypeCombo.addActionListener(e -> {
            String selected = (String) heapTypeCombo.getSelectedItem();
            boolean isMaxHeap = "Max Heap".equals(selected);
            visualizer.setMaxHeap(isMaxHeap);
            statusLabel.setText("Switched to " + selected);
        });
        
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
                String content = PDFDocumentGenerator.generateBinaryHeapDocumentation();
                PDFDocumentGenerator.generateModuleDocumentation(
                    "BinaryHeap",
                    "Binary Heap",
                    content
                );
                statusLabel.setText("Binary Heap documentation generated");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to generate documentation: " + ex.getMessage(),
                    "Documentation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        valueField.addActionListener(e -> insertBtn.doClick());
        
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
                
                insertBtn.setEnabled(!isPlaying);
                extractBtn.setEnabled(!isPlaying);
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
