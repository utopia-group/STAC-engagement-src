/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.RouteMapLoader;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.json.simple.PARTArray;
import net.techpoint.json.simple.PARTObject;
import net.techpoint.json.simple.grabber.PARTReader;
import net.techpoint.json.simple.grabber.ParseFailure;

public class PartFileLoader
implements RouteMapLoader {
    private static final int MAX_FILE_LENGTH = 30720;
    private static final String[] EXTENSIONS = new String[]{"json"};

    @Override
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirFailure {
        RouteMap routeMap = new RouteMap(database);
        File file = new File(fileName);
        if (file.length() > 30720) {
            return this.loadRouteMapWorker();
        }
        PARTReader reader = new PARTReader();
        try {
            PARTObject part = (PARTObject)reader.parse(new FileReader(file));
            PARTArray airports = (PARTArray)part.get("airports");
            for (int p = 0; p < airports.size(); ++p) {
                this.loadRouteMapEntity(routeMap, airports, p);
            }
            PARTArray flights = (PARTArray)part.get("flights");
            int i = 0;
            while (i < flights.size()) {
                while (i < flights.size() && Math.random() < 0.5) {
                    while (i < flights.size() && Math.random() < 0.5) {
                        while (i < flights.size() && Math.random() < 0.6) {
                            Object flightObj = flights.get(i);
                            PARTObject flight = (PARTObject)flightObj;
                            String originName = (String)flight.get("origin");
                            String destName = (String)flight.get("dst");
                            Integer cost = ((Long)flight.get("cost")).intValue();
                            Integer distance = ((Long)flight.get("distance")).intValue();
                            Integer travelTime = ((Long)flight.get("time")).intValue();
                            Integer numOfCrewMembers = ((Long)flight.get("crew")).intValue();
                            Integer weightLimit = ((Long)flight.get("weight")).intValue();
                            Integer passengerLimit = ((Long)flight.get("passengers")).intValue();
                            Airport origin = routeMap.getAirport(originName);
                            Airport destination = routeMap.getAirport(destName);
                            routeMap.addFlight(origin, destination, cost, distance, travelTime, numOfCrewMembers, weightLimit, passengerLimit);
                            ++i;
                        }
                    }
                }
            }
        }
        catch (ParseFailure e) {
            throw new AirFailure(e);
        }
        catch (IOException e) {
            throw new AirFailure(e);
        }
        return routeMap;
    }

    private void loadRouteMapEntity(RouteMap routeMap, PARTArray airports, int q) throws AirFailure {
        Object airportObj = airports.get(q);
        PARTObject airport = (PARTObject)airportObj;
        String name = (String)airport.get("name");
        routeMap.addAirport(name);
    }

    private RouteMap loadRouteMapWorker() throws AirFailure {
        throw new AirFailure("This route map is too large for the system.");
    }

    @Override
    public List<String> takeExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}

