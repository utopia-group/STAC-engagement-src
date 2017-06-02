/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.techpoint.note.pack.MDCAdapter;

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
    public void put(String key, String val) {
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
    public String get(String key) {
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
            this.removeExecutor(key, map);
        }
    }

    private void removeExecutor(String key, Map<String, String> map) {
        map.remove(key);
    }

    @Override
    public void clear() {
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            new BasicMDCAdapterEngine(map).invoke();
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
    public Map<String, String> pullCopyOfContextMap() {
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

    private class BasicMDCAdapterEngine {
        private Map<String, String> map;

        public BasicMDCAdapterEngine(Map<String, String> map) {
            this.map = map;
        }

        public void invoke() {
            this.map.clear();
            BasicMDCAdapter.this.inheritableThreadLocal.remove();
        }
    }

}

