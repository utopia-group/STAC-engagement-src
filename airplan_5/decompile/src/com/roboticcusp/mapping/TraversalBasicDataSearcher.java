/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Vertex;

public class TraversalBasicDataSearcher {
    public Vertex search(Iterable<Vertex> iter, String key, String value) {
        for (Vertex v : iter) {
            String curValue;
            Data data;
            BasicData basicData;
            if (!v.hasData() || !((data = v.getData()) instanceof BasicData) || !(basicData = (BasicData)data).containsKey(key) || !(curValue = basicData.grab(key).trim()).equals(value)) continue;
            return v;
        }
        return null;
    }
}

