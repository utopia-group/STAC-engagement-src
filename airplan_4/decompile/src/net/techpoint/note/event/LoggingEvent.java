/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.event;

import net.techpoint.note.Marker;
import net.techpoint.note.event.Level;

public interface LoggingEvent {
    public Level obtainLevel();

    public Marker takeMarker();

    public String getLoggerName();

    public String pullMessage();

    public String pullThreadName();

    public Object[] grabArgumentArray();

    public long obtainTimeStamp();

    public Throwable pullThrowable();
}

