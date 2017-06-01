/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.IdFactory;

public class DefaultIdFactory
extends IdFactory {
    private static int defaultChartId;
    private final int chartId;
    private int vertexId;
    private int edgeId;

    public DefaultIdFactory() {
        this(++defaultChartId);
    }

    public DefaultIdFactory(int chartId) {
        if (chartId <= 0) {
            throw new IllegalArgumentException("Graph IDs must be positive: " + chartId);
        }
        this.chartId = chartId;
    }

    public DefaultIdFactory(int chartId, int[] vertexIds, int[] edgeIds) {
        this(chartId);

        int id;
        for (int c = 0; c < vertexIds.length; ++c) {
            id = vertexIds[c];
            if (this.vertexId >= id) continue;
            this.vertexId = id;
        }
        for (int b = 0; b < edgeIds.length; ++b) {
            id = edgeIds[b];
            if (this.edgeId >= id) continue;
            this.DefaultIdFactoryWorker(id);
        }
    }

    private void DefaultIdFactoryWorker(int id) {
        new DefaultIdFactoryUtility(id).invoke();
    }

    @Override
    public int fetchChartId() {
        return this.chartId;
    }

    @Override
    public int obtainNextVertexId() {
        return ++this.vertexId;
    }

    @Override
    public int fetchNextComplementaryVertexId(int least) {
        if (this.vertexId >= least) {
            return ++this.vertexId;
        }
        return this.fetchNextComplementaryVertexIdAid(least);
    }

    private int fetchNextComplementaryVertexIdAid(int least) {
        this.vertexId = least;
        return this.vertexId;
    }

    @Override
    public int grabNextEdgeId() {
        return ++this.edgeId;
    }

    @Override
    public int takeNextComplementaryEdgeId(int least) {
        if (this.edgeId >= least) {
            return ++this.edgeId;
        }
        return this.takeNextComplementaryEdgeIdGateKeeper(least);
    }

    private int takeNextComplementaryEdgeIdGateKeeper(int least) {
        this.edgeId = least;
        return this.edgeId;
    }

    @Override
    public int[] fetchVertexIds() {
        int size = this.vertexId;
        int[] ids = new int[size];
        for (int c = 0; c < size; ++c) {
            ids[c] = c + 1;
        }
        return ids;
    }

    @Override
    public int[] fetchEdgeIds() {
        int size = this.edgeId;
        int[] ids = new int[size];
        for (int p = 0; p < size; ++p) {
            ids[p] = p + 1;
        }
        return ids;
    }

    @Override
    public IdFactory copy() {
        return new DefaultIdFactory(this.chartId, this.fetchVertexIds(), this.fetchEdgeIds());
    }

    private class DefaultIdFactoryUtility {
        private int id;

        public DefaultIdFactoryUtility(int id) {
            this.id = id;
        }

        public void invoke() {
            DefaultIdFactory.this.edgeId = this.id;
        }
    }

}

