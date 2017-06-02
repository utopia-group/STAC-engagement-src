/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing;

import java.io.FileNotFoundException;
import java.util.List;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;

public interface RouteMapLoader {
    public RouteMap loadRouteMap(String var1, AirDatabase var2) throws FileNotFoundException, AirTrouble;

    public List<String> takeExtensions();
}

