/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.techpoint.note.ILoggerFactory;
import net.techpoint.note.Logger;
import net.techpoint.note.impl.SimpleLogger;
import net.techpoint.note.impl.SimpleLoggerBuilder;

public class SimpleLoggerFactory
implements ILoggerFactory {
    ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

    public SimpleLoggerFactory() {
        SimpleLogger.init();
    }

    @Override
    public Logger takeLogger(String name) {
        Logger simpleLogger = this.loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        }
        SimpleLogger newInstance = new SimpleLoggerBuilder().assignName(name).formSimpleLogger();
        Logger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
        return oldInstance == null ? newInstance : oldInstance;
    }

    void reset() {
        this.loggerMap.clear();
    }
}

