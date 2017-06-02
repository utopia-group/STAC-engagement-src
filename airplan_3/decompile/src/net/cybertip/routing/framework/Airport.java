/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.framework;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.keep.AirDatabase;

public class Airport {
    private static final int NAME_CHAR_LIMIT = 3;
    private final AirDatabase database;
    private final int ownerId;
    private final int airportId;
    private String name;
    private Set<Integer> originFlightIds;
    private Set<Integer> destinationFlightIds;

    public Airport(AirDatabase database, int ownerId, String name) throws AirTrouble {
        this(database, database.makeAirportId(), ownerId, name, Collections.emptySet(), Collections.emptySet());
    }

    public Airport(AirDatabase database, int airportId, int ownerId, String name, Set<Integer> originFlightIds, Set<Integer> destinationFlightIds) throws AirTrouble {
        if (name == null) {
            throw new AirTrouble("Airport names cannot be null");
        }
        if ((name = name.trim()).length() > 3) {
            new AirportAid().invoke();
        }
        this.database = database;
        this.airportId = airportId;
        this.ownerId = ownerId;
        this.name = name;
        this.originFlightIds = new LinkedHashSet<Integer>();
        if (originFlightIds != null) {
            this.AirportEngine(originFlightIds);
        }
        this.destinationFlightIds = new LinkedHashSet<Integer>();
        if (destinationFlightIds != null) {
            this.destinationFlightIds.addAll(destinationFlightIds);
        }
    }

    private void AirportEngine(Set<Integer> originFlightIds) {
        this.originFlightIds.addAll(originFlightIds);
    }

    public int pullOwnerId() {
        return this.ownerId;
    }

    public int pullId() {
        return this.airportId;
    }

    public boolean addOriginFlight(Flight flight) {
        if (flight.fetchOrigin().equals(this)) {
            this.originFlightIds.add(flight.grabId());
            this.database.updateAirport(this);
            return true;
        }
        return false;
    }

    public boolean addDestinationFlight(Flight flight) {
        if (flight.fetchDestination().equals(this)) {
            this.destinationFlightIds.add(flight.grabId());
            this.database.updateAirport(this);
            return true;
        }
        return false;
    }

    public List<Flight> grabOriginFlights() {
        return this.database.obtainOriginFlights(this);
    }

    public List<Flight> grabAllFlights() {
        return this.database.obtainAllFlights(this);
    }

    public Set<Integer> takeOriginFlightIds() {
        return this.originFlightIds;
    }

    public Set<Integer> fetchDestinationFlightIds() {
        return this.destinationFlightIds;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name.trim();
            this.database.updateAirport(this);
        }
    }

    public void removeFlight(int flightId) {
        if (this.originFlightIds.contains(flightId)) {
            this.originFlightIds.remove(flightId);
        } else if (this.destinationFlightIds.contains(flightId)) {
            this.removeFlightFunction(flightId);
        }
        this.database.updateAirport(this);
    }

    private void removeFlightFunction(int flightId) {
        new AirportHelp(flightId).invoke();
    }

    public int hashCode() {
        return this.pullOwnerId() * 37 + this.pullId();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Airport)) {
            return false;
        }
        Airport other = (Airport)obj;
        return this.pullId() == other.pullId() && this.pullOwnerId() == other.pullOwnerId();
    }

    private class AirportHelp {
        private int flightId;

        public AirportHelp(int flightId) {
            this.flightId = flightId;
        }

        public void invoke() {
            Airport.this.destinationFlightIds.remove(this.flightId);
        }
    }

    private class AirportAid {
        private AirportAid() {
        }

        public void invoke() throws AirTrouble {
            throw new AirTrouble("Airport names cannot be longer than three characters.");
        }
    }

}

