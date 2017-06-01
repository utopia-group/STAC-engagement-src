/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.inplace;

import com.roboticcusp.slf4j.ILoggerFactory;
import com.roboticcusp.slf4j.inplace.SimpleLoggerFactory;
import com.roboticcusp.slf4j.service.LoggerFactoryBinder;

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
    public String fetchLoggerFactoryClassStr() {
        return loggerFactoryClassStr;
    }
}

