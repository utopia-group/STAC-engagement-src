/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import net.techpoint.flightrouter.manager.CellFormatter;

public class CellFormatterBuilder {
    private int length;

    public CellFormatterBuilder assignLength(int length) {
        this.length = length;
        return this;
    }

    public CellFormatter formCellFormatter() {
        return new CellFormatter(this.length);
    }
}

