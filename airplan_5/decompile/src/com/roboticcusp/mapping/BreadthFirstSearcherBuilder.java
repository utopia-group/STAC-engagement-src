/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.BreadthFirstSearcher;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.Vertex;

public class BreadthFirstSearcherBuilder {
    private Vertex start;
    private Chart chart;

    public BreadthFirstSearcherBuilder defineStart(Vertex start) {
        this.start = start;
        return this;
    }

    public BreadthFirstSearcherBuilder setChart(Chart chart) {
        this.chart = chart;
        return this;
    }

    public BreadthFirstSearcher composeBreadthFirstSearcher() {
        return new BreadthFirstSearcher(this.chart, this.start);
    }
}

