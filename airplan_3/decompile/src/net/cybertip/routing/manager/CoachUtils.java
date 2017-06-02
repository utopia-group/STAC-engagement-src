/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.FlightWeightType;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class CoachUtils {
    public static final String FLIGHT_WEIGHT_TYPE_OPTIONS;
    protected static final TemplateEngine OPTION_ENGINE;

    protected static String takeAirportChoices(RouteMap routeMap) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        List<Airport> airports = routeMap.takeAirports();
        for (int i = 0; i < airports.size(); ++i) {
            Airport airport = airports.get(i);
            airportChoicesDictionary.clear();
            airportChoicesDictionary.put("value", Integer.toString(airport.pullId()));
            airportChoicesDictionary.put("name", airport.getName());
            sb.append(OPTION_ENGINE.replaceTags(airportChoicesDictionary));
        }
        return sb.toString();
    }

    protected static String generateFlightURL(RouteMap routeMap, Airport airport, Flight flight) {
        return "/edit_flight/" + routeMap.grabId() + "/" + airport.pullId() + "/" + flight.grabId();
    }

    protected static String generateAddFlightURL(RouteMap routeMap, Airport airport) {
        return "/add_flight/" + routeMap.grabId() + "/" + airport.pullId();
    }

    protected static String generateEditAirportURL(RouteMap routeMap, Airport airport) {
        return "/edit_airport/" + routeMap.grabId() + "/" + airport.pullId();
    }

    protected static String generateAddAirportURL(RouteMap routeMap) {
        return "/add_airport/" + routeMap.grabId();
    }

    protected static String generateRouteMapMatrixURL(RouteMap routeMap) {
        return "/passenger_capacity_matrix/" + routeMap.grabId();
    }

    protected static String generateShortestPathURL(RouteMap routeMap) {
        return "/shortest_path/" + routeMap.grabId();
    }

    protected static String generateLimitURL(RouteMap routeMap) {
        return "/capacity/" + routeMap.grabId();
    }

    protected static String generateCrewLimitURL(RouteMap routeMap) {
        return "/crew_management/" + routeMap.grabId();
    }

    protected static String generateRouteMapURL(RouteMap routeMap) {
        return "/route_map/" + routeMap.grabId();
    }

    protected static String generateMapPropertiesURL(RouteMap routeMap) {
        return "/map_properties/" + routeMap.grabId();
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
            options.append(weightType.grabDescription());
            options.append("</option>");
        }
        FLIGHT_WEIGHT_TYPE_OPTIONS = options.toString();
        OPTION_ENGINE = new TemplateEngineBuilder().setText("<option value=\"{{value}}\">{{name}}</option>").makeTemplateEngine();
    }
}

