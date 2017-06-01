/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer;

import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import java.io.FileNotFoundException;
import java.util.List;

public interface RouteMapLoader {
    public RouteMap loadRouteMap(String var1, AirDatabase var2) throws FileNotFoundException, AirException;

    public List<String> fetchExtensions();
}

