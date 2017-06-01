/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j;

import com.roboticcusp.slf4j.IMarkerFactory;
import com.roboticcusp.slf4j.Marker;
import com.roboticcusp.slf4j.helpers.BasicMarkerFactory;
import com.roboticcusp.slf4j.helpers.Util;
import com.roboticcusp.slf4j.inplace.StaticMarkerBinder;

public class MarkerFactory {
    static IMarkerFactory MARKER_FACTORY;

    MarkerFactory() {
    }

    private static IMarkerFactory bwCompatibleFetchMarkerFactoryFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMarkerBinder.obtainSingleton().pullMarkerFactory();
        }
        catch (NoSuchMethodError nsme) {
            return StaticMarkerBinder.SINGLETON.pullMarkerFactory();
        }
    }

    public static Marker fetchMarker(String name) {
        return MARKER_FACTORY.takeMarker(name);
    }

    public static Marker takeDetachedMarker(String name) {
        return MARKER_FACTORY.takeDetachedMarker(name);
    }

    public static IMarkerFactory getIMarkerFactory() {
        return MARKER_FACTORY;
    }

    static {
        try {
            MARKER_FACTORY = MarkerFactory.bwCompatibleFetchMarkerFactoryFromBinder();
        }
        catch (NoClassDefFoundError e) {
            MARKER_FACTORY = new BasicMarkerFactory();
        }
        catch (Exception e) {
            Util.report("Unexpected failure while binding MarkerFactory", e);
        }
    }
}

