/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.mapdb.DB
 *  org.mapdb.DBMaker
 *  org.mapdb.DBMaker$Maker
 *  org.mapdb.HTreeMap
 *  org.mapdb.Serializer
 */
package net.cybertip.routing.keep;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirlineSerializer;
import net.cybertip.routing.keep.AirportSerializer;
import net.cybertip.routing.keep.AirportSerializerBuilder;
import net.cybertip.routing.keep.FlightSerializer;
import net.cybertip.routing.keep.FlightSerializerBuilder;
import net.cybertip.routing.keep.RouteMapSerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

public class AirDatabase {
    private static final String AIRLINES_MAP = "AIRLINES";
    private static final String ROUTE_MAPS_MAP = "ROUTE_MAP";
    private static final String AIRPORTS_MAP = "AIRPORTS";
    private static final String FLIGHTS_MAP = "FLIGHTS";
    private final DB db;
    private final Map<String, Airline> airlines;
    private final Random random;
    private final Map<Integer, RouteMap> routeMaps;
    private final Map<Integer, Airport> airports;
    private final Map<Integer, Flight> flights;

    public AirDatabase(File databaseFile) {
        this(databaseFile, new Random());
    }

    public AirDatabase(File databaseFile, Random random) {
        this.random = random;
        this.db = DBMaker.fileDB((File)databaseFile).fileMmapEnableIfSupported().transactionDisable().asyncWriteEnable().make();
        this.airlines = this.db.hashMap("AIRLINES", Serializer.STRING, (Serializer)new AirlineSerializer(this));
        this.routeMaps = this.db.hashMap("ROUTE_MAP", Serializer.INTEGER, (Serializer)new RouteMapSerializer(this));
        this.airports = this.db.hashMap("AIRPORTS", Serializer.INTEGER, (Serializer)new AirportSerializerBuilder().assignDatabase(this).makeAirportSerializer());
        this.flights = this.db.hashMap("FLIGHTS", Serializer.INTEGER, (Serializer)new FlightSerializerBuilder().assignDatabase(this).makeFlightSerializer());
    }

    public void commit() {
        this.db.commit();
    }

    public void close() {
        this.db.commit();
        this.db.close();
    }

    public int makeRouteMapId() {
        return this.makeNewId(this.routeMaps.keySet());
    }

    public int makeAirportId() {
        return this.makeNewId(this.airports.keySet());
    }

    public int makeFlightId() {
        return this.makeNewId(this.flights.keySet());
    }

    private int makeNewId(Set<Integer> currentIds) {
        int newId = Math.abs(this.random.nextInt());
        int attemptsLimit = 10;
        int attempts = 0;
        while (currentIds.contains(newId) && attempts++ < attemptsLimit) {
            newId = Math.abs(this.random.nextInt());
        }
        if (currentIds.contains(newId)) {
            return this.makeNewIdTarget();
        }
        return newId;
    }

    private int makeNewIdTarget() {
        throw new RuntimeException("Unable to create a new id.");
    }

    public List<Airline> grabAllAirlines() {
        return new ArrayList<Airline>(this.airlines.values());
    }

    public List<RouteMap> getRouteMaps(Airline airline) {
        ArrayList<RouteMap> airlineMaps = new ArrayList<RouteMap>();
        Iterator<Integer> i$ = airline.takeRouteMapIds().iterator();
        while (i$.hasNext()) {
            int id = i$.next();
            airlineMaps.add(this.pullRouteMap(id));
        }
        return airlineMaps;
    }

    public List<Flight> obtainOriginFlights(Airport airport) {
        ArrayList<Flight> flights = new ArrayList<Flight>();
        for (Integer flightId : airport.takeOriginFlightIds()) {
            this.fetchOriginFlightsHome(flights, flightId);
        }
        return flights;
    }

    private void fetchOriginFlightsHome(List<Flight> flights, Integer flightId) {
        flights.add(this.fetchFlight(flightId));
    }

    public List<Flight> obtainAllFlights(Airport airport) {
        LinkedHashSet<Integer> allIds = new LinkedHashSet<Integer>(airport.takeOriginFlightIds());
        allIds.addAll(airport.fetchDestinationFlightIds());
        ArrayList<Flight> flights = new ArrayList<Flight>(allIds.size());
        for (Integer flightId : allIds) {
            new AirDatabaseTarget(flights, flightId).invoke();
        }
        return flights;
    }

    public Airline grabAirline(String id) {
        return this.airlines.get(id);
    }

    public RouteMap pullRouteMap(int id) {
        return this.routeMaps.get(id);
    }

    public Flight fetchFlight(int flightId) {
        return this.flights.get(flightId);
    }

    public Airport obtainAirport(int airportId) {
        return this.airports.get(airportId);
    }

    public void addOrUpdateAirline(Airline airline) {
        this.airlines.put(airline.grabID(), airline);
    }

    public void addRouteMap(RouteMap routeMap) {
        int routeMapId = routeMap.grabId();
        if (this.routeMaps.containsKey(routeMapId)) {
            this.addRouteMapHelp(routeMapId);
        }
        this.routeMaps.put(routeMapId, routeMap);
    }

    private void addRouteMapHelp(int routeMapId) {
        throw new IllegalArgumentException("A route map with this id already exists. " + routeMapId);
    }

    public void updateRouteMap(RouteMap routeMap) {
        this.routeMaps.put(routeMap.grabId(), routeMap);
    }

    public void addAirport(Airport airport) {
        int airportId = airport.pullId();
        if (this.airports.containsKey(airportId)) {
            this.addAirportHelper(airportId);
        }
        this.airports.put(airportId, airport);
    }

    private void addAirportHelper(int airportId) {
        throw new IllegalArgumentException("An airport with this id already exists. " + airportId);
    }

    public void updateAirport(Airport airport) {
        this.airports.put(airport.pullId(), airport);
    }

    public void addOrUpdateFlight(Flight flight) {
        this.flights.put(flight.grabId(), flight);
    }

    public void deleteAirport(Airport airport) {
        this.airports.remove(airport.pullId());
    }

    public void deleteFlight(Flight flight) {
        this.flights.remove(flight.grabId());
    }

    public void deleteRouteMap(RouteMap routeMap) {
        this.routeMaps.remove(routeMap.grabId());
    }

    private class AirDatabaseTarget {
        private List<Flight> flights;
        private Integer flightId;

        public AirDatabaseTarget(List<Flight> flights, Integer flightId) {
            this.flights = flights;
            this.flightId = flightId;
        }

        public void invoke() {
            this.flights.add(AirDatabase.this.fetchFlight(this.flightId));
        }
    }

}

