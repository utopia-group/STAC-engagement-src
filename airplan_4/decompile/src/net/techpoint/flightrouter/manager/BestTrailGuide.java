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

public class BestTrailGuide
extends AirGuide {
    protected static final String TRAIL = "/shortest_path";
    private static final String TITLE = "Shortest Path";
    private static final String ORIGIN_FIELD = "origin";
    private static final String DESTINATION_FIELD = "destination";
    private static final String WEIGHT_TYPE_FIELD = "weight-type";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">   <ul>    <li>       <label for=\"origin\"> Origin: </label>       <select name=\"origin\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"destination\"> Destination: </label>       <select name=\"destination\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"weight-type\">Weight type: </label>       <select name=\"weight-type\">           {{weightTypeChoices}}       </select>    </li>   </ul>    <input type=\"submit\" value=\"Find shortest path\" name=\"submit\" id=\"submit\" /></form>");
    private static final TemplateEngine RESULTS_PAGE_ENGINE = new TemplateEngine("<ul>\n<li>Origin: {{origin}}</li><li>Destination: {{destination}}</li><li>{{weightLabel}}: {{distance}}</li><li>Path: {{path}}</li></ul>\n");
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public BestTrailGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String obtainTrail() {
        return "/shortest_path";
    }

    private RouteMap fetchRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 2) {
            return airline.grabRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    private String pullContents(RouteMap routeMap) {
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        airportChoicesDictionary.put("airportChoices", GuideUtils.obtainAirportChoices(routeMap));
        airportChoicesDictionary.put("weightTypeChoices", GuideUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(airportChoicesDictionary);
    }

    private String obtainContents(RouteMap routeMap, Airport origin, Airport dest, FlightWeightType weightType, String weightLabel) throws AirFailure {
        SchemeAdapter schemeAdapter = new SchemeAdapter(routeMap, weightType);
        SchemeAdapter.BestTrailData bestTrailData = schemeAdapter.getBestTrail(origin, dest);
        if (bestTrailData.hasTrail()) {
            StringBuilder trail = new StringBuilder();
            List<Airport> grabAirports = bestTrailData.grabAirports();
            int c = 0;
            while (c < grabAirports.size()) {
                while (c < grabAirports.size() && Math.random() < 0.5) {
                    Airport airport = grabAirports.get(c);
                    if (trail.length() > 0) {
                        this.fetchContentsWorker(trail);
                    }
                    trail.append(airport.obtainName());
                    ++c;
                }
            }
            HashMap<String, String> contentsDictionary = new HashMap<String, String>();
            contentsDictionary.put("origin", origin.obtainName());
            contentsDictionary.put("destination", dest.obtainName());
            contentsDictionary.put("weightLabel", weightLabel);
            contentsDictionary.put("distance", Double.toString(bestTrailData.pullDistance()));
            contentsDictionary.put("path", trail.toString());
            return RESULTS_PAGE_ENGINE.replaceTags(contentsDictionary);
        }
        return origin.obtainName() + " is not connected to " + dest.obtainName() + ". There is no" + " shortest path between them.";
    }

    private void fetchContentsWorker(StringBuilder trail) {
        trail.append(" -> ");
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline user) {
        try {
            RouteMap routeMap = this.fetchRouteMapFromTrail(remainingTrail, user);
            if (routeMap == null) {
                return BestTrailGuide.getErrorResponse(400, "This route map does not exist.");
            }
            return this.getTemplateResponse("Shortest Path", this.pullContents(routeMap), user);
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), user);
        }
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline user) {
        RouteMap routeMap = this.fetchRouteMapFromTrail(remainingTrail, user);
        if (routeMap == null) {
            return BestTrailGuide.getErrorResponse(400, "This route map does not exist.");
        }
        Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
        if (!(data.containsKey("origin") && data.containsKey("destination") && data.containsKey("weight-type"))) {
            return BestTrailGuide.getErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        int originId = Integer.parseInt(data.get("origin").get(0));
        Airport origin = routeMap.obtainAirport(originId);
        int destId = Integer.parseInt(data.get("destination").get(0));
        Airport destination = routeMap.obtainAirport(destId);
        String weightTypeStr = data.get("weight-type").get(0);
        FlightWeightType weightType = FlightWeightType.fromString(weightTypeStr);
        if (origin == null || destination == null || weightType == null) {
            return BestTrailGuide.getErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        try {
            return this.getTemplateResponse("Shortest Path", this.obtainContents(routeMap, origin, destination, weightType, weightType.takeDescription()), user);
        }
        catch (AirFailure e) {
            return BestTrailGuide.getErrorResponse(400, e.getMessage());
        }
    }

    static {
        ALL_FIELDS.add("origin");
        ALL_FIELDS.add("destination");
        ALL_FIELDS.add("weight-type");
    }
}

