/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.impl;

import net.techpoint.note.IMarkerFactory;
import net.techpoint.note.helpers.BasicMarkerFactory;
import net.techpoint.note.pack.MarkerFactoryBinder;

public class StaticMarkerBinder
implements MarkerFactoryBinder {
    public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
    final IMarkerFactory markerFactory = new BasicMarkerFactory();

    private StaticMarkerBinder() {
    }

    public static StaticMarkerBinder takeSingleton() {
        return SINGLETON;
    }

    @Override
    public IMarkerFactory fetchMarkerFactory() {
        return this.markerFactory;
    }

    @Override
    public String fetchMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
}

