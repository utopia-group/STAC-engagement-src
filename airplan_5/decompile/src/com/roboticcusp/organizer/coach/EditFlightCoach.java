/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.organizer.coach.AddFlightCoach;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class EditFlightCoach
extends AirCoach {
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

    public EditFlightCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String fetchContents(Flight flight) {
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("origin", flight.obtainOrigin().takeName());
        contentsDictionary.put("dest", flight.fetchDestination().takeName());
        contentsDictionary.put("flightData", this.generateFlightDataHTML(flight));
        return ENGINE.replaceTags(contentsDictionary);
    }

    @Override
    public String getTrail() {
        return "/edit_flight";
    }

    private String generateFlightDataHTML(Flight flight) {
        StringBuilder stringBuilder = new StringBuilder();
        String distance = Integer.toString(flight.fetchDistance());
        stringBuilder.append(AddFlightCoach.pullFlightAttributeHTML("distance", "Distance", distance));
        String cost = Integer.toString(flight.pullFuelCosts());
        stringBuilder.append(AddFlightCoach.pullFlightAttributeHTML("cost", "Cost", cost));
        String time = Integer.toString(flight.obtainTravelTime());
        stringBuilder.append(AddFlightCoach.pullFlightAttributeHTML("time", "Travel Time", time));
        String crew = Integer.toString(flight.takeNumCrewMembers());
        stringBuilder.append(AddFlightCoach.pullFlightAttributeHTML("crewMembers", "Number of Crew Members", crew));
        String weight = Integer.toString(flight.grabWeightAccommodation());
        stringBuilder.append(AddFlightCoach.pullFlightAttributeHTML("weightCapacity", "Weight Capacity", weight));
        String passengers = Integer.toString(flight.obtainPassengerAccommodation());
        stringBuilder.append(AddFlightCoach.pullFlightAttributeHTML("passengerCapacity", "Number of Passengers", passengers));
        return stringBuilder.toString();
    }

    private Pair<Flight, RouteMap> getRouteMapFlightPairFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        RouteMap routeMap;
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 4 && (routeMap = airline.pullRouteMap(Integer.parseInt(urlSplit[1]))) != null) {
            return this.getRouteMapFlightPairFromTrailAdviser(urlSplit[3], routeMap);
        }
        return new ImmutablePair((Object)null, (Object)null);
    }

    private Pair<Flight, RouteMap> getRouteMapFlightPairFromTrailAdviser(String s, RouteMap routeMap) {
        Flight flight = routeMap.obtainFlight(Integer.parseInt(s));
        return new ImmutablePair((Object)flight, (Object)routeMap);
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            Pair<Flight, RouteMap> flightRouteMapPair = this.getRouteMapFlightPairFromTrail(remainingTrail, airline);
            Flight flight = (Flight)flightRouteMapPair.getLeft();
            if (flight == null) {
                return EditFlightCoach.grabErrorResponse(400, "This flight does not exist.");
            }
            return this.obtainTemplateResponse("Edit Flight Path", this.fetchContents(flight), airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        catch (NullPointerException e) {
            return EditFlightCoach.grabErrorResponse(400, "Unable to parse the URL");
        }
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            Pair<Flight, RouteMap> flightRouteMapPair = this.getRouteMapFlightPairFromTrail(remainingTrail, airline);
            Flight flight = (Flight)flightRouteMapPair.getLeft();
            RouteMap routeMap = (RouteMap)flightRouteMapPair.getRight();
            if (flight == null) {
                return EditFlightCoach.grabErrorResponse(400, "This flight does not exist.");
            }
            Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, ALL_FIELDS);
            if (data.containsKey("delete")) {
                routeMap.deleteFlight(flight);
            } else {
                String crewStr;
                String weightStr;
                String distanceStr;
                String timeStr;
                String passengerStr;
                if (data.containsKey("distance") && !(distanceStr = data.get("distance").get(0)).isEmpty()) {
                    int newDistance = Integer.parseInt(distanceStr);
                    flight.fixDistance(newDistance);
                }
                if (data.containsKey("cost")) {
                    this.handlePostHelper(flight, data);
                }
                if (data.containsKey("time") && !(timeStr = data.get("time").get(0)).isEmpty()) {
                    int travelTime = Integer.parseInt(timeStr);
                    flight.defineTravelTime(travelTime);
                }
                if (data.containsKey("crewMembers") && !(crewStr = data.get("crewMembers").get(0)).isEmpty()) {
                    new EditFlightCoachHelp(flight, crewStr).invoke();
                }
                if (data.containsKey("weightCapacity") && !(weightStr = data.get("weightCapacity").get(0)).isEmpty()) {
                    int weight = Integer.parseInt(weightStr);
                    flight.fixWeightAccommodation(weight);
                }
                if (data.containsKey("passengerCapacity") && !(passengerStr = data.get("passengerCapacity").get(0)).isEmpty()) {
                    this.handlePostExecutor(flight, passengerStr);
                }
            }
            return EditFlightCoach.grabDefaultRedirectResponse();
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Unable to parse number from string. " + e.getMessage(), airline);
        }
        catch (NullPointerException e) {
            return EditFlightCoach.grabErrorResponse(400, "Unable to parse the URL");
        }
    }

    private void handlePostExecutor(Flight flight, String passengerStr) {
        int passengers = Integer.parseInt(passengerStr);
        flight.fixPassengerAccommodation(passengers);
    }

    private void handlePostHelper(Flight flight, Map<String, List<String>> data) {
        String costStr = data.get("cost").get(0);
        if (!costStr.isEmpty()) {
            int newCost = Integer.parseInt(costStr);
            flight.fixFuelCosts(newCost);
        }
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

    private class EditFlightCoachHelp {
        private Flight flight;
        private String crewStr;

        public EditFlightCoachHelp(Flight flight, String crewStr) {
            this.flight = flight;
            this.crewStr = crewStr;
        }

        public void invoke() {
            int numCrewMembers = Integer.parseInt(this.crewStr);
            this.flight.assignNumCrewMembers(numCrewMembers);
        }
    }

}

