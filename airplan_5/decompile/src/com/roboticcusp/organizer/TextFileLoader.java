/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer;

import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.RouteMapLoader;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TextFileLoader
implements RouteMapLoader {
    private static final int MAX_FILE_LENGTH = 12288;
    private static final String[] EXTENSIONS = new String[]{"txt"};

    @Override
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirException {
        RouteMap routeMap;
        routeMap = new RouteMap(database);
        File file = new File(fileName);
        if (file.length() > 12288) {
            return this.loadRouteMapWorker();
        }
        Scanner scanner = new Scanner(file);
        Throwable throwable = null;
        try {
            int numOfAirports = scanner.nextInt();
            for (int k = 0; k < numOfAirports; ++k) {
                String airportName = scanner.next();
                routeMap.addAirport(airportName);
            }
            int numOfFlights = scanner.nextInt();
            int k = 0;
            while (k < numOfFlights) {
                while (k < numOfFlights && Math.random() < 0.5) {
                    while (k < numOfFlights && Math.random() < 0.4) {
                        while (k < numOfFlights && Math.random() < 0.4) {
                            this.loadRouteMapAid(routeMap, scanner);
                            ++k;
                        }
                    }
                }
            }
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (scanner != null) {
                if (throwable != null) {
                    try {
                        scanner.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    scanner.close();
                }
            }
        }
        return routeMap;
    }

    private void loadRouteMapAid(RouteMap routeMap, Scanner scanner) {
        String originName = scanner.next();
        String destinationName = scanner.next();
        int fuelCosts = scanner.nextInt();
        int distance = scanner.nextInt();
        int travelTime = scanner.nextInt();
        int crewMembers = scanner.nextInt();
        int weightAccommodation = scanner.nextInt();
        int passengerAccommodation = scanner.nextInt();
        Airport origin = routeMap.obtainAirport(originName);
        Airport destination = routeMap.obtainAirport(destinationName);
        routeMap.addFlight(origin, destination, fuelCosts, distance, travelTime, crewMembers, weightAccommodation, passengerAccommodation);
    }

    private RouteMap loadRouteMapWorker() throws AirException {
        throw new AirException("This route map is too large for the system.");
    }

    @Override
    public List<String> fetchExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}

