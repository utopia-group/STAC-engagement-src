/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Vertex;

public class TraversalBasicDataSearcher {
    public Vertex search(Iterable<Vertex> iter, String key, String value) {
        for (Vertex v : iter) {
            if (!this.searchAdviser(key, value, v)) continue;
            return v;
        }
        return null;
    }

    private boolean searchAdviser(String key, String value, Vertex v) {
        if (v.hasData() && this.searchAdviserSupervisor(key, value, v)) {
            return true;
        }
        return false;
    }

    private boolean searchAdviserSupervisor(String key, String value, Vertex v) {
        Data data = v.getData();
        if (data instanceof BasicData && this.searchAdviserSupervisorSupervisor(key, value, (BasicData)data)) {
            return true;
        }
        return false;
    }

    private boolean searchAdviserSupervisorSupervisor(String key, String value, BasicData data) {
        if (new TraversalBasicDataSearcherGuide(key, value, data).invoke()) {
            return true;
        }
        return false;
    }

    private class TraversalBasicDataSearcherGuide {
        private boolean myResult;
        private String key;
        private String value;
        private BasicData data;

        public TraversalBasicDataSearcherGuide(String key, String value, BasicData data) {
            this.key = key;
            this.value = value;
            this.data = data;
        }

        boolean is() {
            return this.myResult;
        }

        public boolean invoke() {
            BasicData basicData = this.data;
            if (basicData.containsKey(this.key) && this.invokeHelp(basicData)) {
                return true;
            }
            return false;
        }

        private boolean invokeHelp(BasicData basicData) {
            String curValue = basicData.pull(this.key).trim();
            if (curValue.equals(this.value)) {
                return true;
            }
            return false;
        }
    }

}

