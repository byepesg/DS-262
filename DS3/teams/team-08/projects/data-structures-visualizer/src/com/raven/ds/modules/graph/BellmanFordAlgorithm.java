package com.raven.ds.modules.graph;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.SimpleAnimationStep;

import java.util.*;

/**
 * Implementation of Bellman-Ford shortest path algorithm with animation support
 */
public class BellmanFordAlgorithm {
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;
    private GraphNode startNode;
    private GraphNode endNode;
    private List<GraphNode> shortestPath;
    private AnimationEngine animationEngine;
    private boolean hasNegativeCycle;
    
    public BellmanFordAlgorithm(AnimationEngine animationEngine) {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.animationEngine = animationEngine;
        this.shortestPath = new ArrayList<>();
        this.hasNegativeCycle = false;
    }
    
    public void setNodes(List<GraphNode> nodes) {
        this.nodes = nodes;
        updateEdgesList();
    }
    
    private void updateEdgesList() {
        edges.clear();
        for (GraphNode node : nodes) {
            edges.addAll(node.getEdges());
        }
    }
    
    public void setStartNode(GraphNode start) {
        this.startNode = start;
    }
    
    public void setEndNode(GraphNode end) {
        this.endNode = end;
    }
    
    public void reset() {
        for (GraphNode node : nodes) {
            node.reset();
            for (GraphEdge edge : node.getEdges()) {
                edge.reset();
            }
        }
        shortestPath.clear();
        hasNegativeCycle = false;
    }
    
