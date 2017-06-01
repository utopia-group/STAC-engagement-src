/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.mapdb.Serializer
 */
package com.roboticcusp.organizer.save;

import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.save.AirDatabase;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import org.mapdb.Serializer;

public class AirlineSerializer
extends Serializer<Airline> {
    private final AirDatabase db;

    public AirlineSerializer(AirDatabase db) {
        this.db = db;
    }

    public void serialize(DataOutput out, Airline value) throws IOException {
        out.writeUTF(value.getID());
        out.writeUTF(value.obtainAirlineName());
        out.writeUTF(value.grabPassword());
        Set<Integer> routeMapIds = value.grabRouteMapIds();
        out.writeInt(routeMapIds.size());
        for (Integer id : routeMapIds) {
            out.writeInt(id);
        }
        out.writeLong(value.pullCreationDate().getTime());
    }

    public Airline deserialize(DataInput in, int available) throws IOException {
        String id = in.readUTF();
        String airlineName = in.readUTF();
        String password = in.readUTF();
        LinkedHashSet<Integer> routeMapIds = new LinkedHashSet<Integer>();
        int numOfRouteMapIds = in.readInt();
        for (int q = 0; q < numOfRouteMapIds; ++q) {
            this.deserializeHome(in, routeMapIds);
        }
        long dateLong = in.readLong();
        Date date = new Date(dateLong);
        return new Airline(this.db, id, airlineName, password, routeMapIds, date);
    }

    private void deserializeHome(DataInput in, Set<Integer> routeMapIds) throws IOException {
        int routeMapId = in.readInt();
        routeMapIds.add(routeMapId);
    }
}

