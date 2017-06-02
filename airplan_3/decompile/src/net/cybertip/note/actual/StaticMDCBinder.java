/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.actual;

import net.cybertip.note.helpers.NOPMDCAdapter;
import net.cybertip.note.service.MDCAdapter;

public class StaticMDCBinder {
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder() {
    }

    public static final StaticMDCBinder obtainSingleton() {
        return SINGLETON;
    }

    public MDCAdapter grabMDCA() {
        return new NOPMDCAdapter();
    }

    public String obtainMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }
}

