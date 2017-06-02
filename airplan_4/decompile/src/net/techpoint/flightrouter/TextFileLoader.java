/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.RouteMapLoader;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;

public class TextFileLoader
implements RouteMapLoader {
    private static final int MAX_FILE_LENGTH = 12288;
    private static final String[] EXTENSIONS = new String[]{"txt"};

    @Override
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirFailure {
        RouteMap routeMap;
        routeMap = new RouteMap(database);
        File file = new File(fileName);
        if (file.length() > 12288) {
            throw new AirFailure("This route map is too large for the system.");
        }
        Scanner scanner = new Scanner(file);
        Throwable throwable = null;
        try {
            int numOfAirports = scanner.nextInt();
            for (int a = 0; a < numOfAirports; ++a) {
                String airportName = scanner.next();
                routeMap.addAirport(airportName);
            }
            int numOfFlights = scanner.nextInt();
            int j = 0;
            while (j < numOfFlights) {
                while (j < numOfFlights && Math.random() < 0.5) {
                    String originName = scanner.next();
                    String destinationName = scanner.next();
                    int fuelCosts = scanner.nextInt();
                    int distance = scanner.nextInt();
                    int travelTime = scanner.nextInt();
                    int crewMembers = scanner.nextInt();
                    int weightLimit = scanner.nextInt();
                    int passengerLimit = scanner.nextInt();
                    Airport origin = routeMap.getAirport(originName);
                    Airport destination = routeMap.getAirport(destinationName);
                    routeMap.addFlight(origin, destination, fuelCosts, distance, travelTime, crewMembers, weightLimit, passengerLimit);
                    ++j;
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

    @Override
    public List<String> takeExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}

