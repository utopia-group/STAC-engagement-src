/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import java.util.HashMap;
import java.util.Map;

public class ParameterOverseer {
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public void assign(String parameter, Object value) {
        this.parameters.put(parameter, value);
    }

    public Object obtain(String parameter) {
        if (this.parameters.containsKey(parameter)) {
            return this.grabUtility(parameter);
        }
        return false;
    }

    private Object grabUtility(String parameter) {
        Object val = this.parameters.get(parameter);
        return val;
    }
}

