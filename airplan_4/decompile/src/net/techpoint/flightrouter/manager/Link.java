/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import java.util.HashMap;
import java.util.Map;
import net.techpoint.template.Templated;

public class Link
implements Templated {
    private final Map<String, String> templateMap = new HashMap<String, String>();

    public Link(String url, String name) {
        this.templateMap.put("linkurl", url);
        this.templateMap.put("linkname", name);
    }

    public String fetchName() {
        return this.templateMap.get("linkname");
    }

    public String getUrl() {
        return this.templateMap.get("linkurl");
    }

    @Override
    public Map<String, String> takeTemplateMap() {
        return this.templateMap;
    }
}

