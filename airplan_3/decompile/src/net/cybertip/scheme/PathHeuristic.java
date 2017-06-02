/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.List;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;

public class PathHeuristic {
    private Graph graph;

    public PathHeuristic(Graph graph) {
        this.graph = graph;
    }

    public double heuristic(int u, int v) throws GraphTrouble {
        if (u == v) {
            return 0.0;
        }
        double minEdge = Double.MAX_VALUE;
        List<Edge> fetchEdges = this.graph.fetchEdges(u);
        for (int i = 0; i < fetchEdges.size(); ++i) {
            Edge e = fetchEdges.get(i);
            double w = e.getWeight();
            if (minEdge <= w) continue;
            minEdge = w;
        }
        return minEdge;
    }
}

