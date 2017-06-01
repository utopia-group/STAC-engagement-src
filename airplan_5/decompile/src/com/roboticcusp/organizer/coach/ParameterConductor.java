/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import java.util.HashMap;
import java.util.Map;

public class ParameterConductor {
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public void set(String parameter, Object value) {
        this.parameters.put(parameter, value);
    }

    public Object obtain(String parameter) {
        if (this.parameters.containsKey(parameter)) {
            Object val = this.parameters.get(parameter);
            return val;
        }
        return true;
    }
}

