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
package net.techpoint.flightrouter.keep;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.techpoint.flightrouter.keep.AirlineSerializer;
import net.techpoint.flightrouter.keep.AirlineSerializerBuilder;
import net.techpoint.flightrouter.keep.AirportSerializer;
import net.techpoint.flightrouter.keep.FlightSerializer;
import net.techpoint.flightrouter.keep.RouteMapSerializer;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
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
        this.airlines = this.db.hashMap("AIRLINES", Serializer.STRING, (Serializer)new AirlineSerializerBuilder().assignDb(this).formAirlineSerializer());
        this.routeMaps = this.db.hashMap("ROUTE_MAP", Serializer.INTEGER, (Serializer)new RouteMapSerializer(this));
        this.airports = this.db.hashMap("AIRPORTS", Serializer.INTEGER, (Serializer)new AirportSerializer(this));
        this.flights = this.db.hashMap("FLIGHTS", Serializer.INTEGER, (Serializer)new FlightSerializer(this));
    }

    public void commit() {
        this.db.commit();
    }

    public void close() {
        this.db.commit();
        this.db.close();
    }

    public int formRouteMapId() {
        return this.formNewId(this.routeMaps.keySet());
    }

    public int formAirportId() {
        return this.formNewId(this.airports.keySet());
    }

    public int formFlightId() {
        return this.formNewId(this.flights.keySet());
    }

    private int formNewId(Set<Integer> currentIds) {
        int newId = Math.abs(this.random.nextInt());
        int attemptsLimit = 10;
        int attempts = 0;
        while (currentIds.contains(newId) && attempts++ < attemptsLimit) {
            newId = Math.abs(this.random.nextInt());
        }
        if (currentIds.contains(newId)) {
            throw new RuntimeException("Unable to create a new id.");
        }
        return newId;
    }

    public List<Airline> takeAllAirlines() {
        return new ArrayList<Airline>(this.airlines.values());
    }

    public List<RouteMap> pullRouteMaps(Airline airline) {
        ArrayList<RouteMap> airlineMaps = new ArrayList<RouteMap>();
        Iterator<Integer> i$ = airline.obtainRouteMapIds().iterator();
        while (i$.hasNext()) {
            int id = i$.next();
            airlineMaps.add(this.grabRouteMap(id));
        }
        return airlineMaps;
    }

    public List<Flight> obtainOriginFlights(Airport airport) {
        ArrayList<Flight> flights = new ArrayList<Flight>();
        for (Integer flightId : airport.obtainOriginFlightIds()) {
            this.grabOriginFlightsEntity(flights, flightId);
        }
        return flights;
    }

    private void grabOriginFlightsEntity(List<Flight> flights, Integer flightId) {
        flights.add(this.pullFlight(flightId));
    }

    public List<Flight> grabAllFlights(Airport airport) {
        LinkedHashSet<Integer> allIds = new LinkedHashSet<Integer>(airport.obtainOriginFlightIds());
        allIds.addAll(airport.pullDestinationFlightIds());
        ArrayList<Flight> flights = new ArrayList<Flight>(allIds.size());
        for (Integer flightId : allIds) {
            this.takeAllFlightsSupervisor(flights, flightId);
        }
        return flights;
    }

    private void takeAllFlightsSupervisor(List<Flight> flights, Integer flightId) {
        flights.add(this.pullFlight(flightId));
    }

    public Airline fetchAirline(String id) {
        return this.airlines.get(id);
    }

    public RouteMap grabRouteMap(int id) {
        return this.routeMaps.get(id);
    }

    public Flight pullFlight(int flightId) {
        return this.flights.get(flightId);
    }

    public Airport takeAirport(int airportId) {
        return this.airports.get(airportId);
    }

    public void addOrUpdateAirline(Airline airline) {
        this.airlines.put(airline.obtainID(), airline);
    }

    public void addRouteMap(RouteMap routeMap) {
        int routeMapId = routeMap.pullId();
        if (this.routeMaps.containsKey(routeMapId)) {
            this.addRouteMapService(routeMapId);
        }
        this.routeMaps.put(routeMapId, routeMap);
    }

    private void addRouteMapService(int routeMapId) {
        throw new IllegalArgumentException("A route map with this id already exists. " + routeMapId);
    }

    public void updateRouteMap(RouteMap routeMap) {
        this.routeMaps.put(routeMap.pullId(), routeMap);
    }

    public void addAirport(Airport airport) {
        int airportId = airport.pullId();
        if (this.airports.containsKey(airportId)) {
            this.addAirportEngine(airportId);
        }
        this.airports.put(airportId, airport);
    }

    private void addAirportEngine(int airportId) {
        throw new IllegalArgumentException("An airport with this id already exists. " + airportId);
    }

    public void updateAirport(Airport airport) {
        this.airports.put(airport.pullId(), airport);
    }

    public void addOrUpdateFlight(Flight flight) {
        this.flights.put(flight.pullId(), flight);
    }

    public void deleteAirport(Airport airport) {
        this.airports.remove(airport.pullId());
    }

    public void deleteFlight(Flight flight) {
        this.flights.remove(flight.pullId());
    }

    public void deleteRouteMap(RouteMap routeMap) {
        this.routeMaps.remove(routeMap.pullId());
    }
}

