/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.pack;

import java.util.Map;

public interface MDCAdapter {
    public void put(String var1, String var2);

    public String get(String var1);

    public void remove(String var1);

    public void clear();

    public Map<String, String> pullCopyOfContextMap();

    public void fixContextMap(Map<String, String> var1);
}

