/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.event;

import net.cybertip.note.Marker;
import net.cybertip.note.event.Level;
import net.cybertip.note.event.LoggingEvent;
import net.cybertip.note.helpers.SubstituteLogger;

public class SubstituteLoggingEvent
implements LoggingEvent {
    Level level;
    Marker marker;
    String loggerName;
    SubstituteLogger logger;
    String threadName;
    String message;
    Object[] argArray;
    long timeStamp;
    Throwable throwable;

    @Override
    public Level grabLevel() {
        return this.level;
    }

    public void assignLevel(Level level) {
        this.level = level;
    }

    @Override
    public Marker fetchMarker() {
        return this.marker;
    }

    public void fixMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public String takeLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public SubstituteLogger getLogger() {
        return this.logger;
    }

    public void fixLogger(SubstituteLogger logger) {
        this.logger = logger;
    }

    @Override
    public String pullMessage() {
        return this.message;
    }

    public void assignMessage(String message) {
        this.message = message;
    }

    @Override
    public Object[] grabArgumentArray() {
        return this.argArray;
    }

    public void fixArgumentArray(Object[] argArray) {
        this.argArray = argArray;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void defineTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String takeThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Throwable fetchThrowable() {
        return this.throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}

