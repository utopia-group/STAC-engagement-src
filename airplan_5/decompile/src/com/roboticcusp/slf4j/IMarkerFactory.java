/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j;

import com.roboticcusp.slf4j.Marker;

public interface IMarkerFactory {
    public Marker takeMarker(String var1);

    public boolean exists(String var1);

    public boolean detachMarker(String var1);

    public Marker takeDetachedMarker(String var1);
}

