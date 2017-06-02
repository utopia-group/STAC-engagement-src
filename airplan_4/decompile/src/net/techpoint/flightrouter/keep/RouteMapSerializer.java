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
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.RouteMap;
import org.mapdb.Serializer;

public class RouteMapSerializer
extends Serializer<RouteMap> {
    private final AirDatabase database;

    public RouteMapSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, RouteMap value) throws IOException {
        out.writeUTF(value.fetchName());
        out.writeInt(value.pullId());
        Set<Integer> airportIds = value.grabAirportIds();
        Set<Integer> flightIds = value.takeFlightIds();
        out.writeInt(airportIds.size());
        for (Integer id : airportIds) {
            this.serializeEntity(out, id);
        }
        out.writeInt(flightIds.size());
        for (Integer id : flightIds) {
            out.writeInt(id);
        }
    }

    private void serializeEntity(DataOutput out, Integer id) throws IOException {
        out.writeInt(id);
    }

    public RouteMap deserialize(DataInput in, int available) throws IOException {
        String name = in.readUTF();
        int id = in.readInt();
        int numOfAirportIds = in.readInt();
        LinkedHashSet<Integer> airportIds = new LinkedHashSet<Integer>();
        for (int q = 0; q < numOfAirportIds; ++q) {
            this.deserializeExecutor(in, airportIds);
        }
        int numOfFlightIds = in.readInt();
        LinkedHashSet<Integer> flightIds = new LinkedHashSet<Integer>();
        for (int b = 0; b < numOfFlightIds; ++b) {
            int flightId = in.readInt();
            flightIds.add(flightId);
        }
        return new RouteMap(this.database, id, name, flightIds, airportIds);
    }

    private void deserializeExecutor(DataInput in, Set<Integer> airportIds) throws IOException {
        int airportId = in.readInt();
        airportIds.add(airportId);
    }
}

