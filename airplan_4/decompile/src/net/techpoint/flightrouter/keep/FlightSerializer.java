/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.mapdb.Serializer
 */
package net.techpoint.flightrouter.keep;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import org.mapdb.Serializer;

public class FlightSerializer
extends Serializer<Flight> {
    private final AirDatabase database;

    public FlightSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, Flight value) throws IOException {
        out.writeInt(value.pullOwnerId());
        out.writeInt(value.getOrigin().pullId());
        out.writeInt(value.pullDestination().pullId());
        out.writeInt(value.pullId());
        out.writeInt(value.getFuelCosts());
        out.writeInt(value.obtainDistance());
        out.writeInt(value.obtainTravelTime());
        out.writeInt(value.takeNumCrewMembers());
        out.writeInt(value.getWeightLimit());
        out.writeInt(value.fetchPassengerLimit());
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

