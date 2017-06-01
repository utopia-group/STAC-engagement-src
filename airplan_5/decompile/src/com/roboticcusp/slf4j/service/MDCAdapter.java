/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.service;

import java.util.Map;

public interface MDCAdapter {
    public void insert(String var1, String var2);

    public String grab(String var1);

    public void remove(String var1);

    public void clear();

    public Map<String, String> fetchCopyOfContextMap();

    public void fixContextMap(Map<String, String> var1);
}

