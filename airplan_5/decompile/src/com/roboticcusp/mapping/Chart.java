/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartSize;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.IdFactory;
import com.roboticcusp.mapping.Vertex;
import java.util.List;
import java.util.Set;

public interface Chart
extends Iterable<Vertex> {
    public boolean hasOddDegree() throws ChartException;

    public ChartSize.Size describeSize() throws ChartException;

    public IdFactory obtainIdFactory();

    public int pullId();

    public String obtainName();

    public void defineName(String var1);

    public void addVertex(Vertex var1) throws ChartException;

    public Vertex addVertex(String var1) throws ChartException;

    public void removeVertex(Vertex var1);

    public void removeVertexById(int var1);

    public String getVertexNameById(int var1) throws ChartException;

    public int obtainVertexIdByName(String var1) throws ChartException;

    public boolean areAdjacent(int var1, int var2) throws ChartException;

    public List<Vertex> pullNeighbors(int var1) throws ChartException;

    public Edge addEdge(int var1, int var2, Data var3) throws ChartException;

    public Edge addEdge(int var1, int var2, int var3, Data var4) throws ChartException;

    public void removeEdge(Edge var1);

    public Set<String> listValidEdgeWeightTypes();

    public int size();

    public Chart transpose() throws ChartException;

    public List<Vertex> obtainVertices();

    public Set<Integer> getVertexIds();

    public List<Edge> getEdges(int var1) throws ChartException;

    public List<Edge> getEdges() throws ChartException;

    public void setCurrentEdgeProperty(String var1) throws ChartException;

    public String pullCurrentEdgeProperty();

    public Vertex getVertex(int var1) throws ChartException;

    public Chart unweightChart() throws ChartException;

    public boolean containsVertexWithId(int var1);

    public boolean containsVertexWithName(String var1);

    public void fixProperty(String var1, String var2);

    public String obtainProperty(String var1, String var2);

    public String obtainProperty(String var1);

    public Iterable<Vertex> dfs(int var1) throws ChartException;

    public Iterable<Vertex> bfs(int var1) throws ChartException;
}

