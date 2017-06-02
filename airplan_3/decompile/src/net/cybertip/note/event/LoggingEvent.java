/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.event;

import net.cybertip.note.Marker;
import net.cybertip.note.event.Level;

public interface LoggingEvent {
    public Level grabLevel();

    public Marker fetchMarker();

    public String takeLoggerName();

    public String pullMessage();

    public String takeThreadName();

    public Object[] grabArgumentArray();

    public long getTimeStamp();

    public Throwable fetchThrowable();
}

