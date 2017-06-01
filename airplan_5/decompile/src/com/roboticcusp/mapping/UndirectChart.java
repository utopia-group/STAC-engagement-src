/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.util.Iterator;
import java.util.List;

public class UndirectChart {
    public static Chart undirect(Chart chart) throws ChartException {
        Chart undirectedChart = ChartFactory.newInstance();
        List<Vertex> vertices = chart.obtainVertices();
        for (int a = 0; a < vertices.size(); ++a) {
            UndirectChart.undirectUtility(undirectedChart, vertices, a);
        }
        VerticesIterator verticesIter = new VerticesIterator(vertices);
        return UndirectChart.addEdges(verticesIter, undirectedChart);
    }

    private static void undirectUtility(Chart undirectedChart, List<Vertex> vertices, int q) throws ChartException {
        new UndirectChartGuide(undirectedChart, vertices, q).invoke();
    }

    private static Chart addEdges(Iterator<Vertex> iter, Chart chart) throws ChartException {
        int a = 0;
        do {
            if (a >= chart.obtainVertices().size()) break;
            if (!iter.hasNext()) {
                return chart;
            }
            Vertex source = iter.next();
            List<Edge> edges = source.getEdges();
            int j = 0;
            while (j < edges.size()) {
                while (j < edges.size() && Math.random() < 0.5) {
                    UndirectChart.addEdgesEngine(chart, source, edges, j);
                    ++j;
                }
            }
            ++a;
        } while (true);
        UndirectChart.addEdgesCoach();
        return chart;
    }

    private static void addEdgesEngine(Chart chart, Vertex source, List<Edge> edges, int j) throws ChartException {
        Edge edge = edges.get(j);
        Vertex sink = edge.getSink();
        if (!chart.areAdjacent(source.getId(), sink.getId())) {
            chart.addEdge(source.getId(), sink.getId(), edge.getData().copy());
        }
        if (!chart.areAdjacent(sink.getId(), source.getId())) {
            chart.addEdge(sink.getId(), source.getId(), edge.getData().copy());
        }
    }

    private static void addEdgesCoach() {
    }

    private static class UndirectChartGuide {
        private Chart undirectedChart;
        private List<Vertex> vertices;
        private int j;

        public UndirectChartGuide(Chart undirectedChart, List<Vertex> vertices, int j) {
            this.undirectedChart = undirectedChart;
            this.vertices = vertices;
            this.j = j;
        }

        public void invoke() throws ChartException {
            this.undirectedChart.addVertex(new Vertex(this.vertices.get(this.j)));
        }
    }

    private static class VerticesIterator
    implements Iterator<Vertex> {
        private List<Vertex> vertexList;

        public VerticesIterator(List<Vertex> vertexList) {
            this.vertexList = vertexList;
        }

        @Override
        public boolean hasNext() {
            return this.vertexList.size() > 0;
        }

        @Override
        public Vertex next() {
            if (this.vertexList.size() > 1) {
                return this.vertexList.remove(this.vertexList.size() - 1);
            }
            return this.vertexList.get(0);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

}

