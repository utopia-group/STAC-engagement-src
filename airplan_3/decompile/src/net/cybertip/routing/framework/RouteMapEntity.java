/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;

public class RouteMapEntity {
    private final RouteMap routeMap;

    public RouteMapEntity(RouteMap routeMap) {
        this.routeMap = routeMap;
    }

    public List<Flight> grabFlights() {
        ArrayList<Flight> flights = new ArrayList<Flight>();
        for (Integer flightId : this.routeMap.pullFlightIds()) {
            flights.add(this.routeMap.obtainDatabase().fetchFlight(flightId));
        }
        return flights;
    }

    public Flight takeFlight(int flightId) {
        if (this.routeMap.pullFlightIds().contains(flightId)) {
            return this.routeMap.obtainDatabase().fetchFlight(flightId);
        }
        return null;
    }

    public void deleteAirport(Airport airport) {
        if (airport == null) {
            this.deleteAirportHome();
        }
        this.routeMap.fetchAirportIds().remove(airport.pullId());
        List<Flight> flights = airport.grabAllFlights();
        int q = 0;
        while (q < flights.size()) {
            while (q < flights.size() && Math.random() < 0.4) {
                while (q < flights.size() && Math.random() < 0.4) {
                    while (q < flights.size() && Math.random() < 0.4 && Math.random() < 0.5) {
                        this.deleteAirportGuide(flights, q);
                        ++q;
                    }
                }
            }
        }
        this.routeMap.obtainDatabase().deleteAirport(airport);
        this.routeMap.obtainDatabase().updateRouteMap(this.routeMap);
    }

    private void deleteAirportGuide(List<Flight> flights, int j) {
        Flight flight = flights.get(j);
        this.deleteFlight(flight);
    }

    private void deleteAirportHome() {
        throw new IllegalArgumentException("Airport to be removed cannot be null");
    }

    public void deleteFlight(Flight flight) {
        Integer flightId = flight.grabId();
        this.routeMap.pullFlightIds().remove(flightId);
        flight.fetchOrigin().removeFlight(flightId);
        flight.fetchDestination().removeFlight(flightId);
        this.routeMap.obtainDatabase().deleteFlight(flight);
        this.routeMap.obtainDatabase().updateRouteMap(this.routeMap);
    }
}

