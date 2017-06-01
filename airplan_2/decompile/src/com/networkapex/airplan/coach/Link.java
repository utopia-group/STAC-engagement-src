/*
 * Decompiled with CFR 0_121.
 */
package com.networkapex.airplan.coach;

import com.networkapex.template.Templated;
import java.util.HashMap;
import java.util.Map;

public class Link
implements Templated {
    private final Map<String, String> templateMap = new HashMap<String, String>();

    public Link(String url, String name) {
        this.templateMap.put("linkurl", url);
        this.templateMap.put("linkname", name);
    }

    public String pullName() {
        return this.templateMap.get("linkname");
    }

    public String grabUrl() {
        return this.templateMap.get("linkurl");
    }

    @Override
    public Map<String, String> pullTemplateMap() {
        return this.templateMap;
    }
}
