/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.AdjacencyListChart;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.DefaultIdFactory;
import com.roboticcusp.mapping.IdFactory;
import com.roboticcusp.mapping.SparseIdFactory;

public class ChartFactory {
    public static Chart newInstance() {
        return ChartFactory.newInstance(new SparseIdFactory());
    }

    public static Chart newInstance(String name) {
        return new DefaultIdFactory().newInstance(name);
    }

    public static Chart newInstance(IdFactory idFactory) {
        return new AdjacencyListChart(idFactory);
    }
}

