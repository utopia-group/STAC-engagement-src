/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.techpoint.note.ILoggerFactory;
import net.techpoint.note.Logger;
import net.techpoint.note.event.SubstituteLoggingEvent;
import net.techpoint.note.helpers.SubstituteLogger;
import net.techpoint.note.helpers.SubstituteLoggerBuilder;

public class SubstituteLoggerFactory
implements ILoggerFactory {
    final ConcurrentMap<String, SubstituteLogger> loggers = new ConcurrentHashMap<String, SubstituteLogger>();
    final List<SubstituteLoggingEvent> eventList = Collections.synchronizedList(new ArrayList());

    @Override
    public Logger takeLogger(String name) {
        SubstituteLogger oldLogger;
        SubstituteLogger logger = this.loggers.get(name);
        if (logger == null && (oldLogger = this.loggers.putIfAbsent(name, logger = new SubstituteLoggerBuilder().fixName(name).setEventList(this.eventList).formSubstituteLogger())) != null) {
            logger = oldLogger;
        }
        return logger;
    }

    public List<String> takeLoggerNames() {
        return new ArrayList<String>(this.loggers.keySet());
    }

    public List<SubstituteLogger> pullLoggers() {
        return new ArrayList<SubstituteLogger>(this.loggers.values());
    }

    public List<SubstituteLoggingEvent> getEventList() {
        return this.eventList;
    }

    public void clear() {
        this.loggers.clear();
        this.eventList.clear();
    }
}

