/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.cybertip.note.IMarkerFactory;
import net.cybertip.note.Marker;
import net.cybertip.note.helpers.BasicMarker;

public class BasicMarkerFactory
implements IMarkerFactory {
    private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<String, Marker>();

    @Override
    public Marker fetchMarker(String name) {
        Marker oldMarker;
        if (name == null) {
            throw new IllegalArgumentException("Marker name cannot be null");
        }
        Marker marker = this.markerMap.get(name);
        if (marker == null && (oldMarker = this.markerMap.putIfAbsent(name, marker = new BasicMarker(name))) != null) {
            marker = oldMarker;
        }
        return marker;
    }

    @Override
    public boolean exists(String name) {
        if (name == null) {
            return false;
        }
        return this.markerMap.containsKey(name);
    }

    @Override
    public boolean detachMarker(String name) {
        if (name == null) {
            return false;
        }
        return this.markerMap.remove(name) != null;
    }

    @Override
    public Marker fetchDetachedMarker(String name) {
        return new BasicMarker(name);
    }
}

