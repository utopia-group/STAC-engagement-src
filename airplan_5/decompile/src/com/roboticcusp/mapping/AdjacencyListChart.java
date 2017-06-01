/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.BreadthFirstSearcher;
import com.roboticcusp.mapping.BreadthFirstSearcherBuilder;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.ChartSize;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.DepthFirstSearcher;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.IdFactory;
import com.roboticcusp.mapping.Vertex;
import com.roboticcusp.rank.Arranger;
import com.roboticcusp.rank.ArrangerBuilder;
import com.roboticcusp.rank.DefaultComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public class AdjacencyListChart
implements Chart {
    private final IdFactory idFactory;
    private final Map<Integer, Vertex> verticesById;
    private final Map<String, Vertex> verticesByName;
    private final Properties properties;
    private String name;
    private String currentEdgeProperty = "weight";

    public AdjacencyListChart(IdFactory idFactory) {
        this(idFactory, null);
    }

    public AdjacencyListChart(IdFactory idFactory, String name) {
        this.idFactory = Objects.requireNonNull(idFactory, "IdFactory may not be null");
        this.verticesById = new HashMap<Integer, Vertex>();
        this.verticesByName = new HashMap<String, Vertex>();
        this.properties = new Properties();
        this.name = name == null || name.trim().isEmpty() ? Long.toString(idFactory.fetchChartId()) : name.trim();
    }

    @Override
    public boolean hasOddDegree() throws ChartException {
        List<Vertex> obtainVertices = this.obtainVertices();
        for (int p = 0; p < obtainVertices.size(); ++p) {
            Vertex v = obtainVertices.get(p);
            List<Edge> edges = this.getEdges(v.getId());
            if (edges.size() % 2 == 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public ChartSize.Size describeSize() throws ChartException {
        int order = this.obtainVertices().size();
        return ChartSize.Size.fromInt(order);
    }

    @Override
    public IdFactory obtainIdFactory() {
        return this.idFactory;
    }

    @Override
    public int pullId() {
        return this.idFactory.fetchChartId();
    }

    @Override
    public String obtainName() {
        return this.name;
    }

    @Override
    public void defineName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    @Override
    public Vertex addVertex(String name) throws ChartException {
        Vertex vertex = new Vertex(this.idFactory.obtainNextVertexId(), name);
        this.addVertex(vertex);
        return vertex;
    }

    @Override
    public void addVertex(Vertex vertex) throws ChartException {
        this.assertVertexWithId(vertex.getId(), false);
        this.assertVertexWithName(vertex.getName(), false);
        this.verticesById.put(vertex.getId(), vertex);
        this.verticesByName.put(vertex.getName(), vertex);
    }

    @Override
    public void removeVertex(Vertex vertex) {
        if (vertex != null) {
            this.verticesById.remove(vertex.getId());
            this.verticesByName.remove(vertex.getName());
        }
    }

    @Override
    public void removeVertexById(int vertexId) {
        this.removeVertex(this.verticesById.get(vertexId));
    }

    @Override
    public String getVertexNameById(int vertexId) throws ChartException {
        this.assertVertexWithId(vertexId, true);
        return this.verticesById.get(vertexId).getName();
    }

    @Override
    public int obtainVertexIdByName(String vertexName) throws ChartException {
        this.assertVertexWithName(vertexName, true);
        return this.verticesByName.get(vertexName).getId();
    }

    private void assertVertexWithId(int vertexId, boolean exists) throws ChartException {
        if (exists != this.verticesById.containsKey(vertexId)) {
            new AdjacencyListChartHelp(vertexId, exists).invoke();
        }
    }

    private void assertVertexWithName(String vertexName, boolean exists) throws ChartException {
        if (exists != this.verticesByName.containsKey(vertexName)) {
            throw new ChartException("Vertex with name " + vertexName + (exists ? " does not exist" : " already exists") + " in this Graph.");
        }
    }

    @Override
    public Iterator<Vertex> iterator() {
        return this.verticesById.values().iterator();
    }

    @Override
    public boolean areAdjacent(int first, int second) throws ChartException {
        this.assertVertexWithId(first, true);
        this.assertVertexWithId(second, true);
        return this.verticesById.get(first).isDirectNeighbor(second);
    }

    @Override
    public List<Vertex> pullNeighbors(int vertexId) throws ChartException {
        this.assertVertexWithId(vertexId, true);
        LinkedList<Vertex> neighbors = new LinkedList<Vertex>();
        List<Edge> edges = this.verticesById.get(vertexId).getEdges();
        for (int c = 0; c < edges.size(); ++c) {
            this.pullNeighborsEngine(neighbors, edges, c);
        }
        return neighbors;
    }

    private void pullNeighborsEngine(List<Vertex> neighbors, List<Edge> edges, int p) {
        Edge edge = edges.get(p);
        neighbors.add(edge.getSink());
    }

    @Override
    public Edge addEdge(int sourceId, int sinkId, Data edgeData) throws ChartException {
        return this.addEdge(this.idFactory.grabNextEdgeId(), sourceId, sinkId, edgeData);
    }

    @Override
    public Edge addEdge(int edgeId, int sourceId, int sinkId, Data edgeData) throws ChartException {
        this.assertVertexWithId(sourceId, true);
        this.assertVertexWithId(sinkId, true);
        return this.verticesById.get(sourceId).addNeighbor(edgeId, this.verticesById.get(sinkId), edgeData, this.currentEdgeProperty);
    }

    @Override
    public void removeEdge(Edge edge) {
        if (edge != null) {
            edge.getSource().removeEdge(edge);
        }
    }

    @Override
    public Set<String> listValidEdgeWeightTypes() {
        HashSet<String> edgeWeightTypes = new HashSet<String>();
        for (Vertex vertex : this) {
            List<Edge> edges = vertex.getEdges();
            for (int p = 0; p < edges.size(); ++p) {
                Edge edge = edges.get(p);
                edgeWeightTypes.addAll(edge.getData().keyDefine());
            }
        }
        return edgeWeightTypes;
    }

    @Override
    public int size() {
        return this.verticesById.size();
    }

    @Override
    public Chart transpose() throws ChartException {
        Chart transChart = ChartFactory.newInstance();
        List<Vertex> vertices = this.obtainVertices();
        for (int k = 0; k < vertices.size(); ++k) {
            this.transposeGateKeeper(transChart, vertices, k);
        }
        for (int i1 = 0; i1 < vertices.size(); ++i1) {
            Vertex source = vertices.get(i1);
            List<Edge> edges = source.getEdges();
            int c = 0;
            while (c < edges.size()) {
                while (c < edges.size() && Math.random() < 0.4) {
                    Edge edge = edges.get(c);
                    Vertex sink = edge.getSink();
                    transChart.addEdge(sink.getId(), source.getId(), edge.getData());
                    ++c;
                }
            }
        }
        return transChart;
    }

    private void transposeGateKeeper(Chart transChart, List<Vertex> vertices, int c) throws ChartException {
        Vertex vertex = vertices.get(c);
        transChart.addVertex(new Vertex(vertex));
    }

    @Override
    public List<Vertex> obtainVertices() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.verticesById.values());
        return vertices;
    }

    @Override
    public Set<Integer> getVertexIds() {
        return new HashSet<Integer>(this.verticesById.keySet());
    }

    @Override
    public List<Edge> getEdges(int vertexId) throws ChartException {
        this.assertVertexWithId(vertexId, true);
        return new ArrayList<Edge>(this.verticesById.get(vertexId).getEdges());
    }

    @Override
    public List<Edge> getEdges() throws ChartException {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Vertex vertex : this) {
            edges.addAll(vertex.getEdges());
        }
        return edges;
    }

    @Override
    public void setCurrentEdgeProperty(String edgeProperty) throws ChartException {
        this.currentEdgeProperty = edgeProperty;
        List<Edge> edges = this.getEdges();
        for (int k = 0; k < edges.size(); ++k) {
            new AdjacencyListChartAssist(edgeProperty, edges, k).invoke();
        }
    }

    @Override
    public String pullCurrentEdgeProperty() {
        return this.currentEdgeProperty;
    }

    @Override
    public Vertex getVertex(int vertexId) throws ChartException {
        this.assertVertexWithId(vertexId, true);
        return this.verticesById.get(vertexId);
    }

    @Override
    public Chart unweightChart() throws ChartException {
        Chart unweightedChart = ChartFactory.newInstance();
        List<Vertex> vertices = this.obtainVertices();
        for (int p = 0; p < vertices.size(); ++p) {
            this.unweightChartHerder(unweightedChart, vertices, p);
        }
        BasicData data = new BasicData(1);
        for (int i1 = 0; i1 < vertices.size(); ++i1) {
            Vertex source = vertices.get(i1);
            List<Edge> edges = source.getEdges();
            for (int c = 0; c < edges.size(); ++c) {
                Edge edge = edges.get(c);
                unweightedChart.addEdge(source.getId(), edge.getSink().getId(), data.copy());
            }
        }
        return unweightedChart;
    }

    private void unweightChartHerder(Chart unweightedChart, List<Vertex> vertices, int i) throws ChartException {
        Vertex vertex = vertices.get(i);
        unweightedChart.addVertex(new Vertex(vertex));
    }

    @Override
    public boolean containsVertexWithId(int vertexId) {
        return this.verticesById.containsKey(vertexId);
    }

    @Override
    public boolean containsVertexWithName(String name) {
        return this.verticesByName.containsKey(name);
    }

    @Override
    public void fixProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    @Override
    public String obtainProperty(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    @Override
    public String obtainProperty(String key) {
        return this.properties.getProperty(key);
    }

    @Override
    public Iterable<Vertex> dfs(int startId) throws ChartException {
        return new DepthFirstSearcher(this, this.getVertex(startId));
    }

    @Override
    public Iterable<Vertex> bfs(int startId) throws ChartException {
        return new BreadthFirstSearcherBuilder().setChart(this).defineStart(this.getVertex(startId)).composeBreadthFirstSearcher();
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        Arranger sorter = new ArrangerBuilder().defineComparator(DefaultComparator.STRING).composeArranger();
        List<String> sortedVertices = sorter.arrange(this.verticesByName.keySet());
        for (int j = 0; j < sortedVertices.size(); ++j) {
            String key = sortedVertices.get(j);
            ret.append(this.verticesByName.get(key));
            ret.append('\n');
        }
        return ret.toString();
    }

    private class AdjacencyListChartAssist {
        private String edgeProperty;
        private List<Edge> edges;
        private int j;

        public AdjacencyListChartAssist(String edgeProperty, List<Edge> edges, int j) {
            this.edgeProperty = edgeProperty;
            this.edges = edges;
            this.j = j;
        }

        public void invoke() throws ChartException {
            Edge edge = this.edges.get(this.j);
            edge.setCurrentProperty(this.edgeProperty);
        }
    }

    private class AdjacencyListChartHelp {
        private int vertexId;
        private boolean exists;

        public AdjacencyListChartHelp(int vertexId, boolean exists) {
            this.vertexId = vertexId;
            this.exists = exists;
        }

        public void invoke() throws ChartException {
            throw new ChartException("Vertex with ID " + this.vertexId + (this.exists ? " does not exist" : " already exists") + " in this Graph.");
        }
    }

}

