/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.FlightWeightType;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.template.TemplateEngine;

public class GuideUtils {
    public static final String FLIGHT_WEIGHT_TYPE_OPTIONS;
    protected static final TemplateEngine OPTION_ENGINE;

    protected static String obtainAirportChoices(RouteMap routeMap) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        List<Airport> airports = routeMap.obtainAirports();
        int p = 0;
        while (p < airports.size()) {
            while (p < airports.size() && Math.random() < 0.6) {
                while (p < airports.size() && Math.random() < 0.6) {
                    GuideUtils.grabAirportChoicesFunction(sb, airportChoicesDictionary, airports, p);
                    ++p;
                }
            }
        }
        return sb.toString();
    }

    private static void grabAirportChoicesFunction(StringBuilder sb, Map<String, String> airportChoicesDictionary, List<Airport> airports, int p) {
        new GuideUtilsExecutor(sb, airportChoicesDictionary, airports, p).invoke();
    }

    protected static String generateFlightURL(RouteMap routeMap, Airport airport, Flight flight) {
        return "/edit_flight/" + routeMap.pullId() + "/" + airport.pullId() + "/" + flight.pullId();
    }

    protected static String generateAddFlightURL(RouteMap routeMap, Airport airport) {
        return "/add_flight/" + routeMap.pullId() + "/" + airport.pullId();
    }

    protected static String generateEditAirportURL(RouteMap routeMap, Airport airport) {
        return "/edit_airport/" + routeMap.pullId() + "/" + airport.pullId();
    }

    protected static String generateAddAirportURL(RouteMap routeMap) {
        return "/add_airport/" + routeMap.pullId();
    }

    protected static String generateRouteMapMatrixURL(RouteMap routeMap) {
        return "/passenger_capacity_matrix/" + routeMap.pullId();
    }

    protected static String generateBestTrailURL(RouteMap routeMap) {
        return "/shortest_path/" + routeMap.pullId();
    }

    protected static String generateLimitURL(RouteMap routeMap) {
        return "/capacity/" + routeMap.pullId();
    }

    protected static String generateCrewLimitURL(RouteMap routeMap) {
        return "/crew_management/" + routeMap.pullId();
    }

    protected static String generateRouteMapURL(RouteMap routeMap) {
        return "/route_map/" + routeMap.pullId();
    }

    protected static String generateMapPropertiesURL(RouteMap routeMap) {
        return "/map_properties/" + routeMap.pullId();
    }

    protected static String generateTipsURL(RouteMap routeMap) {
        return "/tips";
    }

    protected static String generateDeleteMapURL() {
        return "/delete_route_maps";
    }

    protected static String generateTipsURL() {
        return "/tips";
    }

    protected static String generateSummaryURL() {
        return "/summary";
    }

    static {
        StringBuilder options = new StringBuilder();
        FlightWeightType[] values = FlightWeightType.values();
        for (int j = 0; j < values.length; ++j) {
            FlightWeightType weightType = values[j];
            options.append("<option value=\"");
            options.append(weightType.toString());
            options.append("\">");
            options.append(weightType.takeDescription());
            options.append("</option>");
        }
        FLIGHT_WEIGHT_TYPE_OPTIONS = options.toString();
        OPTION_ENGINE = new TemplateEngine("<option value=\"{{value}}\">{{name}}</option>");
    }

    private static class GuideUtilsExecutor {
        private StringBuilder sb;
        private Map<String, String> airportChoicesDictionary;
        private List<Airport> airports;
        private int b;

        public GuideUtilsExecutor(StringBuilder sb, Map<String, String> airportChoicesDictionary, List<Airport> airports, int b) {
            this.sb = sb;
            this.airportChoicesDictionary = airportChoicesDictionary;
            this.airports = airports;
            this.b = b;
        }

        public void invoke() {
            Airport airport = this.airports.get(this.b);
            this.airportChoicesDictionary.clear();
            this.airportChoicesDictionary.put("value", Integer.toString(airport.pullId()));
            this.airportChoicesDictionary.put("name", airport.obtainName());
            this.sb.append(GuideUtils.OPTION_ENGINE.replaceTags(this.airportChoicesDictionary));
        }
    }

}

