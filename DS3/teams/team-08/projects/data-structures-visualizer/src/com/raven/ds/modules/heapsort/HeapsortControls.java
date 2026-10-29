package com.raven.ds.modules.heapsort;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.PDFDocumentGenerator;
import com.raven.swing.Button;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Control panel for Heapsort operations
 */
public class HeapsortControls extends JPanel {
    private HeapsortPanel visualizer;
    private AnimationEngine animationEngine;
    
    private JTextField inputField;
    private Button loadBtn;
    private Button sortBtn;
    private Button sampleBtn;
    private Button playBtn;
    private Button pauseBtn;
    private Button stepBtn;
    private Button resetBtn;
    private Button downloadDocsBtn;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JLabel stepLabel;
    
    public HeapsortControls(HeapsortPanel visualizer, AnimationEngine animationEngine) {
        this.visualizer = visualizer;
        this.animationEngine = animationEngine;
        
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        // Input fields
        inputField = new JTextField(20);
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        inputField.setToolTipText("Example: 12, 11, 13, 5, 6");
        
        // Operation buttons (50% bigger)
        loadBtn = new Button();
        loadBtn.setText("Load");
        loadBtn.setBackground(new Color(52, 152, 219)); // Blue
        loadBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        loadBtn.setForeground(Color.WHITE);
        loadBtn.setPreferredSize(new Dimension(120, 40));
        loadBtn.setToolTipText("Load data from input field");
        
        sortBtn = new Button();
        sortBtn.setText("Sort");
        sortBtn.setBackground(new Color(39, 174, 96)); // Green
        sortBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        sortBtn.setForeground(Color.WHITE);
        sortBtn.setPreferredSize(new Dimension(120, 40));
        sortBtn.setToolTipText("Start Heapsort algorithm");
        
        sampleBtn = new Button();
        sampleBtn.setText("Sample");
        sampleBtn.setBackground(new Color(53, 162, 230)); // Lighter blue
        sampleBtn.setFont(new Font("sansserif", Font.BOLD, 12));
        sampleBtn.setForeground(Color.WHITE);
        sampleBtn.setPreferredSize(new Dimension(120, 40));
        sampleBtn.setToolTipText("Load sample data");
        
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
        statusLabel = new JLabel("Ready to sort");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        stepLabel = new JLabel("Step: 0/0");
        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Heapsort Controls"));
    }
    
    private void setupLayout() {
        setLayout(new MigLayout("", "[][grow][]", "[]5[]5[]5[]5[]"));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Heapsort Controls"));
        
        // Input row
        add(new JLabel("Data:"), "cell 0 0");
        add(inputField, "cell 1 0, split 2");
        add(loadBtn, "cell 1 0");
        add(sortBtn, "cell 2 0");
        
        // Sample row
        add(new JLabel("Sample:"), "cell 0 1");
        add(sampleBtn, "cell 1 1");
        
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
        loadBtn.addActionListener(e -> {
            String input = inputField.getText().trim();
            if (input.isEmpty()) return;
            
            try {
                String[] parts = input.split("[,\\s]+");
                int[] values = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    values[i] = Integer.parseInt(parts[i].trim());
                }
                
                visualizer.loadData(values);
                statusLabel.setText("Data loaded. Click 'Start Sort' to begin.");
                updateAnimationControls();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid input format. Use comma separated numbers.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        sortBtn.addActionListener(e -> {
            visualizer.sort();
            statusLabel.setText("Sorting started...");
            updateAnimationControls();
        });
        
        sampleBtn.addActionListener(e -> {
            visualizer.loadSample();
            statusLabel.setText("Sample data loaded");
            updateAnimationControls();
        });
        
        // Animation controls
        playBtn.addActionListener(e -> {
            animationEngine.play();
            statusLabel.setText("Sorting in progress...");
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
                String content = PDFDocumentGenerator.generateHeapsortDocumentation();
                PDFDocumentGenerator.generateModuleDocumentation(
                    "Heapsort",
                    "Heapsort Algorithm",
                    content
                );
                statusLabel.setText("Heapsort documentation generated");
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
                stepBtn.setEnabled(!isPlaying);
                
                loadBtn.setEnabled(!isPlaying);
                sortBtn.setEnabled(!isPlaying);
                sampleBtn.setEnabled(!isPlaying);
            }
            
            @Override
            public void onAnimationComplete() {
                statusLabel.setText("Sorting completed!");
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
