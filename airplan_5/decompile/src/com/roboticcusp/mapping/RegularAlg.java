/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.util.List;

public class RegularAlg {
    private Chart chart;
    private int outDegree = -1;

    public RegularAlg(Chart g) {
        this.chart = g;
    }

    public boolean isOutRegular() throws ChartException {
        int prevOutDegree = -1;
        List<Vertex> obtainVertices = this.chart.obtainVertices();
        for (int p = 0; p < obtainVertices.size(); ++p) {
            Vertex vertex = obtainVertices.get(p);
            List<Edge> edges = this.chart.getEdges(vertex.getId());
            int outDegree = edges.size();
            if (prevOutDegree == -1) {
                prevOutDegree = outDegree;
                continue;
            }
            if (prevOutDegree == outDegree) continue;
            return false;
        }
        this.outDegree = prevOutDegree;
        return true;
    }

    public int pullOutDegree() throws ChartException {
        if (this.isOutRegular()) {
            return this.outDegree;
        }
        return -1;
    }
}

