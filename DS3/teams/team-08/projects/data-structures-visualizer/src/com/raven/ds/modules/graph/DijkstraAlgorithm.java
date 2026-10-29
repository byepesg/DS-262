package com.raven.ds.modules.graph;

import com.raven.ds.core.AnimationEngine;
import com.raven.ds.core.SimpleAnimationStep;

import java.util.*;

/**
 * Implementation of Dijkstra's shortest path algorithm with animation support
 */
public class DijkstraAlgorithm {
    private List<GraphNode> nodes;
    private GraphNode startNode;
    private GraphNode endNode;
    private List<GraphNode> shortestPath;
    private PriorityQueue<GraphNode> unvisitedNodes;
    private AnimationEngine animationEngine;
    
    public DijkstraAlgorithm(AnimationEngine animationEngine) {
        this.nodes = new ArrayList<>();
        this.animationEngine = animationEngine;
        this.shortestPath = new ArrayList<>();
    }
    
    public void addNode(GraphNode node) {
        nodes.add(node);
    }
    
    public void removeNode(GraphNode node) {
        nodes.remove(node);
        // Remove edges pointing to this node
        for (GraphNode n : nodes) {
            n.getEdges().removeIf(edge -> edge.getTarget() == node);
        }
    }
    
    public void addEdge(GraphNode source, GraphNode target, int weight) {
        source.addEdge(target, weight);
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
        startNode = null;
        endNode = null;
    }
    
