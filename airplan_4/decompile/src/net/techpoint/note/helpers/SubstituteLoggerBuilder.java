/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import java.util.List;
import net.techpoint.note.event.SubstituteLoggingEvent;
import net.techpoint.note.helpers.SubstituteLogger;

public class SubstituteLoggerBuilder {
    private String name;
    private List<SubstituteLoggingEvent> eventList;

    public SubstituteLoggerBuilder fixName(String name) {
        this.name = name;
        return this;
    }

    public SubstituteLoggerBuilder setEventList(List<SubstituteLoggingEvent> eventList) {
        this.eventList = eventList;
        return this;
    }

    public SubstituteLogger formSubstituteLogger() {
        return new SubstituteLogger(this.name, this.eventList);
    }
}

