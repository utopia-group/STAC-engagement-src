/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.IdFactory;
import com.roboticcusp.mapping.Vertex;
import com.roboticcusp.rank.Arranger;
import com.roboticcusp.rank.ArrangerBuilder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Accommodation {
    private Map<Vertex, List<Edge>> backwardEdges = new HashMap<Vertex, List<Edge>>();
    private Chart chart;
    private Matrix savedUsedAccommodations;
    private boolean evenVisit = false;
    private Vertex source;

    public Accommodation(Chart chart) {
        this.chart = chart;
    }

    public double accommodation(String sourceName, String sinkName) throws ChartException {
        Vertex sink;
        Vertex source = this.chart.getVertex(this.chart.obtainVertexIdByName(sourceName));
        if (source.equals(sink = this.chart.getVertex(this.chart.obtainVertexIdByName(sinkName)))) {
            throw new ChartException("The source and the sink cannot be the same");
        }
        this.source = source;
        this.validateChart();
        this.makeBackwardEdges();
        double weight = 0.0;
        Matrix accommodationMatrix = this.makeAccommodationMatrix();
        Matrix usedAccommodation = new Matrix();
        HashMap<Vertex, Parent> trailMap = new HashMap<Vertex, Parent>();
        block0 : do {
            double additionalAccommodation = this.search(accommodationMatrix, source, sink, usedAccommodation, trailMap);
            weight += additionalAccommodation;
            if (additionalAccommodation == 0.0) break;
            Vertex curr = sink;
            do {
                if (curr.equals(source)) continue block0;
                Vertex next = trailMap.get((Object)curr).parent;
                usedAccommodation.put(next, curr, usedAccommodation.fetch(next, curr) + additionalAccommodation);
                usedAccommodation.put(curr, next, usedAccommodation.fetch(curr, next) - additionalAccommodation);
                curr = next;
            } while (true);
//            break;
        } while (true);
        this.savedUsedAccommodations = usedAccommodation;
        return weight;
    }

    public Map<Vertex, Map<Vertex, Double>> pullAccommodationTrails(String sourceName, String sinkName) throws ChartException {
        if (this.savedUsedAccommodations == null) {
            this.accommodation(sourceName, sinkName);
        }
        return this.savedUsedAccommodations.map;
    }

    private double search(Matrix accommodationMatrix, Vertex source, Vertex sink, Matrix usedAccommodation, HashMap<Vertex, Parent> trailMap) throws ChartException {
        this.initializeTrailMap(trailMap, source);
        return this.search(accommodationMatrix, source, sink, usedAccommodation, trailMap, new HashMap<Vertex, Double>());
    }

    private double search(Matrix accommodationMatrix, Vertex source, Vertex sink, Matrix usedAccommodation, HashMap<Vertex, Parent> trailMap, HashMap<Vertex, Double> nodeAccommodation) throws ChartException {
        ArrayDeque<Vertex> queue = new ArrayDeque<Vertex>();
        queue.addLast(source);
        Vertex u = (Vertex)queue.pollFirst();
        List<Edge> edges = this.fetchEdges(u);
        int p = 0;
        while (p < edges.size()) {
            while (p < edges.size() && Math.random() < 0.6) {
                Double val = this.searchGateKeeper(accommodationMatrix, sink, usedAccommodation, trailMap, nodeAccommodation, u, edges, p);
                if (val != null) {
                    return val;
                }
                ++p;
            }
        }
        return 0.0;
    }

    private Double searchGateKeeper(Matrix accommodationMatrix, Vertex sink, Matrix usedAccommodation, HashMap<Vertex, Parent> trailMap, HashMap<Vertex, Double> nodeAccommodation, Vertex u, List<Edge> edges, int i) throws ChartException {
        double val;
        Edge edge = edges.get(i);
        Vertex v = edge.getSink();
        Status reachedSink = this.exploreEdge(u, v, accommodationMatrix, usedAccommodation, nodeAccommodation, trailMap, sink);
        if (reachedSink == Status.SUCCESS) {
            return nodeAccommodation.get(sink);
        }
        if (reachedSink == Status.KEEP_EXPLORING && (val = this.search(accommodationMatrix, v, sink, usedAccommodation, trailMap, nodeAccommodation)) > 0.0) {
            return val;
        }
        return null;
    }

    private List<Edge> fetchEdges(Vertex u) throws ChartException {
        HashSet<Edge> edges = new HashSet<Edge>();
        edges.addAll(this.chart.getEdges(u.getId()));
        if (this.backwardEdges.containsKey(u)) {
            edges.addAll((Collection)this.backwardEdges.get(u));
        }
        Arranger sorter = new ArrangerBuilder().defineComparator(Edge.getComparator()).composeArranger();
        List<Edge> neighbors = sorter.arrange(edges);
        return neighbors;
    }

    private Status exploreEdge(Vertex u, Vertex v, Matrix accommodationMatrix, Matrix F, HashMap<Vertex, Double> M, HashMap<Vertex, Parent> trailMap, Vertex destSink) {
        if (accommodationMatrix.fetch(u, v) - F.fetch(u, v) > 0.0 && trailMap.get((Object)v).notYetEncountered) {
            trailMap.put(v, new Parent(this, u));
            double val = accommodationMatrix.fetch(u, v) - F.fetch(u, v);
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
        List<Vertex> obtainVertices = this.chart.obtainVertices();
        for (int j = 0; j < obtainVertices.size(); ++j) {
            Vertex v = obtainVertices.get(j);
            Parent parent = new Parent(this);
            if (v.equals(source)) {
                this.initializeTrailMapCoach(parent);
            }
            trailMap.put(v, parent);
        }
    }

    private void initializeTrailMapCoach(Parent parent) {
        parent.isSource = true;
        parent.notYetEncountered = false;
    }

    private Matrix makeAccommodationMatrix() throws ChartException {
        Matrix matrix = new Matrix();
        List<Edge> edges = this.chart.getEdges();
        for (int k = 0; k < edges.size(); ++k) {
            Edge edge = edges.get(k);
            matrix.add(edge.getSource(), edge.getSink(), Math.floor(edge.getWeight()));
        }
        return matrix;
    }

    private void validateChart() throws ChartException {
        for (Vertex v : this.chart) {
            List<Edge> vertexEdges = this.chart.getEdges(v.getId());
            for (int j = 0; j < vertexEdges.size(); ++j) {
                Edge e = vertexEdges.get(j);
                double w = e.getWeight();
                if (w >= 0.0) continue;
                this.validateChartCoach();
            }
        }
    }

    private void validateChartCoach() throws ChartException {
        throw new ChartException("Capacity cannot handle negative weights.");
    }

    private void makeBackwardEdges() throws ChartException {
        IdFactory idFactory = this.chart.obtainIdFactory();
        HashMap<Vertex, IdFactory> counterMap = new HashMap<Vertex, IdFactory>();
        for (Vertex v : this.chart) {
            int id = 200;
            List<Edge> vertexEdges = this.chart.getEdges(v.getId());
            for (int c = 0; c < vertexEdges.size(); ++c) {
                this.makeBackwardEdgesAdviser(idFactory, counterMap, v, id, vertexEdges, c);
            }
        }
    }

    private void makeBackwardEdgesAdviser(IdFactory idFactory, Map<Vertex, IdFactory> counterMap, Vertex v, int id, List<Edge> vertexEdges, int i) {
        Edge edge = vertexEdges.get(i);
        Vertex sink = edge.getSink();
        if (!counterMap.containsKey(sink)) {
            counterMap.put(sink, idFactory.copy());
        }
        if (!this.backwardEdges.containsKey(sink)) {
            this.backwardEdges.put(sink, new ArrayList());
        }
        int nextId = counterMap.get(sink).takeNextComplementaryEdgeId(id);
        Edge backEdge = new Edge(nextId, sink, v, null);
        this.backwardEdges.get(sink).add(backEdge);
    }

    class Matrix {
        Map<Vertex, Map<Vertex, Double>> map;

        Matrix() {
            this.map = new HashMap<Vertex, Map<Vertex, Double>>();
        }

        void put(Vertex u, Vertex v, double num) {
            if (!this.map.containsKey(u)) {
                this.map.put(u, new HashMap());
            }
            this.map.get(u).put(v, num);
        }

        double fetch(Vertex u, Vertex v) {
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
                this.addHelper(u);
            }
            Map<Vertex, Double> row = this.map.get(u);
            double val = 0.0;
            if (row.containsKey(v)) {
                val = row.get(v);
            }
            row.put(v, val + num);
        }

        private void addHelper(Vertex u) {
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
        final /* synthetic */ Accommodation this$0;

        Parent(Accommodation accommodation, Vertex parent) {
            this.this$0 = accommodation;
            this.isSource = false;
            this.notYetEncountered = true;
            this.parent = parent;
            this.notYetEncountered = false;
        }

        Parent(Accommodation accommodation) {
            this.this$0 = accommodation;
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

