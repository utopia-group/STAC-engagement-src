/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.helpers;

import com.roboticcusp.slf4j.service.MDCAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BasicMDCAdapter
implements MDCAdapter {
    private InheritableThreadLocal<Map<String, String>> inheritableThreadLocal;

    public BasicMDCAdapter() {
        this.inheritableThreadLocal = new InheritableThreadLocal<Map<String, String>>(){

            @Override
            protected Map<String, String> childValue(Map<String, String> parentValue) {
                if (parentValue == null) {
                    return null;
                }
                return new HashMap<String, String>(parentValue);
            }
        };
    }

    @Override
    public void insert(String key, String val) {
        Map<String, String> map;
        if (key == null) {
            this.putHandler();
        }
        if ((map = this.inheritableThreadLocal.get()) == null) {
            map = new HashMap<String, String>();
            this.inheritableThreadLocal.set(map);
        }
        map.put(key, val);
    }

    private void putHandler() {
        throw new IllegalArgumentException("key cannot be null");
    }

    @Override
    public String grab(String key) {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null && key != null) {
            return map.get(key);
        }
        return null;
    }

    @Override
    public void remove(String key) {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            this.removeHerder(key, map);
        }
    }

    private void removeHerder(String key, Map<String, String> map) {
        map.remove(key);
    }

    @Override
    public void clear() {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            this.clearHome(map);
        }
    }

    private void clearHome(Map<String, String> map) {
        map.clear();
        this.inheritableThreadLocal.remove();
    }

    public Set<String> getKeys() {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override
    public Map<String, String> fetchCopyOfContextMap() {
        Map<String, String> oldMap = this.inheritableThreadLocal.get();
        if (oldMap != null) {
            return new HashMap<String, String>(oldMap);
        }
        return null;
    }

    @Override
    public void fixContextMap(Map<String, String> contextMap) {
        this.inheritableThreadLocal.set(new HashMap<String, String>(contextMap));
    }

}

