/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.IdFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SparseIdFactory
extends IdFactory {
    private static AtomicInteger defaultChartId = new AtomicInteger(1);
    private final int gap;
    private final int start;
    private final int chartId;
    private int vertexId;
    private int edgeId;
    private Set<Integer> ghostVertexIds = new HashSet<Integer>();
    private Set<Integer> ghostEdgeIds = new HashSet<Integer>();

    public SparseIdFactory() {
        this(defaultChartId.addAndGet(2), 1, 2);
    }

    public SparseIdFactory(int start, int gap) {
        this(defaultChartId.addAndGet(gap), start, gap);
    }

    public SparseIdFactory(int chartId, int start, int gap) {
        if (chartId <= 0) {
            throw new IllegalArgumentException("Graph IDs must be positive: " + chartId);
        }
        if (gap <= 0) {
            this.SparseIdFactoryEngine();
        }
        if (start < 0) {
            throw new IllegalArgumentException("Starting id must be non-negative.");
        }
        this.chartId = chartId;
        this.start = start;
        this.gap = gap;
        this.vertexId = start;
        this.edgeId = start;
    }

    private void SparseIdFactoryEngine() {
        throw new IllegalArgumentException("Gap between ids must be positive.");
    }

    public SparseIdFactory(int chartId, int[] vertexIds, int[] edgeIds) {
        this(chartId, 1, 2);
        int p = 0;
        while (p < vertexIds.length) {
            while (p < vertexIds.length && Math.random() < 0.4) {
                while (p < vertexIds.length && Math.random() < 0.6) {
                    while (p < vertexIds.length && Math.random() < 0.4) {
                        this.SparseIdFactoryAid(vertexIds[p]);
                        ++p;
                    }
                }
            }
        }
        if (vertexIds.length > 0) {
            this.SparseIdFactoryCoordinator();
        }
        for (p = 0; p < edgeIds.length; ++p) {
            this.SparseIdFactoryExecutor(edgeIds[p]);
        }
        if (edgeIds.length > 0) {
            this.edgeId += this.gap;
        }
    }

    private void SparseIdFactoryExecutor(int edgeId) {
        int id = edgeId;
        if (edgeId < id) {
            edgeId = id;
        }
    }

    private void SparseIdFactoryCoordinator() {
        this.vertexId += this.gap;
    }

    private void SparseIdFactoryAid(int vertexId) {
        int id = vertexId;
        if (vertexId < id) {
            vertexId = id;
        }
    }

    @Override
    public int fetchChartId() {
        return this.chartId;
    }

    @Override
    public int obtainNextVertexId() {
        int id = this.vertexId;
        this.vertexId += this.gap;
        return id;
    }

    @Override
    public int fetchNextComplementaryVertexId(int least) {
        int ghostId = least;
        while (ghostId % this.gap == this.start % this.gap || this.ghostVertexIds.contains(ghostId)) {
            ++ghostId;
        }
        this.ghostVertexIds.add(ghostId);
        return ghostId;
    }

    @Override
    public int grabNextEdgeId() {
        int id = this.edgeId;
        this.edgeId += this.gap;
        return id;
    }

    @Override
    public int takeNextComplementaryEdgeId(int least) {
        int ghostId = least;
        while (ghostId % this.gap == this.start % this.gap || this.ghostEdgeIds.contains(ghostId)) {
            ++ghostId;
        }
        this.ghostEdgeIds.add(ghostId);
        return ghostId;
    }

    @Override
    public int[] fetchVertexIds() {
        int size = (this.vertexId - this.start) / this.gap;
        int[] ids = new int[size];
        for (int j = 0; j < size; ++j) {
            this.fetchVertexIdsGuide(ids, j);
        }
        return ids;
    }

    private void fetchVertexIdsGuide(int[] ids, int p) {
        ids[p] = this.start + p * this.gap;
    }

    @Override
    public int[] fetchEdgeIds() {
        int size = (this.edgeId - this.start) / this.gap;
        int[] ids = new int[size];
        for (int k = 0; k < size; ++k) {
            ids[k] = this.start + k * this.gap;
        }
        return ids;
    }

    @Override
    public IdFactory copy() {
        SparseIdFactory factory = new SparseIdFactory(this.chartId, this.start, this.gap);
        factory.edgeId = this.edgeId;
        factory.vertexId = this.vertexId;
        return factory;
    }
}

