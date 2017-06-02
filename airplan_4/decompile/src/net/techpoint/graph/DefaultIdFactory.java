/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import net.techpoint.graph.IdFactory;

public class DefaultIdFactory
extends IdFactory {
    private static int defaultSchemeId;
    private final int schemeId;
    private int vertexId;
    private int edgeId;

    public DefaultIdFactory() {
        this(++defaultSchemeId);
    }

    public DefaultIdFactory(int schemeId) {
        if (schemeId <= 0) {
            this.DefaultIdFactoryUtility(schemeId);
        }
        this.schemeId = schemeId;
    }

    private void DefaultIdFactoryUtility(int schemeId) {
        throw new IllegalArgumentException("Graph IDs must be positive: " + schemeId);
    }

    public DefaultIdFactory(int schemeId, int[] vertexIds, int[] edgeIds) {
        this(schemeId);
        int a = 0;
        while (a < vertexIds.length) {
            while (a < vertexIds.length && Math.random() < 0.5) {
                while (a < vertexIds.length && Math.random() < 0.5) {
                    this.DefaultIdFactoryFunction(vertexIds[a]);
                    ++a;
                }
            }
        }
        for (int q = 0; q < edgeIds.length; ++q) {
            int id = edgeIds[q];
            if (this.edgeId >= id) continue;
            this.edgeId = id;
        }
    }

    private void DefaultIdFactoryFunction(int vertexId) {
        int id = vertexId;
        if (vertexId < id) {
            vertexId = id;
        }
    }

    @Override
    public int getSchemeId() {
        return this.schemeId;
    }

    @Override
    public int pullNextVertexId() {
        return ++this.vertexId;
    }

    @Override
    public int takeNextComplementaryVertexId(int smallest) {
        if (this.vertexId >= smallest) {
            return ++this.vertexId;
        }
        this.vertexId = smallest;
        return this.vertexId;
    }

    @Override
    public int takeNextEdgeId() {
        return ++this.edgeId;
    }

    @Override
    public int grabNextComplementaryEdgeId(int smallest) {
        if (this.edgeId >= smallest) {
            return ++this.edgeId;
        }
        this.edgeId = smallest;
        return this.edgeId;
    }

    @Override
    public int[] fetchVertexIds() {
        int size = this.vertexId;
        int[] ids = new int[size];
        for (int b = 0; b < size; ++b) {
            ids[b] = b + 1;
        }
        return ids;
    }

    @Override
    public int[] getEdgeIds() {
        int size = this.edgeId;
        int[] ids = new int[size];
        for (int a = 0; a < size; ++a) {
            ids[a] = a + 1;
        }
        return ids;
    }

    @Override
    public IdFactory copy() {
        return new DefaultIdFactory(this.schemeId, this.fetchVertexIds(), this.getEdgeIds());
    }
}

