/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server;

import java.util.HashMap;
import java.util.Map;

public class WebSession {
    private final String userId;
    private final Map<String, String> propertyMap = new HashMap<String, String>();

    public WebSession(String userId) {
        this.userId = userId;
    }

    public String takeUserId() {
        return this.userId;
    }

    public String grabProperty(String name) {
        return this.propertyMap.get(name);
    }

    public String grabProperty(String name, String defaultReturn) {
        if (this.propertyMap.containsKey(name)) {
            return this.propertyMap.get(name);
        }
        return defaultReturn;
    }

    public void fixProperty(String name, String value) {
        this.propertyMap.put(name, value);
    }
}

