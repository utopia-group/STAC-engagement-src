/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AddFlightGuide;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class EditFlightGuide
extends AirGuide {
    protected static final String TRAIL = "/edit_flight";
    private static final String TITLE = "Edit Flight Path";
    private static final String DELETE_FIELD = "delete";
    private static final String COST_FIELD = "cost";
    private static final String DISTANCE_FIELD = "distance";
    private static final String TIME_FIELD = "time";
    private static final String CREW_FIELD = "crewMembers";
    private static final String WEIGHT_FIELD = "weightCapacity";
    private static final String PASSENGER_FIELD = "passengerCapacity";
    private static final TemplateEngine ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <input type=\"submit\" value=\"Delete Flight\" name=\"delete\" id=\"delete\" /></form><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <p> Origin: {{origin}}</p>    <p> Destination: {{dest}}</p>    {{flightData}}<br>    <input type=\"submit\" value=\"Submit\" name=\"submit\" id=\"submit\"/>    <br/></form>");
    private static final Set<String> ALL_FIELDS = new HashSet<String>();

    public EditFlightGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String grabContents(Flight flight) {
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("origin", flight.getOrigin().obtainName());
        contentsDictionary.put("dest", flight.pullDestination().obtainName());
        contentsDictionary.put("flightData", this.generateFlightDataHTML(flight));
        return ENGINE.replaceTags(contentsDictionary);
    }

    @Override
    public String obtainTrail() {
        return "/edit_flight";
    }

    private String generateFlightDataHTML(Flight flight) {
        StringBuilder stringBuilder = new StringBuilder();
        String distance = Integer.toString(flight.obtainDistance());
        stringBuilder.append(AddFlightGuide.fetchFlightAttributeHTML("distance", "Distance", distance));
        String cost = Integer.toString(flight.getFuelCosts());
        stringBuilder.append(AddFlightGuide.fetchFlightAttributeHTML("cost", "Cost", cost));
        String time = Integer.toString(flight.obtainTravelTime());
        stringBuilder.append(AddFlightGuide.fetchFlightAttributeHTML("time", "Travel Time", time));
        String crew = Integer.toString(flight.takeNumCrewMembers());
        stringBuilder.append(AddFlightGuide.fetchFlightAttributeHTML("crewMembers", "Number of Crew Members", crew));
        String weight = Integer.toString(flight.getWeightLimit());
        stringBuilder.append(AddFlightGuide.fetchFlightAttributeHTML("weightCapacity", "Weight Capacity", weight));
        String passengers = Integer.toString(flight.fetchPassengerLimit());
        stringBuilder.append(AddFlightGuide.fetchFlightAttributeHTML("passengerCapacity", "Number of Passengers", passengers));
        return stringBuilder.toString();
    }

    private Pair<Flight, RouteMap> grabRouteMapFlightPairFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        EditFlightGuideTarget editFlightGuideTarget;
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 4 && (editFlightGuideTarget = new EditFlightGuideTarget(airline, urlSplit).invoke()).is()) {
            return new ImmutablePair((Object)editFlightGuideTarget.fetchFlight(), (Object)editFlightGuideTarget.takeRouteMap());
        }
        return new ImmutablePair((Object)null, (Object)null);
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            Pair<Flight, RouteMap> flightRouteMapPair = this.grabRouteMapFlightPairFromTrail(remainingTrail, airline);
            Flight flight = (Flight)flightRouteMapPair.getLeft();
            if (flight == null) {
                return EditFlightGuide.getErrorResponse(400, "This flight does not exist.");
            }
            return this.getTemplateResponse("Edit Flight Path", this.grabContents(flight), airline);
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        catch (NullPointerException e) {
            return EditFlightGuide.getErrorResponse(400, "Unable to parse the URL");
        }
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            Pair<Flight, RouteMap> flightRouteMapPair = this.grabRouteMapFlightPairFromTrail(remainingTrail, airline);
            Flight flight = (Flight)flightRouteMapPair.getLeft();
            RouteMap routeMap = (RouteMap)flightRouteMapPair.getRight();
            if (flight == null) {
                return EditFlightGuide.getErrorResponse(400, "This flight does not exist.");
            }
            Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
            if (data.containsKey("delete")) {
                new EditFlightGuideHelper(flight, routeMap).invoke();
            } else {
                String passengerStr;
                String distanceStr;
                if (data.containsKey("distance") && !(distanceStr = data.get("distance").get(0)).isEmpty()) {
                    this.handlePostGateKeeper(flight, distanceStr);
                }
                if (data.containsKey("cost")) {
                    this.handlePostAssist(flight, data);
                }
                if (data.containsKey("time")) {
                    this.handlePostWorker(flight, data);
                }
                if (data.containsKey("crewMembers")) {
                    this.handlePostHelp(flight, data);
                }
                if (data.containsKey("weightCapacity")) {
                    this.handlePostEntity(flight, data);
                }
                if (data.containsKey("passengerCapacity") && !(passengerStr = data.get("passengerCapacity").get(0)).isEmpty()) {
                    int passengers = Integer.parseInt(passengerStr);
                    flight.setPassengerLimit(passengers);
                }
            }
            return EditFlightGuide.takeDefaultRedirectResponse();
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Unable to parse number from string. " + e.getMessage(), airline);
        }
        catch (NullPointerException e) {
            return EditFlightGuide.getErrorResponse(400, "Unable to parse the URL");
        }
    }

    private void handlePostEntity(Flight flight, Map<String, List<String>> data) {
        String weightStr = data.get("weightCapacity").get(0);
        if (!weightStr.isEmpty()) {
            this.handlePostEntityCoordinator(flight, weightStr);
        }
    }

    private void handlePostEntityCoordinator(Flight flight, String weightStr) {
        int weight = Integer.parseInt(weightStr);
        flight.setWeightLimit(weight);
    }

    private void handlePostHelp(Flight flight, Map<String, List<String>> data) {
        String crewStr = data.get("crewMembers").get(0);
        if (!crewStr.isEmpty()) {
            new EditFlightGuideGuide(flight, crewStr).invoke();
        }
    }

    private void handlePostWorker(Flight flight, Map<String, List<String>> data) {
        new EditFlightGuideExecutor(flight, data).invoke();
    }

    private void handlePostAssist(Flight flight, Map<String, List<String>> data) {
        String costStr = data.get("cost").get(0);
        if (!costStr.isEmpty()) {
            new EditFlightGuideCoordinator(flight, costStr).invoke();
        }
    }

    private void handlePostGateKeeper(Flight flight, String distanceStr) {
        int newDistance = Integer.parseInt(distanceStr);
        flight.setDistance(newDistance);
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

    private class EditFlightGuideGuide {
        private Flight flight;
        private String crewStr;

        public EditFlightGuideGuide(Flight flight, String crewStr) {
            this.flight = flight;
            this.crewStr = crewStr;
        }

        public void invoke() {
            int numCrewMembers = Integer.parseInt(this.crewStr);
            this.flight.assignNumCrewMembers(numCrewMembers);
        }
    }

    private class EditFlightGuideExecutor {
        private Flight flight;
        private Map<String, List<String>> data;

        public EditFlightGuideExecutor(Flight flight, Map<String, List<String>> data) {
            this.flight = flight;
            this.data = data;
        }

        public void invoke() {
            String timeStr = this.data.get("time").get(0);
            if (!timeStr.isEmpty()) {
                this.invokeCoordinator(timeStr);
            }
        }

        private void invokeCoordinator(String timeStr) {
            int travelTime = Integer.parseInt(timeStr);
            this.flight.defineTravelTime(travelTime);
        }
    }

    private class EditFlightGuideCoordinator {
        private Flight flight;
        private String costStr;

        public EditFlightGuideCoordinator(Flight flight, String costStr) {
            this.flight = flight;
            this.costStr = costStr;
        }

        public void invoke() {
            int newCost = Integer.parseInt(this.costStr);
            this.flight.assignFuelCosts(newCost);
        }
    }

    private class EditFlightGuideHelper {
        private Flight flight;
        private RouteMap routeMap;

        public EditFlightGuideHelper(Flight flight, RouteMap routeMap) {
            this.flight = flight;
            this.routeMap = routeMap;
        }

        public void invoke() {
            this.routeMap.deleteFlight(this.flight);
        }
    }

    private class EditFlightGuideTarget {
        private boolean myResult;
        private Airline airline;
        private String[] urlSplit;
        private RouteMap routeMap;
        private Flight flight;

        public EditFlightGuideTarget(Airline airline, String[] urlSplit) {
            this.airline = airline;
            this.urlSplit = urlSplit;
        }

        boolean is() {
            return this.myResult;
        }

        public RouteMap takeRouteMap() {
            return this.routeMap;
        }

        public Flight fetchFlight() {
            return this.flight;
        }

        public EditFlightGuideTarget invoke() {
            this.routeMap = this.airline.grabRouteMap(Integer.parseInt(this.urlSplit[1]));
            if (this.routeMap != null) {
                return this.invokeWorker();
            }
            this.myResult = false;
            return this;
        }

        private EditFlightGuideTarget invokeWorker() {
            this.flight = this.routeMap.getFlight(Integer.parseInt(this.urlSplit[3]));
            this.myResult = true;
            return this;
        }
    }

}

