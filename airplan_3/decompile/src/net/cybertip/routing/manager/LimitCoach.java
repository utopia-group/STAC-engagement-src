/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.GraphDelegate;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.FlightWeightType;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.CoachUtils;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class LimitCoach
extends AirCoach {
    protected static final String PATH = "/capacity";
    private static final String TITLE = "Capacity";
    private static final Double NOT_CONNECTED_CAPACITY = 0.0;
    private static final String ORIGIN_FIELD = "origin";
    private static final String DESTINATION_FIELD = "destination";
    private static final String WEIGHT_TYPE_FIELD = "weight";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngineBuilder().setText("<p>Find the capacity between the origin and the destination for a flight weight type</p><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">   <ul>    <li>       <label for=\"origin\"> Origin: </label>       <select name=\"origin\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"destination\"> Destination: </label>       <select name=\"destination\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"weight\">Weight type: </label>       <select name=\"weight\">           {{weightTypeChoices}}       </select>    </li>   </ul>    <input type=\"submit\" value=\"Find the Maximum Capacity\" name=\"submit\" id=\"submit\" /></form>").makeTemplateEngine();
    private static final TemplateEngine RESULTS_PAGE_ENGINE = new TemplateEngineBuilder().setText("<p>This describes the maximum \"{{lowerCaseWeightLabel}}\" capacity between {{origin}} and {{destination}}</p><ul>\n<li>Origin: {{origin}}</li><li>Destination: {{destination}}</li><li>{{weightLabel}} Capacity: {{capacity}}</li></ul>\n").makeTemplateEngine();
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public LimitCoach(AirDatabase database, WebSessionService webSessionService) {
        super(database, webSessionService);
    }

    @Override
    public String grabPath() {
        return "/capacity";
    }

    private String grabContents(RouteMap routeMap) {
        HashMap<String, String> choicesDictionary = new HashMap<String, String>();
        choicesDictionary.put("airportChoices", CoachUtils.takeAirportChoices(routeMap));
        choicesDictionary.put("weightTypeChoices", CoachUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(choicesDictionary);
    }

    private RouteMap fetchRouteMapFromPath(String remainingPath, Airline airline) throws NumberFormatException {
        String[] urlSplit = remainingPath.split("/");
        if (urlSplit.length == 2) {
            return airline.obtainRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    private String grabContents(RouteMap routeMap, Airport origin, Airport dest, FlightWeightType weightType, String weightLabel) throws AirTrouble {
        GraphDelegate graphDelegate = new GraphDelegate(routeMap, weightType);
        double limit = graphDelegate.fetchLimit(origin, dest);
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("origin", origin.getName());
        contentsDictionary.put("destination", dest.getName());
        contentsDictionary.put("weightLabel", weightLabel);
        contentsDictionary.put("lowerCaseWeightLabel", weightLabel.toLowerCase());
        if (limit == NOT_CONNECTED_CAPACITY) {
            this.takeContentsHelp(contentsDictionary);
        } else {
            contentsDictionary.put("capacity", Double.toString(limit));
        }
        return RESULTS_PAGE_ENGINE.replaceTags(contentsDictionary);
    }

    private void takeContentsHelp(Map<String, String> contentsDictionary) {
        contentsDictionary.put("capacity", "No Capacity. The airports are not connected.");
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        try {
            RouteMap routeMap = this.fetchRouteMapFromPath(remainingPath, airline);
            if (routeMap == null) {
                return LimitCoach.obtainErrorResponse(400, "This route map does not exist.");
            }
            return this.grabTemplateResponse("Capacity", this.grabContents(routeMap), airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline airline) {
        RouteMap routeMap = this.fetchRouteMapFromPath(remainingPath, airline);
        if (routeMap == null) {
            return LimitCoach.obtainErrorResponse(400, "This route map does not exist.");
        }
        Map<String, List<String>> data = MultipartHelper.pullMultipartValues(httpExchange, ALL_FIELDS);
        if (!(data.containsKey("origin") && data.containsKey("destination") && data.containsKey("weight"))) {
            return LimitCoach.obtainErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        int originId = Integer.parseInt(data.get("origin").get(0));
        Airport origin = routeMap.obtainAirport(originId);
        int destId = Integer.parseInt(data.get("destination").get(0));
        Airport destination = routeMap.obtainAirport(destId);
        String weightTypeStr = data.get("weight").get(0);
        FlightWeightType weightType = FlightWeightType.fromString(weightTypeStr);
        if (origin == null || destination == null || weightType == null) {
            return LimitCoach.obtainErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        try {
            return this.grabTemplateResponse("Capacity", this.grabContents(routeMap, origin, destination, weightType, weightType.grabDescription()), airline);
        }
        catch (AirTrouble e) {
            return LimitCoach.obtainErrorResponse(400, e.getMessage());
        }
    }

    static {
        ALL_FIELDS.add("origin");
        ALL_FIELDS.add("destination");
        ALL_FIELDS.add("weight");
    }
}

