/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.actual;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.cybertip.note.ILoggerFactory;
import net.cybertip.note.Logger;
import net.cybertip.note.actual.SimpleLogger;
import net.cybertip.note.actual.SimpleLoggerBuilder;

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
        return this.takeLoggerAid(name);
    }

    private Logger takeLoggerAid(String name) {
        SimpleLogger newInstance = new SimpleLoggerBuilder().assignName(name).makeSimpleLogger();
        Logger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
        return oldInstance == null ? newInstance : oldInstance;
    }

    void reset() {
        this.loggerMap.clear();
    }
}

