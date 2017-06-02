/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note;

import net.cybertip.note.Marker;

public interface IMarkerFactory {
    public Marker fetchMarker(String var1);

    public boolean exists(String var1);

    public boolean detachMarker(String var1);

    public Marker fetchDetachedMarker(String var1);
}

