/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.techpoint.graph.IdFactory;

public class SparseIdFactory
extends IdFactory {
    private static AtomicInteger defaultSchemeId = new AtomicInteger(1);
    private final int gap;
    private final int start;
    private final int schemeId;
    private int vertexId;
    private int edgeId;
    private Set<Integer> ghostVertexIds;
    private Set<Integer> ghostEdgeIds;

    public SparseIdFactory() {
        this(defaultSchemeId.addAndGet(2), 1, 2);
    }

    public SparseIdFactory(int start, int gap) {
        this(defaultSchemeId.addAndGet(gap), start, gap);
    }

    public SparseIdFactory(int schemeId, int start, int gap) {
        this.ghostVertexIds = new HashSet<Integer>();
        this.ghostEdgeIds = new HashSet<Integer>();
        if (schemeId <= 0) {
            throw new IllegalArgumentException("Graph IDs must be positive: " + schemeId);
        }
        if (gap <= 0) {
            throw new IllegalArgumentException("Gap between ids must be positive.");
        }
        if (start < 0) {
            throw new IllegalArgumentException("Starting id must be non-negative.");
        }
        this.schemeId = schemeId;
        this.start = start;
        this.gap = gap;
        this.vertexId = start;
        this.edgeId = start;
    }

    public SparseIdFactory(int schemeId, int[] vertexIds, int[] edgeIds) {
        this(schemeId, 1, 2);
        int id;

        for (int i = 0; i < vertexIds.length; ++i) {
            id = vertexIds[i];
            if (this.vertexId >= id) continue;
            this.vertexId = id;
        }
        if (vertexIds.length > 0) {
            this.vertexId += this.gap;
        }
        int k = 0;
        while (k < edgeIds.length) {
            while (k < edgeIds.length && Math.random() < 0.5) {
                while (k < edgeIds.length && Math.random() < 0.4) {
                    id = edgeIds[k];
                    if (this.edgeId < id) {
                        this.edgeId = id;
                    }
                    ++k;
                }
            }
        }
        if (edgeIds.length > 0) {
            this.edgeId += this.gap;
        }
    }

    @Override
    public int getSchemeId() {
        return this.schemeId;
    }

    @Override
    public int pullNextVertexId() {
        int id = this.vertexId;
        this.vertexId += this.gap;
        return id;
    }

    @Override
    public int takeNextComplementaryVertexId(int smallest) {
        int ghostId = smallest;
        while (ghostId % this.gap == this.start % this.gap || this.ghostVertexIds.contains(ghostId)) {
            ++ghostId;
        }
        this.ghostVertexIds.add(ghostId);
        return ghostId;
    }

    @Override
    public int takeNextEdgeId() {
        int id = this.edgeId;
        this.edgeId += this.gap;
        return id;
    }

    @Override
    public int grabNextComplementaryEdgeId(int smallest) {
        int ghostId = smallest;
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
        for (int c = 0; c < size; ++c) {
            ids[c] = this.start + c * this.gap;
        }
        return ids;
    }

    @Override
    public int[] getEdgeIds() {
        int size = (this.edgeId - this.start) / this.gap;
        int[] ids = new int[size];
        for (int k = 0; k < size; ++k) {
            new SparseIdFactoryGuide(ids, k).invoke();
        }
        return ids;
    }

    @Override
    public IdFactory copy() {
        SparseIdFactory factory = new SparseIdFactory(this.schemeId, this.start, this.gap);
        factory.edgeId = this.edgeId;
        factory.vertexId = this.vertexId;
        return factory;
    }

    private class SparseIdFactoryGuide {
        private int[] ids;
        private int p;

        public SparseIdFactoryGuide(int[] ids, int p) {
            this.ids = ids;
            this.p = p;
        }

        public void invoke() {
            this.ids[this.p] = SparseIdFactory.this.start + this.p * SparseIdFactory.this.gap;
        }
    }

}

