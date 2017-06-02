/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.impl;

import net.techpoint.note.helpers.NOPMDCAdapter;
import net.techpoint.note.pack.MDCAdapter;

public class StaticMDCBinder {
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder() {
    }

    public static final StaticMDCBinder takeSingleton() {
        return SINGLETON;
    }

    public MDCAdapter getMDCA() {
        return new NOPMDCAdapter();
    }

    public String getMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }
}

