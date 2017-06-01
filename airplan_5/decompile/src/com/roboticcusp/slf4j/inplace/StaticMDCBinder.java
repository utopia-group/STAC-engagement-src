/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.inplace;

import com.roboticcusp.slf4j.helpers.NOPMDCAdapter;
import com.roboticcusp.slf4j.inplace.StaticMDCBinderBuilder;
import com.roboticcusp.slf4j.service.MDCAdapter;

public class StaticMDCBinder {
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinderBuilder().composeStaticMDCBinder();

    StaticMDCBinder() {
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

