/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import net.techpoint.graph.BasicData;
import net.techpoint.graph.Data;
import net.techpoint.graph.Vertex;

public class TraversalBasicDataSearcher {
    public Vertex search(Iterable<Vertex> iter, String key, String value) {
        for (Vertex v : iter) {
            if (!this.searchAdviser(key, value, v)) continue;
            return v;
        }
        return null;
    }

    private boolean searchAdviser(String key, String value, Vertex v) {
        if (v.hasData() && this.searchAdviserFunction(key, value, v)) {
            return true;
        }
        return false;
    }

    private boolean searchAdviserFunction(String key, String value, Vertex v) {
        String curValue;
        BasicData basicData;
        Data data = v.getData();
        if (data instanceof BasicData && (basicData = (BasicData)data).containsKey(key) && (curValue = basicData.obtain(key).trim()).equals(value)) {
            return true;
        }
        return false;
    }
}

