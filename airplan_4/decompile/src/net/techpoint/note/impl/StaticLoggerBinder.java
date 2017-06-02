/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.impl;

import net.techpoint.note.ILoggerFactory;
import net.techpoint.note.impl.SimpleLoggerFactory;
import net.techpoint.note.pack.LoggerFactoryBinder;

public class StaticLoggerBinder
implements LoggerFactoryBinder {
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    public static String REQUESTED_API_VERSION = "1.6.99";
    private static final String loggerFactoryClassStr = SimpleLoggerFactory.class.getName();
    private final ILoggerFactory loggerFactory = new SimpleLoggerFactory();

    public static final StaticLoggerBinder pullSingleton() {
        return SINGLETON;
    }

    private StaticLoggerBinder() {
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    @Override
    public String fetchLoggerFactoryClassStr() {
        return loggerFactoryClassStr;
    }
}

