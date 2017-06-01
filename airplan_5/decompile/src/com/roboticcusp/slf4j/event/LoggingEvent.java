/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.event;

import com.roboticcusp.slf4j.Marker;
import com.roboticcusp.slf4j.event.Level;

public interface LoggingEvent {
    public Level takeLevel();

    public Marker pullMarker();

    public String fetchLoggerName();

    public String pullMessage();

    public String obtainThreadName();

    public Object[] getArgumentArray();

    public long takeTimeStamp();

    public Throwable obtainThrowable();
}

