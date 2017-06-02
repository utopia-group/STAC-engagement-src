/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.RouteMapLoader;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;

public class TextFileLoader
implements RouteMapLoader {
    private static final int MAX_FILE_LENGTH = 12288;
    private static final String[] EXTENSIONS = new String[]{"txt"};

    @Override
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirTrouble {
        RouteMap routeMap;
        routeMap = new RouteMap(database);
        File file = new File(fileName);
        if (file.length() > 12288) {
            return new TextFileLoaderFunction().invoke();
        }
        Scanner scanner = new Scanner(file);
        Throwable throwable = null;
        try {
            int numOfAirports = scanner.nextInt();
            for (int q = 0; q < numOfAirports; ++q) {
                this.loadRouteMapExecutor(routeMap, scanner);
            }
            int numOfFlights = scanner.nextInt();
            for (int c = 0; c < numOfFlights; ++c) {
                this.loadRouteMapHelp(routeMap, scanner);
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

    private void loadRouteMapHelp(RouteMap routeMap, Scanner scanner) {
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
    }

    private void loadRouteMapExecutor(RouteMap routeMap, Scanner scanner) throws AirTrouble {
        String airportName = scanner.next();
        routeMap.addAirport(airportName);
    }

    @Override
    public List<String> takeExtensions() {
        return Arrays.asList(EXTENSIONS);
    }

    private class TextFileLoaderFunction {
        private TextFileLoaderFunction() {
        }

        public RouteMap invoke() throws AirTrouble {
            throw new AirTrouble("This route map is too large for the system.");
        }
    }

}

