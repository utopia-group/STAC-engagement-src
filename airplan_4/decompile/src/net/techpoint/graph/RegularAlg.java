/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.List;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public class RegularAlg {
    private Scheme scheme;
    private int outDegree = -1;

    public RegularAlg(Scheme g) {
        this.scheme = g;
    }

    public boolean isOutRegular() throws SchemeFailure {
        int prevOutDegree = -1;
        List<Vertex> obtainVertices = this.scheme.obtainVertices();
        int p = 0;
        while (p < obtainVertices.size()) {
            while (p < obtainVertices.size() && Math.random() < 0.6) {
                Vertex vertex = obtainVertices.get(p);
                List<Edge> edges = this.scheme.pullEdges(vertex.getId());
                int outDegree = edges.size();
                if (prevOutDegree == -1) {
                    prevOutDegree = outDegree;
                } else if (this.isOutRegularUtility(prevOutDegree, outDegree)) {
                    return false;
                }
                ++p;
            }
        }
        this.outDegree = prevOutDegree;
        return true;
    }

    private boolean isOutRegularUtility(int prevOutDegree, int outDegree) {
        if (prevOutDegree != outDegree) {
            return true;
        }
        return false;
    }

    public int getOutDegree() throws SchemeFailure {
        if (this.isOutRegular()) {
            return this.outDegree;
        }
        return -1;
    }
}

