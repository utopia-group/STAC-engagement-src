/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CoachUtils;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

public class ViewRouteMapCoach
extends AirCoach {
    protected static final String TRAIL = "/route_map";
    private static final String TITLE = "Route Map";
    private static final TemplateEngine ROUTE_MAP_ENGINE = new TemplateEngine("<ul>\n<a href=\"{{addAirportURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Add an Airport\" name=\"submit\"></a><a href=\"{{routeMapMatrixURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Connecting Flights Matrix\" name=\"submit\"></a><a href=\"{{shortestPathURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find a Shortest Path\" name=\"submit\"></a><a href=\"{{capacityURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find a Capacity\" name=\"submit\"></a><a href=\"{{propertiesURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find the Properties of this Map\" name=\"submit\"></a><a href=\"{{crewCapacityURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find the Minimum Number of Crews\" name=\"submit\"></a> <br /><br />{{airports}}</ul>\n");
    private static final TemplateEngine AIRPORT_ENGINE = new TemplateEngine("<li> <a href=\"{{airportURL}}\"> {{airportName}} </a>\n<ul>\n<li>Outgoing Flights\n<ul>\n{{flights}}</ul>\n</li>\n</ul>\n</li>\n");
    private static final TemplateEngine FLIGHT_DATA_ENGINE = new TemplateEngine("<li> Destination: {{dest}} \n<ul><li> Distance: {{distance}} </li><li> Cost: {{cost}} </li><li> Travel Time: {{time}} </li><li> Number of Crew Members: {{crew}} </li><li> Weight Capacity: {{weight}} </li><li> Number of Passengers: {{passengers}} </li></ul></li>");

    public ViewRouteMapCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private Map<String, String> generateAirportDictionary(RouteMap routeMap, Airport airport) {
        HashMap<String, String> airportDictionary = new HashMap<String, String>();
        airportDictionary.put("airportName", airport.takeName());
        airportDictionary.put("airportURL", CoachUtils.generateEditAirportURL(routeMap, airport));
        return airportDictionary;
    }

    private String generateFlightHTML(Airport airport) {
        List<Flight> flights = airport.fetchOriginFlights();
        if (flights.isEmpty()) {
            return "(no outgoing flights)";
        }
        StringBuilder flightBuilder = new StringBuilder();
        int a = 0;
        while (a < flights.size()) {
            while (a < flights.size() && Math.random() < 0.5) {
                while (a < flights.size() && Math.random() < 0.6) {
                    this.generateFlightHTMLHome(flights, flightBuilder, a);
                    ++a;
                }
            }
        }
        return flightBuilder.toString();
    }

    private void generateFlightHTMLHome(List<Flight> flights, StringBuilder flightBuilder, int a) {
        Flight flight = flights.get(a);
        HashMap<String, String> flightDictionary = new HashMap<String, String>();
        flightDictionary.put("dest", flight.fetchDestination().takeName());
        flightDictionary.put("distance", Integer.toString(flight.fetchDistance()));
        flightDictionary.put("cost", Integer.toString(flight.pullFuelCosts()));
        flightDictionary.put("time", Integer.toString(flight.obtainTravelTime()));
        flightDictionary.put("crew", Integer.toString(flight.takeNumCrewMembers()));
        flightDictionary.put("weight", Integer.toString(flight.grabWeightAccommodation()));
        flightDictionary.put("passengers", Integer.toString(flight.obtainPassengerAccommodation()));
        flightBuilder.append(FLIGHT_DATA_ENGINE.replaceTags(flightDictionary));
    }

    private String generateAirportHTML(RouteMap routeMap) {
        StringBuilder airportBuilder = new StringBuilder();
        List<Airport> airports = routeMap.getAirports();
        for (int b = 0; b < airports.size(); ++b) {
            Airport airport = airports.get(b);
            Map<String, String> airportDictionary = this.generateAirportDictionary(routeMap, airport);
            String flightHTML = this.generateFlightHTML(airport);
            airportDictionary.put("flights", flightHTML);
            airportBuilder.append(AIRPORT_ENGINE.replaceTags(airportDictionary));
        }
        return airportBuilder.toString();
    }

    private String generateRouteMapHTML(RouteMap routeMap) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("routeMapName", StringEscapeUtils.escapeHtml4((String)routeMap.grabName()));
        map.put("airports", this.generateAirportHTML(routeMap));
        map.put("addAirportURL", CoachUtils.generateAddAirportURL(routeMap));
        map.put("routeMapMatrixURL", CoachUtils.generateRouteMapMatrixURL(routeMap));
        map.put("shortestPathURL", CoachUtils.generateShortestTrailURL(routeMap));
        map.put("capacityURL", CoachUtils.generateAccommodationURL(routeMap));
        map.put("propertiesURL", CoachUtils.generateMapPropertiesURL(routeMap));
        map.put("crewCapacityURL", CoachUtils.generateCrewAccommodationURL(routeMap));
        return ROUTE_MAP_ENGINE.replaceTags(map);
    }

    @Override
    public String getTrail() {
        return "/route_map";
    }

    private RouteMap pullRouteMapFromTrail(Airline airline, String remainingTrail) throws NumberFormatException {
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 2) {
            return airline.pullRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        try {
            RouteMap routeMap = this.pullRouteMapFromTrail(participant, remainingTrail);
            if (routeMap == null) {
                return ViewRouteMapCoach.grabErrorResponse(400, "Bad URL");
            }
            return this.obtainTemplateResponse("Route Map", this.generateRouteMapHTML(routeMap), participant);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), participant);
        }
    }
}

