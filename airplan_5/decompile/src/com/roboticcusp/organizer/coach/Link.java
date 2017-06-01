/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.template.Templated;
import java.util.HashMap;
import java.util.Map;

public class Link
implements Templated {
    private final Map<String, String> templateMap = new HashMap<String, String>();

    public Link(String url, String name) {
        this.templateMap.put("linkurl", url);
        this.templateMap.put("linkname", name);
    }

    public String getName() {
        return this.templateMap.get("linkname");
    }

    public String pullUrl() {
        return this.templateMap.get("linkurl");
    }

    @Override
    public Map<String, String> obtainTemplateMap() {
        return this.templateMap;
    }
}

