/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.save;

import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.organizer.save.RouteMapSerializer;

public class RouteMapSerializerBuilder {
    private AirDatabase database;

    public RouteMapSerializerBuilder fixDatabase(AirDatabase database) {
        this.database = database;
        return this;
    }

    public RouteMapSerializer composeRouteMapSerializer() {
        return new RouteMapSerializer(this.database);
    }
}

