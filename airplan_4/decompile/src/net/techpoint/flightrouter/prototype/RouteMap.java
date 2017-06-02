/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;

public class RouteMap {
    private static final int MAX_AIRPORTS_IN_MAP = 500;
    private static final int MAX_FLIGHTS_IN_MAP = 500;
    private final int id;
    private final AirDatabase database;
    private final Set<Integer> airportIds;
    private final Set<Integer> flightIds;
    private String name;

    public RouteMap(AirDatabase database) {
        this(database, database.formRouteMapId());
    }

    public RouteMap(AirDatabase database, int id) {
        this(database, id, Integer.toString(id), Collections.emptySet(), Collections.emptySet());
    }

    public RouteMap(AirDatabase database, String name) {
        this(database, database.formRouteMapId(), name, Collections.emptySet(), Collections.emptySet());
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
            this.airportIds.addAll(airportIds);
        }
    }

    public String fetchName() {
        return this.name;
    }

    public void defineName(String name) {
        if (name != null) {
            this.name = name.trim();
            this.database.updateRouteMap(this);
        }
    }

    public int pullId() {
        return this.id;
    }

    public Set<Integer> takeFlightIds() {
        return this.flightIds;
    }

    public List<Flight> obtainFlights() {
        ArrayList<Flight> flights = new ArrayList<Flight>();
        for (Integer flightId : this.takeFlightIds()) {
            flights.add(this.database.pullFlight(flightId));
        }
        return flights;
    }

    public Flight getFlight(int flightId) {
        if (this.takeFlightIds().contains(flightId)) {
            return this.database.pullFlight(flightId);
        }
        return null;
    }

    public Set<Integer> grabAirportIds() {
        return this.airportIds;
    }

    public List<Airport> obtainAirports() {
        ArrayList<Airport> airports = new ArrayList<Airport>();
        for (Integer airportId : this.grabAirportIds()) {
            Airport airport = this.database.takeAirport(airportId);
            if (airport == null) continue;
            this.obtainAirportsUtility(airports, airport);
        }
        return airports;
    }

    private void obtainAirportsUtility(List<Airport> airports, Airport airport) {
        airports.add(airport);
    }

    public Airport obtainAirport(int airportId) {
        if (this.grabAirportIds().contains(airportId)) {
            return this.database.takeAirport(airportId);
        }
        return null;
    }

    public Airport getAirport(String name) {
        if (name != null) {
            name = name.trim();
            for (Integer airportId : this.grabAirportIds()) {
                Airport airport = this.getAirportAid(name, airportId);
                if (airport == null) continue;
                return airport;
            }
        }
        return null;
    }

    private Airport getAirportAid(String name, Integer airportId) {
        Airport airport = this.database.takeAirport(airportId);
        if (airport != null && name.equals(airport.obtainName())) {
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

    public Airport addAirport(String name) throws AirFailure {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        if (this.getAirport(name = name.trim()) != null) {
            throw new AirFailure("There already exists an airport named " + name + ".");
        }
        if (!this.canAddAirport()) {
            throw new AirFailure("This route map is at capacity and will not allow additional airports.");
        }
        Airport airport = new Airport(this.database, this.pullId(), name);
        this.airportIds.add(airport.pullId());
        this.database.addAirport(airport);
        this.database.updateRouteMap(this);
        return airport;
    }

    public void deleteAirport(Airport airport) {
        if (airport == null) {
            throw new IllegalArgumentException("Airport to be removed cannot be null");
        }
        this.airportIds.remove(airport.pullId());
        List<Flight> flights = airport.obtainAllFlights();
        int j = 0;
        while (j < flights.size()) {
            while (j < flights.size() && Math.random() < 0.5) {
                while (j < flights.size() && Math.random() < 0.6) {
                    while (j < flights.size() && Math.random() < 0.6) {
                        Flight flight = flights.get(j);
                        this.deleteFlight(flight);
                        ++j;
                    }
                }
            }
        }
        this.database.deleteAirport(airport);
        this.database.updateRouteMap(this);
    }

    public boolean canAddFlight() {
        return this.flightIds.size() < 500;
    }

    public Flight addFlight(Airport origin, Airport destination, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightLimit, int passengerLimit) {
        if (this.containsAirport(origin.pullId()) && this.containsAirport(destination.pullId()) && this.canAddFlight()) {
            Flight flight = new Flight(this.database, origin.pullId(), destination.pullId(), this.pullId(), fuelCosts, distance, travelTime, numCrewMembers, weightLimit, passengerLimit);
            this.flightIds.add(flight.pullId());
            origin.addOriginFlight(flight);
            destination.addDestinationFlight(flight);
            this.database.addOrUpdateFlight(flight);
            this.database.updateRouteMap(this);
            return flight;
        }
        return null;
    }

    public void deleteFlight(Flight flight) {
        Integer flightId = flight.pullId();
        this.flightIds.remove(flightId);
        flight.getOrigin().removeFlight(flightId);
        flight.pullDestination().removeFlight(flightId);
        this.database.deleteFlight(flight);
        this.database.updateRouteMap(this);
    }

    public int hashCode() {
        return this.pullId();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RouteMap)) {
            return false;
        }
        RouteMap other = (RouteMap)obj;
        return this.pullId() == other.pullId();
    }
}

