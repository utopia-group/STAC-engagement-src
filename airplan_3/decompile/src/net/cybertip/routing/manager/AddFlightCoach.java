/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.tuple.MutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.WebTemplate;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.note.Logger;
import net.cybertip.note.LoggerFactory;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.CoachUtils;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class AddFlightCoach
extends AirCoach {
    private static final Logger logger = LoggerFactory.takeLogger(AddFlightCoach.class);
    protected static final String PATH = "/add_flight";
    private static final String TITLE = "Add Flight Path";
    private static final String DEST_FIELD = "destination";
    private static final String COST_FIELD = "cost";
    private static final String DISTANCE_FIELD = "distance";
    private static final String TIME_FIELD = "time";
    private static final String CREW_FIELD = "crewMembers";
    private static final String WEIGHT_FIELD = "weightCapacity";
    private static final String PASSENGER_FIELD = "passengerCapacity";
    private static final WebTemplate FLIGHT_ATTR_TEMPLATE = new WebTemplate("FlightAttributeSnippet.html", AddFlightCoach.class);
    private static final TemplateEngine START_ENGINE = new TemplateEngineBuilder().setText("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <h2>Add a Flight</h2>    <h2>Route map: {{routeMapName}} </h2>   <label for=\"destination\"> Destination: </label>    <select name=\"destination\">       {{airportChoices}}   </select> <br />").makeTemplateEngine();
    private static final String HTML_END = "    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>";
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public AddFlightCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String pullContents(RouteMap routeMap, Airport origin) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("routeMapName", routeMap.pullName());
        contentsDictionary.put("originName", origin.getName());
        contentsDictionary.put("airportChoices", AddFlightCoach.takeAirportNameChoices(routeMap));
        stringBuilder.append(START_ENGINE.replaceTags(contentsDictionary));
        stringBuilder.append(AddFlightCoach.fetchFlightAttributeHTML("distance", "Distance"));
        stringBuilder.append(AddFlightCoach.fetchFlightAttributeHTML("cost", "Cost"));
        stringBuilder.append(AddFlightCoach.fetchFlightAttributeHTML("time", "Travel Time"));
        stringBuilder.append(AddFlightCoach.fetchFlightAttributeHTML("crewMembers", "Number of Crew Members"));
        stringBuilder.append(AddFlightCoach.fetchFlightAttributeHTML("weightCapacity", "Weight Capacity"));
        stringBuilder.append(AddFlightCoach.fetchFlightAttributeHTML("passengerCapacity", "Number of Passengers"));
        stringBuilder.append("    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>");
        return stringBuilder.toString();
    }

    private static String fetchFlightAttributeHTML(String name, String title) {
        return AddFlightCoach.takeFlightAttributeHTML(name, title, "");
    }

    protected static String takeFlightAttributeHTML(String name, String title, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("fieldName", name);
        map.put("fieldTitle", title);
        map.put("fieldValue", value);
        return FLIGHT_ATTR_TEMPLATE.getEngine().replaceTags(map);
    }

    @Override
    public String grabPath() {
        return "/add_flight";
    }

    private Pair<RouteMap, Airport> takeRouteMapAirportPairFromPath(String remainingPath, Airline airline) throws NumberFormatException {
        String[] remainingSplit = remainingPath.split("/");
        if (remainingSplit.length == 3) {
            String routeMapId = remainingSplit[1];
            String originId = remainingSplit[2];
            if (NumberUtils.isNumber((String)routeMapId) && NumberUtils.isNumber((String)originId)) {
                return this.grabRouteMapAirportPairFromPathGateKeeper(airline, routeMapId, originId);
            }
        }
        return new MutablePair((Object)null, (Object)null);
    }

    private Pair<RouteMap, Airport> grabRouteMapAirportPairFromPathGateKeeper(Airline airline, String routeMapId, String originId) {
        RouteMap routeMap = airline.obtainRouteMap(Integer.parseInt(routeMapId));
        Airport origin = routeMap.obtainAirport(Integer.parseInt(originId));
        return new MutablePair((Object)routeMap, (Object)origin);
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        RouteMap routeMap;
        Airport origin;
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.takeRouteMapAirportPairFromPath(remainingPath, airline);
            routeMap = (RouteMap)routeMapAirportPair.getLeft();
            origin = (Airport)routeMapAirportPair.getRight();
            if (routeMap == null) {
                return AddFlightCoach.obtainErrorResponse(400, "Bad URL: " + remainingPath + " is not associated with a known route map.");
            }
            if (origin == null) {
                return AddFlightCoach.obtainErrorResponse(400, "Bad URL: " + remainingPath + " is not associated with a known airport.");
            }
        }
        catch (NumberFormatException e) {
            return AddFlightCoach.obtainErrorResponse(400, e.getMessage());
        }
        return this.grabTemplateResponse("Add Flight Path", this.pullContents(routeMap, origin), airline);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline airline) {
        Map<String, List<String>> data = MultipartHelper.pullMultipartValues(httpExchange, ALL_FIELDS);
        if (data == null || !data.keySet().containsAll(ALL_FIELDS)) {
            return this.handlePostAid();
        }
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.takeRouteMapAirportPairFromPath(remainingPath, airline);
            RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
            Airport origin = (Airport)routeMapAirportPair.getRight();
            if (routeMap == null) {
                return AddFlightCoach.obtainErrorResponse(400, "Bad URL: " + remainingPath + " is not associated with a known route map.");
            }
            if (origin == null) {
                return AddFlightCoach.obtainErrorResponse(400, "Bad URL: " + remainingPath + " is not associated with a known airport.");
            }
            if (!routeMap.canAddFlight()) {
                return this.pullTemplateErrorResponse("This route map is not allowed to add additional flights.", airline);
            }
            String destinationName = data.get("destination").get(0);
            String distance = data.get("distance").get(0);
            String cost = data.get("cost").get(0);
            String travelTime = data.get("time").get(0);
            String crewMembers = data.get("crewMembers").get(0);
            String weightLimit = data.get("weightCapacity").get(0);
            String passengers = data.get("passengerCapacity").get(0);
            Airport destination = routeMap.getAirport(destinationName);
            if (destination == null) {
                return AddFlightCoach.obtainErrorResponse(400, "Bad Argument: " + remainingPath + " cannot find a valid destination airport with the name " + destinationName);
            }
            routeMap.addFlight(origin, destination, Integer.parseInt(cost), Integer.parseInt(distance), Integer.parseInt(travelTime), Integer.parseInt(crewMembers), Integer.parseInt(weightLimit), Integer.parseInt(passengers));
        }
        catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return this.pullTemplateErrorResponse("Unable to parse number from string. " + e.getMessage(), airline);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AddFlightCoach.obtainErrorResponse(400, e.getMessage());
        }
        return AddFlightCoach.obtainDefaultRedirectResponse();
    }

    private HttpCoachResponse handlePostAid() {
        throw new NullPointerException("Bad request.");
    }

    private static String takeAirportNameChoices(RouteMap routeMap) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        List<Airport> airports = routeMap.takeAirports();
        int p = 0;
        while (p < airports.size()) {
            while (p < airports.size() && Math.random() < 0.6) {
                while (p < airports.size() && Math.random() < 0.6) {
                    Airport airport = airports.get(p);
                    airportChoicesDictionary.clear();
                    airportChoicesDictionary.put("value", airport.getName());
                    airportChoicesDictionary.put("name", airport.getName());
                    sb.append(CoachUtils.OPTION_ENGINE.replaceTags(airportChoicesDictionary));
                    ++p;
                }
            }
        }
        return sb.toString();
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

