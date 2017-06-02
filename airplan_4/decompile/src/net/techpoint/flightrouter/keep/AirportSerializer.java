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
import java.util.LinkedHashSet;
import java.util.Set;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;
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
        out.writeUTF(value.obtainName());
        Set<Integer> originFlightIds = value.obtainOriginFlightIds();
        out.writeInt(originFlightIds.size());
        for (Integer flightId : originFlightIds) {
            out.writeInt(flightId);
        }
        Set<Integer> destinationFlightIds = value.pullDestinationFlightIds();
        out.writeInt(destinationFlightIds.size());
        for (Integer flightId : destinationFlightIds) {
            out.writeInt(flightId);
        }
    }

    public Airport deserialize(DataInput in, int available) throws IOException {
        int id = in.readInt();
        int routeMapId = in.readInt();
        String name = in.readUTF();
        LinkedHashSet<Integer> originFlightIds = new LinkedHashSet<Integer>();
        int numOfOriginFlights = in.readInt();
        for (int q = 0; q < numOfOriginFlights; ++q) {
            this.deserializeGateKeeper(in, originFlightIds);
        }
        LinkedHashSet<Integer> destinationFlightIds = new LinkedHashSet<Integer>();
        int numOfDestinationFlights = in.readInt();
        for (int k = 0; k < numOfDestinationFlights; ++k) {
            this.deserializeWorker(in, destinationFlightIds);
        }
        try {
            return new Airport(this.database, id, routeMapId, name, originFlightIds, destinationFlightIds);
        }
        catch (AirFailure e) {
            throw new IOException(e);
        }
    }

    private void deserializeWorker(DataInput in, Set<Integer> destinationFlightIds) throws IOException {
        destinationFlightIds.add(in.readInt());
    }

    private void deserializeGateKeeper(DataInput in, Set<Integer> originFlightIds) throws IOException {
        originFlightIds.add(in.readInt());
    }
}

