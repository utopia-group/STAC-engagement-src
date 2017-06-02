/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import net.cybertip.note.ILoggerFactory;
import net.cybertip.note.Logger;
import net.cybertip.note.helpers.NOPLogger;

public class NOPLoggerFactory
implements ILoggerFactory {
    @Override
    public Logger takeLogger(String name) {
        return NOPLogger.NOP_LOGGER;
    }
}

