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
package com.roboticcusp.organizer.save;

import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirlineSerializer;
import com.roboticcusp.organizer.save.AirportSerializer;
import com.roboticcusp.organizer.save.FlightSerializer;
import com.roboticcusp.organizer.save.RouteMapSerializer;
import com.roboticcusp.organizer.save.RouteMapSerializerBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
        this.routeMaps = this.db.hashMap("ROUTE_MAP", Serializer.INTEGER, (Serializer)new RouteMapSerializerBuilder().fixDatabase(this).composeRouteMapSerializer());
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

    public int composeRouteMapId() {
        return this.composeNewId(this.routeMaps.keySet());
    }

    public int composeAirportId() {
        return this.composeNewId(this.airports.keySet());
    }

    public int composeFlightId() {
        return this.composeNewId(this.flights.keySet());
    }

    private int composeNewId(Set<Integer> currentIds) {
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

    public List<Airline> getAllAirlines() {
        return new ArrayList<Airline>(this.airlines.values());
    }

    public List<RouteMap> getRouteMaps(Airline airline) {
        ArrayList<RouteMap> airlineMaps = new ArrayList<RouteMap>();
        Iterator<Integer> i$ = airline.grabRouteMapIds().iterator();
        while (i$.hasNext()) {
            int id = i$.next();
            this.obtainRouteMapsHerder(airlineMaps, id);
        }
        return airlineMaps;
    }

    private void obtainRouteMapsHerder(List<RouteMap> airlineMaps, int id) {
        airlineMaps.add(this.obtainRouteMap(id));
    }

    public List<Flight> takeOriginFlights(Airport airport) {
        ArrayList<Flight> flights = new ArrayList<Flight>();
        for (Integer flightId : airport.pullOriginFlightIds()) {
            this.takeOriginFlightsCoordinator(flights, flightId);
        }
        return flights;
    }

    private void takeOriginFlightsCoordinator(List<Flight> flights, Integer flightId) {
        flights.add(this.obtainFlight(flightId));
    }

    public List<Flight> fetchAllFlights(Airport airport) {
        LinkedHashSet<Integer> allIds = new LinkedHashSet<Integer>(airport.pullOriginFlightIds());
        allIds.addAll(airport.takeDestinationFlightIds());
        ArrayList<Flight> flights = new ArrayList<Flight>(allIds.size());
        for (Integer flightId : allIds) {
            this.getAllFlightsAdviser(flights, flightId);
        }
        return flights;
    }

    private void getAllFlightsAdviser(List<Flight> flights, Integer flightId) {
        flights.add(this.obtainFlight(flightId));
    }

    public Airline obtainAirline(String id) {
        return this.airlines.get(id);
    }

    public RouteMap obtainRouteMap(int id) {
        return this.routeMaps.get(id);
    }

    public Flight obtainFlight(int flightId) {
        return this.flights.get(flightId);
    }

    public Airport getAirport(int airportId) {
        return this.airports.get(airportId);
    }

    public void addOrUpdateAirline(Airline airline) {
        this.airlines.put(airline.getID(), airline);
    }

    public void addRouteMap(RouteMap routeMap) {
        int routeMapId = routeMap.getId();
        if (this.routeMaps.containsKey(routeMapId)) {
            this.addRouteMapHerder(routeMapId);
        }
        this.routeMaps.put(routeMapId, routeMap);
    }

    private void addRouteMapHerder(int routeMapId) {
        throw new IllegalArgumentException("A route map with this id already exists. " + routeMapId);
    }

    public void updateRouteMap(RouteMap routeMap) {
        this.routeMaps.put(routeMap.getId(), routeMap);
    }

    public void addAirport(Airport airport) {
        int airportId = airport.fetchId();
        if (this.airports.containsKey(airportId)) {
            this.addAirportCoach(airportId);
        }
        this.airports.put(airportId, airport);
    }

    private void addAirportCoach(int airportId) {
        throw new IllegalArgumentException("An airport with this id already exists. " + airportId);
    }

    public void updateAirport(Airport airport) {
        this.airports.put(airport.fetchId(), airport);
    }

    public void addOrUpdateFlight(Flight flight) {
        this.flights.put(flight.grabId(), flight);
    }

    public void deleteAirport(Airport airport) {
        this.airports.remove(airport.fetchId());
    }

    public void deleteFlight(Flight flight) {
        this.flights.remove(flight.grabId());
    }

    public void deleteRouteMap(RouteMap routeMap) {
        this.routeMaps.remove(routeMap.getId());
    }
}

