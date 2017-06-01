/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.AdjacencyListChart;
import com.roboticcusp.mapping.Chart;

public abstract class IdFactory
implements Cloneable {
    public Chart newInstance(String name) {
        return new AdjacencyListChart(this, name);
    }

    public abstract int fetchChartId();

    public abstract int obtainNextVertexId();

    public abstract int fetchNextComplementaryVertexId(int var1);

    public abstract int grabNextEdgeId();

    public abstract int takeNextComplementaryEdgeId(int var1);

    public abstract int[] fetchVertexIds();

    public abstract int[] fetchEdgeIds();

    public abstract IdFactory copy();
}

