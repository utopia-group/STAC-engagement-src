/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import net.techpoint.graph.AdjacencyListScheme;
import net.techpoint.graph.DefaultIdFactory;
import net.techpoint.graph.IdFactory;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SparseIdFactory;

public class SchemeFactory {
    public static Scheme newInstance() {
        return new SparseIdFactory().newInstance();
    }

    public static Scheme newInstance(String name) {
        return SchemeFactory.newInstance(new DefaultIdFactory(), name);
    }

    public static Scheme newInstance(IdFactory idFactory, String name) {
        return new AdjacencyListScheme(idFactory, name);
    }
}

