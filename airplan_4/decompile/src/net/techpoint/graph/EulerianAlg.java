/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import net.techpoint.graph.ConnectedAlg;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;

public class EulerianAlg {
    public static boolean isEulerian(Scheme scheme) throws SchemeFailure {
        ConnectedAlg ca = new ConnectedAlg();
        return ConnectedAlg.isConnected(scheme) && !scheme.hasOddDegree();
    }
}

