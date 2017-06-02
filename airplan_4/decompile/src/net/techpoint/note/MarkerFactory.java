/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note;

import net.techpoint.note.IMarkerFactory;
import net.techpoint.note.Marker;
import net.techpoint.note.helpers.BasicMarkerFactory;
import net.techpoint.note.helpers.Util;
import net.techpoint.note.impl.StaticMarkerBinder;

public class MarkerFactory {
    static IMarkerFactory MARKER_FACTORY;

    private MarkerFactory() {
    }

    private static IMarkerFactory bwCompatibleGetMarkerFactoryFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMarkerBinder.takeSingleton().fetchMarkerFactory();
        }
        catch (NoSuchMethodError nsme) {
            return StaticMarkerBinder.SINGLETON.fetchMarkerFactory();
        }
    }

    public static Marker pullMarker(String name) {
        return MARKER_FACTORY.grabMarker(name);
    }

    public static Marker grabDetachedMarker(String name) {
        return MARKER_FACTORY.grabDetachedMarker(name);
    }

    public static IMarkerFactory obtainIMarkerFactory() {
        return MARKER_FACTORY;
    }

    static {
        try {
            MARKER_FACTORY = MarkerFactory.bwCompatibleGetMarkerFactoryFromBinder();
        }
        catch (NoClassDefFoundError e) {
            MARKER_FACTORY = new BasicMarkerFactory();
        }
        catch (Exception e) {
            Util.report("Unexpected failure while binding MarkerFactory", e);
        }
    }
}

