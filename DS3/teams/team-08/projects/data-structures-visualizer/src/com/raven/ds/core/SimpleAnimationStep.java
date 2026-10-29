package com.raven.ds.core;

/**
 * Simple implementation of AnimationStep interface
 */
public class SimpleAnimationStep implements AnimationEngine.AnimationStep {
    private String name;
    private Runnable executeAction;
    private Runnable resetAction;
    private String description;
    
    public SimpleAnimationStep(String name, Runnable executeAction, String description) {
        this(name, executeAction, () -> {}, description);
    }
    
    public SimpleAnimationStep(String name, Runnable executeAction, Runnable resetAction, String description) {
        this.name = name;
        this.executeAction = executeAction;
        this.resetAction = resetAction;
        this.description = description;
    }
    
    @Override
    public void execute() {
        if (executeAction != null) {
            executeAction.run();
        }
    }
    
    @Override
    public void reset() {
        if (resetAction != null) {
            resetAction.run();
        }
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    public String getName() {
        return name;
    }
}
