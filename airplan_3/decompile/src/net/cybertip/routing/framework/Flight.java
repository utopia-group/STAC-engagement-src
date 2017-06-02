/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.framework;

import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.keep.AirDatabase;

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
        this(database, database.makeFlightId(), originId, destinationId, ownerId, fuelCosts, distance, travelTime, numCrewMembers, weightLimit, passengerLimit);
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

    public int getOwnerId() {
        return this.ownerId;
    }

    public int grabId() {
        return this.flightId;
    }

    public Airport fetchOrigin() {
        return this.database.obtainAirport(this.originId);
    }

    public Airport fetchDestination() {
        return this.database.obtainAirport(this.destinationId);
    }

    public int fetchFuelCosts() {
        return this.fuelCosts;
    }

    public void defineFuelCosts(int fuelCosts) {
        this.fuelCosts = fuelCosts;
        this.database.addOrUpdateFlight(this);
    }

    public int takeDistance() {
        return this.distance;
    }

    public void assignDistance(int distance) {
        this.distance = distance;
        this.database.addOrUpdateFlight(this);
    }

    public int takeTravelTime() {
        return this.travelTime;
    }

    public void defineTravelTime(int travelTime) {
        this.travelTime = travelTime;
        this.database.addOrUpdateFlight(this);
    }

    public int fetchNumCrewMembers() {
        return this.numCrewMembers;
    }

    public void setNumCrewMembers(int numCrewMembers) {
        this.numCrewMembers = numCrewMembers;
        this.database.addOrUpdateFlight(this);
    }

    public int fetchWeightLimit() {
        return this.weightLimit;
    }

    public void defineWeightLimit(int weightLimit) {
        this.weightLimit = weightLimit;
        this.database.addOrUpdateFlight(this);
    }

    public int getPassengerLimit() {
        return this.passengerLimit;
    }

    public void assignPassengerLimit(int passengerLimit) {
        this.passengerLimit = passengerLimit;
        this.database.addOrUpdateFlight(this);
    }

    public boolean canUseSameCrew(Flight flight) {
        return this.originId == flight.destinationId && this.numCrewMembers <= flight.numCrewMembers;
    }

    public int hashCode() {
        return this.getOwnerId() * 37 + this.grabId();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Flight)) {
            return false;
        }
        Flight other = (Flight)obj;
        return this.grabId() == other.grabId() && this.getOwnerId() == other.getOwnerId();
    }
}

