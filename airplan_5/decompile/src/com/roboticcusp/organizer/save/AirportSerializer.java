/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.mapdb.Serializer
 */
package com.roboticcusp.organizer.save;

import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.save.AirDatabase;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.mapdb.Serializer;

public class AirportSerializer
extends Serializer<Airport> {
    private final AirDatabase database;

    public AirportSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, Airport value) throws IOException {
        out.writeInt(value.fetchId());
        out.writeInt(value.obtainOwnerId());
        out.writeUTF(value.takeName());
        Set<Integer> originFlightIds = value.pullOriginFlightIds();
        out.writeInt(originFlightIds.size());
        for (Integer flightId : originFlightIds) {
            out.writeInt(flightId);
        }
        Set<Integer> destinationFlightIds = value.takeDestinationFlightIds();
        out.writeInt(destinationFlightIds.size());
        for (Integer flightId : destinationFlightIds) {
            this.serializeSupervisor(out, flightId);
        }
    }

    private void serializeSupervisor(DataOutput out, Integer flightId) throws IOException {
        out.writeInt(flightId);
    }

    public Airport deserialize(DataInput in, int available) throws IOException {
        int id = in.readInt();
        int routeMapId = in.readInt();
        String name = in.readUTF();
        LinkedHashSet<Integer> originFlightIds = new LinkedHashSet<Integer>();
        int numOfOriginFlights = in.readInt();
        for (int a = 0; a < numOfOriginFlights; ++a) {
            originFlightIds.add(in.readInt());
        }
        LinkedHashSet<Integer> destinationFlightIds = new LinkedHashSet<Integer>();
        int numOfDestinationFlights = in.readInt();
        for (int i = 0; i < numOfDestinationFlights; ++i) {
            destinationFlightIds.add(in.readInt());
        }
        try {
            return new Airport(this.database, id, routeMapId, name, originFlightIds, destinationFlightIds);
        }
        catch (AirException e) {
            throw new IOException(e);
        }
    }
}

