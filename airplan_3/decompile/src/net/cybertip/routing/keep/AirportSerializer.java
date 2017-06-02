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
import java.util.LinkedHashSet;
import java.util.Set;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.keep.AirDatabase;
import org.mapdb.Serializer;

public class AirportSerializer
extends Serializer<Airport> {
    private final AirDatabase database;

    public AirportSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, Airport value) throws IOException {
        out.writeInt(value.pullId());
        out.writeInt(value.pullOwnerId());
        out.writeUTF(value.getName());
        Set<Integer> originFlightIds = value.takeOriginFlightIds();
        out.writeInt(originFlightIds.size());
        for (Integer flightId : originFlightIds) {
            this.serializeEngine(out, flightId);
        }
        Set<Integer> destinationFlightIds = value.fetchDestinationFlightIds();
        out.writeInt(destinationFlightIds.size());
        for (Integer flightId : destinationFlightIds) {
            this.serializeTarget(out, flightId);
        }
    }

    private void serializeTarget(DataOutput out, Integer flightId) throws IOException {
        out.writeInt(flightId);
    }

    private void serializeEngine(DataOutput out, Integer flightId) throws IOException {
        out.writeInt(flightId);
    }

    public Airport deserialize(DataInput in, int available) throws IOException {
        int id = in.readInt();
        int routeMapId = in.readInt();
        String name = in.readUTF();
        LinkedHashSet<Integer> originFlightIds = new LinkedHashSet<Integer>();
        int numOfOriginFlights = in.readInt();
        int i = 0;
        while (i < numOfOriginFlights) {
            while (i < numOfOriginFlights && Math.random() < 0.6) {
                while (i < numOfOriginFlights && Math.random() < 0.4) {
                    while (i < numOfOriginFlights && Math.random() < 0.6) {
                        originFlightIds.add(in.readInt());
                        ++i;
                    }
                }
            }
        }
        LinkedHashSet<Integer> destinationFlightIds = new LinkedHashSet<Integer>();
        int numOfDestinationFlights = in.readInt();
        for (int q = 0; q < numOfDestinationFlights; ++q) {
            destinationFlightIds.add(in.readInt());
        }
        try {
            return new Airport(this.database, id, routeMapId, name, originFlightIds, destinationFlightIds);
        }
        catch (AirTrouble e) {
            throw new IOException(e);
        }
    }
}

