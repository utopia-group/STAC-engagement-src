/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.event;

import net.techpoint.note.Marker;
import net.techpoint.note.event.Level;
import net.techpoint.note.event.LoggingEvent;
import net.techpoint.note.helpers.SubstituteLogger;

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
    public Level obtainLevel() {
        return this.level;
    }

    public void assignLevel(Level level) {
        this.level = level;
    }

    @Override
    public Marker takeMarker() {
        return this.marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public String getLoggerName() {
        return this.loggerName;
    }

    public void fixLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public SubstituteLogger takeLogger() {
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
    public long obtainTimeStamp() {
        return this.timeStamp;
    }

    public void fixTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String pullThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Throwable pullThrowable() {
        return this.throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}

