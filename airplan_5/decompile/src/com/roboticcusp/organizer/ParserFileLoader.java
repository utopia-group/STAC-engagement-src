/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer;

import com.roboticcusp.json.simple.PARSERArray;
import com.roboticcusp.json.simple.PARSERObject;
import com.roboticcusp.json.simple.reader.PARSERGrabber;
import com.roboticcusp.json.simple.reader.ParseException;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.RouteMapLoader;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class ParserFileLoader
implements RouteMapLoader {
    private static final int MAX_FILE_LENGTH = 30720;
    private static final String[] EXTENSIONS = new String[]{"json"};

    @Override
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirException {
        RouteMap routeMap = new RouteMap(database);
        File file = new File(fileName);
        if (file.length() > 30720) {
            throw new AirException("This route map is too large for the system.");
        }
        PARSERGrabber grabber = new PARSERGrabber();
        try {
            PARSERObject parser = (PARSERObject)grabber.parse(new FileReader(file));
            PARSERArray airports = (PARSERArray)parser.get("airports");
            for (int q = 0; q < airports.size(); ++q) {
                Object airportObj = airports.get(q);
                PARSERObject airport = (PARSERObject)airportObj;
                String name = (String)airport.get("name");
                routeMap.addAirport(name);
            }
            PARSERArray flights = (PARSERArray)parser.get("flights");
            int j = 0;
            while (j < flights.size()) {
                while (j < flights.size() && Math.random() < 0.6) {
                    while (j < flights.size() && Math.random() < 0.5) {
                        new ParserFileLoaderCoach(routeMap, flights, j).invoke();
                        ++j;
                    }
                }
            }
        }
        catch (ParseException e) {
            throw new AirException(e);
        }
        catch (IOException e) {
            throw new AirException(e);
        }
        return routeMap;
    }

    @Override
    public List<String> fetchExtensions() {
        return Arrays.asList(EXTENSIONS);
    }

    private class ParserFileLoaderCoach {
        private RouteMap routeMap;
        private PARSERArray flights;
        private int c;

        public ParserFileLoaderCoach(RouteMap routeMap, PARSERArray flights, int c) {
            this.routeMap = routeMap;
            this.flights = flights;
            this.c = c;
        }

        public void invoke() {
            Object flightObj = this.flights.get(this.c);
            PARSERObject flight = (PARSERObject)flightObj;
            String originName = (String)flight.get("origin");
            String destName = (String)flight.get("dst");
            Integer cost = ((Long)flight.get("cost")).intValue();
            Integer distance = ((Long)flight.get("distance")).intValue();
            Integer travelTime = ((Long)flight.get("time")).intValue();
            Integer numOfCrewMembers = ((Long)flight.get("crew")).intValue();
            Integer weightAccommodation = ((Long)flight.get("weight")).intValue();
            Integer passengerAccommodation = ((Long)flight.get("passengers")).intValue();
            Airport origin = this.routeMap.obtainAirport(originName);
            Airport destination = this.routeMap.obtainAirport(destName);
            this.routeMap.addFlight(origin, destination, cost, distance, travelTime, numOfCrewMembers, weightAccommodation, passengerAccommodation);
        }
    }

}

