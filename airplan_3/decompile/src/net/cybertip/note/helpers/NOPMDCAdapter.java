/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.util.Map;
import net.cybertip.note.service.MDCAdapter;

public class NOPMDCAdapter
implements MDCAdapter {
    @Override
    public void clear() {
    }

    @Override
    public String obtain(String key) {
        return null;
    }

    @Override
    public void insert(String key, String val) {
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public Map<String, String> takeCopyOfContextMap() {
        return null;
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
    }
}

