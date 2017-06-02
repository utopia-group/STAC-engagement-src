/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
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
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AddFlightCoach;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class EditFlightCoach
extends AirCoach {
    protected static final String PATH = "/edit_flight";
    private static final String TITLE = "Edit Flight Path";
    private static final String DELETE_FIELD = "delete";
    private static final String COST_FIELD = "cost";
    private static final String DISTANCE_FIELD = "distance";
    private static final String TIME_FIELD = "time";
    private static final String CREW_FIELD = "crewMembers";
    private static final String WEIGHT_FIELD = "weightCapacity";
    private static final String PASSENGER_FIELD = "passengerCapacity";
    private static final TemplateEngine ENGINE = new TemplateEngineBuilder().setText("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <input type=\"submit\" value=\"Delete Flight\" name=\"delete\" id=\"delete\" /></form><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <p> Origin: {{origin}}</p>    <p> Destination: {{dest}}</p>    {{flightData}}<br>    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>").makeTemplateEngine();
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public EditFlightCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String obtainContents(Flight flight) {
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("origin", flight.fetchOrigin().getName());
        contentsDictionary.put("dest", flight.fetchDestination().getName());
        contentsDictionary.put("flightData", this.generateFlightDataHTML(flight));
        return ENGINE.replaceTags(contentsDictionary);
    }

    @Override
    public String grabPath() {
        return "/edit_flight";
    }

    private String generateFlightDataHTML(Flight flight) {
        StringBuilder stringBuilder = new StringBuilder();
        String distance = Integer.toString(flight.takeDistance());
        stringBuilder.append(AddFlightCoach.takeFlightAttributeHTML("distance", "Distance", distance));
        String cost = Integer.toString(flight.fetchFuelCosts());
        stringBuilder.append(AddFlightCoach.takeFlightAttributeHTML("cost", "Cost", cost));
        String time = Integer.toString(flight.takeTravelTime());
        stringBuilder.append(AddFlightCoach.takeFlightAttributeHTML("time", "Travel Time", time));
        String crew = Integer.toString(flight.fetchNumCrewMembers());
        stringBuilder.append(AddFlightCoach.takeFlightAttributeHTML("crewMembers", "Number of Crew Members", crew));
        String weight = Integer.toString(flight.fetchWeightLimit());
        stringBuilder.append(AddFlightCoach.takeFlightAttributeHTML("weightCapacity", "Weight Capacity", weight));
        String passengers = Integer.toString(flight.getPassengerLimit());
        stringBuilder.append(AddFlightCoach.takeFlightAttributeHTML("passengerCapacity", "Number of Passengers", passengers));
        return stringBuilder.toString();
    }

    private Pair<Flight, RouteMap> fetchRouteMapFlightPairFromPath(String remainingPath, Airline airline) throws NumberFormatException {
        RouteMap routeMap;
        String[] urlSplit = remainingPath.split("/");
        if (urlSplit.length == 4 && (routeMap = airline.obtainRouteMap(Integer.parseInt(urlSplit[1]))) != null) {
            return this.getRouteMapFlightPairFromPathSupervisor(urlSplit[3], routeMap);
        }
        return new ImmutablePair((Object)null, (Object)null);
    }

    private Pair<Flight, RouteMap> getRouteMapFlightPairFromPathSupervisor(String s, RouteMap routeMap) {
        Flight flight = routeMap.takeFlight(Integer.parseInt(s));
        return new ImmutablePair((Object)flight, (Object)routeMap);
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        try {
            Pair<Flight, RouteMap> flightRouteMapPair = this.fetchRouteMapFlightPairFromPath(remainingPath, airline);
            Flight flight = (Flight)flightRouteMapPair.getLeft();
            if (flight == null) {
                return EditFlightCoach.obtainErrorResponse(400, "This flight does not exist.");
            }
            return this.grabTemplateResponse("Edit Flight Path", this.obtainContents(flight), airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        catch (NullPointerException e) {
            return EditFlightCoach.obtainErrorResponse(400, "Unable to parse the URL");
        }
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline airline) {
        try {
            Pair<Flight, RouteMap> flightRouteMapPair = this.fetchRouteMapFlightPairFromPath(remainingPath, airline);
            Flight flight = (Flight)flightRouteMapPair.getLeft();
            RouteMap routeMap = (RouteMap)flightRouteMapPair.getRight();
            if (flight == null) {
                return EditFlightCoach.obtainErrorResponse(400, "This flight does not exist.");
            }
            Map<String, List<String>> data = MultipartHelper.pullMultipartValues(httpExchange, ALL_FIELDS);
            if (data.containsKey("delete")) {
                routeMap.deleteFlight(flight);
            } else {
                this.handlePostSupervisor(flight, data);
            }
            return EditFlightCoach.obtainDefaultRedirectResponse();
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Unable to parse number from string. " + e.getMessage(), airline);
        }
        catch (NullPointerException e) {
            return EditFlightCoach.obtainErrorResponse(400, "Unable to parse the URL");
        }
    }

    private void handlePostSupervisor(Flight flight, Map<String, List<String>> data) {
        String costStr;
        String distanceStr;
        String timeStr;
        if (data.containsKey("distance") && !(distanceStr = data.get("distance").get(0)).isEmpty()) {
            this.handlePostSupervisorTarget(flight, distanceStr);
        }
        if (data.containsKey("cost") && !(costStr = data.get("cost").get(0)).isEmpty()) {
            new EditFlightCoachHelper(flight, costStr).invoke();
        }
        if (data.containsKey("time") && !(timeStr = data.get("time").get(0)).isEmpty()) {
            this.handlePostSupervisorService(flight, timeStr);
        }
        if (data.containsKey("crewMembers")) {
            this.handlePostSupervisorGateKeeper(flight, data);
        }
        if (data.containsKey("weightCapacity")) {
            this.handlePostSupervisorWorker(flight, data);
        }
        if (data.containsKey("passengerCapacity")) {
            this.handlePostSupervisorAssist(flight, data);
        }
    }

    private void handlePostSupervisorAssist(Flight flight, Map<String, List<String>> data) {
        String passengerStr = data.get("passengerCapacity").get(0);
        if (!passengerStr.isEmpty()) {
            int passengers = Integer.parseInt(passengerStr);
            flight.assignPassengerLimit(passengers);
        }
    }

    private void handlePostSupervisorWorker(Flight flight, Map<String, List<String>> data) {
        String weightStr = data.get("weightCapacity").get(0);
        if (!weightStr.isEmpty()) {
            this.handlePostSupervisorWorkerHerder(flight, weightStr);
        }
    }

    private void handlePostSupervisorWorkerHerder(Flight flight, String weightStr) {
        int weight = Integer.parseInt(weightStr);
        flight.defineWeightLimit(weight);
    }

    private void handlePostSupervisorGateKeeper(Flight flight, Map<String, List<String>> data) {
        String crewStr = data.get("crewMembers").get(0);
        if (!crewStr.isEmpty()) {
            this.handlePostSupervisorGateKeeperCoach(flight, crewStr);
        }
    }

    private void handlePostSupervisorGateKeeperCoach(Flight flight, String crewStr) {
        new EditFlightCoachHome(flight, crewStr).invoke();
    }

    private void handlePostSupervisorService(Flight flight, String timeStr) {
        int travelTime = Integer.parseInt(timeStr);
        flight.defineTravelTime(travelTime);
    }

    private void handlePostSupervisorTarget(Flight flight, String distanceStr) {
        int newDistance = Integer.parseInt(distanceStr);
        flight.assignDistance(newDistance);
    }

    static {
        ALL_FIELDS.add("delete");
        ALL_FIELDS.add("cost");
        ALL_FIELDS.add("distance");
        ALL_FIELDS.add("time");
        ALL_FIELDS.add("crewMembers");
        ALL_FIELDS.add("weightCapacity");
        ALL_FIELDS.add("passengerCapacity");
    }

    private class EditFlightCoachHome {
        private Flight flight;
        private String crewStr;

        public EditFlightCoachHome(Flight flight, String crewStr) {
            this.flight = flight;
            this.crewStr = crewStr;
        }

        public void invoke() {
            int numCrewMembers = Integer.parseInt(this.crewStr);
            this.flight.setNumCrewMembers(numCrewMembers);
        }
    }

    private class EditFlightCoachHelper {
        private Flight flight;
        private String costStr;

        public EditFlightCoachHelper(Flight flight, String costStr) {
            this.flight = flight;
            this.costStr = costStr;
        }

        public void invoke() {
            int newCost = Integer.parseInt(this.costStr);
            this.flight.defineFuelCosts(newCost);
        }
    }

}

