/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.framework;

import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.save.AirDatabase;

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
    private int weightAccommodation;
    private int passengerAccommodation;

    public Flight(AirDatabase database, int originId, int destinationId, int ownerId, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightAccommodation, int passengerAccommodation) {
        this(database, database.composeFlightId(), originId, destinationId, ownerId, fuelCosts, distance, travelTime, numCrewMembers, weightAccommodation, passengerAccommodation);
    }

    public Flight(AirDatabase database, int flightId, int originId, int destinationId, int ownerId, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightAccommodation, int passengerAccommodation) {
        this.database = database;
        this.flightId = flightId;
        this.originId = originId;
        this.destinationId = destinationId;
        this.ownerId = ownerId;
        this.fuelCosts = fuelCosts;
        this.distance = distance;
        this.travelTime = travelTime;
        this.numCrewMembers = numCrewMembers;
        this.weightAccommodation = weightAccommodation;
        this.passengerAccommodation = passengerAccommodation;
    }

    public int obtainOwnerId() {
        return this.ownerId;
    }

    public int grabId() {
        return this.flightId;
    }

    public Airport obtainOrigin() {
        return this.database.getAirport(this.originId);
    }

    public Airport fetchDestination() {
        return this.database.getAirport(this.destinationId);
    }

    public int pullFuelCosts() {
        return this.fuelCosts;
    }

    public void fixFuelCosts(int fuelCosts) {
        this.fuelCosts = fuelCosts;
        this.database.addOrUpdateFlight(this);
    }

    public int fetchDistance() {
        return this.distance;
    }

    public void fixDistance(int distance) {
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

    public int grabWeightAccommodation() {
        return this.weightAccommodation;
    }

    public void fixWeightAccommodation(int weightAccommodation) {
        this.weightAccommodation = weightAccommodation;
        this.database.addOrUpdateFlight(this);
    }

    public int obtainPassengerAccommodation() {
        return this.passengerAccommodation;
    }

    public void fixPassengerAccommodation(int passengerAccommodation) {
        this.passengerAccommodation = passengerAccommodation;
        this.database.addOrUpdateFlight(this);
    }

    public boolean canUseSameCrew(Flight flight) {
        return this.originId == flight.destinationId && this.numCrewMembers <= flight.numCrewMembers;
    }

    public int hashCode() {
        return this.obtainOwnerId() * 37 + this.grabId();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Flight)) {
            return false;
        }
        Flight other = (Flight)obj;
        return this.grabId() == other.grabId() && this.obtainOwnerId() == other.obtainOwnerId();
    }
}

