/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.event;

import com.roboticcusp.slf4j.Marker;
import com.roboticcusp.slf4j.event.Level;
import com.roboticcusp.slf4j.event.LoggingEvent;
import com.roboticcusp.slf4j.helpers.SubstituteLogger;

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
    public Level takeLevel() {
        return this.level;
    }

    public void defineLevel(Level level) {
        this.level = level;
    }

    @Override
    public Marker pullMarker() {
        return this.marker;
    }

    public void assignMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public String fetchLoggerName() {
        return this.loggerName;
    }

    public void fixLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public SubstituteLogger getLogger() {
        return this.logger;
    }

    public void assignLogger(SubstituteLogger logger) {
        this.logger = logger;
    }

    @Override
    public String pullMessage() {
        return this.message;
    }

    public void fixMessage(String message) {
        this.message = message;
    }

    @Override
    public Object[] getArgumentArray() {
        return this.argArray;
    }

    public void assignArgumentArray(Object[] argArray) {
        this.argArray = argArray;
    }

    @Override
    public long takeTimeStamp() {
        return this.timeStamp;
    }

    public void assignTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String obtainThreadName() {
        return this.threadName;
    }

    public void defineThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Throwable obtainThrowable() {
        return this.throwable;
    }

    public void defineThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}

