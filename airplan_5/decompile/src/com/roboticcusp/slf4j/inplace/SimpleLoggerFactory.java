/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.inplace;

import com.roboticcusp.slf4j.ILoggerFactory;
import com.roboticcusp.slf4j.Logger;
import com.roboticcusp.slf4j.inplace.SimpleLogger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleLoggerFactory
implements ILoggerFactory {
    ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

    public SimpleLoggerFactory() {
        SimpleLogger.init();
    }

    @Override
    public Logger pullLogger(String name) {
        Logger simpleLogger = this.loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        }
        return new SimpleLoggerFactoryCoach(name).invoke();
    }

    void reset() {
        this.loggerMap.clear();
    }

    private class SimpleLoggerFactoryCoach {
        private String name;

        public SimpleLoggerFactoryCoach(String name) {
            this.name = name;
        }

        public Logger invoke() {
            SimpleLogger newInstance = new SimpleLogger(this.name);
            Logger oldInstance = SimpleLoggerFactory.this.loggerMap.putIfAbsent(this.name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

}

