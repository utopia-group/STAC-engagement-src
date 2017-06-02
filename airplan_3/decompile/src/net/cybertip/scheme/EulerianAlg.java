/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.List;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class EulerianAlg {
    public static boolean hasOddDegree(Graph graph) throws GraphTrouble {
        List<Vertex> grabVertices = graph.grabVertices();
        for (int i = 0; i < grabVertices.size(); ++i) {
            Vertex v = grabVertices.get(i);
            List<Edge> edges = graph.fetchEdges(v.getId());
            if (edges.size() % 2 == 0) continue;
            return false;
        }
        return true;
    }
}

