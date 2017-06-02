/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.mapdb.Serializer
 */
package net.cybertip.routing.keep;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.keep.AirDatabase;
import org.mapdb.Serializer;

public class FlightSerializer
extends Serializer<Flight> {
    private final AirDatabase database;

    public FlightSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, Flight value) throws IOException {
        out.writeInt(value.getOwnerId());
        out.writeInt(value.fetchOrigin().pullId());
        out.writeInt(value.fetchDestination().pullId());
        out.writeInt(value.grabId());
        out.writeInt(value.fetchFuelCosts());
        out.writeInt(value.takeDistance());
        out.writeInt(value.takeTravelTime());
        out.writeInt(value.fetchNumCrewMembers());
        out.writeInt(value.fetchWeightLimit());
        out.writeInt(value.getPassengerLimit());
    }

    public Flight deserialize(DataInput in, int available) throws IOException {
        int ownerId = in.readInt();
        int originId = in.readInt();
        int destinationId = in.readInt();
        int id = in.readInt();
        int fuelCosts = in.readInt();
        int distance = in.readInt();
        int travelTime = in.readInt();
        int crewMembers = in.readInt();
        int weightLimit = in.readInt();
        int passengerLimit = in.readInt();
        return new Flight(this.database, id, originId, destinationId, ownerId, fuelCosts, distance, travelTime, crewMembers, weightLimit, passengerLimit);
    }
}

