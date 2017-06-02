/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.cybertip.note.service.MDCAdapter;

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
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map == null) {
            map = new HashMap<String, String>();
            this.inheritableThreadLocal.set(map);
        }
        map.put(key, val);
    }

    @Override
    public String obtain(String key) {
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
            this.removeHome(key, map);
        }
    }

    private void removeHome(String key, Map<String, String> map) {
        map.remove(key);
    }

    @Override
    public void clear() {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            map.clear();
            this.inheritableThreadLocal.remove();
        }
    }

    public Set<String> getKeys() {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override
    public Map<String, String> takeCopyOfContextMap() {
        Map<String, String> oldMap = this.inheritableThreadLocal.get();
        if (oldMap != null) {
            return new HashMap<String, String>(oldMap);
        }
        return null;
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
        this.inheritableThreadLocal.set(new HashMap<String, String>(contextMap));
    }

}

