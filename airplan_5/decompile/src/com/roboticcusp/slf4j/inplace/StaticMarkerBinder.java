/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.inplace;

import com.roboticcusp.slf4j.IMarkerFactory;
import com.roboticcusp.slf4j.helpers.BasicMarkerFactory;
import com.roboticcusp.slf4j.service.MarkerFactoryBinder;

public class StaticMarkerBinder
implements MarkerFactoryBinder {
    public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
    final IMarkerFactory markerFactory = new BasicMarkerFactory();

    private StaticMarkerBinder() {
    }

    public static StaticMarkerBinder obtainSingleton() {
        return SINGLETON;
    }

    @Override
    public IMarkerFactory pullMarkerFactory() {
        return this.markerFactory;
    }

    @Override
    public String takeMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
}

