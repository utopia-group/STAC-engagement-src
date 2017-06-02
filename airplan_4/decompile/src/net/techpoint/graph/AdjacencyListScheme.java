/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

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
import net.techpoint.graph.BasicData;
import net.techpoint.graph.BreadthFirstSearcher;
import net.techpoint.graph.Data;
import net.techpoint.graph.DepthFirstSearcher;
import net.techpoint.graph.DepthFirstSearcherBuilder;
import net.techpoint.graph.Edge;
import net.techpoint.graph.IdFactory;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;
import net.techpoint.order.DefaultComparator;
import net.techpoint.order.Ranker;

public class AdjacencyListScheme
implements Scheme {
    private final IdFactory idFactory;
    private final Map<Integer, Vertex> verticesById;
    private final Map<String, Vertex> verticesByName;
    private final Properties properties;
    private String name;
    private String currentEdgeProperty = "weight";

    public AdjacencyListScheme(IdFactory idFactory) {
        this(idFactory, null);
    }

    public AdjacencyListScheme(IdFactory idFactory, String name) {
        this.idFactory = Objects.requireNonNull(idFactory, "IdFactory may not be null");
        this.verticesById = new HashMap<Integer, Vertex>();
        this.verticesByName = new HashMap<String, Vertex>();
        this.properties = new Properties();
        if (name == null || name.trim().isEmpty()) {
            this.AdjacencyListSchemeGuide(idFactory);
        } else {
            this.AdjacencyListSchemeEngine(name);
        }
    }

    private void AdjacencyListSchemeEngine(String name) {
        this.name = name.trim();
    }

    private void AdjacencyListSchemeGuide(IdFactory idFactory) {
        this.name = Long.toString(idFactory.getSchemeId());
    }

    @Override
    public boolean hasOddDegree() throws SchemeFailure {
        List<Vertex> obtainVertices = this.obtainVertices();
        int c = 0;
        while (c < obtainVertices.size()) {
            while (c < obtainVertices.size() && Math.random() < 0.6) {
                while (c < obtainVertices.size() && Math.random() < 0.4) {
                    Vertex v = obtainVertices.get(c);
                    List<Edge> edges = this.pullEdges(v.getId());
                    if (edges.size() % 2 != 0) {
                        return false;
                    }
                    ++c;
                }
            }
        }
        return true;
    }

    @Override
    public IdFactory pullIdFactory() {
        return this.idFactory;
    }

    @Override
    public int takeId() {
        return this.idFactory.getSchemeId();
    }

    @Override
    public String takeName() {
        return this.name;
    }

    @Override
    public void defineName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    @Override
    public Vertex addVertex(String name) throws SchemeFailure {
        Vertex vertex = new Vertex(this.idFactory.pullNextVertexId(), name);
        this.addVertex(vertex);
        return vertex;
    }

    @Override
    public void addVertex(Vertex vertex) throws SchemeFailure {
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
    public String grabVertexNameById(int vertexId) throws SchemeFailure {
        this.assertVertexWithId(vertexId, true);
        return this.verticesById.get(vertexId).getName();
    }

    @Override
    public int getVertexIdByName(String vertexName) throws SchemeFailure {
        this.assertVertexWithName(vertexName, true);
        return this.verticesByName.get(vertexName).getId();
    }

    private void assertVertexWithId(int vertexId, boolean exists) throws SchemeFailure {
        if (exists != this.verticesById.containsKey(vertexId)) {
            throw new SchemeFailure("Vertex with ID " + vertexId + (exists ? " does not exist" : " already exists") + " in this Graph.");
        }
    }

    private void assertVertexWithName(String vertexName, boolean exists) throws SchemeFailure {
        if (exists != this.verticesByName.containsKey(vertexName)) {
            this.assertVertexWithNameEngine(vertexName, exists);
        }
    }

    private void assertVertexWithNameEngine(String vertexName, boolean exists) throws SchemeFailure {
        new AdjacencyListSchemeGuide(vertexName, exists).invoke();
    }

    @Override
    public Iterator<Vertex> iterator() {
        return this.verticesById.values().iterator();
    }

    @Override
    public boolean areAdjacent(int first, int second) throws SchemeFailure {
        this.assertVertexWithId(first, true);
        this.assertVertexWithId(second, true);
        return this.verticesById.get(first).isDirectNeighbor(second);
    }

    @Override
    public List<Vertex> grabNeighbors(int vertexId) throws SchemeFailure {
        this.assertVertexWithId(vertexId, true);
        LinkedList<Vertex> neighbors = new LinkedList<Vertex>();
        List<Edge> edges = this.verticesById.get(vertexId).getEdges();
        for (int j = 0; j < edges.size(); ++j) {
            this.grabNeighborsSupervisor(neighbors, edges, j);
        }
        return neighbors;
    }

    private void grabNeighborsSupervisor(List<Vertex> neighbors, List<Edge> edges, int j) {
        Edge edge = edges.get(j);
        neighbors.add(edge.getSink());
    }

    @Override
    public Edge addEdge(int sourceId, int sinkId, Data edgeData) throws SchemeFailure {
        return this.addEdge(this.idFactory.takeNextEdgeId(), sourceId, sinkId, edgeData);
    }

    @Override
    public Edge addEdge(int edgeId, int sourceId, int sinkId, Data edgeData) throws SchemeFailure {
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
            for (int q = 0; q < edges.size(); ++q) {
                new AdjacencyListSchemeHerder(edgeWeightTypes, edges, q).invoke();
            }
        }
        return edgeWeightTypes;
    }

    @Override
    public int size() {
        return this.verticesById.size();
    }

    @Override
    public Scheme transpose() throws SchemeFailure {
        Scheme transScheme = SchemeFactory.newInstance();
        List<Vertex> vertices = this.obtainVertices();
        for (int i = 0; i < vertices.size(); ++i) {
            this.transposeWorker(transScheme, vertices, i);
        }
        for (int i1 = 0; i1 < vertices.size(); ++i1) {
            Vertex source = vertices.get(i1);
            List<Edge> edges = source.getEdges();
            for (int k = 0; k < edges.size(); ++k) {
                this.transposeGateKeeper(transScheme, source, edges, k);
            }
        }
        return transScheme;
    }

    private void transposeGateKeeper(Scheme transScheme, Vertex source, List<Edge> edges, int c) throws SchemeFailure {
        Edge edge = edges.get(c);
        Vertex sink = edge.getSink();
        transScheme.addEdge(sink.getId(), source.getId(), edge.getData());
    }

    private void transposeWorker(Scheme transScheme, List<Vertex> vertices, int a) throws SchemeFailure {
        Vertex vertex = vertices.get(a);
        transScheme.addVertex(new Vertex(vertex));
    }

    @Override
    public List<Vertex> obtainVertices() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.verticesById.values());
        return vertices;
    }

    @Override
    public Set<Integer> grabVertexIds() {
        return new HashSet<Integer>(this.verticesById.keySet());
    }

    @Override
    public List<Edge> pullEdges(int vertexId) throws SchemeFailure {
        this.assertVertexWithId(vertexId, true);
        return new ArrayList<Edge>(this.verticesById.get(vertexId).getEdges());
    }

    @Override
    public List<Edge> obtainEdges() throws SchemeFailure {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Vertex vertex : this) {
            edges.addAll(vertex.getEdges());
        }
        return edges;
    }

    @Override
    public void fixCurrentEdgeProperty(String edgeProperty) throws SchemeFailure {
        this.currentEdgeProperty = edgeProperty;
        List<Edge> obtainEdges = this.obtainEdges();
        for (int k = 0; k < obtainEdges.size(); ++k) {
            this.fixCurrentEdgePropertyTarget(edgeProperty, obtainEdges, k);
        }
    }

    private void fixCurrentEdgePropertyTarget(String edgeProperty, List<Edge> obtainEdges, int k) throws SchemeFailure {
        Edge edge = obtainEdges.get(k);
        edge.setCurrentProperty(edgeProperty);
    }

    @Override
    public String getCurrentEdgeProperty() {
        return this.currentEdgeProperty;
    }

    @Override
    public Vertex grabVertex(int vertexId) throws SchemeFailure {
        this.assertVertexWithId(vertexId, true);
        return this.verticesById.get(vertexId);
    }

    @Override
    public Scheme unweightScheme() throws SchemeFailure {
        Scheme unweightedScheme = SchemeFactory.newInstance();
        List<Vertex> vertices = this.obtainVertices();
        for (int a = 0; a < vertices.size(); ++a) {
            this.unweightSchemeHelper(unweightedScheme, vertices, a);
        }
        BasicData data = new BasicData(1);
        for (int i1 = 0; i1 < vertices.size(); ++i1) {
            Vertex source = vertices.get(i1);
            List<Edge> edges = source.getEdges();
            for (int p = 0; p < edges.size(); ++p) {
                this.unweightSchemeCoordinator(unweightedScheme, data, source, edges, p);
            }
        }
        return unweightedScheme;
    }

    private void unweightSchemeCoordinator(Scheme unweightedScheme, Data data, Vertex source, List<Edge> edges, int i) throws SchemeFailure {
        Edge edge = edges.get(i);
        unweightedScheme.addEdge(source.getId(), edge.getSink().getId(), data.copy());
    }

    private void unweightSchemeHelper(Scheme unweightedScheme, List<Vertex> vertices, int j) throws SchemeFailure {
        Vertex vertex = vertices.get(j);
        unweightedScheme.addVertex(new Vertex(vertex));
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
    public void assignProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    @Override
    public String takeProperty(String key) {
        return this.properties.getProperty(key);
    }

    @Override
    public Iterable<Vertex> dfs(int startId) throws SchemeFailure {
        return new DepthFirstSearcherBuilder().assignScheme(this).setStart(this.grabVertex(startId)).formDepthFirstSearcher();
    }

    @Override
    public Iterable<Vertex> bfs(int startId) throws SchemeFailure {
        return new BreadthFirstSearcher(this, this.grabVertex(startId));
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        Ranker<String> sorter = new Ranker<String>(DefaultComparator.STRING);
        List<String> sortedVertices = sorter.align(this.verticesByName.keySet());
        for (int b = 0; b < sortedVertices.size(); ++b) {
            String key = sortedVertices.get(b);
            ret.append(this.verticesByName.get(key));
            ret.append('\n');
        }
        return ret.toString();
    }

    private class AdjacencyListSchemeHerder {
        private Set<String> edgeWeightTypes;
        private List<Edge> edges;
        private int q;

        public AdjacencyListSchemeHerder(Set<String> edgeWeightTypes, List<Edge> edges, int q) {
            this.edgeWeightTypes = edgeWeightTypes;
            this.edges = edges;
            this.q = q;
        }

        public void invoke() {
            Edge edge = this.edges.get(this.q);
            this.edgeWeightTypes.addAll(edge.getData().keyAssign());
        }
    }

    private class AdjacencyListSchemeGuide {
        private String vertexName;
        private boolean exists;

        public AdjacencyListSchemeGuide(String vertexName, boolean exists) {
            this.vertexName = vertexName;
            this.exists = exists;
        }

        public void invoke() throws SchemeFailure {
            throw new SchemeFailure("Vertex with name " + this.vertexName + (this.exists ? " does not exist" : " already exists") + " in this Graph.");
        }
    }

}

