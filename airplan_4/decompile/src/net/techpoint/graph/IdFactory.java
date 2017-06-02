/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import net.techpoint.graph.AdjacencyListScheme;
import net.techpoint.graph.Scheme;

public abstract class IdFactory
implements Cloneable {
    public Scheme newInstance() {
        return new AdjacencyListScheme(this);
    }

    public abstract int getSchemeId();

    public abstract int pullNextVertexId();

    public abstract int takeNextComplementaryVertexId(int var1);

    public abstract int takeNextEdgeId();

    public abstract int grabNextComplementaryEdgeId(int var1);

    public abstract int[] fetchVertexIds();

    public abstract int[] getEdgeIds();

    public abstract IdFactory copy();
}

