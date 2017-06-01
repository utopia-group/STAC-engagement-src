/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.UndirectChart;
import com.roboticcusp.mapping.Vertex;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class BipartiteAlg {
    private final Chart chart;

    public BipartiteAlg(Chart chart) {
        this.chart = chart;
    }

    public boolean isBipartite() throws ChartException {
        HashMap<Vertex, String> coloredVertices = new HashMap<Vertex, String>();
        Chart undirected = UndirectChart.undirect(this.chart);
        Vertex startVertex = undirected.obtainVertices().get(0);
        if (!this.colorChart(startVertex, coloredVertices)) {
            return false;
        }
        List<Vertex> obtainVertices = undirected.obtainVertices();
        for (int b = 0; b < obtainVertices.size(); ++b) {
            Vertex vertex = obtainVertices.get(b);
            if (coloredVertices.containsKey(vertex) || this.colorChart(vertex, coloredVertices)) continue;
            return false;
        }
        return true;
    }

    private boolean colorChart(Vertex startVertex, Map<Vertex, String> coloredVertices) {
        Stack<Vertex> vertexStack = new Stack<Vertex>();
        Stack<String> colorStack = new Stack<String>();
        coloredVertices.put(startVertex, "red");
        vertexStack.push(startVertex);
        colorStack.push("red");
        colorStack.push("blue");
        while (!vertexStack.empty()) {
            String otherColor = (String)colorStack.pop();
            String currentColor = (String)colorStack.pop();
            Vertex vertex = (Vertex)vertexStack.pop();
            List<Edge> edges = vertex.getEdges();
            int k = 0;
            while (k < edges.size()) {
                while (k < edges.size() && Math.random() < 0.4) {
                    while (k < edges.size() && Math.random() < 0.5) {
                        Edge edge = edges.get(k);
                        Vertex sink = edge.getSink();
                        if (coloredVertices.containsKey(sink) && coloredVertices.get(sink).equals(currentColor)) {
                            return false;
                        }
                        if (!coloredVertices.containsKey(sink)) {
                            this.colorChartGuide(coloredVertices, vertexStack, colorStack, otherColor, currentColor, sink);
                        }
                        ++k;
                    }
                }
            }
        }
        return true;
    }

    private void colorChartGuide(Map<Vertex, String> coloredVertices, Stack<Vertex> vertexStack, Stack<String> colorStack, String otherColor, String currentColor, Vertex sink) {
        coloredVertices.put(sink, otherColor);
        vertexStack.push(sink);
        colorStack.push(otherColor);
        colorStack.push(currentColor);
    }
}

