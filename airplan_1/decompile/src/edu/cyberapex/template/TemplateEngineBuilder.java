/*
 * Decompiled with CFR 0_121.
 */
package edu.cyberapex.template;

import edu.cyberapex.template.TemplateEngine;

public class TemplateEngineBuilder {
    private String endTag = "\\}\\}";
    private String startTag = "\\{\\{";
    private String text;

    public TemplateEngineBuilder setEndTag(String endTag) {
        this.endTag = endTag;
        return this;
    }

    public TemplateEngineBuilder fixStartTag(String startTag) {
        this.startTag = startTag;
        return this;
    }

    public TemplateEngineBuilder defineText(String text) {
        this.text = text;
        return this;
    }

    public TemplateEngine generateTemplateEngine() {
        return new TemplateEngine(this.startTag, this.endTag, this.text);
    }
}

