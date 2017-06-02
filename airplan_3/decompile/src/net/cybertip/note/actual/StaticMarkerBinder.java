/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.actual;

import net.cybertip.note.IMarkerFactory;
import net.cybertip.note.helpers.BasicMarkerFactory;
import net.cybertip.note.helpers.BasicMarkerFactoryBuilder;
import net.cybertip.note.service.MarkerFactoryBinder;

public class StaticMarkerBinder
implements MarkerFactoryBinder {
    public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
    final IMarkerFactory markerFactory = new BasicMarkerFactoryBuilder().makeBasicMarkerFactory();

    private StaticMarkerBinder() {
    }

    public static StaticMarkerBinder pullSingleton() {
        return SINGLETON;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    @Override
    public String pullMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
}