    public void runDijkstra() {
        if (startNode == null) {
            throw new IllegalStateException("Start node must be set before running Dijkstra");
        }
        
        // Save start and end nodes before reset
        GraphNode savedStartNode = startNode;
        GraphNode savedEndNode = endNode;
        
        reset();
        animationEngine.clearSteps();
        
        // Restore start and end nodes after reset
        startNode = savedStartNode;
        endNode = savedEndNode;
        
        // Initialize distances
        startNode.setDistance(0);
        
        // Create priority queue
        unvisitedNodes = new PriorityQueue<>(Comparator.comparingInt(GraphNode::getDistance));
        unvisitedNodes.addAll(nodes);
        
        // Add initialization step with detailed explanation
        animationEngine.addStep(new SimpleAnimationStep("Initialize Algorithm", () -> {
            startNode.setHighlighted(true);
            startNode.setColor(GraphNode.START_COLOR);
            if (endNode != null) {
                endNode.setColor(GraphNode.END_COLOR);
            }
        }, "🚀 <b>Dijkstra's Algorithm Initialization:</b> Starting from node '" + startNode.getId() + 
           "' (distance = 0). Target: " + (endNode != null ? "node '" + endNode.getId() + "'" : "all nodes") + 
           ". All other nodes start with distance ∞. Algorithm will process nodes in order of shortest distance found so far."));

        int stepCount = 0;
        boolean targetFound = false;
        
        while (!unvisitedNodes.isEmpty()) {
            GraphNode current = unvisitedNodes.poll();
            
            if (current.getDistance() == Integer.MAX_VALUE) {
                animationEngine.addStep(new SimpleAnimationStep("No More Reachable Nodes", () -> {
                    clearEdgeHighlights();
                }, "⚠️ <b>Algorithm Termination:</b> No more nodes can be reached from the start node. " +
                   "Remaining nodes in queue have infinite distance, meaning they are disconnected from the source."));
                break; // No more reachable nodes
            }
            
            stepCount++;
            final GraphNode currentNode = current;
            final int currentStep = stepCount;
            
            // Check if we've reached the target
            if (current == endNode && !targetFound) {
                targetFound = true;
                animationEngine.addStep(new SimpleAnimationStep("Target Found!", () -> {
                    clearEdgeHighlights();
                    currentNode.setColor(GraphNode.END_COLOR);
                    currentNode.setHighlighted(true);
                }, "🎯 <b>TARGET REACHED!</b> Found node '" + currentNode.getId() + 
                   "' with shortest distance: <b>" + currentNode.getDistance() + "</b>. " +
                   "Will continue processing to show complete algorithm and find shortest paths to all remaining nodes."));
            }
            
            animationEngine.addStep(new SimpleAnimationStep("Visit Node " + currentStep, () -> {
                // Clear only edge highlights but preserve node visited states
                clearEdgeHighlights();
                // Set current node to processing color (unless it's start/end)
                if (currentNode != startNode && currentNode != endNode) {
                    currentNode.setColor(GraphNode.CURRENT_COLOR);
                }
                currentNode.setHighlighted(true);
                currentNode.setVisited(true);
            }, "🔍 <b>Step " + currentStep + " - Processing Node '" + currentNode.getId() + "':</b> " +
               "Current shortest distance = " + currentNode.getDistance() + ". " +
               "This node has the smallest unprocessed distance, so its shortest path is now finalized. " +
               "Examining all neighbors to see if we can improve their distances."));
            
            // First, show all neighbors to illuminate the exploration
            animationEngine.addStep(new SimpleAnimationStep("Show Neighbors", () -> {
                // Highlight all unvisited neighbors and their connecting edges
                for (GraphEdge edge : currentNode.getEdges()) {
                    GraphNode neighbor = edge.getTarget();
                    if (!neighbor.isVisited()) {
                        // Illuminate neighbor with exploration color
                        neighbor.setColor(GraphNode.EXPLORING_COLOR);
                        neighbor.setHighlighted(true);
                        // Highlight edge with exploration color
                        edge.setColor(GraphEdge.EXPLORING_COLOR);
                        edge.setHighlighted(true);
                    }
                }
            }, "🔍 <b>Exploring Neighbors:</b> Highlighting all unvisited neighbors of '" + 
               currentNode.getId() + "' to show algorithm's search progression."));
            
            // Process each neighbor with enhanced visualization
            for (GraphEdge edge : current.getEdges()) {
                GraphNode neighbor = edge.getTarget();
                
                if (neighbor.isVisited()) continue;
                
                int newDistance = current.getDistance() + edge.getWeight();
                final GraphNode finalNeighbor = neighbor;
                final GraphEdge finalEdge = edge;
                
                // Show which edge/neighbor we're currently examining
                animationEngine.addStep(new SimpleAnimationStep("Focus Edge", () -> {
                    // Reset neighbor exploration colors first
                    for (GraphEdge e : currentNode.getEdges()) {
                        GraphNode n = e.getTarget();
                        if (!n.isVisited() && n != finalNeighbor) {
                            n.setColor(GraphNode.DEFAULT_COLOR);
                            n.setHighlighted(false);
                            e.setColor(GraphEdge.DEFAULT_COLOR);
                            e.setHighlighted(false);
                        }
                    }
                    // Highlight the current edge and neighbor being examined
                    finalEdge.setColor(GraphEdge.RELAXING_COLOR);
                    finalEdge.setHighlighted(true);
                    finalNeighbor.setColor(GraphNode.RELAXING_COLOR);
                    finalNeighbor.setHighlighted(true);
                }, "🎯 <b>Examining Edge:</b> Focusing on path from '" + current.getId() + 
                   "' to '" + neighbor.getId() + "' with weight " + edge.getWeight() + 
                   ". Calculating total distance: " + current.getDistance() + " + " + edge.getWeight() + " = " + newDistance + "."));
                
                if (newDistance < neighbor.getDistance()) {
                    final int oldDistance = neighbor.getDistance();
                    animationEngine.addStep(new SimpleAnimationStep("Update Distance", () -> {
                        neighbor.setDistance(newDistance);
                        neighbor.setPredecessor(current);
                        // Use improvement color to show successful relaxation
                        neighbor.setColor(GraphNode.IMPROVEMENT_COLOR);
                        edge.setColor(GraphEdge.IMPROVEMENT_COLOR);
                        // Update priority queue
                        unvisitedNodes.remove(neighbor);
                        unvisitedNodes.add(neighbor);
                    }, "✅ <b>Path Improved!</b> Found shorter path to '" + neighbor.getId() + 
                       "'. Distance updated from " + (oldDistance == Integer.MAX_VALUE ? "∞" : oldDistance) + 
                       " to " + newDistance + " via node '" + current.getId() + "'."));
                } else {
                    animationEngine.addStep(new SimpleAnimationStep("No Improvement", () -> {
                        // Show that this path doesn't improve distance
                        finalNeighbor.setColor(GraphNode.NO_IMPROVEMENT_COLOR);
                        finalEdge.setColor(GraphEdge.NO_IMPROVEMENT_COLOR);
                    }, "❌ <b>No Improvement:</b> Path cost " + newDistance + 
                       " ≥ current distance " + neighbor.getDistance() + ". Keeping existing path."));
                    
                    // Reset the edge and neighbor color after showing rejection
                    animationEngine.addStep(new SimpleAnimationStep("Reset Colors", () -> {
                        finalEdge.setColor(GraphEdge.DEFAULT_COLOR);
                        finalEdge.setHighlighted(false);
                        finalNeighbor.setColor(GraphNode.DEFAULT_COLOR);
                        finalNeighbor.setHighlighted(false);
                    }, ""));
                }
            }
            
            // Mark the current node as fully processed
            animationEngine.addStep(new SimpleAnimationStep("Mark Processed", () -> {
                currentNode.setColor(GraphNode.VISITED_COLOR);
                currentNode.setHighlighted(false);
                // Clean up any remaining neighbor highlights
                for (GraphEdge edge : currentNode.getEdges()) {
                    GraphNode neighbor = edge.getTarget();
                    if (!neighbor.isVisited()) {
                        neighbor.setHighlighted(false);
                        if (neighbor.getPredecessor() != currentNode) {
                            neighbor.setColor(GraphNode.DEFAULT_COLOR);
                        }
                        edge.setHighlighted(false);
                        if (!edge.getColor().equals(GraphEdge.IMPROVEMENT_COLOR)) {
                            edge.setColor(GraphEdge.DEFAULT_COLOR);
                        }
                    }
                }
            }, "✅ <b>Node '" + currentNode.getId() + "' Processed:</b> All outgoing edges examined. " +
               "Shortest path to this node is now permanently established (distance = " + currentNode.getDistance() + "). " +
               (targetFound ? "Target found but continuing to process remaining nodes for complete algorithm demonstration." : 
                "Moving to next closest unvisited node.")));
        }
        
        // Show final results and path highlighting
        if (targetFound) {
            buildShortestPath();
            animationEngine.addStep(new SimpleAnimationStep("Highlight Shortest Path", () -> {
                highlightShortestPath();
            }, "🎯 <b>Shortest Path Complete!</b> Final path from '" + startNode.getId() + 
               "' to '" + endNode.getId() + "' highlighted with total distance: <b>" + endNode.getDistance() + "</b>. " +
               "This is guaranteed to be the optimal solution."));
        }
        
        animationEngine.addStep(new SimpleAnimationStep("Algorithm Complete", () -> {
            // Keep the shortest path highlighted but clear other highlights
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
        }, "🏁 <b>Dijkstra's Algorithm Complete!</b> All reachable nodes processed and shortest distances determined. " +
           (targetFound ? "Optimal path is highlighted in the graph." : 
            "Shortest paths from '" + startNode.getId() + "' to all reachable nodes have been computed.")));
    }
    
