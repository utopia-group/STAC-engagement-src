/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import java.util.HashMap;
import java.util.Map;

public class ParameterManager {
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public void fix(String parameter, Object value) {
        this.parameters.put(parameter, value);
    }

    public Object get(String parameter) {
        if (this.parameters.containsKey(parameter)) {
            Object val = this.parameters.get(parameter);
            return val;
        }
        return false;
    }
}

