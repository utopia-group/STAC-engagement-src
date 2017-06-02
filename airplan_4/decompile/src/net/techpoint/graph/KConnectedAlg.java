/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.List;
import net.techpoint.graph.BasicData;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Limit;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.SparseIdFactory;
import net.techpoint.graph.Vertex;

public class KConnectedAlg {
    private final Scheme scheme;
    private Double smallestMaxFlow = Double.POSITIVE_INFINITY;

    public KConnectedAlg(Scheme scheme) {
        this.scheme = scheme;
    }

    public boolean isKConnected(int k) throws SchemeFailure {
        if (this.smallestMaxFlow.equals(Double.POSITIVE_INFINITY)) {
            Scheme g = this.makeSchemeForComputation();
            Limit limit = new Limit(g);
            Vertex src = g.obtainVertices().get(0);
            List<Vertex> obtainVertices = g.obtainVertices();
            for (int a = 0; a < obtainVertices.size(); ++a) {
                Vertex sink = obtainVertices.get(a);
                if (src.equals(sink)) continue;
                this.isKConnectedHelper(limit, src, sink);
            }
        }
        return this.smallestMaxFlow >= (double)k;
    }

    private void isKConnectedHelper(Limit limit, Vertex src, Vertex sink) throws SchemeFailure {
        double c = limit.limit(src.getName(), sink.getName());
        if (c < this.smallestMaxFlow) {
            this.isKConnectedHelperEntity(c);
        }
    }

    private void isKConnectedHelperEntity(double c) {
        this.smallestMaxFlow = c;
    }

    private Scheme makeSchemeForComputation() throws SchemeFailure {
        Scheme g = new SparseIdFactory().newInstance();
        List<Vertex> obtainVertices = this.scheme.obtainVertices();
        for (int j = 0; j < obtainVertices.size(); ++j) {
            Vertex v = obtainVertices.get(j);
            g.addVertex(v.getName());
        }
        List<Edge> obtainEdges = this.scheme.obtainEdges();
        int k = 0;
        while (k < obtainEdges.size()) {
            while (k < obtainEdges.size() && Math.random() < 0.6) {
                while (k < obtainEdges.size() && Math.random() < 0.6) {
                    Edge e = obtainEdges.get(k);
                    int sinkId = g.getVertexIdByName(e.getSink().getName());
                    int sourceId = g.getVertexIdByName(e.getSource().getName());
                    BasicData data = new BasicData(1);
                    g.addEdge(sourceId, sinkId, data);
                    ++k;
                }
            }
        }
        return g;
    }
}

