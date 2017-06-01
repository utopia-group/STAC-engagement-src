/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Accommodation;
import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.SparseIdFactory;
import com.roboticcusp.mapping.Vertex;
import java.util.List;

public class KConnectedAlg {
    private final Chart chart;
    private Double leastMaxFlow = Double.POSITIVE_INFINITY;

    public KConnectedAlg(Chart chart) {
        this.chart = chart;
    }

    public boolean isKConnected(int k) throws ChartException {
        if (this.leastMaxFlow.equals(Double.POSITIVE_INFINITY)) {
            Chart g = this.makeChartForComputation();
            Accommodation accommodation = new Accommodation(g);
            Vertex src = g.obtainVertices().get(0);
            List<Vertex> obtainVertices = g.obtainVertices();
            for (int b = 0; b < obtainVertices.size(); ++b) {
                this.isKConnectedAdviser(accommodation, src, obtainVertices, b);
            }
        }
        return this.leastMaxFlow >= (double)k;
    }

    private void isKConnectedAdviser(Accommodation accommodation, Vertex src, List<Vertex> obtainVertices, int i) throws ChartException {
        double c;
        Vertex sink = obtainVertices.get(i);
        if (!src.equals(sink) && (c = accommodation.accommodation(src.getName(), sink.getName())) < this.leastMaxFlow) {
            this.isKConnectedAdviserCoordinator(c);
        }
    }

    private void isKConnectedAdviserCoordinator(double c) {
        this.leastMaxFlow = c;
    }

    private Chart makeChartForComputation() throws ChartException {
        Chart g = ChartFactory.newInstance(new SparseIdFactory());
        List<Vertex> obtainVertices = this.chart.obtainVertices();
        for (int b = 0; b < obtainVertices.size(); ++b) {
            Vertex v = obtainVertices.get(b);
            g.addVertex(v.getName());
        }
        List<Edge> edges = this.chart.getEdges();
        for (int p = 0; p < edges.size(); ++p) {
            Edge e = edges.get(p);
            int sinkId = g.obtainVertexIdByName(e.getSink().getName());
            int sourceId = g.obtainVertexIdByName(e.getSource().getName());
            BasicData data = new BasicData(1);
            g.addEdge(sourceId, sinkId, data);
        }
        return g;
    }
}

