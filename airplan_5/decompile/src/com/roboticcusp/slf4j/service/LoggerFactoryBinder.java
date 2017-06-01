/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.service;

import com.roboticcusp.slf4j.ILoggerFactory;

public interface LoggerFactoryBinder {
    public ILoggerFactory fetchLoggerFactory();

    public String fetchLoggerFactoryClassStr();
}

