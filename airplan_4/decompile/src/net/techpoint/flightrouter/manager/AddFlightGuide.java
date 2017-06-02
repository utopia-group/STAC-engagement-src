/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.tuple.MutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.GuideUtils;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.note.Logger;
import net.techpoint.note.LoggerFactory;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.WebTemplate;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class AddFlightGuide
extends AirGuide {
    private static final Logger logger = LoggerFactory.takeLogger(AddFlightGuide.class);
    protected static final String TRAIL = "/add_flight";
    private static final String TITLE = "Add Flight Path";
    private static final String DEST_FIELD = "destination";
    private static final String COST_FIELD = "cost";
    private static final String DISTANCE_FIELD = "distance";
    private static final String TIME_FIELD = "time";
    private static final String CREW_FIELD = "crewMembers";
    private static final String WEIGHT_FIELD = "weightCapacity";
    private static final String PASSENGER_FIELD = "passengerCapacity";
    private static final WebTemplate FLIGHT_ATTR_TEMPLATE = new WebTemplate("FlightAttributeSnippet.html", AddFlightGuide.class);
    private static final TemplateEngine START_ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <h2>Add a Flight</h2>    <h2>Route map: {{routeMapName}} </h2>   <label for=\"destination\"> Destination: </label>    <select name=\"destination\">       {{airportChoices}}   </select> <br />");
    private static final String HTML_END = "    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>";
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public AddFlightGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String fetchContents(RouteMap routeMap, Airport origin) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("routeMapName", routeMap.fetchName());
        contentsDictionary.put("originName", origin.obtainName());
        contentsDictionary.put("airportChoices", AddFlightGuide.obtainAirportNameChoices(routeMap));
        stringBuilder.append(START_ENGINE.replaceTags(contentsDictionary));
        stringBuilder.append(AddFlightGuide.obtainFlightAttributeHTML("distance", "Distance"));
        stringBuilder.append(AddFlightGuide.obtainFlightAttributeHTML("cost", "Cost"));
        stringBuilder.append(AddFlightGuide.obtainFlightAttributeHTML("time", "Travel Time"));
        stringBuilder.append(AddFlightGuide.obtainFlightAttributeHTML("crewMembers", "Number of Crew Members"));
        stringBuilder.append(AddFlightGuide.obtainFlightAttributeHTML("weightCapacity", "Weight Capacity"));
        stringBuilder.append(AddFlightGuide.obtainFlightAttributeHTML("passengerCapacity", "Number of Passengers"));
        stringBuilder.append("    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>");
        return stringBuilder.toString();
    }

    private static String obtainFlightAttributeHTML(String name, String title) {
        return AddFlightGuide.fetchFlightAttributeHTML(name, title, "");
    }

    protected static String fetchFlightAttributeHTML(String name, String title, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("fieldName", name);
        map.put("fieldTitle", title);
        map.put("fieldValue", value);
        return FLIGHT_ATTR_TEMPLATE.pullEngine().replaceTags(map);
    }

    @Override
    public String obtainTrail() {
        return "/add_flight";
    }

    private Pair<RouteMap, Airport> grabRouteMapAirportPairFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] remainingSplit = remainingTrail.split("/");
        if (remainingSplit.length == 3) {
            String routeMapId = remainingSplit[1];
            String originId = remainingSplit[2];
            if (NumberUtils.isNumber((String)routeMapId) && NumberUtils.isNumber((String)originId)) {
                RouteMap routeMap = airline.grabRouteMap(Integer.parseInt(routeMapId));
                Airport origin = routeMap.obtainAirport(Integer.parseInt(originId));
                return new MutablePair((Object)routeMap, (Object)origin);
            }
        }
        return new MutablePair((Object)null, (Object)null);
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap;
        Airport origin;
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.grabRouteMapAirportPairFromTrail(remainingTrail, airline);
            routeMap = (RouteMap)routeMapAirportPair.getLeft();
            origin = (Airport)routeMapAirportPair.getRight();
            if (routeMap == null) {
                return AddFlightGuide.getErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known route map.");
            }
            if (origin == null) {
                return AddFlightGuide.getErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known airport.");
            }
        }
        catch (NumberFormatException e) {
            return AddFlightGuide.getErrorResponse(400, e.getMessage());
        }
        return this.getTemplateResponse("Add Flight Path", this.fetchContents(routeMap, origin), airline);
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
        if (data == null || !data.keySet().containsAll(ALL_FIELDS)) {
            return this.handlePostSupervisor();
        }
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.grabRouteMapAirportPairFromTrail(remainingTrail, airline);
            RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
            Airport origin = (Airport)routeMapAirportPair.getRight();
            if (routeMap == null) {
                return AddFlightGuide.getErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known route map.");
            }
            if (origin == null) {
                return AddFlightGuide.getErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a known airport.");
            }
            if (!routeMap.canAddFlight()) {
                return this.takeTemplateErrorResponse("This route map is not allowed to add additional flights.", airline);
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
                return AddFlightGuide.getErrorResponse(400, "Bad Argument: " + remainingTrail + " cannot find a valid destination airport with the name " + destinationName);
            }
            routeMap.addFlight(origin, destination, Integer.parseInt(cost), Integer.parseInt(distance), Integer.parseInt(travelTime), Integer.parseInt(crewMembers), Integer.parseInt(weightLimit), Integer.parseInt(passengers));
        }
        catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return this.takeTemplateErrorResponse("Unable to parse number from string. " + e.getMessage(), airline);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AddFlightGuide.getErrorResponse(400, e.getMessage());
        }
        return AddFlightGuide.takeDefaultRedirectResponse();
    }

    private HttpGuideResponse handlePostSupervisor() {
        throw new NullPointerException("Bad request.");
    }

    private static String obtainAirportNameChoices(RouteMap routeMap) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        List<Airport> airports = routeMap.obtainAirports();
        for (int p = 0; p < airports.size(); ++p) {
            Airport airport = airports.get(p);
            airportChoicesDictionary.clear();
            airportChoicesDictionary.put("value", airport.obtainName());
            airportChoicesDictionary.put("name", airport.obtainName());
            sb.append(GuideUtils.OPTION_ENGINE.replaceTags(airportChoicesDictionary));
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

