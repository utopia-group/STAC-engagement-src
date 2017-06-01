/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.tuple.MutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.WebTemplate;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CoachUtils;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.slf4j.Logger;
import com.roboticcusp.slf4j.LoggerFactory;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class AddFlightCoach
extends AirCoach {
    private static final Logger logger = LoggerFactory.fetchLogger(AddFlightCoach.class);
    protected static final String TRAIL = "/add_flight";
    private static final String TITLE = "Add Flight Path";
    private static final String DEST_FIELD = "destination";
    private static final String COST_FIELD = "cost";
    private static final String DISTANCE_FIELD = "distance";
    private static final String TIME_FIELD = "time";
    private static final String CREW_FIELD = "crewMembers";
    private static final String WEIGHT_FIELD = "weightCapacity";
    private static final String PASSENGER_FIELD = "passengerCapacity";
    private static final WebTemplate FLIGHT_ATTR_TEMPLATE = new WebTemplate("FlightAttributeSnippet.html", AddFlightCoach.class);
    private static final TemplateEngine START_ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <h2>Add a Flight</h2>    <h2>Route map: {{routeMapName}} </h2>   <label for=\"destination\"> Destination: </label>    <select name=\"destination\">       {{airportChoices}}   </select> <br />");
    private static final String HTML_END = "    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>";
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public AddFlightCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String takeContents(RouteMap routeMap, Airport origin) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("routeMapName", routeMap.grabName());
        contentsDictionary.put("originName", origin.takeName());
        contentsDictionary.put("airportChoices", AddFlightCoach.grabAirportNameChoices(routeMap));
        stringBuilder.append(START_ENGINE.replaceTags(contentsDictionary));
        stringBuilder.append(AddFlightCoach.grabFlightAttributeHTML("distance", "Distance"));
        stringBuilder.append(AddFlightCoach.grabFlightAttributeHTML("cost", "Cost"));
        stringBuilder.append(AddFlightCoach.grabFlightAttributeHTML("time", "Travel Time"));
        stringBuilder.append(AddFlightCoach.grabFlightAttributeHTML("crewMembers", "Number of Crew Members"));
        stringBuilder.append(AddFlightCoach.grabFlightAttributeHTML("weightCapacity", "Weight Capacity"));
        stringBuilder.append(AddFlightCoach.grabFlightAttributeHTML("passengerCapacity", "Number of Passengers"));
        stringBuilder.append("    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>");
        return stringBuilder.toString();
    }

    private static String grabFlightAttributeHTML(String name, String title) {
        return AddFlightCoach.pullFlightAttributeHTML(name, title, "");
    }

    protected static String pullFlightAttributeHTML(String name, String title, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("fieldName", name);
        map.put("fieldTitle", title);
        map.put("fieldValue", value);
        return FLIGHT_ATTR_TEMPLATE.getEngine().replaceTags(map);
    }

    @Override
    public String getTrail() {
        return "/add_flight";
    }

    private Pair<RouteMap, Airport> takeRouteMapAirportPairFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] remainingSplit = remainingTrail.split("/");
        if (remainingSplit.length == 3) {
            String routeMapId = remainingSplit[1];
            String originId = remainingSplit[2];
            if (NumberUtils.isNumber((String)routeMapId) && NumberUtils.isNumber((String)originId)) {
                RouteMap routeMap = airline.pullRouteMap(Integer.parseInt(routeMapId));
                Airport origin = routeMap.takeAirport(Integer.parseInt(originId));
                return new MutablePair((Object)routeMap, (Object)origin);
            }
        }
        return new MutablePair((Object)null, (Object)null);
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        Airport origin;
        RouteMap routeMap;
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.takeRouteMapAirportPairFromTrail(remainingTrail, airline);
            routeMap = (RouteMap)routeMapAirportPair.getLeft();
            origin = (Airport)routeMapAirportPair.getRight();
            if (routeMap == null) {
                return AddFlightCoach.grabErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known route map.");
            }
            if (origin == null) {
                return AddFlightCoach.grabErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known airport.");
            }
        }
        catch (NumberFormatException e) {
            return AddFlightCoach.grabErrorResponse(400, e.getMessage());
        }
        return this.obtainTemplateResponse("Add Flight Path", this.takeContents(routeMap, origin), airline);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
        if (data == null || !data.keySet().containsAll(ALL_FIELDS)) {
            return this.handlePostGateKeeper();
        }
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.takeRouteMapAirportPairFromTrail(remainingTrail, airline);
            RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
            Airport origin = (Airport)routeMapAirportPair.getRight();
            if (routeMap == null) {
                return AddFlightCoach.grabErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known route map.");
            }
            if (origin == null) {
                return AddFlightCoach.grabErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known airport.");
            }
            if (!routeMap.canAddFlight()) {
                return this.pullTemplateErrorResponse("This route map is not allowed to add additional flights.", airline);
            }
            String destinationName = data.get("destination").get(0);
            String distance = data.get("distance").get(0);
            String cost = data.get("cost").get(0);
            String travelTime = data.get("time").get(0);
            String crewMembers = data.get("crewMembers").get(0);
            String weightAccommodation = data.get("weightCapacity").get(0);
            String passengers = data.get("passengerCapacity").get(0);
            Airport destination = routeMap.obtainAirport(destinationName);
            if (destination == null) {
                return AddFlightCoach.grabErrorResponse(400, "Bad Argument: " + remainingTrail + " cannot find a valid destination airport with the name " + destinationName);
            }
            routeMap.addFlight(origin, destination, Integer.parseInt(cost), Integer.parseInt(distance), Integer.parseInt(travelTime), Integer.parseInt(crewMembers), Integer.parseInt(weightAccommodation), Integer.parseInt(passengers));
        }
        catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return this.pullTemplateErrorResponse("Unable to parse number from string. " + e.getMessage(), airline);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AddFlightCoach.grabErrorResponse(400, e.getMessage());
        }
        return AddFlightCoach.grabDefaultRedirectResponse();
    }

    private HttpCoachResponse handlePostGateKeeper() {
        throw new NullPointerException("Bad request.");
    }

    private static String grabAirportNameChoices(RouteMap routeMap) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        List<Airport> airports = routeMap.getAirports();
        for (int k = 0; k < airports.size(); ++k) {
            AddFlightCoach.obtainAirportNameChoicesAssist(sb, airportChoicesDictionary, airports, k);
        }
        return sb.toString();
    }

    private static void obtainAirportNameChoicesAssist(StringBuilder sb, Map<String, String> airportChoicesDictionary, List<Airport> airports, int p) {
        Airport airport = airports.get(p);
        airportChoicesDictionary.clear();
        airportChoicesDictionary.put("value", airport.takeName());
        airportChoicesDictionary.put("name", airport.takeName());
        sb.append(CoachUtils.OPTION_ENGINE.replaceTags(airportChoicesDictionary));
    }

    static {
        ALL_FIELDS.add("destination");
        ALL_FIELDS.add("cost");
        ALL_FIELDS.add("distance");
        ALL_FIELDS.add("time");
        ALL_FIELDS.add("crewMembers");
        ALL_FIELDS.add("weightCapacity");
        ALL_FIELDS.add("passengerCapacity");
    }
}

