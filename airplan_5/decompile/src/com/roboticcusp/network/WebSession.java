/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network;

import java.util.HashMap;
import java.util.Map;

public class WebSession {
    private final String participantId;
    private final Map<String, String> propertyMap = new HashMap<String, String>();

    public WebSession(String participantId) {
        this.participantId = participantId;
    }

    public String grabParticipantId() {
        return this.participantId;
    }

    public String obtainProperty(String name) {
        return this.propertyMap.get(name);
    }

    public String pullProperty(String name, String defaultReturn) {
        if (this.propertyMap.containsKey(name)) {
            return this.propertyMap.get(name);
        }
        return defaultReturn;
    }

    public void setProperty(String name, String value) {
        this.propertyMap.put(name, value);
    }
}

