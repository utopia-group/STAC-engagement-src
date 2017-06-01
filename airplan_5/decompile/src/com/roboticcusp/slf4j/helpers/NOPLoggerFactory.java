/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.helpers;

import com.roboticcusp.slf4j.ILoggerFactory;
import com.roboticcusp.slf4j.Logger;
import com.roboticcusp.slf4j.helpers.NOPLogger;

public class NOPLoggerFactory
implements ILoggerFactory {
    @Override
    public Logger pullLogger(String name) {
        return NOPLogger.NOP_LOGGER;
    }
}

