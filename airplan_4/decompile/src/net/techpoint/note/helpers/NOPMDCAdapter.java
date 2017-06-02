/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import java.util.Map;
import net.techpoint.note.pack.MDCAdapter;

public class NOPMDCAdapter
implements MDCAdapter {
    @Override
    public void clear() {
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void put(String key, String val) {
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public Map<String, String> pullCopyOfContextMap() {
        return null;
    }

    @Override
    public void fixContextMap(Map<String, String> contextMap) {
    }
}

