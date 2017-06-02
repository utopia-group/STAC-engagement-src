/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.List;
import java.util.Set;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.IdFactory;
import net.cybertip.scheme.Vertex;

public interface Graph
extends Iterable<Vertex> {
    public double computeDensity() throws GraphTrouble;

    public boolean isConnected() throws GraphTrouble;

    public boolean isEulerian() throws GraphTrouble;

    public IdFactory grabIdFactory();

    public int fetchId();

    public String obtainName();

    public void assignName(String var1);

    public void addVertex(Vertex var1) throws GraphTrouble;

    public Vertex addVertex(String var1) throws GraphTrouble;

    public void removeVertex(Vertex var1);

    public void removeVertexById(int var1);

    public String grabVertexNameById(int var1) throws GraphTrouble;

    public int fetchVertexIdByName(String var1) throws GraphTrouble;

    public boolean areAdjacent(int var1, int var2) throws GraphTrouble;

    public List<Vertex> obtainNeighbors(int var1) throws GraphTrouble;

    public Edge addEdge(int var1, int var2, Data var3) throws GraphTrouble;

    public Edge addEdge(int var1, int var2, int var3, Data var4) throws GraphTrouble;

    public void removeEdge(Edge var1);

    public Set<String> listValidEdgeWeightTypes();

    public int size();

    public Graph transpose() throws GraphTrouble;

    public List<Vertex> grabVertices();

    public Set<Integer> fetchVertexIds();

    public List<Edge> fetchEdges(int var1) throws GraphTrouble;

    public List<Edge> grabEdges() throws GraphTrouble;

    public void fixCurrentEdgeProperty(String var1) throws GraphTrouble;

    public String getCurrentEdgeProperty();

    public Vertex getVertex(int var1) throws GraphTrouble;

    public Graph unweightGraph() throws GraphTrouble;

    public boolean containsVertexWithId(int var1);

    public boolean containsVertexWithName(String var1);

    public void defineProperty(String var1, String var2);

    public String getProperty(String var1, String var2);

    public String fetchProperty(String var1);

    public Iterable<Vertex> dfs(int var1) throws GraphTrouble;

    public Iterable<Vertex> bfs(int var1) throws GraphTrouble;
}

