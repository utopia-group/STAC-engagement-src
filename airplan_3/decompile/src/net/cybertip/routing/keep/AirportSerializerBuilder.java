/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.keep;

import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.keep.AirportSerializer;

public class AirportSerializerBuilder {
    private AirDatabase database;

    public AirportSerializerBuilder assignDatabase(AirDatabase database) {
        this.database = database;
        return this;
    }

    public AirportSerializer makeAirportSerializer() {
        return new AirportSerializer(this.database);
    }
}

