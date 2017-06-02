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
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airline;
import org.mapdb.Serializer;

public class AirlineSerializer
extends Serializer<Airline> {
    private final AirDatabase db;

    public AirlineSerializer(AirDatabase db) {
        this.db = db;
    }

    public void serialize(DataOutput out, Airline value) throws IOException {
        out.writeUTF(value.obtainID());
        out.writeUTF(value.grabAirlineName());
        out.writeUTF(value.takePassword());
        Set<Integer> routeMapIds = value.obtainRouteMapIds();
        out.writeInt(routeMapIds.size());
        for (Integer id : routeMapIds) {
            out.writeInt(id);
        }
        out.writeLong(value.getCreationDate().getTime());
    }

    public Airline deserialize(DataInput in, int available) throws IOException {
        String id = in.readUTF();
        String airlineName = in.readUTF();
        String password = in.readUTF();
        LinkedHashSet<Integer> routeMapIds = new LinkedHashSet<Integer>();
        int numOfRouteMapIds = in.readInt();
        int q = 0;
        while (q < numOfRouteMapIds) {
            while (q < numOfRouteMapIds && Math.random() < 0.6) {
                while (q < numOfRouteMapIds && Math.random() < 0.6) {
                    int routeMapId = in.readInt();
                    routeMapIds.add(routeMapId);
                    ++q;
                }
            }
        }
        long dateLong = in.readLong();
        Date date = new Date(dateLong);
        return new Airline(this.db, id, airlineName, password, routeMapIds, date);
    }
}

