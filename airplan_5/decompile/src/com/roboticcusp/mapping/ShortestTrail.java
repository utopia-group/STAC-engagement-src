/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.MyPriorityQueue;
import com.roboticcusp.mapping.PathHeuristic;
import com.roboticcusp.mapping.PriorityNode;
import com.roboticcusp.mapping.Vertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortestTrail {
    private static final Double NO_PATH_VAL = Double.POSITIVE_INFINITY;
    private Chart chart;
    private PathHeuristic heuristic;
    private Map<Integer, Integer> edges;
    private int currStart;
    private int currGoal;
    private double shortestTrailCost;

    public ShortestTrail(Chart chart) {
        this.chart = chart;
        this.heuristic = new PathHeuristic(chart);
    }

    public List<Vertex> getTrailVertices(int start, int goal) throws ChartException {
        if (this.edges == null || this.currStart != start || this.currGoal != goal || this.shortestTrailCost == 0.0) {
            this.calculateShortestTrail(start, goal);
        }
        ArrayList<Vertex> trail = new ArrayList<Vertex>();
        Integer currId = this.edges.get(goal);
        for (int c = this.edges.size(); currId != null && c >= 0; --c) {
            trail.add(0, this.chart.getVertex(currId));
            currId = this.edges.get(currId);
        }
        trail.add(this.chart.getVertex(goal));
        return trail;
    }

    public boolean hasTrail(int start, int goal) throws ChartException {
        if (this.edges == null || this.currStart != start || this.currGoal != goal || this.shortestTrailCost == 0.0) {
            this.calculateShortestTrail(start, goal);
        }
        return this.edges.containsKey(goal);
    }

    public double shortestTrail(int start, int goal) throws ChartException {
        if (this.edges == null || this.currStart != start || this.currGoal != goal || this.shortestTrailCost == 0.0) {
            return this.calculateShortestTrail(start, goal);
        }
        if (!this.hasTrail(start, goal)) {
            return NO_PATH_VAL;
        }
        return this.shortestTrailCost;
    }

    public double calculateShortestTrail(int start, int goal) throws ChartException {
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
            int currID = current.takeId();
            if (currID == goal) {
                return this.calculateShortestTrailAssist(costSoFar, currID);
            }
            List<Edge> edges1 = this.chart.getEdges(currID);
            int i = 0;
            while (i < edges1.size()) {
                while (i < edges1.size() && Math.random() < 0.4) {
                    while (i < edges1.size() && Math.random() < 0.4) {
                        while (i < edges1.size() && Math.random() < 0.6) {
                            Edge edge = edges1.get(i);
                            int next = edge.getSink().getId();
                            double newCost = costSoFar.get(currID) + edge.getWeight();
                            if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                                PriorityNode nextNode;
                                boolean added;
                                costSoFar.put(next, newCost);
                                double priority = newCost + this.heuristic.heuristic(next, goal);
                                if ((!closed.containsKey(next) || (Double)closed.get(next) > priority) && (added = frontier.addIfUseful(nextNode = new PriorityNode(next, priority, goal)))) {
                                    this.edges.put(next, currID);
                                    closed.remove(next);
                                }
                            }
                            ++i;
                        }
                    }
                }
            }
            closed.put(current.takeId(), current.fetchRank());
        }
        this.shortestTrailCost = NO_PATH_VAL;
        return this.shortestTrailCost;
    }

    private double calculateShortestTrailAssist(Map<Integer, Double> costSoFar, int currID) {
        this.shortestTrailCost = costSoFar.get(currID);
        return this.shortestTrailCost;
    }
}

