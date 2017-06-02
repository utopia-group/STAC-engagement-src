/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import net.cybertip.scheme.AdjacencyListGraph;
import net.cybertip.scheme.DefaultIdFactory;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.IdFactory;
import net.cybertip.scheme.SparseIdFactory;

public class GraphFactory {
    public static Graph newInstance() {
        return new SparseIdFactory().newInstance();
    }

    public static Graph newInstance(String name) {
        return GraphFactory.newInstance(new DefaultIdFactory(), name);
    }

    public static Graph newInstance(IdFactory idFactory, String name) {
        return new AdjacencyListGraph(idFactory, name);
    }
}

