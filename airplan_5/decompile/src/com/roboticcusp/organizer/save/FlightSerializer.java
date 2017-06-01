/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.mapdb.Serializer
 */
package com.roboticcusp.organizer.save;

import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.save.AirDatabase;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.mapdb.Serializer;

public class FlightSerializer
extends Serializer<Flight> {
    private final AirDatabase database;

    public FlightSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, Flight value) throws IOException {
        out.writeInt(value.obtainOwnerId());
        out.writeInt(value.obtainOrigin().fetchId());
        out.writeInt(value.fetchDestination().fetchId());
        out.writeInt(value.grabId());
        out.writeInt(value.pullFuelCosts());
        out.writeInt(value.fetchDistance());
        out.writeInt(value.obtainTravelTime());
        out.writeInt(value.takeNumCrewMembers());
        out.writeInt(value.grabWeightAccommodation());
        out.writeInt(value.obtainPassengerAccommodation());
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
        int weightAccommodation = in.readInt();
        int passengerAccommodation = in.readInt();
        return new Flight(this.database, id, originId, destinationId, ownerId, fuelCosts, distance, travelTime, crewMembers, weightAccommodation, passengerAccommodation);
    }
}