    private void clearHighlights() {
        for (GraphNode node : nodes) {
            node.setHighlighted(false);
            for (GraphEdge edge : node.getEdges()) {
                edge.setHighlighted(false);
            }
        }
    }
    
    private void clearEdgeHighlights() {
        for (GraphNode node : nodes) {
            for (GraphEdge edge : node.getEdges()) {
                edge.setHighlighted(false);
            }
        }
    }
    
    private void buildShortestPath() {
        shortestPath.clear();
        GraphNode current = endNode;
        
        while (current != null) {
            shortestPath.add(0, current);
            current = current.getPredecessor();
        }
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
    
    public void loadSampleGraph() {
        reset();
        nodes.clear();
        
        // Create sample nodes
        GraphNode nodeA = new GraphNode("A", 100, 100);
        GraphNode nodeB = new GraphNode("B", 300, 100);
        GraphNode nodeC = new GraphNode("C", 200, 200);
        GraphNode nodeD = new GraphNode("D", 400, 200);
        GraphNode nodeE = new GraphNode("E", 300, 300);
        
        addNode(nodeA);
        addNode(nodeB);
        addNode(nodeC);
        addNode(nodeD);
        addNode(nodeE);
        
        // Add edges with weights
        addEdge(nodeA, nodeB, 4);
        addEdge(nodeA, nodeC, 2);
        addEdge(nodeB, nodeC, 1);
        addEdge(nodeB, nodeD, 5);
        addEdge(nodeC, nodeD, 8);
        addEdge(nodeC, nodeE, 10);
        addEdge(nodeD, nodeE, 2);
        
        setStartNode(nodeA);
        setEndNode(nodeE);
    }
    
    // Getters
    public List<GraphNode> getNodes() { return nodes; }
    public GraphNode getStartNode() { return startNode; }
    public GraphNode getEndNode() { return endNode; }
    public List<GraphNode> getShortestPath() { return shortestPath; }
    
    public GraphNode getNodeAt(int x, int y) {
        for (GraphNode node : nodes) {
            if (node.contains(x, y)) {
                return node;
            }
        }
        return null;
    }
}
