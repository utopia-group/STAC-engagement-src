/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import java.io.FileNotFoundException;
import java.util.List;

public interface ChartFileLoader {
    public Chart loadChart(String var1) throws FileNotFoundException, ChartException;

    public List<String> obtainExtensions();
}

