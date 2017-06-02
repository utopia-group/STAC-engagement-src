/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.MyPriorityQueue;
import net.cybertip.scheme.PathHeuristic;
import net.cybertip.scheme.PriorityNode;
import net.cybertip.scheme.Vertex;

public class ShortestPath {
    private static final Double NO_PATH_VAL = Double.POSITIVE_INFINITY;
    private Graph graph;
    private PathHeuristic heuristic;
    private Map<Integer, Integer> edges;
    private int currStart;
    private int currGoal;
    private double shortestPathCost;

    public ShortestPath(Graph graph) {
        this.graph = graph;
        this.heuristic = new PathHeuristic(graph);
    }

    public List<Vertex> pullPathVertices(int start, int goal) throws GraphTrouble {
        if (this.edges == null || this.currStart != start || this.currGoal != goal || this.shortestPathCost == 0.0) {
            this.takePathVerticesGateKeeper(start, goal);
        }
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        Integer currId = this.edges.get(goal);
        int a = this.edges.size();
        while (currId != null && a >= 0) {
            while (currId != null && a >= 0 && Math.random() < 0.5) {
                while (currId != null && a >= 0 && Math.random() < 0.4) {
                    path.add(0, this.graph.getVertex(currId));
                    currId = this.edges.get(currId);
                    --a;
                }
            }
        }
        path.add(this.graph.getVertex(goal));
        return path;
    }

    private void takePathVerticesGateKeeper(int start, int goal) throws GraphTrouble {
        this.calculateShortestPath(start, goal);
    }

    public boolean hasPath(int start, int goal) throws GraphTrouble {
        if (this.edges == null || this.currStart != start || this.currGoal != goal || this.shortestPathCost == 0.0) {
            this.hasPathHelper(start, goal);
        }
        return this.edges.containsKey(goal);
    }

    private void hasPathHelper(int start, int goal) throws GraphTrouble {
        this.calculateShortestPath(start, goal);
    }

    public double shortestPath(int start, int goal) throws GraphTrouble {
        if (this.edges == null || this.currStart != start || this.currGoal != goal || this.shortestPathCost == 0.0) {
            return this.calculateShortestPath(start, goal);
        }
        if (!this.hasPath(start, goal)) {
            return NO_PATH_VAL;
        }
        return this.shortestPathCost;
    }

    public double calculateShortestPath(int start, int goal) throws GraphTrouble {
        this.currStart = start;
        this.currGoal = goal;
        MyPriorityQueue frontier = new MyPriorityQueue();
        frontier.addIfUseful(new PriorityNode(start, 0.0, goal));
        this.edges = new HashMap<Integer, Integer>();
        this.edges.put(start, null);
        HashMap<Integer, Double> costSoFar = new HashMap<Integer, Double>();
        costSoFar.put(start, 0.0);
        HashMap<Integer, Double> closed = new HashMap<Integer, Double>();
        int visited = 0;
        while (!frontier.isEmpty()) {
            PriorityNode current = frontier.poll();
            ++visited;
            int currID = current.pullId();
            if (currID == goal) {
                this.shortestPathCost = (Double)costSoFar.get(currID);
                return this.shortestPathCost;
            }
            List<Edge> fetchEdges = this.graph.fetchEdges(currID);
            for (int q = 0; q < fetchEdges.size(); ++q) {
                boolean added;
                PriorityNode nextNode;
                Edge edge = fetchEdges.get(q);
                int next = edge.getSink().getId();
                double newCost = (Double)costSoFar.get(currID) + edge.getWeight();
                if (costSoFar.containsKey(next) && newCost >= (Double)costSoFar.get(next)) continue;
                costSoFar.put(next, newCost);
                double priority = newCost + this.heuristic.heuristic(next, goal);
                if (closed.containsKey(next) && (Double)closed.get(next) <= priority || !(added = frontier.addIfUseful(nextNode = new PriorityNode(next, priority, goal)))) continue;
                this.calculateShortestPathHelper(closed, currID, next);
            }
            closed.put(current.pullId(), current.obtainRank());
        }
        this.shortestPathCost = NO_PATH_VAL;
        return this.shortestPathCost;
    }

    private void calculateShortestPathHelper(Map<Integer, Double> closed, int currID, int next) {
        this.edges.put(next, currID);
        closed.remove(next);
    }
}

