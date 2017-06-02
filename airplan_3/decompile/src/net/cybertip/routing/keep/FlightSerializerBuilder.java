/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.keep;

import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.keep.FlightSerializer;

public class FlightSerializerBuilder {
    private AirDatabase database;

    public FlightSerializerBuilder assignDatabase(AirDatabase database) {
        this.database = database;
        return this;
    }

    public FlightSerializer makeFlightSerializer() {
        return new FlightSerializer(this.database);
    }
}

