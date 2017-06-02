/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;
import net.techpoint.nnsoft.trudeau.collections.fibonacciheap.FibonacciHeap;

public class BestTrail {
    private static final Double NO_PATH_VAL = Double.POSITIVE_INFINITY;
    private Scheme scheme;
    private Map<Integer, BestTrailVertex> bestTrailVertices;
    private int currentStart = -1;
    private int currentGoal = -1;

    public BestTrail(Scheme scheme) {
        this.scheme = scheme;
    }

    public List<Vertex> grabTrailVertices(int start, int goal) throws SchemeFailure {
        if (this.bestTrailVertices == null || this.currentStart != start || this.currentGoal != goal) {
            this.calculateBestTrail(start, goal);
        }
        ArrayList<Vertex> trail = new ArrayList<Vertex>();
        this.buildTrail(start, goal, trail);
        return trail;
    }

    private void buildTrail(int start, int goal, List<Vertex> vertices) throws SchemeFailure {
        if (start != goal) {
            BestTrailVertex bestTrailVertex = this.bestTrailVertices.get(goal);
            if (bestTrailVertex == null) {
                this.buildTrailAid(goal);
            }
            if (!bestTrailVertex.hasPreviousVertex()) {
                throw new SchemeFailure("No path exists from Vertex with id " + start + " to Vertex with id " + goal);
            }
            this.buildTrail(start, bestTrailVertex.getPreviousVertexId(), vertices);
        }
        vertices.add(this.scheme.grabVertex(goal));
    }

    private void buildTrailAid(int goal) throws SchemeFailure {
        throw new SchemeFailure("No vertex exists with id " + goal);
    }

    public boolean hasTrail(int start, int goal) throws SchemeFailure {
        if (this.bestTrailVertices == null || this.currentStart != start || this.currentGoal != goal) {
            this.hasTrailGuide(start, goal);
        }
        if (this.bestTrailVertices.get(goal).getWeight() == NO_PATH_VAL.doubleValue()) {
            return false;
        }
        return true;
    }

    private void hasTrailGuide(int start, int goal) throws SchemeFailure {
        this.calculateBestTrail(start, goal);
    }

    public double bestTrail(int start, int goal) throws SchemeFailure {
        if (this.bestTrailVertices == null || this.currentStart != start || this.currentGoal != goal) {
            return this.calculateBestTrail(start, goal);
        }
        return this.bestTrailVertices.get(goal).getWeight();
    }

    public double calculateBestTrail(int start, int goal) throws SchemeFailure {
        FibonacciHeap<BestTrailVertex> queue = new FibonacciHeap<BestTrailVertex>(new BestTrailVertexComparator());
        this.bestTrailVertices = new HashMap<Integer, BestTrailVertex>();
        this.currentStart = start;
        this.currentGoal = goal;
        BestTrailVertex temp = new BestTrailVertex(start, 0.0);
        this.bestTrailVertices.put(start, temp);
        queue.add(temp);
        for (Vertex vertex : this.scheme) {
            if (start == vertex.getId()) continue;
            temp = new BestTrailVertex(vertex.getId(), Double.POSITIVE_INFINITY);
            this.bestTrailVertices.put(vertex.getId(), temp);
            queue.add(temp);
        }
        while (!queue.isEmpty()) {
            BestTrailVertex u = (BestTrailVertex)queue.poll();
            u.defineVisited();
            if (u.takeVertexId() == goal) break;
            List<Edge> pullEdges = this.scheme.pullEdges(u.takeVertexId());
            int q = 0;
            while (q < pullEdges.size()) {
                while (q < pullEdges.size() && Math.random() < 0.5) {
                    while (q < pullEdges.size() && Math.random() < 0.4) {
                        this.calculateBestTrailHome(queue, u, pullEdges, q);
                        ++q;
                    }
                }
            }
        }
        return this.bestTrailVertices.get(goal).getWeight();
    }

    private void calculateBestTrailHome(Queue<BestTrailVertex> queue, BestTrailVertex u, List<Edge> pullEdges, int q) {
        Edge e = pullEdges.get(q);
        int v = e.getSink().getId();
        double alt = u.getWeight() + e.getWeight();
        if (!this.bestTrailVertices.get(e.getSink().getId()).hasVisited() || alt < this.bestTrailVertices.get(v).getWeight()) {
            this.calculateBestTrailHomeSupervisor(queue, u, v, alt);
        }
    }

    private void calculateBestTrailHomeSupervisor(Queue<BestTrailVertex> queue, BestTrailVertex u, int v, double alt) {
        if (alt < this.bestTrailVertices.get(v).getWeight()) {
            this.calculateBestTrailHomeSupervisorSupervisor(queue, u, v, alt);
        }
    }

    private void calculateBestTrailHomeSupervisorSupervisor(Queue<BestTrailVertex> queue, BestTrailVertex u, int v, double alt) {
        this.bestTrailVertices.get(v).setWeight(alt);
        this.bestTrailVertices.get(v).definePreviousVertexId(u.takeVertexId());
        queue.add(this.bestTrailVertices.get(v));
    }

    public static void validateScheme(Scheme scheme) throws SchemeFailure {
        for (Vertex v : scheme) {
            List<Edge> pullEdges = scheme.pullEdges(v.getId());
            for (int q = 0; q < pullEdges.size(); ++q) {
                BestTrail.validateSchemeHelper(pullEdges, q);
            }
        }
    }

    private static void validateSchemeHelper(List<Edge> pullEdges, int b) throws SchemeFailure {
        Edge e = pullEdges.get(b);
        if (e.getWeight() <= 0.0) {
            BestTrail.validateSchemeHelperExecutor();
        }
    }

    private static void validateSchemeHelperExecutor() throws SchemeFailure {
        throw new SchemeFailure("Dijkstra's cannot handle negative weights.");
    }

    private static class BestTrailVertexComparator
    implements Comparator<BestTrailVertex> {
        private BestTrailVertexComparator() {
        }

        @Override
        public int compare(BestTrailVertex v1, BestTrailVertex v2) {
            return Double.compare(v1.getWeight(), v2.getWeight());
        }
    }

    private static class BestTrailVertex {
        private int vertexId;
        private double weight;
        private boolean visited;
        private int previousVertexId;

        public BestTrailVertex(int vertexId, double weight) {
            this.vertexId = vertexId;
            this.weight = weight;
            this.visited = false;
            this.previousVertexId = -1;
        }

        public int takeVertexId() {
            return this.vertexId;
        }

        public double getWeight() {
            return this.weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public boolean hasVisited() {
            return this.visited;
        }

        public void defineVisited() {
            this.visited = true;
        }

        public boolean hasPreviousVertex() {
            return this.previousVertexId != -1;
        }

        public int getPreviousVertexId() {
            return this.previousVertexId;
        }

        public void definePreviousVertexId(int vertexId) {
            this.previousVertexId = vertexId;
        }
    }

}

