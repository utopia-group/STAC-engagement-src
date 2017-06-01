/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.FlightWeightType;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.template.TemplateEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoachUtils {
    public static final String FLIGHT_WEIGHT_TYPE_OPTIONS;
    protected static final TemplateEngine OPTION_ENGINE;

    protected static String grabAirportChoices(RouteMap routeMap) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        List<Airport> airports = routeMap.getAirports();
        for (int q = 0; q < airports.size(); ++q) {
            CoachUtils.obtainAirportChoicesEntity(sb, airportChoicesDictionary, airports, q);
        }
        return sb.toString();
    }

    private static void obtainAirportChoicesEntity(StringBuilder sb, Map<String, String> airportChoicesDictionary, List<Airport> airports, int q) {
        Airport airport = airports.get(q);
        airportChoicesDictionary.clear();
        airportChoicesDictionary.put("value", Integer.toString(airport.fetchId()));
        airportChoicesDictionary.put("name", airport.takeName());
        sb.append(OPTION_ENGINE.replaceTags(airportChoicesDictionary));
    }

    protected static String generateFlightURL(RouteMap routeMap, Airport airport, Flight flight) {
        return "/edit_flight/" + routeMap.getId() + "/" + airport.fetchId() + "/" + flight.grabId();
    }

    protected static String generateAddFlightURL(RouteMap routeMap, Airport airport) {
        return "/add_flight/" + routeMap.getId() + "/" + airport.fetchId();
    }

    protected static String generateEditAirportURL(RouteMap routeMap, Airport airport) {
        return "/edit_airport/" + routeMap.getId() + "/" + airport.fetchId();
    }

    protected static String generateAddAirportURL(RouteMap routeMap) {
        return "/add_airport/" + routeMap.getId();
    }

    protected static String generateRouteMapMatrixURL(RouteMap routeMap) {
        return "/passenger_capacity_matrix/" + routeMap.getId();
    }

    protected static String generateShortestTrailURL(RouteMap routeMap) {
        return "/shortest_path/" + routeMap.getId();
    }

    protected static String generateAccommodationURL(RouteMap routeMap) {
        return "/capacity/" + routeMap.getId();
    }

    protected static String generateCrewAccommodationURL(RouteMap routeMap) {
        return "/crew_management/" + routeMap.getId();
    }

    protected static String generateRouteMapURL(RouteMap routeMap) {
        return "/route_map/" + routeMap.getId();
    }

    protected static String generateMapPropertiesURL(RouteMap routeMap) {
        return "/map_properties/" + routeMap.getId();
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
        for (int a = 0; a < values.length; ++a) {
            FlightWeightType weightType = values[a];
            options.append("<option value=\"");
            options.append(weightType.toString());
            options.append("\">");
            options.append(weightType.grabDescription());
            options.append("</option>");
        }
        FLIGHT_WEIGHT_TYPE_OPTIONS = options.toString();
        OPTION_ENGINE = new TemplateEngine("<option value=\"{{value}}\">{{name}}</option>");
    }
}

