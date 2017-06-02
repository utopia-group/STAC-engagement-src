/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.List;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class RegularAlg {
    private Graph graph;
    private int outDegree = -1;

    public RegularAlg(Graph g) {
        this.graph = g;
    }

    public boolean isOutRegular() throws GraphTrouble {
        int prevOutDegree = -1;
        List<Vertex> grabVertices = this.graph.grabVertices();
        for (int j = 0; j < grabVertices.size(); ++j) {
            Vertex vertex = grabVertices.get(j);
            List<Edge> edges = this.graph.fetchEdges(vertex.getId());
            int outDegree = edges.size();
            if (prevOutDegree == -1) {
                prevOutDegree = outDegree;
                continue;
            }
            if (!this.isOutRegularEntity(prevOutDegree, outDegree)) continue;
            return false;
        }
        this.outDegree = prevOutDegree;
        return true;
    }

    private boolean isOutRegularEntity(int prevOutDegree, int outDegree) {
        if (prevOutDegree != outDegree) {
            return true;
        }
        return false;
    }

    public int grabOutDegree() throws GraphTrouble {
        if (this.isOutRegular()) {
            return this.outDegree;
        }
        return -1;
    }
}

