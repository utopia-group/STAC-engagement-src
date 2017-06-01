/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.mapdb.Serializer
 */
package com.roboticcusp.organizer.save;

import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.mapdb.Serializer;

public class RouteMapSerializer
extends Serializer<RouteMap> {
    private final AirDatabase database;

    public RouteMapSerializer(AirDatabase database) {
        this.database = database;
    }

    public void serialize(DataOutput out, RouteMap value) throws IOException {
        out.writeUTF(value.grabName());
        out.writeInt(value.getId());
        Set<Integer> airportIds = value.fetchAirportIds();
        Set<Integer> flightIds = value.grabFlightIds();
        out.writeInt(airportIds.size());
        for (Integer id : airportIds) {
            this.serializeTarget(out, id);
        }
        out.writeInt(flightIds.size());
        for (Integer id : flightIds) {
            new RouteMapSerializerHome(out, id).invoke();
        }
    }

    private void serializeTarget(DataOutput out, Integer id) throws IOException {
        out.writeInt(id);
    }

    public RouteMap deserialize(DataInput in, int available) throws IOException {
        String name = in.readUTF();
        int id = in.readInt();
        int numOfAirportIds = in.readInt();
        LinkedHashSet<Integer> airportIds = new LinkedHashSet<Integer>();
        int j = 0;
        while (j < numOfAirportIds) {
            while (j < numOfAirportIds && Math.random() < 0.5) {
                this.deserializeUtility(in, airportIds);
                ++j;
            }
        }
        int numOfFlightIds = in.readInt();
        LinkedHashSet<Integer> flightIds = new LinkedHashSet<Integer>();
        for (int q = 0; q < numOfFlightIds; ++q) {
            int flightId = in.readInt();
            flightIds.add(flightId);
        }
        return new RouteMap(this.database, id, name, flightIds, airportIds);
    }

    private void deserializeUtility(DataInput in, Set<Integer> airportIds) throws IOException {
        int airportId = in.readInt();
        airportIds.add(airportId);
    }

    private class RouteMapSerializerHome {
        private DataOutput out;
        private Integer id;

        public RouteMapSerializerHome(DataOutput out, Integer id) {
            this.out = out;
            this.id = id;
        }

        public void invoke() throws IOException {
            this.out.writeInt(this.id);
        }
    }

}

