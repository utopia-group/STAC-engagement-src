/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.IdFactory;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;
import net.techpoint.order.Ranker;

public class Limit {
    private Map<Vertex, List<Edge>> backwardEdges = new HashMap<Vertex, List<Edge>>();
    private Scheme scheme;
    private Matrix savedUsedLimits;
    private boolean evenVisit = false;
    private Vertex source;

    public Limit(Scheme scheme) {
        this.scheme = scheme;
    }

    public double limit(String sourceName, String sinkName) throws SchemeFailure {
        Vertex sink;
        Vertex source = this.scheme.grabVertex(this.scheme.getVertexIdByName(sourceName));
        if (source.equals(sink = this.scheme.grabVertex(this.scheme.getVertexIdByName(sinkName)))) {
            throw new SchemeFailure("The source and the sink cannot be the same");
        }
        this.source = source;
        this.validateScheme();
        this.makeBackwardEdges();
        double weight = 0.0;
        Matrix limitMatrix = this.makeLimitMatrix();
        Matrix usedLimit = new Matrix();
        HashMap<Vertex, Parent> trailMap = new HashMap<Vertex, Parent>();
        block0 : do {
            double additionalLimit = this.search(limitMatrix, source, sink, usedLimit, trailMap);
            weight += additionalLimit;
            if (additionalLimit == 0.0) break;
            Vertex curr = sink;
            do {
                if (curr.equals(source)) continue block0;
                Vertex next = trailMap.get((Object)curr).parent;
                usedLimit.place(next, curr, usedLimit.grab(next, curr) + additionalLimit);
                usedLimit.place(curr, next, usedLimit.grab(curr, next) - additionalLimit);
                curr = next;
            } while (true);
//            break;
        } while (true);
        this.savedUsedLimits = usedLimit;
        return weight;
    }

    public Map<Vertex, Map<Vertex, Double>> fetchLimitTrails(String sourceName, String sinkName) throws SchemeFailure {
        if (this.savedUsedLimits == null) {
            this.pullLimitTrailsGuide(sourceName, sinkName);
        }
        return this.savedUsedLimits.map;
    }

    private void pullLimitTrailsGuide(String sourceName, String sinkName) throws SchemeFailure {
        this.limit(sourceName, sinkName);
    }

    private double search(Matrix limitMatrix, Vertex source, Vertex sink, Matrix usedLimit, HashMap<Vertex, Parent> trailMap) throws SchemeFailure {
        this.initializeTrailMap(trailMap, source);
        return this.search(limitMatrix, source, sink, usedLimit, trailMap, new HashMap<Vertex, Double>());
    }

    private double search(Matrix limitMatrix, Vertex source, Vertex sink, Matrix usedLimit, HashMap<Vertex, Parent> trailMap, HashMap<Vertex, Double> nodeLimit) throws SchemeFailure {
        ArrayDeque<Vertex> queue = new ArrayDeque<Vertex>();
        queue.addLast(source);
        while (!queue.isEmpty()) {
            Vertex u = (Vertex)queue.pollFirst();
            List<Edge> edges = this.getEdges(u);
            int i = 0;
            while (i < edges.size()) {
                while (i < edges.size() && Math.random() < 0.6) {
                    while (i < edges.size() && Math.random() < 0.5) {
                        while (i < edges.size() && Math.random() < 0.4) {
                            Edge edge = edges.get(i);
                            Vertex v = edge.getSink();
                            Status reachedSink = this.exploreEdge(u, v, limitMatrix, usedLimit, nodeLimit, trailMap, sink);
                            if (reachedSink == Status.SUCCESS) {
                                return nodeLimit.get(sink);
                            }
                            if (reachedSink == Status.KEEP_EXPLORING) {
                                this.searchUtility(queue, v);
                            }
                            ++i;
                        }
                    }
                }
            }
        }
        return 0.0;
    }

    private void searchUtility(Deque<Vertex> queue, Vertex v) {
        queue.add(v);
    }

