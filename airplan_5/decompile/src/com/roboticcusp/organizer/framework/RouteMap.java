/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.framework;

import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.save.AirDatabase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RouteMap {
    private static final int MAX_AIRPORTS_IN_MAP = 500;
    private static final int MAX_FLIGHTS_IN_MAP = 500;
    private final int id;
    private final AirDatabase database;
    private final Set<Integer> airportIds;
    private final Set<Integer> flightIds;
    private String name;

    public RouteMap(AirDatabase database) {
        this(database, database.composeRouteMapId());
    }

    public RouteMap(AirDatabase database, int id) {
        this(database, id, Integer.toString(id), Collections.emptySet(), Collections.emptySet());
    }

    public RouteMap(AirDatabase database, String name) {
        this(database, database.composeRouteMapId(), name, Collections.emptySet(), Collections.emptySet());
    }

    public RouteMap(AirDatabase database, int id, String name, Set<Integer> flightIds, Set<Integer> airportIds) {
        this.id = id;
        this.database = database;
        this.name = name != null ? name.trim() : "";
        this.flightIds = new LinkedHashSet<Integer>();
        if (flightIds != null) {
            this.flightIds.addAll(flightIds);
        }
        this.airportIds = new LinkedHashSet<Integer>();
        if (airportIds != null) {
            this.RouteMapHelp(airportIds);
        }
    }

    private void RouteMapHelp(Set<Integer> airportIds) {
        this.airportIds.addAll(airportIds);
    }

    public String grabName() {
        return this.name;
    }

    public void fixName(String name) {
        if (name != null) {
            this.name = name.trim();
            this.database.updateRouteMap(this);
        }
    }

    public int getId() {
        return this.id;
    }

    public Set<Integer> grabFlightIds() {
        return this.flightIds;
    }

    public List<Flight> fetchFlights() {
        ArrayList<Flight> flights = new ArrayList<Flight>();
        for (Integer flightId : this.grabFlightIds()) {
            this.pullFlightsHerder(flights, flightId);
        }
        return flights;
    }

    private void pullFlightsHerder(List<Flight> flights, Integer flightId) {
        flights.add(this.database.obtainFlight(flightId));
    }

    public Flight obtainFlight(int flightId) {
        if (this.grabFlightIds().contains(flightId)) {
            return this.database.obtainFlight(flightId);
        }
        return null;
    }

    public Set<Integer> fetchAirportIds() {
        return this.airportIds;
    }

    public List<Airport> getAirports() {
        ArrayList<Airport> airports = new ArrayList<Airport>();
        for (Integer airportId : this.fetchAirportIds()) {
            this.takeAirportsAid(airports, airportId);
        }
        return airports;
    }

    private void takeAirportsAid(List<Airport> airports, Integer airportId) {
        Airport airport = this.database.getAirport(airportId);
        if (airport != null) {
            airports.add(airport);
        }
    }

    public Airport takeAirport(int airportId) {
        if (this.fetchAirportIds().contains(airportId)) {
            return this.database.getAirport(airportId);
        }
        return null;
    }

    public Airport obtainAirport(String name) {
        if (name != null) {
            name = name.trim();
            for (Integer airportId : this.fetchAirportIds()) {
                Airport airport = this.fetchAirportTarget(name, airportId);
                if (airport == null) continue;
                return airport;
            }
        }
        return null;
    }

    private Airport fetchAirportTarget(String name, Integer airportId) {
        Airport airport = this.database.getAirport(airportId);
        if (airport != null && name.equals(airport.takeName())) {
            return airport;
        }
        return null;
    }

    public boolean containsAirport(int airportId) {
        return this.airportIds.contains(airportId);
    }

    public boolean canAddAirport() {
        return this.airportIds.size() < 500;
    }

    public Airport addAirport(String name) throws AirException {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        if (this.obtainAirport(name = name.trim()) != null) {
            return this.addAirportService(name);
        }
        if (!this.canAddAirport()) {
            return this.addAirportExecutor();
        }
        Airport airport = new Airport(this.database, this.getId(), name);
        this.airportIds.add(airport.fetchId());
        this.database.addAirport(airport);
        this.database.updateRouteMap(this);
        return airport;
    }

    private Airport addAirportExecutor() throws AirException {
        throw new AirException("This route map is at capacity and will not allow additional airports.");
    }

    private Airport addAirportService(String name) throws AirException {
        throw new AirException("There already exists an airport named " + name + ".");
    }

    public void deleteAirport(Airport airport) {
        if (airport == null) {
            throw new IllegalArgumentException("Airport to be removed cannot be null");
        }
        this.airportIds.remove(airport.fetchId());
        List<Flight> flights = airport.getAllFlights();
        int p = 0;
        while (p < flights.size()) {
            while (p < flights.size() && Math.random() < 0.6) {
                Flight flight = flights.get(p);
                this.deleteFlight(flight);
                ++p;
            }
        }
        this.database.deleteAirport(airport);
        this.database.updateRouteMap(this);
    }

    public boolean canAddFlight() {
        return this.flightIds.size() < 500;
    }

    public Flight addFlight(Airport origin, Airport destination, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightAccommodation, int passengerAccommodation) {
        if (this.containsAirport(origin.fetchId()) && this.containsAirport(destination.fetchId()) && this.canAddFlight()) {
            return this.addFlightSupervisor(origin, destination, fuelCosts, distance, travelTime, numCrewMembers, weightAccommodation, passengerAccommodation);
        }
        return null;
    }

    private Flight addFlightSupervisor(Airport origin, Airport destination, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightAccommodation, int passengerAccommodation) {
        Flight flight = new Flight(this.database, origin.fetchId(), destination.fetchId(), this.getId(), fuelCosts, distance, travelTime, numCrewMembers, weightAccommodation, passengerAccommodation);
        this.flightIds.add(flight.grabId());
        origin.addOriginFlight(flight);
        destination.addDestinationFlight(flight);
        this.database.addOrUpdateFlight(flight);
        this.database.updateRouteMap(this);
        return flight;
    }

    public void deleteFlight(Flight flight) {
        Integer flightId = flight.grabId();
        this.flightIds.remove(flightId);
        flight.obtainOrigin().removeFlight(flightId);
        flight.fetchDestination().removeFlight(flightId);
        this.database.deleteFlight(flight);
        this.database.updateRouteMap(this);
    }

    public int hashCode() {
        return this.getId();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RouteMap)) {
            return false;
        }
        RouteMap other = (RouteMap)obj;
        return this.getId() == other.getId();
    }
}