    public void runBellmanFord() {
        if (startNode == null || nodes.isEmpty()) {
            throw new IllegalStateException("Start node must be set before running Bellman-Ford");
        }
        
        reset();
        animationEngine.clearSteps();
        hasNegativeCycle = false;
        
        // Initialize distances
        startNode.setDistance(0);
        
        animationEngine.addStep(new SimpleAnimationStep("Initialize Algorithm", () -> {
            startNode.setHighlighted(true);
        }, "🚀 <b>Bellman-Ford Initialization:</b> Set distance of start node '" + startNode.getId() + 
           "' to 0, all others to infinity (∞). Will relax edges " + (nodes.size()-1) + " times."));
        
        // Relax edges |V| - 1 times
        int V = nodes.size();
        for (int i = 0; i < V - 1; i++) {
            final int iteration = i + 1;
            
            animationEngine.addStep(new SimpleAnimationStep("Iteration " + iteration, () -> {
                clearHighlights();
            }, "🔄 <b>Iteration " + iteration + " of " + (V - 1) + ":</b> Process all edges to find shorter paths. " +
               "Each iteration can improve distances by considering indirect routes."));
            
            boolean relaxed = false;
            for (GraphEdge edge : edges) {
                GraphNode source = edge.getSource();
                GraphNode target = edge.getTarget();
                
                if (source.getDistance() != Integer.MAX_VALUE) {
                    int newDistance = source.getDistance() + edge.getWeight();
                    
                    animationEngine.addStep(new SimpleAnimationStep("Check Edge", () -> {
                        clearHighlights();
                        // Enhanced visualization: show exploration
                        edge.setColor(GraphEdge.RELAXING_COLOR);
                        edge.setHighlighted(true);
                        source.setColor(GraphNode.EXPLORING_COLOR);
                        source.setHighlighted(true);
                        target.setColor(GraphNode.EXPLORING_COLOR);
                        target.setHighlighted(true);
                    }, "🔍 <b>Examining Edge:</b> " + source.getId() + " → " + target.getId() + 
                       " (weight: " + edge.getWeight() + "). Check if path through '" + source.getId() + 
                       "' offers shorter route to '" + target.getId() + "'. Current distance: " + 
                       (target.getDistance() == Integer.MAX_VALUE ? "∞" : target.getDistance()) + 
                       ", New path cost: " + newDistance));
                    
                    if (newDistance < target.getDistance()) {
                        final int oldDistance = target.getDistance();
                        relaxed = true;
                        
                        animationEngine.addStep(new SimpleAnimationStep("Relax Edge", () -> {
                            target.setDistance(newDistance);
                            target.setPredecessor(source);
                            // Show successful relaxation with green colors
                            target.setColor(GraphNode.IMPROVEMENT_COLOR);
                            edge.setColor(GraphEdge.IMPROVEMENT_COLOR);
                        }, "✅ <b>Edge Relaxed:</b> Found shorter path! Update '" + target.getId() + 
                           "' distance from " + (oldDistance == Integer.MAX_VALUE ? "∞" : oldDistance) + 
                           " to " + newDistance + " via '" + source.getId() + "'"));
                    } else {
                        animationEngine.addStep(new SimpleAnimationStep("No Relaxation", () -> {
                            // Show rejection with light red colors
                            target.setColor(GraphNode.NO_IMPROVEMENT_COLOR);
                            edge.setColor(GraphEdge.NO_IMPROVEMENT_COLOR);
                        }, "❌ <b>No Improvement:</b> Current path cost " + newDistance + 
                           " ≥ existing distance " + target.getDistance() + ". Keep current best path."));
                    }
                }
            }
            
            if (!relaxed) {
                animationEngine.addStep(new SimpleAnimationStep("Early Termination", () -> {
                    clearHighlights();
                }, "🏃‍♂️ <b>Early Termination:</b> No edges were relaxed in iteration " + iteration + 
                   ". All shortest paths found! Skipping remaining iterations."));
                break;
            }
        }
        
        // Check for negative cycles with enhanced explanation
        animationEngine.addStep(new SimpleAnimationStep("Negative Cycle Check", () -> {
            clearHighlights();
        }, "🔍 <b>Negative Cycle Detection:</b> Final check - try relaxing edges once more. " +
           "If any edge can still be relaxed, a negative cycle exists!"));
        
        for (GraphEdge edge : edges) {
            GraphNode source = edge.getSource();
            GraphNode target = edge.getTarget();
            
            if (source.getDistance() != Integer.MAX_VALUE) {
                int newDistance = source.getDistance() + edge.getWeight();
                
                if (newDistance < target.getDistance()) {
                    hasNegativeCycle = true;
                    
                    animationEngine.addStep(new SimpleAnimationStep("Negative Cycle Found", () -> {
                        edge.setHighlighted(true);
                        source.setHighlighted(true);
                        target.setHighlighted(true);
                    }, "⚠️ <b>Negative Cycle Detected!</b> Edge " + source.getId() + " → " + target.getId() + 
                       " can still be relaxed. This graph contains a negative cycle!"));
                    
                    animationEngine.addStep(new SimpleAnimationStep("Algorithm Failed", () -> {
                        clearHighlights();
                    }, "🚫 <b>Algorithm Failed:</b> Bellman-Ford cannot find shortest paths in graphs with negative cycles."));
                    return;
                }
            }
        }
        
        if (endNode != null && endNode.getDistance() != Integer.MAX_VALUE) {
            buildShortestPath();
            animationEngine.addStep(new SimpleAnimationStep("Path Found", () -> {
                highlightShortestPath();
            }, "🎯 <b>Shortest Path Complete!</b> Optimal distance from '" + startNode.getId() + 
               "' to '" + endNode.getId() + "' is " + endNode.getDistance() + 
               ". Path highlighted showing the sequence of edges that form the shortest route."));
        } else if (endNode != null) {
            animationEngine.addStep(new SimpleAnimationStep("No Path Found", () -> {
                endNode.setHighlighted(true);
                endNode.setColor(GraphNode.NO_IMPROVEMENT_COLOR);
            }, "🚫 <b>No Path Available:</b> Target node '" + endNode.getId() + 
               "' is not reachable from start node '" + startNode.getId() + "'. Distance remains ∞."));
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Algorithm Complete", () -> {
            // Keep shortest path highlighted but clear other highlights
            for (GraphNode node : nodes) {
                if (!shortestPath.contains(node)) {
                    node.setHighlighted(false);
                }
                for (GraphEdge edge : node.getEdges()) {
                    if (!isEdgeInShortestPath(edge)) {
                        edge.setHighlighted(false);
                    }
                }
            }
        }, "🏁 <b>Bellman-Ford Algorithm Complete!</b> " +
           "Successfully computed shortest paths from '" + startNode.getId() + "' to all reachable nodes. " +
           "This algorithm can handle negative edge weights and detect negative cycles."));
    }
    
    private boolean isEdgeInShortestPath(GraphEdge edge) {
        if (shortestPath.size() < 2) return false;
        
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            GraphNode from = shortestPath.get(i);
            GraphNode to = shortestPath.get(i + 1);
            
            if ((edge.getSource() == from && edge.getTarget() == to) ||
                (edge.getSource() == to && edge.getTarget() == from)) {
                return true;
            }
        }
        return false;
    }
    
    private void clearHighlights() {
        for (GraphNode node : nodes) {
            node.setHighlighted(false);
        }
        for (GraphEdge edge : edges) {
            edge.setHighlighted(false);
        }
    }
    
    private void buildShortestPath() {
        shortestPath.clear();
        if (endNode == null || endNode.getDistance() == Integer.MAX_VALUE) return;
        
        GraphNode current = endNode;
        while (current != null) {
            shortestPath.add(0, current);
            current = current.getPredecessor();
        }
    }
    
    private void highlightShortestPath() {
        clearHighlights();
        
        // Highlight path nodes
        for (GraphNode node : shortestPath) {
            node.setHighlighted(true);
        }
        
        // Highlight path edges
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            GraphNode current = shortestPath.get(i);
            GraphNode next = shortestPath.get(i + 1);
            
            for (GraphEdge edge : current.getEdges()) {
                if (edge.getTarget() == next) {
                    edge.setPathColor();
                    break;
                }
            }
        }
    }
    
    // Getters
    public List<GraphNode> getNodes() { return nodes; }
    public GraphNode getStartNode() { return startNode; }
    public GraphNode getEndNode() { return endNode; }
    public List<GraphNode> getShortestPath() { return shortestPath; }
    public boolean hasNegativeCycle() { return hasNegativeCycle; }
}
