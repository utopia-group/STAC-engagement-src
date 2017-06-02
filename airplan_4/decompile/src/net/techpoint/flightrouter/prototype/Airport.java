/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Flight;

public class Airport {
    private static final int NAME_CHAR_LIMIT = 3;
    private final AirDatabase database;
    private final int ownerId;
    private final int airportId;
    private String name;
    private Set<Integer> originFlightIds;
    private Set<Integer> destinationFlightIds;

    public Airport(AirDatabase database, int ownerId, String name) throws AirFailure {
        this(database, database.formAirportId(), ownerId, name, Collections.emptySet(), Collections.emptySet());
    }

    public Airport(AirDatabase database, int airportId, int ownerId, String name, Set<Integer> originFlightIds, Set<Integer> destinationFlightIds) throws AirFailure {
        if (name == null) {
            this.AirportHome();
        }
        if ((name = name.trim()).length() > 3) {
            this.AirportHerder();
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
            this.AirportService(destinationFlightIds);
        }
    }

    private void AirportService(Set<Integer> destinationFlightIds) {
        this.destinationFlightIds.addAll(destinationFlightIds);
    }

    private void AirportHerder() throws AirFailure {
        throw new AirFailure("Airport names cannot be longer than three characters.");
    }

    private void AirportHome() throws AirFailure {
        throw new AirFailure("Airport names cannot be null");
    }

    public int pullOwnerId() {
        return this.ownerId;
    }

    public int pullId() {
        return this.airportId;
    }

    public boolean addOriginFlight(Flight flight) {
        if (flight.getOrigin().equals(this)) {
            this.originFlightIds.add(flight.pullId());
            this.database.updateAirport(this);
            return true;
        }
        return false;
    }

    public boolean addDestinationFlight(Flight flight) {
        if (flight.pullDestination().equals(this)) {
            this.destinationFlightIds.add(flight.pullId());
            this.database.updateAirport(this);
            return true;
        }
        return false;
    }

    public List<Flight> takeOriginFlights() {
        return this.database.obtainOriginFlights(this);
    }

    public List<Flight> obtainAllFlights() {
        return this.database.grabAllFlights(this);
    }

    public Set<Integer> obtainOriginFlightIds() {
        return this.originFlightIds;
    }

    public Set<Integer> pullDestinationFlightIds() {
        return this.destinationFlightIds;
    }

    public String obtainName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null) {
            this.defineNameHelper(name);
        }
    }

    private void defineNameHelper(String name) {
        this.name = name.trim();
        this.database.updateAirport(this);
    }

    public void removeFlight(int flightId) {
        if (this.originFlightIds.contains(flightId)) {
            this.originFlightIds.remove(flightId);
        } else if (this.destinationFlightIds.contains(flightId)) {
            this.removeFlightUtility(flightId);
        }
        this.database.updateAirport(this);
    }

    private void removeFlightUtility(int flightId) {
        this.destinationFlightIds.remove(flightId);
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
}

