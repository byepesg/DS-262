package com.raven.ds.core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Timer-based animation engine for data structure visualizations
 * Provides play, pause, step, reset, and speed control functionality
 */
public class AnimationEngine {
    private Timer timer;
    private List<AnimationStep> steps;
    private int currentStep;
    private boolean isPlaying;
    private int animationSpeed; // milliseconds between steps
    private List<AnimationListener> listeners;
    
    public AnimationEngine() {
        this.steps = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.currentStep = 0;
        this.isPlaying = false;
        this.animationSpeed = 1000; // Default 1 second per step
        
        this.timer = new Timer(animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < steps.size()) {
                    executeCurrentStep();
                    currentStep++;
                    notifyStepChanged();
                } else {
                    pause();
                    notifyAnimationComplete();
                }
            }
        });
    }
    
    public void addStep(AnimationStep step) {
        steps.add(step);
    }
    
    public void addListener(AnimationListener listener) {
        listeners.add(listener);
    }
    
    public void play() {
        if (currentStep >= steps.size()) {
            reset();
        }
        isPlaying = true;
        timer.start();
        notifyPlayStateChanged();
    }
    
    public void pause() {
        isPlaying = false;
        timer.stop();
        notifyPlayStateChanged();
    }
    
    public void nextStep() {
        if (currentStep < steps.size()) {
            executeCurrentStep();
            currentStep++;
            notifyStepChanged();
        }
        if (currentStep >= steps.size()) {
            notifyAnimationComplete();
        }
    }
    
    public void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            // Re-execute all steps from beginning to current
            reset();
            for (int i = 0; i < currentStep; i++) {
                steps.get(i).execute();
            }
            notifyStepChanged();
        }
    }
    
    public void reset() {
        pause();
        currentStep = 0;
        // Execute reset for all steps
        for (AnimationStep step : steps) {
            step.reset();
        }
        notifyReset();
    }
    
    public void setSpeed(int speedMs) {
        this.animationSpeed = Math.max(50, Math.min(5000, speedMs)); // 50ms to 5s
        timer.setDelay(animationSpeed);
    }
    
    public void clearSteps() {
        pause();
        steps.clear();
        currentStep = 0;
    }
    
    private void executeCurrentStep() {
        if (currentStep < steps.size()) {
            steps.get(currentStep).execute();
        }
    }
    
    private void notifyStepChanged() {
        for (AnimationListener listener : listeners) {
            listener.onStepChanged(currentStep, steps.size());
        }
    }
    
    private void notifyPlayStateChanged() {
        for (AnimationListener listener : listeners) {
            listener.onPlayStateChanged(isPlaying);
        }
    }
    
    private void notifyAnimationComplete() {
        for (AnimationListener listener : listeners) {
            listener.onAnimationComplete();
        }
    }
    
    private void notifyReset() {
        for (AnimationListener listener : listeners) {
            listener.onReset();
        }
    }
    
    // Getters
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public int getCurrentStep() {
        return currentStep;
    }
    
    public int getTotalSteps() {
        return steps.size();
    }
    
    public int getSpeed() {
        return animationSpeed;
    }
    
    public String getCurrentStepDescription() {
        if (currentStep > 0 && currentStep <= steps.size()) {
            return steps.get(currentStep - 1).getDescription();
        }
        return "";
    }
    
    // Interfaces
    public interface AnimationStep {
        void execute();
        void reset();
        String getDescription(); // For step-by-step explanation
    }
    
    public interface AnimationListener {
        void onStepChanged(int currentStep, int totalSteps);
        void onPlayStateChanged(boolean isPlaying);
        void onAnimationComplete();
        void onReset();
    }
}
