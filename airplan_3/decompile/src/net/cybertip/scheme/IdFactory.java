/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import net.cybertip.scheme.AdjacencyListGraph;
import net.cybertip.scheme.Graph;

public abstract class IdFactory
implements Cloneable {
    public Graph newInstance() {
        return new AdjacencyListGraph(this);
    }

    public abstract int pullGraphId();

    public abstract int grabNextVertexId();

    public abstract int fetchNextComplementaryVertexId(int var1);

    public abstract int getNextEdgeId();

    public abstract int pullNextComplementaryEdgeId(int var1);

    public abstract int[] obtainVertexIds();

    public abstract int[] getEdgeIds();

    public abstract IdFactory copy();
}

