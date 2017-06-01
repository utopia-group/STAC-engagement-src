/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ConnectedAlg;

public class EulerianAlg {
    public static boolean isEulerian(Chart graph) throws ChartException {
        ConnectedAlg ca = new ConnectedAlg();
        return ConnectedAlg.isConnected(graph) && !graph.hasOddDegree();
    }
}

