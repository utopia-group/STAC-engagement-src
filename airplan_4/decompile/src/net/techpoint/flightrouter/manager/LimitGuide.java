/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.SchemeAdapter;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.GuideUtils;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.FlightWeightType;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;

public class LimitGuide
extends AirGuide {
    protected static final String TRAIL = "/capacity";
    private static final String TITLE = "Capacity";
    private static final Double NOT_CONNECTED_CAPACITY = 0.0;
    private static final String ORIGIN_FIELD = "origin";
    private static final String DESTINATION_FIELD = "destination";
    private static final String WEIGHT_TYPE_FIELD = "weight";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngine("<p>Find the capacity between the origin and the destination for a flight weight type</p><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">   <ul>    <li>       <label for=\"origin\"> Origin: </label>       <select name=\"origin\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"destination\"> Destination: </label>       <select name=\"destination\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"weight\">Weight type: </label>       <select name=\"weight\">           {{weightTypeChoices}}       </select>    </li>   </ul>    <input type=\"submit\" value=\"Find the Maximum Capacity\" name=\"submit\" id=\"submit\" /></form>");
    private static final TemplateEngine RESULTS_PAGE_ENGINE = new TemplateEngine("<p>This describes the maximum \"{{lowerCaseWeightLabel}}\" capacity between {{origin}} and {{destination}}</p><ul>\n<li>Origin: {{origin}}</li><li>Destination: {{destination}}</li><li>{{weightLabel}} Capacity: {{capacity}}</li></ul>\n");
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public LimitGuide(AirDatabase database, WebSessionService webSessionService) {
        super(database, webSessionService);
    }

    @Override
    public String obtainTrail() {
        return "/capacity";
    }

    private String getContents(RouteMap routeMap) {
        HashMap<String, String> choicesDictionary = new HashMap<String, String>();
        choicesDictionary.put("airportChoices", GuideUtils.obtainAirportChoices(routeMap));
        choicesDictionary.put("weightTypeChoices", GuideUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(choicesDictionary);
    }

    private RouteMap getRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 2) {
            return airline.grabRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    private String pullContents(RouteMap routeMap, Airport origin, Airport dest, FlightWeightType weightType, String weightLabel) throws AirFailure {
        SchemeAdapter schemeAdapter = new SchemeAdapter(routeMap, weightType);
        double limit = schemeAdapter.grabLimit(origin, dest);
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("origin", origin.obtainName());
        contentsDictionary.put("destination", dest.obtainName());
        contentsDictionary.put("weightLabel", weightLabel);
        contentsDictionary.put("lowerCaseWeightLabel", weightLabel.toLowerCase());
        if (limit == NOT_CONNECTED_CAPACITY) {
            this.obtainContentsCoordinator(contentsDictionary);
        } else {
            contentsDictionary.put("capacity", Double.toString(limit));
        }
        return RESULTS_PAGE_ENGINE.replaceTags(contentsDictionary);
    }

    private void obtainContentsCoordinator(Map<String, String> contentsDictionary) {
        contentsDictionary.put("capacity", "No Capacity. The airports are not connected.");
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            RouteMap routeMap = this.getRouteMapFromTrail(remainingTrail, airline);
            if (routeMap == null) {
                return LimitGuide.getErrorResponse(400, "This route map does not exist.");
            }
            return this.getTemplateResponse("Capacity", this.getContents(routeMap), airline);
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap = this.getRouteMapFromTrail(remainingTrail, airline);
        if (routeMap == null) {
            return LimitGuide.getErrorResponse(400, "This route map does not exist.");
        }
        Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
        if (!(data.containsKey("origin") && data.containsKey("destination") && data.containsKey("weight"))) {
            return LimitGuide.getErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        int originId = Integer.parseInt(data.get("origin").get(0));
        Airport origin = routeMap.obtainAirport(originId);
        int destId = Integer.parseInt(data.get("destination").get(0));
        Airport destination = routeMap.obtainAirport(destId);
        String weightTypeStr = data.get("weight").get(0);
        FlightWeightType weightType = FlightWeightType.fromString(weightTypeStr);
        if (origin == null || destination == null || weightType == null) {
            return LimitGuide.getErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        try {
            return this.getTemplateResponse("Capacity", this.pullContents(routeMap, origin, destination, weightType, weightType.takeDescription()), airline);
        }
        catch (AirFailure e) {
            return LimitGuide.getErrorResponse(400, e.getMessage());
        }
    }

    static {
        ALL_FIELDS.add("origin");
        ALL_FIELDS.add("destination");
        ALL_FIELDS.add("weight");
    }
}

