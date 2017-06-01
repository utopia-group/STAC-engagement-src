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
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CoachUtils;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.slf4j.Logger;
import com.roboticcusp.slf4j.LoggerFactory;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class EditAirportCoach
extends AirCoach {
    private static final Logger logger = LoggerFactory.fetchLogger(EditAirportCoach.class);
    protected static final String TRAIL = "/edit_airport";
    private static final String TITLE = "Edit Airport Data";
    private static final String NAME_FIELD = "name";
    private static final String DELETE_FIELD = "delete";
    private static final Set<String> FIELD_NAMES = new LinkedHashSet<String>();
    private static final TemplateEngine ENGINE;
    private static final TemplateEngine FLIGHTS_LIST_ENGINE;

    public EditAirportCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String generateFlightsListHTML(RouteMap routeMap, Airport airport) {
        StringBuilder flightsListBuilder = new StringBuilder();
        HashMap<String, String> flightListDictionary = new HashMap<String, String>();
        List<Flight> originFlights = airport.fetchOriginFlights();
        for (int j = 0; j < originFlights.size(); ++j) {
            Flight flight = originFlights.get(j);
            flightListDictionary.put("flightURL", CoachUtils.generateFlightURL(routeMap, airport, flight));
            flightListDictionary.put("destination", flight.fetchDestination().takeName());
            flightListDictionary.put("distance", Integer.toString(flight.fetchDistance()));
            flightListDictionary.put("cost", Integer.toString(flight.pullFuelCosts()));
            flightListDictionary.put("time", Integer.toString(flight.obtainTravelTime()));
            flightListDictionary.put("crew", Integer.toString(flight.takeNumCrewMembers()));
            flightsListBuilder.append(FLIGHTS_LIST_ENGINE.replaceTags(flightListDictionary));
        }
        return flightsListBuilder.toString();
    }

    private String pullContents(Pair<RouteMap, Airport> routeMapAirportPair) {
        RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
        Airport airport = (Airport)routeMapAirportPair.getRight();
        StringBuilder contentsBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("name", airport.takeName());
        contentsDictionary.put("flightList", this.generateFlightsListHTML(routeMap, airport));
        contentsDictionary.put("addFlightURL", CoachUtils.generateAddFlightURL(routeMap, airport));
        contentsBuilder.append(ENGINE.replaceTags(contentsDictionary));
        return contentsBuilder.toString();
    }

    @Override
    public String getTrail() {
        return "/edit_airport";
    }

    private Pair<RouteMap, Airport> getRouteMapAirportPairFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        Pair<RouteMap, Airport> airport;
        RouteMap routeMap;
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 3 && (routeMap = airline.pullRouteMap(Integer.parseInt(urlSplit[1]))) != null && (airport = this.grabRouteMapAirportPairFromTrailUtility(urlSplit[2], routeMap)) != null) {
            return airport;
        }
        return null;
    }

    private Pair<RouteMap, Airport> grabRouteMapAirportPairFromTrailUtility(String s, RouteMap routeMap) {
        Airport airport = routeMap.takeAirport(Integer.parseInt(s));
        if (airport != null) {
            return new ImmutablePair((Object)routeMap, (Object)airport);
        }
        return null;
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        Pair<RouteMap, Airport> routeMapAirportPair;
        try {
            routeMapAirportPair = this.getRouteMapAirportPairFromTrail(remainingTrail, participant);
            if (routeMapAirportPair == null) {
                return EditAirportCoach.grabErrorResponse(400, "Bad URL");
            }
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), participant);
        }
        return this.obtainTemplateResponse("Edit Airport Data", this.pullContents(routeMapAirportPair), participant);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            String newName;
            Pair<RouteMap, Airport> routeMapAirportPair = this.getRouteMapAirportPairFromTrail(remainingTrail, airline);
            if (routeMapAirportPair == null) {
                return EditAirportCoach.grabErrorResponse(400, "Unable to parse the URL for route map and airport information.");
            }
            RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
            Airport airport = (Airport)routeMapAirportPair.getRight();
            Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, FIELD_NAMES);
            if (data.containsKey("name") && !(newName = data.get("name").get(0)).isEmpty()) {
                this.handlePostExecutor(airport, newName);
            }
            if (data.containsKey("delete")) {
                routeMap.deleteAirport(airport);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return EditAirportCoach.grabErrorResponse(400, e.getMessage());
        }
        return EditAirportCoach.grabDefaultRedirectResponse();
    }

    private void handlePostExecutor(Airport airport, String newName) {
        airport.setName(newName);
    }

    static {
        FIELD_NAMES.add("name");
        FIELD_NAMES.add("delete");
        ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <input type=\"submit\" value=\"Delete Airport\" name=\"delete\" id=\"delete\" /></form></br></br><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <label for=\"name\"> Airport Name: </label>    <input type=\"text\" name=\"name\" value=\"{{name}}\"/> <br/>    <input type=\"submit\" value=\"Submit airport\" name=\"submit\" id=\"submit\" />    <br/>    <ul>        {{flightList}}    </ul>    <p> <a href=\"{{addFlightURL}}\">Add an outgoing flight from this airport</a> </p></form>");
        FLIGHTS_LIST_ENGINE = new TemplateEngine("<li> <a href=\"{{flightURL}}\"> Outgoing flight to <b>{{destination}}</b>, </a>   Distance: <b>{{distance}}</b>   Fuel cost: <b>{{cost}}</b>   Travel Time: <b>{{time}}</b>   Number of Crew Members: <b>{{crew}}</b></li>");
    }
}

