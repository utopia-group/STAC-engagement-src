/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMapEntity;
import net.cybertip.routing.keep.AirDatabase;

public class RouteMap {
    private static final int MAX_AIRPORTS_IN_MAP = 500;
    private static final int MAX_FLIGHTS_IN_MAP = 500;
    private final int id;
    private final AirDatabase database;
    private final Set<Integer> airportIds;
    private final Set<Integer> flightIds;
    private final RouteMapEntity routeMapEntity;
    private String name;

    public RouteMap(AirDatabase database) {
        this(database, database.makeRouteMapId());
    }

    public RouteMap(AirDatabase database, int id) {
        this(database, id, Integer.toString(id), Collections.emptySet(), Collections.emptySet());
    }

    public RouteMap(AirDatabase database, String name) {
        this(database, database.makeRouteMapId(), name, Collections.emptySet(), Collections.emptySet());
    }

    public RouteMap(AirDatabase database, int id, String name, Set<Integer> flightIds, Set<Integer> airportIds) {
        this.routeMapEntity = new RouteMapEntity(this);
        this.id = id;
        this.database = database;
        this.name = name != null ? name.trim() : "";
        this.flightIds = new LinkedHashSet<Integer>();
        if (flightIds != null) {
            this.RouteMapEntity(flightIds);
        }
        this.airportIds = new LinkedHashSet<Integer>();
        if (airportIds != null) {
            this.airportIds.addAll(airportIds);
        }
    }

    private void RouteMapEntity(Set<Integer> flightIds) {
        this.flightIds.addAll(flightIds);
    }

    public String pullName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null) {
            this.setNameHerder(name);
        }
    }

    private void setNameHerder(String name) {
        this.name = name.trim();
        this.database.updateRouteMap(this);
    }

    public int grabId() {
        return this.id;
    }

    public Set<Integer> pullFlightIds() {
        return this.flightIds;
    }

    public List<Flight> pullFlights() {
        return this.routeMapEntity.grabFlights();
    }

    public Flight takeFlight(int flightId) {
        return this.routeMapEntity.takeFlight(flightId);
    }

    public Set<Integer> fetchAirportIds() {
        return this.airportIds;
    }

    public List<Airport> takeAirports() {
        ArrayList<Airport> airports = new ArrayList<Airport>();
        for (Integer airportId : this.fetchAirportIds()) {
            new RouteMapHome(airports, airportId).invoke();
        }
        return airports;
    }

    public Airport obtainAirport(int airportId) {
        if (this.fetchAirportIds().contains(airportId)) {
            return this.database.obtainAirport(airportId);
        }
        return null;
    }

    public Airport getAirport(String name) {
        if (name != null) {
            name = name.trim();
            for (Integer airportId : this.fetchAirportIds()) {
                Airport airport = this.pullAirportAssist(name, airportId);
                if (airport == null) continue;
                return airport;
            }
        }
        return null;
    }

    private Airport pullAirportAssist(String name, Integer airportId) {
        Airport airport = this.database.obtainAirport(airportId);
        if (airport != null && name.equals(airport.getName())) {
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

    public Airport addAirport(String name) throws AirTrouble {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        if (this.getAirport(name = name.trim()) != null) {
            return this.addAirportSupervisor(name);
        }
        if (!this.canAddAirport()) {
            return this.addAirportHerder();
        }
        Airport airport = new Airport(this.database, this.grabId(), name);
        this.airportIds.add(airport.pullId());
        this.database.addAirport(airport);
        this.database.updateRouteMap(this);
        return airport;
    }

    private Airport addAirportHerder() throws AirTrouble {
        throw new AirTrouble("This route map is at capacity and will not allow additional airports.");
    }

    private Airport addAirportSupervisor(String name) throws AirTrouble {
        throw new AirTrouble("There already exists an airport named " + name + ".");
    }

    public void deleteAirport(Airport airport) {
        this.routeMapEntity.deleteAirport(airport);
    }

    public boolean canAddFlight() {
        return this.flightIds.size() < 500;
    }

    public Flight addFlight(Airport origin, Airport destination, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightLimit, int passengerLimit) {
        if (this.containsAirport(origin.pullId()) && this.containsAirport(destination.pullId()) && this.canAddFlight()) {
            return this.addFlightExecutor(origin, destination, fuelCosts, distance, travelTime, numCrewMembers, weightLimit, passengerLimit);
        }
        return null;
    }

    private Flight addFlightExecutor(Airport origin, Airport destination, int fuelCosts, int distance, int travelTime, int numCrewMembers, int weightLimit, int passengerLimit) {
        Flight flight = new Flight(this.database, origin.pullId(), destination.pullId(), this.grabId(), fuelCosts, distance, travelTime, numCrewMembers, weightLimit, passengerLimit);
        this.flightIds.add(flight.grabId());
        origin.addOriginFlight(flight);
        destination.addDestinationFlight(flight);
        this.database.addOrUpdateFlight(flight);
        this.database.updateRouteMap(this);
        return flight;
    }

    public void deleteFlight(Flight flight) {
        this.routeMapEntity.deleteFlight(flight);
    }

    public int hashCode() {
        return this.grabId();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RouteMap)) {
            return false;
        }
        RouteMap other = (RouteMap)obj;
        return this.grabId() == other.grabId();
    }

    public AirDatabase obtainDatabase() {
        return this.database;
    }

    private class RouteMapHome {
        private List<Airport> airports;
        private Integer airportId;

        public RouteMapHome(List<Airport> airports, Integer airportId) {
            this.airports = airports;
            this.airportId = airportId;
        }

        public void invoke() {
            Airport airport = RouteMap.this.database.obtainAirport(this.airportId);
            if (airport != null) {
                this.airports.add(airport);
            }
        }
    }

}

