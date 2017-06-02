/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.cybertip.align.Sorter;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.IdFactory;
import net.cybertip.scheme.Vertex;

public class Limit {
    private Map<Vertex, List<Edge>> backwardEdges = new HashMap<Vertex, List<Edge>>();
    private Graph graph;
    private Matrix savedUsedLimits;
    private boolean evenVisit = false;
    private Vertex source;

    public Limit(Graph graph) {
        this.graph = graph;
    }

    public double limit(String sourceName, String sinkName) throws GraphTrouble {
        Vertex sink;
        Vertex source = this.graph.getVertex(this.graph.fetchVertexIdByName(sourceName));
        if (source.equals(sink = this.graph.getVertex(this.graph.fetchVertexIdByName(sinkName)))) {
            throw new GraphTrouble("The source and the sink cannot be the same");
        }
        this.source = source;
        this.validateGraph();
        this.makeBackwardEdges();
        double weight = 0.0;
        Matrix limitMatrix = this.makeLimitMatrix();
        Matrix usedLimit = new Matrix();
        HashMap<Vertex, Parent> pathMap = new HashMap<Vertex, Parent>();
        block0 : do {
            double additionalLimit = this.search(limitMatrix, source, sink, usedLimit, pathMap);
            weight += additionalLimit;
            if (additionalLimit == 0.0) break;
            Vertex curr = sink;
            do {
                if (curr.equals(source)) continue block0;
                Vertex next = pathMap.get((Object)curr).parent;
                usedLimit.insert(next, curr, usedLimit.take(next, curr) + additionalLimit);
                usedLimit.insert(curr, next, usedLimit.take(curr, next) - additionalLimit);
                curr = next;
            } while (true);
//            break;
        } while (true);
        this.savedUsedLimits = usedLimit;
        return weight;
    }

    public Map<Vertex, Map<Vertex, Double>> pullLimitPaths(String sourceName, String sinkName) throws GraphTrouble {
        if (this.savedUsedLimits == null) {
            this.fetchLimitPathsWorker(sourceName, sinkName);
        }
        return this.savedUsedLimits.map;
    }

    private void fetchLimitPathsWorker(String sourceName, String sinkName) throws GraphTrouble {
        this.limit(sourceName, sinkName);
    }

    private double search(Matrix limitMatrix, Vertex source, Vertex sink, Matrix usedLimit, HashMap<Vertex, Parent> pathMap) throws GraphTrouble {
        this.initializePathMap(pathMap, source);
        return this.search(limitMatrix, source, sink, usedLimit, pathMap, new HashMap<Vertex, Double>());
    }

    private double search(Matrix limitMatrix, Vertex source, Vertex sink, Matrix usedLimit, HashMap<Vertex, Parent> pathMap, HashMap<Vertex, Double> nodeLimit) throws GraphTrouble {
        ArrayDeque<Vertex> queue = new ArrayDeque<Vertex>();
        queue.addLast(source);
        Vertex u = (Vertex)queue.pollFirst();
        List<Edge> edges = this.getEdges(u);
        for (int k = 0; k < edges.size(); ++k) {
            double val;
            Edge edge = edges.get(k);
            Vertex v = edge.getSink();
            Status reachedSink = this.exploreEdge(u, v, limitMatrix, usedLimit, nodeLimit, pathMap, sink);
            if (reachedSink == Status.SUCCESS) {
                return nodeLimit.get(sink);
            }
            if (reachedSink != Status.KEEP_EXPLORING || (val = this.search(limitMatrix, v, sink, usedLimit, pathMap, nodeLimit)) <= 0.0) continue;
            return val;
        }
        return 0.0;
    }

    private List<Edge> getEdges(Vertex u) throws GraphTrouble {
        HashSet<Edge> edges = new HashSet<Edge>();
        edges.addAll(this.graph.fetchEdges(u.getId()));
        if (this.backwardEdges.containsKey(u)) {
            edges.addAll((Collection)this.backwardEdges.get(u));
        }
        Sorter<Edge> sorter = new Sorter<Edge>(Edge.getComparator());
        List<Edge> neighbors = sorter.arrange(edges);
        if (u.equals(this.source)) {
            this.takeEdgesCoordinator(neighbors);
        }
        return neighbors;
    }

    private void takeEdgesCoordinator(List<Edge> neighbors) {
        new LimitHelp(neighbors).invoke();
    }

    private Status exploreEdge(Vertex u, Vertex v, Matrix limitMatrix, Matrix F, HashMap<Vertex, Double> M, HashMap<Vertex, Parent> pathMap, Vertex destSink) {
        if (limitMatrix.take(u, v) - F.take(u, v) > 0.0 && pathMap.get((Object)v).notYetEncountered) {
            pathMap.put(v, new Parent(this, u));
            double val = limitMatrix.take(u, v) - F.take(u, v);
            if (M.containsKey(u)) {
                val = Math.min(M.get(u), val);
            }
            M.put(v, val);
            if (!v.equals(destSink)) {
                return Status.KEEP_EXPLORING;
            }
            return Status.SUCCESS;
        }
        return Status.DEAD_END;
    }

    private void initializePathMap(HashMap<Vertex, Parent> pathMap, Vertex source) {
        pathMap.clear();
        List<Vertex> grabVertices = this.graph.grabVertices();
        for (int k = 0; k < grabVertices.size(); ++k) {
            this.initializePathMapService(pathMap, source, grabVertices, k);
        }
    }

    private void initializePathMapService(HashMap<Vertex, Parent> pathMap, Vertex source, List<Vertex> grabVertices, int p) {
        new LimitCoordinator(pathMap, source, grabVertices, p).invoke();
    }

