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
import java.util.Set;
import net.cybertip.align.DefaultComparator;
import net.cybertip.align.Sorter;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;

public class Vertex {
    private final int id;
    private String name;
    private Map<Integer, List<Edge>> adjacent;
    private List<Integer> neighbors;
    private Data data;

    public Vertex(Vertex vertex) {
        this(vertex.id, vertex.name);
        this.data = vertex.getData().copy();
    }

    public Vertex(int id, String name) {
        if (id <= 0) {
            this.VertexTarget(id);
        }
        this.id = id;
        this.name = name;
        this.adjacent = new HashMap<Integer, List<Edge>>();
        this.neighbors = new LinkedList<Integer>();
        this.data = new BasicData();
    }

    private void VertexTarget(int id) {
        throw new IllegalArgumentException("Vertex id must be positive: " + id);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Edge addNeighbor(int edgeId, Vertex vertex, Data edgeData, String property) {
        Edge edge = new Edge(edgeId, this, vertex, edgeData, property);
        this.addEdge(edge, vertex);
        return edge;
    }

    private void addEdge(Edge edge, Vertex vertex) {
        if (this.adjacent.containsKey(vertex.getId())) {
            this.addEdgeEngine(edge, vertex);
        } else {
            ArrayList<Edge> list = new ArrayList<Edge>();
            list.add(edge);
            this.adjacent.put(vertex.getId(), list);
            this.neighbors.add(vertex.getId());
        }
    }

    private void addEdgeEngine(Edge edge, Vertex vertex) {
        this.adjacent.get(vertex.getId()).add(edge);
    }

    private int findEdgeIndex(List<Edge> edges, Edge edge) {
        if (edge != null) {
            for (int i = 0; i < edges.size(); ++i) {
                if (!this.findEdgeIndexAid(edges, edge, i)) continue;
                return i;
            }
        }
        return -1;
    }

    private boolean findEdgeIndexAid(List<Edge> edges, Edge edge, int i) {
        Edge currentEdge = edges.get(i);
        if (currentEdge.getId() == edge.getId()) {
            return true;
        }
        return false;
    }

    public void removeEdge(Edge edge) {
        int indexOfEdgetoRemove;
        Vertex sink = edge.getSink();
        List<Edge> edges = this.adjacent.get(sink.getId());
        if (edges != null && (indexOfEdgetoRemove = this.findEdgeIndex(edges, edge)) >= 0) {
            edges.remove(indexOfEdgetoRemove);
            if (edges.isEmpty()) {
                new VertexSupervisor(edge).invoke();
            }
        }
    }

    public void removeNeighbor(Vertex v) {
        this.adjacent.remove(v.getId());
        this.neighbors.remove((Object)v.getId());
    }

    public void setData(Data data) {
        this.data = Objects.requireNonNull(data, "Data may not be null");
    }

    public Data getData() {
        return this.data;
    }

    public void clearData() {
        this.data = new BasicData();
    }

    public boolean hasData() {
        return this.data.hasData();
    }

    public Map<Integer, List<Edge>> getAdjacent() {
        HashMap<Integer, List<Edge>> mapCopy = new HashMap<Integer, List<Edge>>();
        for (Map.Entry<Integer, List<Edge>> entry : this.adjacent.entrySet()) {
            this.getAdjacentTarget(mapCopy, entry);
        }
        return mapCopy;
    }

    private void getAdjacentTarget(Map<Integer, List<Edge>> mapCopy, Map.Entry<Integer, List<Edge>> entry) {
        mapCopy.put(entry.getKey(), new ArrayList(entry.getValue()));
    }

    public boolean isDirectNeighbor(int vertexId) {
        return this.adjacent.containsKey(vertexId);
    }

    public List<Edge> getEdges() {
        ArrayList<Edge> list = new ArrayList<Edge>();
        Iterator<Integer> i$ = this.neighbors.iterator();
        while (i$.hasNext()) {
            int neighbor = i$.next();
            List<Edge> edges = this.adjacent.get(neighbor);
            list.addAll(edges);
        }
        return list;
    }

    public List<Edge> getEdges(Vertex otherVertex) {
        List<Edge> list = this.adjacent.get(otherVertex.getId());
        return list == null ? new ArrayList() : list;
    }

    public static Comparator<Vertex> getComparator() {
        return new Comparator<Vertex>(){

            @Override
            public int compare(Vertex vertex1, Vertex vertex2) {
                return vertex1.getName().compareTo(vertex2.getName());
            }
        };
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(' ');
        ret.append(this.getName());
        ret.append(" |");
        HashSet<String> adjacentVertexNames = new HashSet<String>();
        HashMap<String, Integer> adjacentVertexNamesToIds = new HashMap<String, Integer>();
        for (Edge edge : this.getEdges()) {
            String sinkName = edge.getSink().getName();
            int sinkId = edge.getSink().getId();
            adjacentVertexNames.add(sinkName);
            adjacentVertexNamesToIds.put(sinkName, sinkId);
        }
        Sorter<String> sorter = new Sorter<String>(DefaultComparator.STRING);
        List<String> sortedAdjacentVertexNames = sorter.arrange(adjacentVertexNames);
        for (String name : sortedAdjacentVertexNames) {
            ret.append(' ');
            ret.append(name);
            boolean firsttime = true;
            List<Edge> edges = this.adjacent.get(adjacentVertexNamesToIds.get(name));
            for (Edge e : edges) {
                if (firsttime) {
                    firsttime = false;
                } else {
                    new VertexHandler(ret).invoke();
                }
                ret.append(' ');
                ret.append(e.getWeight());
            }
            ret.append(';');
        }
        if (this.data.hasData()) {
            ret.append(this.data);
        }
        return ret.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Vertex vertex = (Vertex)obj;
        return this.id == vertex.id;
    }

    public int hashCode() {
        return this.id;
    }

    private class VertexHandler {
        private StringBuilder ret;

        public VertexHandler(StringBuilder ret) {
            this.ret = ret;
        }

        public void invoke() {
            this.ret.append(',');
        }
    }

    private class VertexSupervisor {
        private Edge edge;

        public VertexSupervisor(Edge edge) {
            this.edge = edge;
        }

        public void invoke() {
            Vertex.this.removeNeighbor(this.edge.getSink());
        }
    }

}

