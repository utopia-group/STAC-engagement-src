/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note;

import net.cybertip.note.IMarkerFactory;
import net.cybertip.note.Marker;
import net.cybertip.note.actual.StaticMarkerBinder;
import net.cybertip.note.helpers.BasicMarkerFactory;
import net.cybertip.note.helpers.BasicMarkerFactoryBuilder;
import net.cybertip.note.helpers.Util;

public class MarkerFactory {
    static IMarkerFactory MARKER_FACTORY;

    private MarkerFactory() {
    }

    private static IMarkerFactory bwCompatibleObtainMarkerFactoryFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMarkerBinder.pullSingleton().getMarkerFactory();
        }
        catch (NoSuchMethodError nsme) {
            return StaticMarkerBinder.SINGLETON.getMarkerFactory();
        }
    }

    public static Marker fetchMarker(String name) {
        return MARKER_FACTORY.fetchMarker(name);
    }

    public static Marker pullDetachedMarker(String name) {
        return MARKER_FACTORY.fetchDetachedMarker(name);
    }

    public static IMarkerFactory getIMarkerFactory() {
        return MARKER_FACTORY;
    }

    static {
        try {
            MARKER_FACTORY = MarkerFactory.bwCompatibleObtainMarkerFactoryFromBinder();
        }
        catch (NoClassDefFoundError e) {
            MARKER_FACTORY = new BasicMarkerFactoryBuilder().makeBasicMarkerFactory();
        }
        catch (Exception e) {
            Util.report("Unexpected failure while binding MarkerFactory", e);
        }
    }
}

