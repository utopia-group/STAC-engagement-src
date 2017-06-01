/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package edu.cyberapex.flightplanner.guide;

import com.sun.net.httpserver.HttpExchange;
import edu.cyberapex.flightplanner.framework.Airline;
import edu.cyberapex.flightplanner.framework.Airport;
import edu.cyberapex.flightplanner.framework.Flight;
import edu.cyberapex.flightplanner.framework.RouteMap;
import edu.cyberapex.flightplanner.guide.AirGuide;
import edu.cyberapex.flightplanner.guide.GuideUtils;
import edu.cyberapex.flightplanner.store.AirDatabase;
import edu.cyberapex.server.WebSessionService;
import edu.cyberapex.server.guide.HttpGuideResponse;
import edu.cyberapex.template.TemplateEngine;
import edu.cyberapex.template.TemplateEngineBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

public class ViewRouteMapGuide
extends AirGuide {
    protected static final String PATH = "/route_map";
    private static final String TITLE = "Route Map";
    private static final TemplateEngine ROUTE_MAP_ENGINE = new TemplateEngineBuilder().defineText("<ul>\n<a href=\"{{addAirportURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Add an Airport\" name=\"submit\"></a><a href=\"{{routeMapMatrixURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Connecting Flights Matrix\" name=\"submit\"></a><a href=\"{{shortestPathURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find a Shortest Path\" name=\"submit\"></a><a href=\"{{capacityURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find a Capacity\" name=\"submit\"></a><a href=\"{{propertiesURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find the Properties of this Map\" name=\"submit\"></a><a href=\"{{crewCapacityURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find the Minimum Number of Crews\" name=\"submit\"></a> <br /><br />{{airports}}</ul>\n").generateTemplateEngine();
    private static final TemplateEngine AIRPORT_ENGINE = new TemplateEngineBuilder().defineText("<li> <a href=\"{{airportURL}}\"> {{airportName}} </a>\n<ul>\n<li>Outgoing Flights\n<ul>\n{{flights}}</ul>\n</li>\n</ul>\n</li>\n").generateTemplateEngine();
    private static final TemplateEngine FLIGHT_DATA_ENGINE = new TemplateEngineBuilder().defineText("<li> Destination: {{dest}} \n<ul><li> Distance: {{distance}} </li><li> Cost: {{cost}} </li><li> Travel Time: {{time}} </li><li> Number of Crew Members: {{crew}} </li><li> Weight Capacity: {{weight}} </li><li> Number of Passengers: {{passengers}} </li></ul></li>").generateTemplateEngine();

    public ViewRouteMapGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private Map<String, String> generateAirportDictionary(RouteMap routeMap, Airport airport) {
        HashMap<String, String> airportDictionary = new HashMap<String, String>();
        airportDictionary.put("airportName", airport.getName());
        airportDictionary.put("airportURL", GuideUtils.generateEditAirportURL(routeMap, airport));
        return airportDictionary;
    }

    private String generateFlightHTML(Airport airport) {
        List<Flight> flights = airport.getOriginFlights();
        if (flights.isEmpty()) {
            return "(no outgoing flights)";
        }
        StringBuilder flightBuilder = new StringBuilder();
        for (int k = 0; k < flights.size(); ++k) {
            new ViewRouteMapGuideWorker(flights, flightBuilder, k).invoke();
        }
        return flightBuilder.toString();
    }

    private String generateAirportHTML(RouteMap routeMap) {
        StringBuilder airportBuilder = new StringBuilder();
        List<Airport> airports = routeMap.obtainAirports();
        for (int i = 0; i < airports.size(); ++i) {
            Airport airport = airports.get(i);
            Map<String, String> airportDictionary = this.generateAirportDictionary(routeMap, airport);
            String flightHTML = this.generateFlightHTML(airport);
            airportDictionary.put("flights", flightHTML);
            airportBuilder.append(AIRPORT_ENGINE.replaceTags(airportDictionary));
        }
        return airportBuilder.toString();
    }

    private String generateRouteMapHTML(RouteMap routeMap) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("routeMapName", StringEscapeUtils.escapeHtml4((String)routeMap.takeName()));
        map.put("airports", this.generateAirportHTML(routeMap));
        map.put("addAirportURL", GuideUtils.generateAddAirportURL(routeMap));
        map.put("routeMapMatrixURL", GuideUtils.generateRouteMapMatrixURL(routeMap));
        map.put("shortestPathURL", GuideUtils.generateOptimalPathURL(routeMap));
        map.put("capacityURL", GuideUtils.generateLimitURL(routeMap));
        map.put("propertiesURL", GuideUtils.generateMapPropertiesURL(routeMap));
        map.put("crewCapacityURL", GuideUtils.generateCrewLimitURL(routeMap));
        return ROUTE_MAP_ENGINE.replaceTags(map);
    }

    @Override
    public String getPath() {
        return "/route_map";
    }

    private RouteMap takeRouteMapFromPath(Airline airline, String remainingPath) throws NumberFormatException {
        String[] urlSplit = remainingPath.split("/");
        if (urlSplit.length == 2) {
            return airline.getRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    @Override
    protected HttpGuideResponse handlePull(HttpExchange httpExchange, String remainingPath, Airline member) {
        try {
            RouteMap routeMap = this.takeRouteMapFromPath(member, remainingPath);
            if (routeMap == null) {
                return ViewRouteMapGuide.getErrorResponse(400, "Bad URL");
            }
            return this.getTemplateResponse("Route Map", this.generateRouteMapHTML(routeMap), member);
        }
        catch (NumberFormatException e) {
            return this.fetchTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), member);
        }
    }

    private class ViewRouteMapGuideWorker {
        private List<Flight> flights;
        private StringBuilder flightBuilder;
        private int a;

        public ViewRouteMapGuideWorker(List<Flight> flights, StringBuilder flightBuilder, int a) {
            this.flights = flights;
            this.flightBuilder = flightBuilder;
            this.a = a;
        }

        public void invoke() {
            Flight flight = this.flights.get(this.a);
            HashMap<String, String> flightDictionary = new HashMap<String, String>();
            flightDictionary.put("dest", flight.grabDestination().getName());
            flightDictionary.put("distance", Integer.toString(flight.grabDistance()));
            flightDictionary.put("cost", Integer.toString(flight.takeFuelCosts()));
            flightDictionary.put("time", Integer.toString(flight.getTravelTime()));
            flightDictionary.put("crew", Integer.toString(flight.pullNumCrewMembers()));
            flightDictionary.put("weight", Integer.toString(flight.getWeightLimit()));
            flightDictionary.put("passengers", Integer.toString(flight.getPassengerLimit()));
            this.flightBuilder.append(FLIGHT_DATA_ENGINE.replaceTags(flightDictionary));
        }
    }

}