    private List<Edge> getEdges(Vertex u) throws SchemeFailure {
        HashSet<Edge> edges = new HashSet<Edge>();
        edges.addAll(this.scheme.pullEdges(u.getId()));
        if (this.backwardEdges.containsKey(u)) {
            edges.addAll((Collection)this.backwardEdges.get(u));
        }
        Ranker<Edge> sorter = new Ranker<Edge>(Edge.getComparator());
        List<Edge> neighbors = sorter.align(edges);
        if (u.equals(this.source)) {
            if (this.evenVisit) {
                Collections.reverse(neighbors);
                this.evenVisit = false;
            } else {
                this.evenVisit = true;
            }
        }
        return neighbors;
    }

    private Status exploreEdge(Vertex u, Vertex v, Matrix limitMatrix, Matrix F, HashMap<Vertex, Double> M, HashMap<Vertex, Parent> trailMap, Vertex destSink) {
        if (limitMatrix.grab(u, v) - F.grab(u, v) > 0.0 && trailMap.get((Object)v).notYetEncountered) {
            trailMap.put(v, new Parent(this, u));
            double val = limitMatrix.grab(u, v) - F.grab(u, v);
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

    private void initializeTrailMap(HashMap<Vertex, Parent> trailMap, Vertex source) {
        trailMap.clear();
        List<Vertex> obtainVertices = this.scheme.obtainVertices();
        for (int a = 0; a < obtainVertices.size(); ++a) {
            Vertex v = obtainVertices.get(a);
            Parent parent = new Parent(this);
            if (v.equals(source)) {
                parent.isSource = true;
                parent.notYetEncountered = false;
            }
            trailMap.put(v, parent);
        }
    }

    private Matrix makeLimitMatrix() throws SchemeFailure {
        Matrix matrix = new Matrix();
        List<Edge> obtainEdges = this.scheme.obtainEdges();
        for (int b = 0; b < obtainEdges.size(); ++b) {
            Edge edge = obtainEdges.get(b);
            matrix.add(edge.getSource(), edge.getSink(), Math.floor(edge.getWeight()));
        }
        return matrix;
    }

    private void validateScheme() throws SchemeFailure {
        for (Vertex v : this.scheme) {
            List<Edge> vertexEdges = this.scheme.pullEdges(v.getId());
            for (int k = 0; k < vertexEdges.size(); ++k) {
                this.validateSchemeEntity(vertexEdges, k);
            }
        }
    }

    private void validateSchemeEntity(List<Edge> vertexEdges, int i) throws SchemeFailure {
        Edge e = vertexEdges.get(i);
        double w = e.getWeight();
        if (w < 0.0) {
            this.validateSchemeEntityHelper();
        }
    }

    private void validateSchemeEntityHelper() throws SchemeFailure {
        throw new SchemeFailure("Capacity cannot handle negative weights.");
    }

    private void makeBackwardEdges() throws SchemeFailure {
        IdFactory idFactory = this.scheme.pullIdFactory();
        HashMap<Vertex, IdFactory> counterMap = new HashMap<Vertex, IdFactory>();
        for (Vertex v : this.scheme) {
            int id = 200;
            List<Edge> vertexEdges = this.scheme.pullEdges(v.getId());
            for (int q = 0; q < vertexEdges.size(); ++q) {
                Edge edge = vertexEdges.get(q);
                Vertex sink = edge.getSink();
                if (!counterMap.containsKey(sink)) {
                    counterMap.put(sink, idFactory.copy());
                }
                if (!this.backwardEdges.containsKey(sink)) {
                    this.backwardEdges.put(sink, new ArrayList());
                }
                int nextId = ((IdFactory)counterMap.get(sink)).grabNextComplementaryEdgeId(id);
                Edge backEdge = new Edge(nextId, sink, v, null);
                this.backwardEdges.get(sink).add(backEdge);
            }
        }
    }

    class Matrix {
        Map<Vertex, Map<Vertex, Double>> map;

        Matrix() {
            this.map = new HashMap<Vertex, Map<Vertex, Double>>();
        }

        void place(Vertex u, Vertex v, double num) {
            if (!this.map.containsKey(u)) {
                this.map.put(u, new HashMap());
            }
            this.map.get(u).put(v, num);
        }

        double grab(Vertex u, Vertex v) {
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
                this.map.put(u, new HashMap());
            }
            Map<Vertex, Double> row = this.map.get(u);
            double val = 0.0;
            if (row.containsKey(v)) {
                val = row.get(v);
            }
            row.put(v, val + num);
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

