/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.actual;

import net.cybertip.note.ILoggerFactory;
import net.cybertip.note.actual.SimpleLoggerFactory;
import net.cybertip.note.service.LoggerFactoryBinder;

public class StaticLoggerBinder
implements LoggerFactoryBinder {
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    public static String REQUESTED_API_VERSION = "1.6.99";
    private static final String loggerFactoryClassStr = SimpleLoggerFactory.class.getName();
    private final ILoggerFactory loggerFactory = new SimpleLoggerFactory();

    public static final StaticLoggerBinder obtainSingleton() {
        return SINGLETON;
    }

    private StaticLoggerBinder() {
    }

    @Override
    public ILoggerFactory fetchLoggerFactory() {
        return this.loggerFactory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return loggerFactoryClassStr;
    }
}

