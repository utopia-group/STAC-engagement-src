/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import java.io.FileNotFoundException;
import java.util.List;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.RouteMap;

public interface RouteMapLoader {
    public RouteMap loadRouteMap(String var1, AirDatabase var2) throws FileNotFoundException, AirFailure;

    public List<String> takeExtensions();
}

