/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

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
import java.util.Stack;
import net.cybertip.align.DefaultComparator;
import net.cybertip.align.Sorter;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.BreadthFirstSearcher;
import net.cybertip.scheme.ConnectedAlg;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.DepthFirstSearcher;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.EulerianAlg;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphDensity;
import net.cybertip.scheme.GraphFactory;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.IdFactory;
import net.cybertip.scheme.Vertex;

public class AdjacencyListGraph
implements Graph {
    private final IdFactory idFactory;
    private final Map<Integer, Vertex> verticesById;
    private final Map<String, Vertex> verticesByName;
    private final Properties properties;
    private String name;
    private String currentEdgeProperty = "weight";

    public AdjacencyListGraph(IdFactory idFactory) {
        this(idFactory, null);
    }

    public AdjacencyListGraph(IdFactory idFactory, String name) {
        this.idFactory = Objects.requireNonNull(idFactory, "IdFactory may not be null");
        this.verticesById = new HashMap<Integer, Vertex>();
        this.verticesByName = new HashMap<String, Vertex>();
        this.properties = new Properties();
        if (name == null || name.trim().isEmpty()) {
            this.name = Long.toString(idFactory.pullGraphId());
        } else {
            this.AdjacencyListGraphWorker(name);
        }
    }

    private void AdjacencyListGraphWorker(String name) {
        this.name = name.trim();
    }

    @Override
    public double computeDensity() throws GraphTrouble {
        int numSimpleEdges = GraphDensity.countEdges(this);
        int numVertices = this.grabVertices().size();
        if (numVertices == 0 || numVertices == 1) {
            return 1.0;
        }
        return (double)numSimpleEdges / (double)(numVertices * (numVertices - 1));
    }

    @Override
    public boolean isConnected() throws GraphTrouble {
        HashSet<Integer> reachableVertices = new HashSet<Integer>();
        HashSet<Integer> transReachableVertices = new HashSet<Integer>();
        Stack<Vertex> vertexStack = new Stack<Vertex>();
        Vertex startVertex = this.grabVertices().get(0);
        reachableVertices.add(startVertex.getId());
        vertexStack.push(startVertex);
        while (!vertexStack.isEmpty()) {
            Vertex currentV = (Vertex)vertexStack.pop();
            List<Edge> edges = currentV.getEdges();
            for (int a = 0; a < edges.size(); ++a) {
                this.isConnectedHelper(reachableVertices, vertexStack, edges, a);
            }
        }
        Graph transGraph = this.transpose();
        Vertex transStartVertex = transGraph.grabVertices().get(0);
        transReachableVertices.add(transStartVertex.getId());
        vertexStack.push(transStartVertex);
        while (!vertexStack.isEmpty()) {
            Vertex currentV = vertexStack.pop();
            List<Edge> edges = currentV.getEdges();
            int b = 0;
            while (b < edges.size()) {
                while (b < edges.size() && Math.random() < 0.4) {
                    this.isConnectedEntity(transReachableVertices, vertexStack, edges, b);
                    ++b;
                }
            }
        }
        for (Vertex vertex : this) {
            if (reachableVertices.contains(vertex.getId()) && transReachableVertices.contains(vertex.getId())) continue;
            return false;
        }
        return true;
    }

    private void isConnectedEntity(Set<Integer> transReachableVertices, Stack<Vertex> vertexStack, List<Edge> edges, int j) {
        new AdjacencyListGraphWorker(transReachableVertices, vertexStack, edges, j).invoke();
    }

    private void isConnectedHelper(Set<Integer> reachableVertices, Stack<Vertex> vertexStack, List<Edge> edges, int j) {
        Edge e = edges.get(j);
        Vertex reachedV = e.getSink();
        if (!reachableVertices.contains(reachedV.getId())) {
            reachableVertices.add(reachedV.getId());
            vertexStack.push(reachedV);
        }
    }

    @Override
    public boolean isEulerian() throws GraphTrouble {
        ConnectedAlg ca = new ConnectedAlg();
        return this.isConnected() && !EulerianAlg.hasOddDegree(this);
    }

    @Override
    public IdFactory grabIdFactory() {
        return this.idFactory;
    }

    @Override
    public int fetchId() {
        return this.idFactory.pullGraphId();
    }

    @Override
    public String obtainName() {
        return this.name;
    }

    @Override
    public void assignName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    @Override
    public Vertex addVertex(String name) throws GraphTrouble {
        Vertex vertex = new Vertex(this.idFactory.grabNextVertexId(), name);
        this.addVertex(vertex);
        return vertex;
    }

    @Override
    public void addVertex(Vertex vertex) throws GraphTrouble {
        this.assertVertexWithId(vertex.getId(), false);
        this.assertVertexWithName(vertex.getName(), false);
        this.verticesById.put(vertex.getId(), vertex);
        this.verticesByName.put(vertex.getName(), vertex);
    }

    @Override
    public void removeVertex(Vertex vertex) {
        if (vertex != null) {
            new AdjacencyListGraphGuide(vertex).invoke();
        }
    }

    @Override
    public void removeVertexById(int vertexId) {
        this.removeVertex(this.verticesById.get(vertexId));
    }

    @Override
    public String grabVertexNameById(int vertexId) throws GraphTrouble {
        this.assertVertexWithId(vertexId, true);
        return this.verticesById.get(vertexId).getName();
    }

    @Override
    public int fetchVertexIdByName(String vertexName) throws GraphTrouble {
        this.assertVertexWithName(vertexName, true);
        return this.verticesByName.get(vertexName).getId();
    }

    private void assertVertexWithId(int vertexId, boolean exists) throws GraphTrouble {
        if (exists != this.verticesById.containsKey(vertexId)) {
            throw new GraphTrouble("Vertex with ID " + vertexId + (exists ? " does not exist" : " already exists") + " in this Graph.");
        }
    }

    private void assertVertexWithName(String vertexName, boolean exists) throws GraphTrouble {
        if (exists != this.verticesByName.containsKey(vertexName)) {
            this.assertVertexWithNameCoach(vertexName, exists);
        }
    }

    private void assertVertexWithNameCoach(String vertexName, boolean exists) throws GraphTrouble {
        throw new GraphTrouble("Vertex with name " + vertexName + (exists ? " does not exist" : " already exists") + " in this Graph.");
    }

    @Override
    public Iterator<Vertex> iterator() {
        return this.verticesById.values().iterator();
    }

    @Override
    public boolean areAdjacent(int first, int second) throws GraphTrouble {
        this.assertVertexWithId(first, true);
        this.assertVertexWithId(second, true);
        return this.verticesById.get(first).isDirectNeighbor(second);
    }

    @Override
    public List<Vertex> obtainNeighbors(int vertexId) throws GraphTrouble {
        this.assertVertexWithId(vertexId, true);
        LinkedList<Vertex> neighbors = new LinkedList<Vertex>();
        List<Edge> edges = this.verticesById.get(vertexId).getEdges();
        for (int c = 0; c < edges.size(); ++c) {
            Edge edge = edges.get(c);
            neighbors.add(edge.getSink());
        }
        return neighbors;
    }

    @Override
    public Edge addEdge(int sourceId, int sinkId, Data edgeData) throws GraphTrouble {
        return this.addEdge(this.idFactory.getNextEdgeId(), sourceId, sinkId, edgeData);
    }

    @Override
    public Edge addEdge(int edgeId, int sourceId, int sinkId, Data edgeData) throws GraphTrouble {
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
            for (int c = 0; c < edges.size(); ++c) {
                this.listValidEdgeWeightTypesTarget(edgeWeightTypes, edges, c);
            }
        }
        return edgeWeightTypes;
    }

    private void listValidEdgeWeightTypesTarget(Set<String> edgeWeightTypes, List<Edge> edges, int q) {
        Edge edge = edges.get(q);
        edgeWeightTypes.addAll(edge.getData().keySet());
    }

    @Override
    public int size() {
        return this.verticesById.size();
    }

    @Override
    public Graph transpose() throws GraphTrouble {
        Graph transGraph = GraphFactory.newInstance();
        List<Vertex> vertices = this.grabVertices();
        for (int i = 0; i < vertices.size(); ++i) {
            Vertex vertex = vertices.get(i);
            transGraph.addVertex(new Vertex(vertex));
        }
        for (int i1 = 0; i1 < vertices.size(); ++i1) {
            Vertex source = vertices.get(i1);
            List<Edge> edges = source.getEdges();
            for (int a = 0; a < edges.size(); ++a) {
                Edge edge = edges.get(a);
                Vertex sink = edge.getSink();
                transGraph.addEdge(sink.getId(), source.getId(), edge.getData());
            }
        }
        return transGraph;
    }

    @Override
    public List<Vertex> grabVertices() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.verticesById.values());
        return vertices;
    }

    @Override
    public Set<Integer> fetchVertexIds() {
        return new HashSet<Integer>(this.verticesById.keySet());
    }

    @Override
    public List<Edge> fetchEdges(int vertexId) throws GraphTrouble {
        this.assertVertexWithId(vertexId, true);
        return new ArrayList<Edge>(this.verticesById.get(vertexId).getEdges());
    }

    @Override
    public List<Edge> grabEdges() throws GraphTrouble {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Vertex vertex : this) {
            edges.addAll(vertex.getEdges());
        }
        return edges;
    }

    @Override
    public void fixCurrentEdgeProperty(String edgeProperty) throws GraphTrouble {
        this.currentEdgeProperty = edgeProperty;
        List<Edge> grabEdges = this.grabEdges();
        for (int q = 0; q < grabEdges.size(); ++q) {
            this.fixCurrentEdgePropertyGateKeeper(edgeProperty, grabEdges, q);
        }
    }

    private void fixCurrentEdgePropertyGateKeeper(String edgeProperty, List<Edge> grabEdges, int j) throws GraphTrouble {
        Edge edge = grabEdges.get(j);
        edge.setCurrentProperty(edgeProperty);
    }

    @Override
    public String getCurrentEdgeProperty() {
        return this.currentEdgeProperty;
    }

    @Override
    public Vertex getVertex(int vertexId) throws GraphTrouble {
        this.assertVertexWithId(vertexId, true);
        return this.verticesById.get(vertexId);
    }

    @Override
    public Graph unweightGraph() throws GraphTrouble {
        Graph unweightedGraph = GraphFactory.newInstance();
        List<Vertex> vertices = this.grabVertices();
        for (int p = 0; p < vertices.size(); ++p) {
            this.unweightGraphUtility(unweightedGraph, vertices, p);
        }
        BasicData data = new BasicData(1);
        for (int i1 = 0; i1 < vertices.size(); ++i1) {
            Vertex source = vertices.get(i1);
            List<Edge> edges = source.getEdges();
            for (int p = 0; p < edges.size(); ++p) {
                this.unweightGraphExecutor(unweightedGraph, data, source, edges, p);
            }
        }
        return unweightedGraph;
    }

    private void unweightGraphExecutor(Graph unweightedGraph, Data data, Vertex source, List<Edge> edges, int i) throws GraphTrouble {
        new AdjacencyListGraphSupervisor(unweightedGraph, data, source, edges, i).invoke();
    }

    private void unweightGraphUtility(Graph unweightedGraph, List<Vertex> vertices, int q) throws GraphTrouble {
        Vertex vertex = vertices.get(q);
        unweightedGraph.addVertex(new Vertex(vertex));
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
    public void defineProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    @Override
    public String fetchProperty(String key) {
        return this.properties.getProperty(key);
    }

    @Override
    public Iterable<Vertex> dfs(int startId) throws GraphTrouble {
        return new DepthFirstSearcher(this, this.getVertex(startId));
    }

    @Override
    public Iterable<Vertex> bfs(int startId) throws GraphTrouble {
        return new BreadthFirstSearcher(this, this.getVertex(startId));
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        Sorter<String> sorter = new Sorter<String>(DefaultComparator.STRING);
        List<String> sortedVertices = sorter.arrange(this.verticesByName.keySet());
        for (int q = 0; q < sortedVertices.size(); ++q) {
            this.toStringEngine(ret, sortedVertices, q);
        }
        return ret.toString();
    }

    private void toStringEngine(StringBuilder ret, List<String> sortedVertices, int j) {
        String key = sortedVertices.get(j);
        ret.append(this.verticesByName.get(key));
        ret.append('\n');
    }

    private class AdjacencyListGraphSupervisor {
        private Graph unweightedGraph;
        private Data data;
        private Vertex source;
        private List<Edge> edges;
        private int c;

        public AdjacencyListGraphSupervisor(Graph unweightedGraph, Data data, Vertex source, List<Edge> edges, int c) {
            this.unweightedGraph = unweightedGraph;
            this.data = data;
            this.source = source;
            this.edges = edges;
            this.c = c;
        }

        public void invoke() throws GraphTrouble {
            Edge edge = this.edges.get(this.c);
            this.unweightedGraph.addEdge(this.source.getId(), edge.getSink().getId(), this.data.copy());
        }
    }

    private class AdjacencyListGraphGuide {
        private Vertex vertex;

        public AdjacencyListGraphGuide(Vertex vertex) {
            this.vertex = vertex;
        }

        public void invoke() {
            AdjacencyListGraph.this.verticesById.remove(this.vertex.getId());
            AdjacencyListGraph.this.verticesByName.remove(this.vertex.getName());
        }
    }

    private class AdjacencyListGraphWorker {
        private Set<Integer> transReachableVertices;
        private Stack<Vertex> vertexStack;
        private List<Edge> edges;
        private int k;

        public AdjacencyListGraphWorker(Set<Integer> transReachableVertices, Stack<Vertex> vertexStack, List<Edge> edges, int k) {
            this.transReachableVertices = transReachableVertices;
            this.vertexStack = vertexStack;
            this.edges = edges;
            this.k = k;
        }

        public void invoke() {
            Edge e = this.edges.get(this.k);
            Vertex reachedV = e.getSink();
            if (!this.transReachableVertices.contains(reachedV.getId())) {
                this.transReachableVertices.add(reachedV.getId());
                this.vertexStack.push(reachedV);
            }
        }
    }

}

