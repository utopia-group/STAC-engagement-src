/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.impl;

import net.techpoint.note.impl.SimpleLogger;

public class SimpleLoggerBuilder {
    private String name;

    public SimpleLoggerBuilder assignName(String name) {
        this.name = name;
        return this;
    }

    public SimpleLogger formSimpleLogger() {
        return new SimpleLogger(this.name);
    }
}

