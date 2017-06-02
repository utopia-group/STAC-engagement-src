/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import net.techpoint.note.ILoggerFactory;
import net.techpoint.note.Logger;
import net.techpoint.note.helpers.NOPLogger;

public class NOPLoggerFactory
implements ILoggerFactory {
    @Override
    public Logger takeLogger(String name) {
        return NOPLogger.NOP_LOGGER;
    }
}

