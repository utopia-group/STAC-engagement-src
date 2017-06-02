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

public class ShortestPathCoach
extends AirCoach {
    protected static final String PATH = "/shortest_path";
    private static final String TITLE = "Shortest Path";
    private static final String ORIGIN_FIELD = "origin";
    private static final String DESTINATION_FIELD = "destination";
    private static final String WEIGHT_TYPE_FIELD = "weight-type";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngineBuilder().setText("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">   <ul>    <li>       <label for=\"origin\"> Origin: </label>       <select name=\"origin\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"destination\"> Destination: </label>       <select name=\"destination\">           {{airportChoices}}       </select>    </li>    <li>       <label for=\"weight-type\">Weight type: </label>       <select name=\"weight-type\">           {{weightTypeChoices}}       </select>    </li>   </ul>    <input type=\"submit\" value=\"Find shortest path\" name=\"submit\" id=\"submit\" /></form>").makeTemplateEngine();
    private static final TemplateEngine RESULTS_PAGE_ENGINE = new TemplateEngineBuilder().setText("<ul>\n<li>Origin: {{origin}}</li><li>Destination: {{destination}}</li><li>{{weightLabel}}: {{distance}}</li><li>Path: {{path}}</li></ul>\n").makeTemplateEngine();
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public ShortestPathCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String grabPath() {
        return "/shortest_path";
    }

    private RouteMap grabRouteMapFromPath(String remainingPath, Airline airline) throws NumberFormatException {
        String[] urlSplit = remainingPath.split("/");
        if (urlSplit.length == 2) {
            return airline.obtainRouteMap(Integer.parseInt(urlSplit[1]));
        }
        return null;
    }

    private String obtainContents(RouteMap routeMap) {
        HashMap<String, String> airportChoicesDictionary = new HashMap<String, String>();
        airportChoicesDictionary.put("airportChoices", CoachUtils.takeAirportChoices(routeMap));
        airportChoicesDictionary.put("weightTypeChoices", CoachUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(airportChoicesDictionary);
    }

    private String fetchContents(RouteMap routeMap, Airport origin, Airport dest, FlightWeightType weightType, String weightLabel) throws AirTrouble {
        GraphDelegate graphDelegate = new GraphDelegate(routeMap, weightType);
        GraphDelegate.ShortestPathData shortestPathData = graphDelegate.takeShortestPath(origin, dest);
        if (shortestPathData.hasPath()) {
            StringBuilder path = new StringBuilder();
            List<Airport> obtainAirports = shortestPathData.obtainAirports();
            for (int k = 0; k < obtainAirports.size(); ++k) {
                this.getContentsCoordinator(path, obtainAirports, k);
            }
            HashMap<String, String> contentsDictionary = new HashMap<String, String>();
            contentsDictionary.put("origin", origin.getName());
            contentsDictionary.put("destination", dest.getName());
            contentsDictionary.put("weightLabel", weightLabel);
            contentsDictionary.put("distance", Double.toString(shortestPathData.getDistance()));
            contentsDictionary.put("path", path.toString());
            return RESULTS_PAGE_ENGINE.replaceTags(contentsDictionary);
        }
        return origin.getName() + " is not connected to " + dest.getName() + ". There is no" + " shortest path between them.";
    }

    private void getContentsCoordinator(StringBuilder path, List<Airport> obtainAirports, int b) {
        Airport airport = obtainAirports.get(b);
        if (path.length() > 0) {
            this.takeContentsCoordinatorWorker(path);
        }
        path.append(airport.getName());
    }

    private void takeContentsCoordinatorWorker(StringBuilder path) {
        path.append(" -> ");
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline member) {
        try {
            RouteMap routeMap = this.grabRouteMapFromPath(remainingPath, member);
            if (routeMap == null) {
                return ShortestPathCoach.obtainErrorResponse(400, "This route map does not exist.");
            }
            return this.grabTemplateResponse("Shortest Path", this.obtainContents(routeMap), member);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), member);
        }
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline member) {
        RouteMap routeMap = this.grabRouteMapFromPath(remainingPath, member);
        if (routeMap == null) {
            return ShortestPathCoach.obtainErrorResponse(400, "This route map does not exist.");
        }
        Map<String, List<String>> data = MultipartHelper.pullMultipartValues(httpExchange, ALL_FIELDS);
        if (!(data.containsKey("origin") && data.containsKey("destination") && data.containsKey("weight-type"))) {
            return ShortestPathCoach.obtainErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        int originId = Integer.parseInt(data.get("origin").get(0));
        Airport origin = routeMap.obtainAirport(originId);
        int destId = Integer.parseInt(data.get("destination").get(0));
        Airport destination = routeMap.obtainAirport(destId);
        String weightTypeStr = data.get("weight-type").get(0);
        FlightWeightType weightType = FlightWeightType.fromString(weightTypeStr);
        if (origin == null || destination == null || weightType == null) {
            return ShortestPathCoach.obtainErrorResponse(400, "The origin, the destination, and/or the weight type was incorrectly selected");
        }
        try {
            return this.grabTemplateResponse("Shortest Path", this.fetchContents(routeMap, origin, destination, weightType, weightType.grabDescription()), member);
        }
        catch (AirTrouble e) {
            return ShortestPathCoach.obtainErrorResponse(400, e.getMessage());
        }
    }

    static {
        ALL_FIELDS.add("origin");
        ALL_FIELDS.add("destination");
        ALL_FIELDS.add("weight-type");
    }
}

