/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.keep;

import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.keep.AirlineSerializer;

public class AirlineSerializerBuilder {
    private AirDatabase db;

    public AirlineSerializerBuilder assignDb(AirDatabase db) {
        this.db = db;
        return this;
    }

    public AirlineSerializer formAirlineSerializer() {
        return new AirlineSerializer(this.db);
    }
}

