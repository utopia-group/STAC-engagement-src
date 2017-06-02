/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import net.cybertip.scheme.IdFactory;

public class DefaultIdFactory
extends IdFactory {
    private static int defaultGraphId;
    private final int graphId;
    private int vertexId;
    private int edgeId;

    public DefaultIdFactory() {
        this(++defaultGraphId);
    }

    public DefaultIdFactory(int graphId) {
        if (graphId <= 0) {
            this.DefaultIdFactoryEngine(graphId);
        }
        this.graphId = graphId;
    }

    private void DefaultIdFactoryEngine(int graphId) {
        new DefaultIdFactoryEntity(graphId).invoke();
    }

    public DefaultIdFactory(int graphId, int[] vertexIds, int[] edgeIds) {
        this(graphId);
        for (int q = 0; q < vertexIds.length; ++q) {
            this.DefaultIdFactoryService(vertexIds[q]);
        }
        int c = 0;
        while (c < edgeIds.length) {
            while (c < edgeIds.length && Math.random() < 0.4) {
                while (c < edgeIds.length && Math.random() < 0.6) {
                    while (c < edgeIds.length && Math.random() < 0.4) {
                        int id = edgeIds[c];
                        if (this.edgeId < id) {
                            this.DefaultIdFactoryEntity(id);
                        }
                        ++c;
                    }
                }
            }
        }
    }

    private void DefaultIdFactoryEntity(int id) {
        this.edgeId = id;
    }

    private void DefaultIdFactoryService(int vertexId) {
        int id = vertexId;
        if (vertexId < id) {
            vertexId = id;
        }
    }

    @Override
    public int pullGraphId() {
        return this.graphId;
    }

    @Override
    public int grabNextVertexId() {
        return ++this.vertexId;
    }

    @Override
    public int fetchNextComplementaryVertexId(int smallest) {
        if (this.vertexId >= smallest) {
            return ++this.vertexId;
        }
        return this.fetchNextComplementaryVertexIdCoach(smallest);
    }

    private int fetchNextComplementaryVertexIdCoach(int smallest) {
        this.vertexId = smallest;
        return this.vertexId;
    }

    @Override
    public int getNextEdgeId() {
        return ++this.edgeId;
    }

    @Override
    public int pullNextComplementaryEdgeId(int smallest) {
        if (this.edgeId >= smallest) {
            return ++this.edgeId;
        }
        return this.pullNextComplementaryEdgeIdAdviser(smallest);
    }

    private int pullNextComplementaryEdgeIdAdviser(int smallest) {
        this.edgeId = smallest;
        return this.edgeId;
    }

    @Override
    public int[] obtainVertexIds() {
        int size = this.vertexId;
        int[] ids = new int[size];
        for (int b = 0; b < size; ++b) {
            this.obtainVertexIdsEngine(ids, b);
        }
        return ids;
    }

    private void obtainVertexIdsEngine(int[] ids, int j) {
        ids[j] = j + 1;
    }

    @Override
    public int[] getEdgeIds() {
        int size = this.edgeId;
        int[] ids = new int[size];
        for (int b = 0; b < size; ++b) {
            this.grabEdgeIdsUtility(ids, b);
        }
        return ids;
    }

    private void grabEdgeIdsUtility(int[] ids, int a) {
        ids[a] = a + 1;
    }

    @Override
    public IdFactory copy() {
        return new DefaultIdFactory(this.graphId, this.obtainVertexIds(), this.getEdgeIds());
    }

    private class DefaultIdFactoryEntity {
        private int graphId;

        public DefaultIdFactoryEntity(int graphId) {
            this.graphId = graphId;
        }

        public void invoke() {
            throw new IllegalArgumentException("Graph IDs must be positive: " + this.graphId);
        }
    }

}

