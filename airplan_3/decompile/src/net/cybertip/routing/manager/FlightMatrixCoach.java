/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.CellFormatter;
import net.cybertip.routing.manager.CoachUtils;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class FlightMatrixCoach
extends AirCoach {
    protected static final String PATH = "/passenger_capacity_matrix";
    private static final String TITLE = "Passenger Capacity Matrix";
    private static final int CELL_LENGTH = 30;
    private CellFormatter cellFormatter;
    private static final TemplateEngine CELL_ENGINE = new TemplateEngineBuilder().setText("<td>{{cell}}</td>").makeTemplateEngine();
    private static final TemplateEngine ROW_ENGINE = new TemplateEngineBuilder().setText("<tr>{{cells}}</tr>\n").makeTemplateEngine();
    private static final TemplateEngine FLIGHT_MATRIX_ENGINE = new TemplateEngineBuilder().setText("<h4>This page allows you to verify the correctness of the uploaded map. Delete this route map if it is incorrect.</h4><a href=\"{{deleteMapURL}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Delete the Map\" name=\"submit\"></a><a href=\"{{tipsURL}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Next\" name=\"submit\"></a><p>Passenger capacity between airports. Origin airports on the left, destinations on the top.</p><table width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: collapse\">\n{{rows}}</table>").makeTemplateEngine();

    public FlightMatrixCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String grabPath() {
        return "/passenger_capacity_matrix";
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        try {
            RouteMap routeMap = this.obtainRouteMapFromPath(airline, remainingPath);
            if (routeMap == null) {
                return FlightMatrixCoach.obtainErrorResponse(400, "Path " + remainingPath + " does not refer to valid routeMap");
            }
            return this.fetchTemplateResponseWithoutMenuItems("Passenger Capacity Matrix", this.routeMapAsTable(routeMap), airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
    }

    private RouteMap obtainRouteMapFromPath(Airline airline, String remainingPath) throws NumberFormatException {
        String[] urlSplit = remainingPath.split("/");
        if (urlSplit.length == 2) {
            return airline.obtainRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    private String generateFirstRow(List<Airport> airports) {
        StringBuilder builder = new StringBuilder();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cell", this.format(""));
        builder.append(CELL_ENGINE.replaceTags(map));
        for (int c = 0; c < airports.size(); ++c) {
            Airport airport = airports.get(c);
            map.clear();
            map.put("cell", this.format(airport.getName()));
            builder.append(CELL_ENGINE.replaceTags(map));
        }
        return builder.toString();
    }

    private String format(String data) {
        return this.cellFormatter.format(data, 30, CellFormatter.Justification.CENTER, true);
    }

    private String generateOneRow(Airport origin, Map<Airport, Integer> airportToFlightLimit) {
        StringBuilder builder = new StringBuilder();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cell", this.format(origin.getName()));
        builder.append(CELL_ENGINE.replaceTags(map));
        List<Flight> originFlights = origin.grabOriginFlights();
        int k = 0;
        while (k < originFlights.size()) {
            while (k < originFlights.size() && Math.random() < 0.6) {
                while (k < originFlights.size() && Math.random() < 0.5) {
                    while (k < originFlights.size() && Math.random() < 0.6) {
                        this.generateOneRowTarget(airportToFlightLimit, originFlights, k);
                        ++k;
                    }
                }
            }
        }
        for (Airport airport : airportToFlightLimit.keySet()) {
            this.generateOneRowGateKeeper(airportToFlightLimit, builder, map, airport);
        }
        String result = builder.toString();
        return result;
    }

    private void generateOneRowGateKeeper(Map<Airport, Integer> airportToFlightLimit, StringBuilder builder, Map<String, String> map, Airport airport) {
        new FlightMatrixCoachEntity(airportToFlightLimit, builder, map, airport).invoke();
    }

    private void generateOneRowTarget(Map<Airport, Integer> airportToFlightLimit, List<Flight> originFlights, int p) {
        Flight flight = originFlights.get(p);
        Airport dest = flight.fetchDestination();
        int prevFlightLimit = airportToFlightLimit.get(dest);
        int currFlightLimit = flight.getPassengerLimit();
        airportToFlightLimit.put(dest, prevFlightLimit + currFlightLimit);
    }

    private String generateRows(List<Airport> airports, List<Flight> flights) {
        StringBuilder builder = new StringBuilder();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cells", this.generateFirstRow(airports));
        builder.append(ROW_ENGINE.replaceTags(map));
        LinkedHashMap<Airport, Integer> airportToFlightLimit = new LinkedHashMap<Airport, Integer>();
        for (int c = 0; c < airports.size(); ++c) {
            Airport airport = airports.get(c);
            airportToFlightLimit.put(airport, 0);
        }
        for (int b = 0; b < airports.size(); ++b) {
            this.generateRowsService(airports, builder, map, airportToFlightLimit, b);
        }
        return builder.toString();
    }

    private void generateRowsService(List<Airport> airports, StringBuilder builder, Map<String, String> map, Map<Airport, Integer> airportToFlightLimit, int c) {
        Airport airport = airports.get(c);
        map.clear();
        LinkedHashMap<Airport, Integer> copy = new LinkedHashMap<Airport, Integer>(airportToFlightLimit);
        map.put("cells", this.generateOneRow(airport, copy));
        builder.append(ROW_ENGINE.replaceTags(map));
    }

    private String routeMapAsTable(RouteMap routeMap) {
        this.cellFormatter = new CellFormatter(30);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("rows", this.generateRows(routeMap.takeAirports(), routeMap.pullFlights()));
        map.put("deleteMapURL", CoachUtils.generateDeleteMapURL());
        map.put("tipsURL", CoachUtils.generateTipsURL(routeMap));
        return FLIGHT_MATRIX_ENGINE.replaceTags(map);
    }

    @Override
    protected String grabDisplayName(Airline member) {
        return this.format(member.grabAirlineName());
    }

    private class FlightMatrixCoachEntity {
        private Map<Airport, Integer> airportToFlightLimit;
        private StringBuilder builder;
        private Map<String, String> map;
        private Airport airport;

        public FlightMatrixCoachEntity(Map<Airport, Integer> airportToFlightLimit, StringBuilder builder, Map<String, String> map, Airport airport) {
            this.airportToFlightLimit = airportToFlightLimit;
            this.builder = builder;
            this.map = map;
            this.airport = airport;
        }

        public void invoke() {
            this.map.clear();
            String numFlights = Integer.toString(this.airportToFlightLimit.get(this.airport));
            this.map.put("cell", FlightMatrixCoach.this.format(numFlights));
            this.builder.append(CELL_ENGINE.replaceTags(this.map));
        }
    }

}

