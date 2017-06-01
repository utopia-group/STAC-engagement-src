/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.ChartProxy;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CoachUtils;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.FlightWeightType;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccommodationCoach
extends AirCoach {
    protected static final String TRAIL = "/capacity";
    private static final String TITLE = "Capacity";
    private static final Double NOT_CONNECTED_CAPACITY = 0.0;
    private static final String ORIGIN_FIELD = "origin";
    private static final String DESTINATION_FIELD = "destination";
    private static final String WEIGHT_TYPE_FIELD = "weight";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngine("<p>Find the capacity between the origin and the destination for a flight weight type</p><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">   <ul>    <li>       <label for=\"origin\"> Origin: </label>       <select name=\"origin\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"destination\"> Destination: </label>       <select name=\"destination\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"weight\">Weight type: </label>       <select name=\"weight\">           {{weightTypeChoices}}       </select>    </li>   </ul>    <input type=\"submit\" value=\"Find the Maximum Capacity\" name=\"submit\" id=\"submit\" /></form>");
    private static final TemplateEngine RESULTS_PAGE_ENGINE = new TemplateEngine("<p>This describes the maximum \"{{lowerCaseWeightLabel}}\" capacity between {{origin}} and {{destination}}</p><ul>\n<li>Origin: {{origin}}</li><li>Destination: {{destination}}</li><li>{{weightLabel}} Capacity: {{capacity}}</li></ul>\n");
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public AccommodationCoach(AirDatabase database, WebSessionService webSessionService) {
        super(database, webSessionService);
    }

    @Override
    public String getTrail() {
        return "/capacity";
    }

    private String getContents(RouteMap routeMap) {
        HashMap<String, String> choicesDictionary = new HashMap<String, String>();
        choicesDictionary.put("airportChoices", CoachUtils.grabAirportChoices(routeMap));
        choicesDictionary.put("weightTypeChoices", CoachUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(choicesDictionary);
    }

    private RouteMap takeRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 2) {
            return airline.pullRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    private String obtainContents(RouteMap routeMap, Airport origin, Airport dest, FlightWeightType weightType, String weightLabel) throws AirException {
        ChartProxy chartProxy = new ChartProxy(routeMap, weightType);
        double accommodation = chartProxy.takeAccommodation(origin, dest);
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("origin", origin.takeName());
        contentsDictionary.put("destination", dest.takeName());
        contentsDictionary.put("weightLabel", weightLabel);
        contentsDictionary.put("lowerCaseWeightLabel", weightLabel.toLowerCase());
        if (accommodation == NOT_CONNECTED_CAPACITY) {
            this.pullContentsGuide(contentsDictionary);
        } else {
            this.takeContentsHome(accommodation, contentsDictionary);
        }
        return RESULTS_PAGE_ENGINE.replaceTags(contentsDictionary);
    }

    private void takeContentsHome(double accommodation, Map<String, String> contentsDictionary) {
        contentsDictionary.put("capacity", Double.toString(accommodation));
    }

    private void pullContentsGuide(Map<String, String> contentsDictionary) {
        contentsDictionary.put("capacity", "No Capacity. The airports are not connected.");
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            RouteMap routeMap = this.takeRouteMapFromTrail(remainingTrail, airline);
            if (routeMap == null) {
                return AccommodationCoach.grabErrorResponse(400, "This route map does not exist.");
            }
            return this.obtainTemplateResponse("Capacity", this.getContents(routeMap), airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap = this.takeRouteMapFromTrail(remainingTrail, airline);
        if (routeMap == null) {
            return AccommodationCoach.grabErrorResponse(400, "This route map does not exist.");
        }
        Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
        if (!(data.containsKey("origin") && data.containsKey("destination") && data.containsKey("weight"))) {
            return AccommodationCoach.grabErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        int originId = Integer.parseInt(data.get("origin").get(0));
        Airport origin = routeMap.takeAirport(originId);
        int destId = Integer.parseInt(data.get("destination").get(0));
        Airport destination = routeMap.takeAirport(destId);
        String weightTypeStr = data.get("weight").get(0);
        FlightWeightType weightType = FlightWeightType.fromString(weightTypeStr);
        if (origin == null || destination == null || weightType == null) {
            return AccommodationCoach.grabErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        try {
            return this.obtainTemplateResponse("Capacity", this.obtainContents(routeMap, origin, destination, weightType, weightType.grabDescription()), airline);
        }
        catch (AirException e) {
            return AccommodationCoach.grabErrorResponse(400, e.getMessage());
        }
    }

    static {
        ALL_FIELDS.add("origin");
        ALL_FIELDS.add("destination");
        ALL_FIELDS.add("weight");
    }
}

