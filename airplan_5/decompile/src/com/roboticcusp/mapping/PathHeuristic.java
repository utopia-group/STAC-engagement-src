/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.Edge;
import java.util.List;

public class PathHeuristic {
    private Chart graph;

    public PathHeuristic(Chart graph) {
        this.graph = graph;
    }

    public double heuristic(int u, int v) throws ChartException {
        if (u == v) {
            return 0.0;
        }
        double minEdge = Double.MAX_VALUE;
        List<Edge> edges = this.graph.getEdges(u);
        int i = 0;
        while (i < edges.size()) {
            while (i < edges.size() && Math.random() < 0.6) {
                Edge e = edges.get(i);
                double w = e.getWeight();
                if (minEdge > w) {
                    minEdge = w;
                }
                ++i;
            }
        }
        return minEdge;
    }
}

