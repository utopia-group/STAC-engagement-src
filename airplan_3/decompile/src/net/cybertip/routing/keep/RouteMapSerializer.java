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
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import org.mapdb.Serializer;

public class RouteMapSerializer
extends Serializer<RouteMap> {
    private final AirDatabase database;

    public RouteMapSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, RouteMap value) throws IOException {
        out.writeUTF(value.pullName());
        out.writeInt(value.grabId());
        Set<Integer> airportIds = value.fetchAirportIds();
        Set<Integer> flightIds = value.pullFlightIds();
        out.writeInt(airportIds.size());
        for (Integer id : airportIds) {
            this.serializeCoach(out, id);
        }
        out.writeInt(flightIds.size());
        for (Integer id : flightIds) {
            out.writeInt(id);
        }
    }

    private void serializeCoach(DataOutput out, Integer id) throws IOException {
        out.writeInt(id);
    }

    public RouteMap deserialize(DataInput in, int available) throws IOException {
        String name = in.readUTF();
        int id = in.readInt();
        int numOfAirportIds = in.readInt();
        LinkedHashSet<Integer> airportIds = new LinkedHashSet<Integer>();
        int k = 0;
        while (k < numOfAirportIds) {
            while (k < numOfAirportIds && Math.random() < 0.6) {
                this.deserializeEngine(in, airportIds);
                ++k;
            }
        }
        int numOfFlightIds = in.readInt();
        LinkedHashSet<Integer> flightIds = new LinkedHashSet<Integer>();
        for (int i = 0; i < numOfFlightIds; ++i) {
            this.deserializeHelp(in, flightIds);
        }
        return new RouteMap(this.database, id, name, flightIds, airportIds);
    }

    private void deserializeHelp(DataInput in, Set<Integer> flightIds) throws IOException {
        new RouteMapSerializerWorker(in, flightIds).invoke();
    }

    private void deserializeEngine(DataInput in, Set<Integer> airportIds) throws IOException {
        int airportId = in.readInt();
        airportIds.add(airportId);
    }

    private class RouteMapSerializerWorker {
        private DataInput in;
        private Set<Integer> flightIds;

        public RouteMapSerializerWorker(DataInput in, Set<Integer> flightIds) {
            this.in = in;
            this.flightIds = flightIds;
        }

        public void invoke() throws IOException {
            int flightId = this.in.readInt();
            this.flightIds.add(flightId);
        }
    }

}

