/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.List;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Limit;
import net.cybertip.scheme.SparseIdFactory;
import net.cybertip.scheme.Vertex;

public class KConnectedAlg {
    private final Graph graph;
    private Double smallestMaxFlow = Double.POSITIVE_INFINITY;

    public KConnectedAlg(Graph graph) {
        this.graph = graph;
    }

    public boolean isKConnected(int k) throws GraphTrouble {
        if (this.smallestMaxFlow.equals(Double.POSITIVE_INFINITY)) {
            Graph g = this.makeGraphForComputation();
            Limit limit = new Limit(g);
            Vertex src = g.grabVertices().get(0);
            List<Vertex> grabVertices = g.grabVertices();
            for (int j = 0; j < grabVertices.size(); ++j) {
                this.isKConnectedHerder(limit, src, grabVertices, j);
            }
        }
        return this.smallestMaxFlow >= (double)k;
    }

    private void isKConnectedHerder(Limit limit, Vertex src, List<Vertex> grabVertices, int k) throws GraphTrouble {
        double c;
        Vertex sink = grabVertices.get(k);
        if (!src.equals(sink) && (c = limit.limit(src.getName(), sink.getName())) < this.smallestMaxFlow) {
            this.isKConnectedHerderWorker(c);
        }
    }

    private void isKConnectedHerderWorker(double c) {
        this.smallestMaxFlow = c;
    }

    private Graph makeGraphForComputation() throws GraphTrouble {
        Graph g = new SparseIdFactory().newInstance();
        List<Vertex> grabVertices = this.graph.grabVertices();
        for (int b = 0; b < grabVertices.size(); ++b) {
            this.makeGraphForComputationExecutor(g, grabVertices, b);
        }
        List<Edge> grabEdges = this.graph.grabEdges();
        for (int j = 0; j < grabEdges.size(); ++j) {
            this.makeGraphForComputationService(g, grabEdges, j);
        }
        return g;
    }

    private void makeGraphForComputationService(Graph g, List<Edge> grabEdges, int c) throws GraphTrouble {
        Edge e = grabEdges.get(c);
        int sinkId = g.fetchVertexIdByName(e.getSink().getName());
        int sourceId = g.fetchVertexIdByName(e.getSource().getName());
        BasicData data = new BasicData(1);
        g.addEdge(sourceId, sinkId, data);
    }

    private void makeGraphForComputationExecutor(Graph g, List<Vertex> grabVertices, int c) throws GraphTrouble {
        Vertex v = grabVertices.get(c);
        g.addVertex(v.getName());
    }
}

