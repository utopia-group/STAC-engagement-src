/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.actual;

import net.cybertip.note.actual.SimpleLogger;

public class SimpleLoggerBuilder {
    private String name;

    public SimpleLoggerBuilder assignName(String name) {
        this.name = name;
        return this;
    }

    public SimpleLogger makeSimpleLogger() {
        return new SimpleLogger(this.name);
    }
}

