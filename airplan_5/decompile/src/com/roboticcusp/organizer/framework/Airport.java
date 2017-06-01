/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.framework;

import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.save.AirDatabase;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Airport {
    private static final int NAME_CHAR_LIMIT = 3;
    private final AirDatabase database;
    private final int ownerId;
    private final int airportId;
    private String name;
    private Set<Integer> originFlightIds;
    private Set<Integer> destinationFlightIds;

    public Airport(AirDatabase database, int ownerId, String name) throws AirException {
        this(database, database.composeAirportId(), ownerId, name, Collections.emptySet(), Collections.emptySet());
    }

    public Airport(AirDatabase database, int airportId, int ownerId, String name, Set<Integer> originFlightIds, Set<Integer> destinationFlightIds) throws AirException {
        if (name == null) {
            throw new AirException("Airport names cannot be null");
        }
        if ((name = name.trim()).length() > 3) {
            this.AirportExecutor();
        }
        this.database = database;
        this.airportId = airportId;
        this.ownerId = ownerId;
        this.name = name;
        this.originFlightIds = new LinkedHashSet<Integer>();
        if (originFlightIds != null) {
            this.originFlightIds.addAll(originFlightIds);
        }
        this.destinationFlightIds = new LinkedHashSet<Integer>();
        if (destinationFlightIds != null) {
            this.destinationFlightIds.addAll(destinationFlightIds);
        }
    }

    private void AirportExecutor() throws AirException {
        throw new AirException("Airport names cannot be longer than three characters.");
    }

    public int obtainOwnerId() {
        return this.ownerId;
    }

    public int fetchId() {
        return this.airportId;
    }

    public boolean addOriginFlight(Flight flight) {
        if (flight.obtainOrigin().equals(this)) {
            return this.addOriginFlightWorker(flight);
        }
        return false;
    }

    private boolean addOriginFlightWorker(Flight flight) {
        this.originFlightIds.add(flight.grabId());
        this.database.updateAirport(this);
        return true;
    }

    public boolean addDestinationFlight(Flight flight) {
        if (flight.fetchDestination().equals(this)) {
            this.destinationFlightIds.add(flight.grabId());
            this.database.updateAirport(this);
            return true;
        }
        return false;
    }

    public List<Flight> fetchOriginFlights() {
        return this.database.takeOriginFlights(this);
    }

    public List<Flight> getAllFlights() {
        return this.database.fetchAllFlights(this);
    }

    public Set<Integer> pullOriginFlightIds() {
        return this.originFlightIds;
    }

    public Set<Integer> takeDestinationFlightIds() {
        return this.destinationFlightIds;
    }

    public String takeName() {
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
            this.removeFlightHome(flightId);
        } else if (this.destinationFlightIds.contains(flightId)) {
            this.removeFlightGateKeeper(flightId);
        }
        this.database.updateAirport(this);
    }

    private void removeFlightGateKeeper(int flightId) {
        this.destinationFlightIds.remove(flightId);
    }

    private void removeFlightHome(int flightId) {
        this.originFlightIds.remove(flightId);
    }

    public int hashCode() {
        return this.obtainOwnerId() * 37 + this.fetchId();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Airport)) {
            return false;
        }
        Airport other = (Airport)obj;
        return this.fetchId() == other.fetchId() && this.obtainOwnerId() == other.obtainOwnerId();
    }
}

