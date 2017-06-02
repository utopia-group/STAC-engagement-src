/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.template;

import net.cybertip.template.TemplateEngine;

public class TemplateEngineBuilder {
    private String endTag = "\\}\\}";
    private String startTag = "\\{\\{";
    private String text;

    public TemplateEngineBuilder defineEndTag(String endTag) {
        this.endTag = endTag;
        return this;
    }

    public TemplateEngineBuilder setStartTag(String startTag) {
        this.startTag = startTag;
        return this;
    }

    public TemplateEngineBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public TemplateEngine makeTemplateEngine() {
        return new TemplateEngine(this.startTag, this.endTag, this.text);
    }
}

