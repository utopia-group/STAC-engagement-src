/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.List;
import java.util.Set;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.IdFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public interface Scheme
extends Iterable<Vertex> {
    public boolean hasOddDegree() throws SchemeFailure;

    public IdFactory pullIdFactory();

    public int takeId();

    public String takeName();

    public void defineName(String var1);

    public void addVertex(Vertex var1) throws SchemeFailure;

    public Vertex addVertex(String var1) throws SchemeFailure;

    public void removeVertex(Vertex var1);

    public void removeVertexById(int var1);

    public String grabVertexNameById(int var1) throws SchemeFailure;

    public int getVertexIdByName(String var1) throws SchemeFailure;

    public boolean areAdjacent(int var1, int var2) throws SchemeFailure;

    public List<Vertex> grabNeighbors(int var1) throws SchemeFailure;

    public Edge addEdge(int var1, int var2, Data var3) throws SchemeFailure;

    public Edge addEdge(int var1, int var2, int var3, Data var4) throws SchemeFailure;

    public void removeEdge(Edge var1);

    public Set<String> listValidEdgeWeightTypes();

    public int size();

    public Scheme transpose() throws SchemeFailure;

    public List<Vertex> obtainVertices();

    public Set<Integer> grabVertexIds();

    public List<Edge> pullEdges(int var1) throws SchemeFailure;

    public List<Edge> obtainEdges() throws SchemeFailure;

    public void fixCurrentEdgeProperty(String var1) throws SchemeFailure;

    public String getCurrentEdgeProperty();

    public Vertex grabVertex(int var1) throws SchemeFailure;

    public Scheme unweightScheme() throws SchemeFailure;

    public boolean containsVertexWithId(int var1);

    public boolean containsVertexWithName(String var1);

    public void assignProperty(String var1, String var2);

    public String getProperty(String var1, String var2);

    public String takeProperty(String var1);

    public Iterable<Vertex> dfs(int var1) throws SchemeFailure;

    public Iterable<Vertex> bfs(int var1) throws SchemeFailure;
}

