/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note;

import net.techpoint.note.Marker;

public interface IMarkerFactory {
    public Marker grabMarker(String var1);

    public boolean exists(String var1);

    public boolean detachMarker(String var1);

    public Marker grabDetachedMarker(String var1);
}

