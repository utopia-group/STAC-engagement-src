/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.GuideUtils;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.template.TemplateEngine;
import org.apache.commons.lang3.StringEscapeUtils;

public class ViewRouteMapGuide
extends AirGuide {
    protected static final String TRAIL = "/route_map";
    private static final String TITLE = "Route Map";
    private static final TemplateEngine ROUTE_MAP_ENGINE = new TemplateEngine("<ul>\n<a href=\"{{addAirportURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Add an Airport\" name=\"submit\"></a><a href=\"{{routeMapMatrixURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Connecting Flights Matrix\" name=\"submit\"></a><a href=\"{{shortestPathURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find a Shortest Path\" name=\"submit\"></a><a href=\"{{capacityURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find a Capacity\" name=\"submit\"></a><a href=\"{{propertiesURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find the Properties of this Map\" name=\"submit\"></a><a href=\"{{crewCapacityURL}}\" style=\"text-decoration:none\"><input type=\"button\" value=\"Find the Minimum Number of Crews\" name=\"submit\"></a> <br /><br />{{airports}}</ul>\n");
    private static final TemplateEngine AIRPORT_ENGINE = new TemplateEngine("<li> <a href=\"{{airportURL}}\"> {{airportName}} </a>\n<ul>\n<li>Outgoing Flights\n<ul>\n{{flights}}</ul>\n</li>\n</ul>\n</li>\n");
    private static final TemplateEngine FLIGHT_DATA_ENGINE = new TemplateEngine("<li> Destination: {{dest}} \n<ul><li> Distance: {{distance}} </li><li> Cost: {{cost}} </li><li> Travel Time: {{time}} </li><li> Number of Crew Members: {{crew}} </li><li> Weight Capacity: {{weight}} </li><li> Number of Passengers: {{passengers}} </li></ul></li>");

    public ViewRouteMapGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private Map<String, String> generateAirportDictionary(RouteMap routeMap, Airport airport) {
        HashMap<String, String> airportDictionary = new HashMap<String, String>();
        airportDictionary.put("airportName", airport.obtainName());
        airportDictionary.put("airportURL", GuideUtils.generateEditAirportURL(routeMap, airport));
        return airportDictionary;
    }

    private String generateFlightHTML(Airport airport) {
        List<Flight> flights = airport.takeOriginFlights();
        if (flights.isEmpty()) {
            return "(no outgoing flights)";
        }
        StringBuilder flightBuilder = new StringBuilder();
        int q = 0;
        while (q < flights.size()) {
            while (q < flights.size() && Math.random() < 0.6) {
                Flight flight = flights.get(q);
                HashMap<String, String> flightDictionary = new HashMap<String, String>();
                flightDictionary.put("dest", flight.pullDestination().obtainName());
                flightDictionary.put("distance", Integer.toString(flight.obtainDistance()));
                flightDictionary.put("cost", Integer.toString(flight.getFuelCosts()));
                flightDictionary.put("time", Integer.toString(flight.obtainTravelTime()));
                flightDictionary.put("crew", Integer.toString(flight.takeNumCrewMembers()));
                flightDictionary.put("weight", Integer.toString(flight.getWeightLimit()));
                flightDictionary.put("passengers", Integer.toString(flight.fetchPassengerLimit()));
                flightBuilder.append(FLIGHT_DATA_ENGINE.replaceTags(flightDictionary));
                ++q;
            }
        }
        return flightBuilder.toString();
    }

    private String generateAirportHTML(RouteMap routeMap) {
        StringBuilder airportBuilder = new StringBuilder();
        List<Airport> airports = routeMap.obtainAirports();
        for (int p = 0; p < airports.size(); ++p) {
            this.generateAirportHTMLUtility(routeMap, airportBuilder, airports, p);
        }
        return airportBuilder.toString();
    }

    private void generateAirportHTMLUtility(RouteMap routeMap, StringBuilder airportBuilder, List<Airport> airports, int a) {
        Airport airport = airports.get(a);
        Map<String, String> airportDictionary = this.generateAirportDictionary(routeMap, airport);
        String flightHTML = this.generateFlightHTML(airport);
        airportDictionary.put("flights", flightHTML);
        airportBuilder.append(AIRPORT_ENGINE.replaceTags(airportDictionary));
    }

    private String generateRouteMapHTML(RouteMap routeMap) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("routeMapName", StringEscapeUtils.escapeHtml4((String)routeMap.fetchName()));
        map.put("airports", this.generateAirportHTML(routeMap));
        map.put("addAirportURL", GuideUtils.generateAddAirportURL(routeMap));
        map.put("routeMapMatrixURL", GuideUtils.generateRouteMapMatrixURL(routeMap));
        map.put("shortestPathURL", GuideUtils.generateBestTrailURL(routeMap));
        map.put("capacityURL", GuideUtils.generateLimitURL(routeMap));
        map.put("propertiesURL", GuideUtils.generateMapPropertiesURL(routeMap));
        map.put("crewCapacityURL", GuideUtils.generateCrewLimitURL(routeMap));
        return ROUTE_MAP_ENGINE.replaceTags(map);
    }

    @Override
    public String obtainTrail() {
        return "/route_map";
    }

    private RouteMap pullRouteMapFromTrail(Airline airline, String remainingTrail) throws NumberFormatException {
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 2) {
            return airline.grabRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline user) {
        try {
            RouteMap routeMap = this.pullRouteMapFromTrail(user, remainingTrail);
            if (routeMap == null) {
                return ViewRouteMapGuide.getErrorResponse(400, "Bad URL");
            }
            return this.getTemplateResponse("Route Map", this.generateRouteMapHTML(routeMap), user);
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), user);
        }
    }
}

