/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.cybertip.note.ILoggerFactory;
import net.cybertip.note.Logger;
import net.cybertip.note.event.SubstituteLoggingEvent;
import net.cybertip.note.helpers.SubstituteLogger;

public class SubstituteLoggerFactory
implements ILoggerFactory {
    final ConcurrentMap<String, SubstituteLogger> loggers = new ConcurrentHashMap<String, SubstituteLogger>();
    final List<SubstituteLoggingEvent> eventList = Collections.synchronizedList(new ArrayList());

    @Override
    public Logger takeLogger(String name) {
        SubstituteLogger oldLogger;
        SubstituteLogger logger = this.loggers.get(name);
        if (logger == null && (oldLogger = this.loggers.putIfAbsent(name, logger = new SubstituteLogger(name, this.eventList))) != null) {
            logger = oldLogger;
        }
        return logger;
    }

    public List<String> getLoggerNames() {
        return new ArrayList<String>(this.loggers.keySet());
    }

    public List<SubstituteLogger> pullLoggers() {
        return new ArrayList<SubstituteLogger>(this.loggers.values());
    }

    public List<SubstituteLoggingEvent> takeEventList() {
        return this.eventList;
    }

    public void clear() {
        this.loggers.clear();
        this.eventList.clear();
    }
}

