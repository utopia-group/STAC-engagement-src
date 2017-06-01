/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.helpers;

import com.roboticcusp.slf4j.service.MDCAdapter;
import java.util.Map;

public class NOPMDCAdapter
implements MDCAdapter {
    @Override
    public void clear() {
    }

    @Override
    public String grab(String key) {
        return null;
    }

    @Override
    public void insert(String key, String val) {
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public Map<String, String> fetchCopyOfContextMap() {
        return null;
    }

    @Override
    public void fixContextMap(Map<String, String> contextMap) {
    }
}