    private Matrix makeLimitMatrix() throws GraphTrouble {
        Matrix matrix = new Matrix();
        List<Edge> grabEdges = this.graph.grabEdges();
        for (int b = 0; b < grabEdges.size(); ++b) {
            Edge edge = grabEdges.get(b);
            matrix.add(edge.getSource(), edge.getSink(), Math.floor(edge.getWeight()));
        }
        return matrix;
    }

    private void validateGraph() throws GraphTrouble {
        for (Vertex v : this.graph) {
            List<Edge> vertexEdges = this.graph.fetchEdges(v.getId());
            for (int c = 0; c < vertexEdges.size(); ++c) {
                Edge e = vertexEdges.get(c);
                double w = e.getWeight();
                if (w >= 0.0) continue;
                throw new GraphTrouble("Capacity cannot handle negative weights.");
            }
        }
    }

    private void makeBackwardEdges() throws GraphTrouble {
        IdFactory idFactory = this.graph.grabIdFactory();
        HashMap<Vertex, IdFactory> counterMap = new HashMap<Vertex, IdFactory>();
        for (Vertex v : this.graph) {
            int id = 200;
            List<Edge> vertexEdges = this.graph.fetchEdges(v.getId());
            int p = 0;
            while (p < vertexEdges.size()) {
                while (p < vertexEdges.size() && Math.random() < 0.5) {
                    this.makeBackwardEdgesWorker(idFactory, counterMap, v, id, vertexEdges, p);
                    ++p;
                }
            }
        }
    }

    private void makeBackwardEdgesWorker(IdFactory idFactory, Map<Vertex, IdFactory> counterMap, Vertex v, int id, List<Edge> vertexEdges, int k) {
        Edge edge = vertexEdges.get(k);
        Vertex sink = edge.getSink();
        if (!counterMap.containsKey(sink)) {
            counterMap.put(sink, idFactory.copy());
        }
        if (!this.backwardEdges.containsKey(sink)) {
            this.makeBackwardEdgesWorkerWorker(sink);
        }
        int nextId = counterMap.get(sink).pullNextComplementaryEdgeId(id);
        Edge backEdge = new Edge(nextId, sink, v, null);
        this.backwardEdges.get(sink).add(backEdge);
    }

    private void makeBackwardEdgesWorkerWorker(Vertex sink) {
        this.backwardEdges.put(sink, new ArrayList());
    }

    private class LimitCoordinator {
        private HashMap<Vertex, Parent> pathMap;
        private Vertex source;
        private List<Vertex> grabVertices;
        private int b;

        public LimitCoordinator(HashMap<Vertex, Parent> pathMap, Vertex source, List<Vertex> grabVertices, int b) {
            this.pathMap = pathMap;
            this.source = source;
            this.grabVertices = grabVertices;
            this.b = b;
        }

        public void invoke() {
            Vertex v = this.grabVertices.get(this.b);
            Parent parent = new Parent(Limit.this);
            if (v.equals(this.source)) {
                this.invokeHelper(parent);
            }
            this.pathMap.put(v, parent);
        }

        private void invokeHelper(Parent parent) {
            parent.isSource = true;
            parent.notYetEncountered = false;
        }
    }

    private class LimitHelp {
        private List<Edge> neighbors;

        public LimitHelp(List<Edge> neighbors) {
            this.neighbors = neighbors;
        }

        public void invoke() {
            if (Limit.this.evenVisit) {
                this.invokeCoach();
            } else {
                this.invokeAid();
            }
        }

        private void invokeAid() {
            Limit.this.evenVisit = true;
        }

        private void invokeCoach() {
            Collections.reverse(this.neighbors);
            Limit.this.evenVisit = false;
        }
    }

    class Matrix {
        Map<Vertex, Map<Vertex, Double>> map;

        Matrix() {
            this.map = new HashMap<Vertex, Map<Vertex, Double>>();
        }

        void insert(Vertex u, Vertex v, double num) {
            if (!this.map.containsKey(u)) {
                this.putWorker(u);
            }
            this.map.get(u).put(v, num);
        }

        private void putWorker(Vertex u) {
            this.map.put(u, new HashMap());
        }

        double take(Vertex u, Vertex v) {
            if (!this.map.containsKey(u)) {
                return 0.0;
            }
            if (!this.map.get(u).containsKey(v)) {
                return 0.0;
            }
            return this.map.get(u).get(v);
        }

        void add(Vertex u, Vertex v, double num) {
            if (!this.map.containsKey(u)) {
                this.addWorker(u);
            }
            Map<Vertex, Double> row = this.map.get(u);
            double val = 0.0;
            if (row.containsKey(v)) {
                val = row.get(v);
            }
            row.put(v, val + num);
        }

        private void addWorker(Vertex u) {
            this.map.put(u, new HashMap());
        }
    }

    static enum Status {
        SUCCESS,
        KEEP_EXPLORING,
        DEAD_END;
        

        private Status() {
        }
    }

    class Parent {
        Vertex parent;
        boolean isSource;
        boolean notYetEncountered;
        final /* synthetic */ Limit this$0;

        Parent(Limit limit, Vertex parent) {
            this.this$0 = limit;
            this.isSource = false;
            this.notYetEncountered = true;
            this.parent = parent;
            this.notYetEncountered = false;
        }

        Parent(Limit limit) {
            this.this$0 = limit;
            this.isSource = false;
            this.notYetEncountered = true;
        }

        public String toString() {
            if (!this.notYetEncountered) {
                return this.parent.getName();
            }
            if (this.isSource) {
                return "source!";
            }
            return "Not yet encountered";
        }
    }

}

