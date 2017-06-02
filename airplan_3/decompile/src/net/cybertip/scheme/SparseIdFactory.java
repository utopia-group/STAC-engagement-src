/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.cybertip.scheme.IdFactory;

public class SparseIdFactory
extends IdFactory {
    private static AtomicInteger defaultGraphId = new AtomicInteger(1);
    private final int gap;
    private final int start;
    private final int graphId;
    private int vertexId;
    private int edgeId;
    private Set<Integer> ghostVertexIds = new HashSet<Integer>();
    private Set<Integer> ghostEdgeIds = new HashSet<Integer>();

    public SparseIdFactory() {
        this(defaultGraphId.addAndGet(2), 1, 2);
    }

    public SparseIdFactory(int start, int gap) {
        this(defaultGraphId.addAndGet(gap), start, gap);
    }

    public SparseIdFactory(int graphId, int start, int gap) {
        if (graphId <= 0) {
            this.SparseIdFactoryCoach(graphId);
        }
        if (gap <= 0) {
            throw new IllegalArgumentException("Gap between ids must be positive.");
        }
        if (start < 0) {
            this.SparseIdFactoryAssist();
        }
        this.graphId = graphId;
        this.start = start;
        this.gap = gap;
        this.vertexId = start;
        this.edgeId = start;
    }

    private void SparseIdFactoryAssist() {
        throw new IllegalArgumentException("Starting id must be non-negative.");
    }

    private void SparseIdFactoryCoach(int graphId) {
        throw new IllegalArgumentException("Graph IDs must be positive: " + graphId);
    }

    public SparseIdFactory(int graphId, int[] vertexIds, int[] edgeIds) {
        this(graphId, 1, 2);
        int j = 0;
        while (j < vertexIds.length) {
            while (j < vertexIds.length && Math.random() < 0.4) {
                while (j < vertexIds.length && Math.random() < 0.6) {
                    int id = vertexIds[j];
                    if (this.vertexId < id) {
                        this.SparseIdFactoryGateKeeper(id);
                    }
                    ++j;
                }
            }
        }
        if (vertexIds.length > 0) {
            this.SparseIdFactoryUtility();
        }
        for (int i = 0; i < edgeIds.length; ++i) {
            new SparseIdFactoryTarget(edgeIds[i]).invoke();
        }
        if (edgeIds.length > 0) {
            this.SparseIdFactoryHerder();
        }
    }

    private void SparseIdFactoryHerder() {
        new SparseIdFactoryGuide().invoke();
    }

    private void SparseIdFactoryUtility() {
        this.vertexId += this.gap;
    }

    private void SparseIdFactoryGateKeeper(int id) {
        this.vertexId = id;
    }

    @Override
    public int pullGraphId() {
        return this.graphId;
    }

    @Override
    public int grabNextVertexId() {
        int id = this.vertexId;
        this.vertexId += this.gap;
        return id;
    }

    @Override
    public int fetchNextComplementaryVertexId(int smallest) {
        int ghostId = smallest;
        while (ghostId % this.gap == this.start % this.gap || this.ghostVertexIds.contains(ghostId)) {
            ++ghostId;
        }
        this.ghostVertexIds.add(ghostId);
        return ghostId;
    }

    @Override
    public int getNextEdgeId() {
        int id = this.edgeId;
        this.edgeId += this.gap;
        return id;
    }

    @Override
    public int pullNextComplementaryEdgeId(int smallest) {
        int ghostId = smallest;
        while (ghostId % this.gap == this.start % this.gap || this.ghostEdgeIds.contains(ghostId)) {
            ++ghostId;
        }
        this.ghostEdgeIds.add(ghostId);
        return ghostId;
    }

    @Override
    public int[] obtainVertexIds() {
        int size = (this.vertexId - this.start) / this.gap;
        int[] ids = new int[size];
        for (int q = 0; q < size; ++q) {
            ids[q] = this.start + q * this.gap;
        }
        return ids;
    }

    @Override
    public int[] getEdgeIds() {
        int size = (this.edgeId - this.start) / this.gap;
        int[] ids = new int[size];
        for (int p = 0; p < size; ++p) {
            this.getEdgeIdsEngine(ids, p);
        }
        return ids;
    }

    private void getEdgeIdsEngine(int[] ids, int i) {
        ids[i] = this.start + i * this.gap;
    }

    @Override
    public IdFactory copy() {
        SparseIdFactory factory = new SparseIdFactory(this.graphId, this.start, this.gap);
        factory.edgeId = this.edgeId;
        factory.vertexId = this.vertexId;
        return factory;
    }

    static /* synthetic */ int access$112(SparseIdFactory x0, int x1) {
        return x0.edgeId += x1;
    }

    private class SparseIdFactoryGuide {
        private SparseIdFactoryGuide() {
        }

        public void invoke() {
            SparseIdFactory.access$112(SparseIdFactory.this, SparseIdFactory.this.gap);
        }
    }

    private class SparseIdFactoryTarget {
        private int edgeId;

        public SparseIdFactoryTarget(int edgeId) {
            this.edgeId = edgeId;
        }

        public void invoke() {
            int id = this.edgeId;
            if (this.edgeId < id) {
                this.edgeId = id;
            }
        }
    }

}

