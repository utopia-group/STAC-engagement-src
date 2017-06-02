/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import java.util.HashMap;
import java.util.Map;

public class WebSession {
    private final String memberId;
    private final Map<String, String> propertyMap = new HashMap<String, String>();

    public WebSession(String memberId) {
        this.memberId = memberId;
    }

    public String obtainMemberId() {
        return this.memberId;
    }

    public String pullProperty(String name) {
        return this.propertyMap.get(name);
    }

    public String obtainProperty(String name, String defaultReturn) {
        if (this.propertyMap.containsKey(name)) {
            return this.propertyMap.get(name);
        }
        return defaultReturn;
    }

    public void setProperty(String name, String value) {
        this.propertyMap.put(name, value);
    }
}

