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

public class ShortestTrailCoach
extends AirCoach {
    protected static final String TRAIL = "/shortest_path";
    private static final String TITLE = "Shortest Path";
    private static final String ORIGIN_FIELD = "origin";
    private static final String DESTINATION_FIELD = "destination";
    private static final String WEIGHT_TYPE_FIELD = "weight-type";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">   <ul>    <li>       <label for=\"origin\"> Origin: </label>       <select name=\"origin\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"destination\"> Destination: </label>       <select name=\"destination\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"weight-type\">Weight type: </label>       <select name=\"weight-type\">           {{weightTypeChoices}}       </select>    </li>   </ul>    <input type=\"submit\" value=\"Find shortest path\" name=\"submit\" id=\"submit\" /></form>");
    private static final TemplateEngine RESULTS_PAGE_ENGINE = new TemplateEngine("<ul>\n<li>Origin: {{origin}}</li><li>Destination: {{destination}}</li><li>{{weightLabel}}: {{distance}}</li><li>Path: {{path}}</li></ul>\n");
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public ShortestTrailCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String getTrail() {
        return "/shortest_path";
    }

    private RouteMap getRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 2) {
            return airline.pullRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    private String grabContents(RouteMap routeMap) {
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        airportChoicesDictionary.put("airportChoices", CoachUtils.grabAirportChoices(routeMap));
        airportChoicesDictionary.put("weightTypeChoices", CoachUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(airportChoicesDictionary);
    }

    private String takeContents(RouteMap routeMap, Airport origin, Airport dest, FlightWeightType weightType, String weightLabel) throws AirException {
        ChartProxy chartProxy = new ChartProxy(routeMap, weightType);
        ChartProxy.ShortestTrailData shortestTrailData = chartProxy.obtainShortestTrail(origin, dest);
        if (shortestTrailData.hasTrail()) {
            StringBuilder trail = new StringBuilder();
            List<Airport> obtainAirports = shortestTrailData.obtainAirports();
            for (int c = 0; c < obtainAirports.size(); ++c) {
                this.obtainContentsGuide(trail, obtainAirports, c);
            }
            HashMap<String, String> contentsDictionary = new HashMap<String, String>();
            contentsDictionary.put("origin", origin.takeName());
            contentsDictionary.put("destination", dest.takeName());
            contentsDictionary.put("weightLabel", weightLabel);
            contentsDictionary.put("distance", Double.toString(shortestTrailData.pullDistance()));
            contentsDictionary.put("path", trail.toString());
            return RESULTS_PAGE_ENGINE.replaceTags(contentsDictionary);
        }
        return origin.takeName() + " is not connected to " + dest.takeName() + ". There is no" + " shortest path between them.";
    }

    private void obtainContentsGuide(StringBuilder trail, List<Airport> obtainAirports, int k) {
        Airport airport = obtainAirports.get(k);
        if (trail.length() > 0) {
            this.pullContentsGuideAdviser(trail);
        }
        trail.append(airport.takeName());
    }

    private void pullContentsGuideAdviser(StringBuilder trail) {
        trail.append(" -> ");
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        try {
            RouteMap routeMap = this.getRouteMapFromTrail(remainingTrail, participant);
            if (routeMap == null) {
                return ShortestTrailCoach.grabErrorResponse(400, "This route map does not exist.");
            }
            return this.obtainTemplateResponse("Shortest Path", this.grabContents(routeMap), participant);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), participant);
        }
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        RouteMap routeMap = this.getRouteMapFromTrail(remainingTrail, participant);
        if (routeMap == null) {
            return ShortestTrailCoach.grabErrorResponse(400, "This route map does not exist.");
        }
        Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
        if (!(data.containsKey("origin") && data.containsKey("destination") && data.containsKey("weight-type"))) {
            return ShortestTrailCoach.grabErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        int originId = Integer.parseInt(data.get("origin").get(0));
        Airport origin = routeMap.takeAirport(originId);
        int destId = Integer.parseInt(data.get("destination").get(0));
        Airport destination = routeMap.takeAirport(destId);
        String weightTypeStr = data.get("weight-type").get(0);
        FlightWeightType weightType = FlightWeightType.fromString(weightTypeStr);
        if (origin == null || destination == null || weightType == null) {
            return ShortestTrailCoach.grabErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        try {
            return this.obtainTemplateResponse("Shortest Path", this.takeContents(routeMap, origin, destination, weightType, weightType.grabDescription()), participant);
        }
        catch (AirException e) {
            return ShortestTrailCoach.grabErrorResponse(400, e.getMessage());
        }
    }

    static {
        ALL_FIELDS.add("origin");
        ALL_FIELDS.add("destination");
        ALL_FIELDS.add("weight-type");
    }
}

