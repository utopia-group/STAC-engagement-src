/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;

public class Flight {
    private final AirDatabase database;
    private final int ownerId;
    private final int flightId;
    private final int originId;
    private final int destinationId;
    private int fuelCosts;
    private int distance;
    private int travelTime;
    private int numCrewMembers;
    private int weightLimit;
    private int passengerLimit;

    public Flight(AirDatabase database, int originId, int destinationId, int ownerId, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightLimit, int passengerLimit) {
        this(database, database.formFlightId(), originId, destinationId, ownerId, fuelCosts, distance, travelTime, numCrewMembers, weightLimit, passengerLimit);
    }

    public Flight(AirDatabase database, int flightId, int originId, int destinationId, int ownerId, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightLimit, int passengerLimit) {
        this.database = database;
        this.flightId = flightId;
        this.originId = originId;
        this.destinationId = destinationId;
        this.ownerId = ownerId;
        this.fuelCosts = fuelCosts;
        this.distance = distance;
        this.travelTime = travelTime;
        this.numCrewMembers = numCrewMembers;
        this.weightLimit = weightLimit;
        this.passengerLimit = passengerLimit;
    }

    public int pullOwnerId() {
        return this.ownerId;
    }

    public int pullId() {
        return this.flightId;
    }

    public Airport getOrigin() {
        return this.database.takeAirport(this.originId);
    }

    public Airport pullDestination() {
        return this.database.takeAirport(this.destinationId);
    }

    public int getFuelCosts() {
        return this.fuelCosts;
    }

    public void assignFuelCosts(int fuelCosts) {
        this.fuelCosts = fuelCosts;
        this.database.addOrUpdateFlight(this);
    }

    public int obtainDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
        this.database.addOrUpdateFlight(this);
    }

    public int obtainTravelTime() {
        return this.travelTime;
    }

    public void defineTravelTime(int travelTime) {
        this.travelTime = travelTime;
        this.database.addOrUpdateFlight(this);
    }

    public int takeNumCrewMembers() {
        return this.numCrewMembers;
    }

    public void assignNumCrewMembers(int numCrewMembers) {
        this.numCrewMembers = numCrewMembers;
        this.database.addOrUpdateFlight(this);
    }

    public int getWeightLimit() {
        return this.weightLimit;
    }

    public void setWeightLimit(int weightLimit) {
        this.weightLimit = weightLimit;
        this.database.addOrUpdateFlight(this);
    }

    public int fetchPassengerLimit() {
        return this.passengerLimit;
    }

    public void setPassengerLimit(int passengerLimit) {
        this.passengerLimit = passengerLimit;
        this.database.addOrUpdateFlight(this);
    }

    public boolean canUseSameCrew(Flight flight) {
        return this.originId == flight.destinationId && this.numCrewMembers <= flight.numCrewMembers;
    }

    public int hashCode() {
        return this.pullOwnerId() * 37 + this.pullId();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Flight)) {
            return false;
        }
        Flight other = (Flight)obj;
        return this.pullId() == other.pullId() && this.pullOwnerId() == other.pullOwnerId();
    }
}

