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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.note.Logger;
import net.cybertip.note.LoggerFactory;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.CoachUtils;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class EditAirportCoach
extends AirCoach {
    private static final Logger logger = LoggerFactory.takeLogger(EditAirportCoach.class);
    protected static final String PATH = "/edit_airport";
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
        List<Flight> originFlights = airport.grabOriginFlights();
        int b = 0;
        while (b < originFlights.size()) {
            while (b < originFlights.size() && Math.random() < 0.5) {
                while (b < originFlights.size() && Math.random() < 0.6) {
                    Flight flight = originFlights.get(b);
                    flightListDictionary.put("flightURL", CoachUtils.generateFlightURL(routeMap, airport, flight));
                    flightListDictionary.put("destination", flight.fetchDestination().getName());
                    flightListDictionary.put("distance", Integer.toString(flight.takeDistance()));
                    flightListDictionary.put("cost", Integer.toString(flight.fetchFuelCosts()));
                    flightListDictionary.put("time", Integer.toString(flight.takeTravelTime()));
                    flightListDictionary.put("crew", Integer.toString(flight.fetchNumCrewMembers()));
                    flightsListBuilder.append(FLIGHTS_LIST_ENGINE.replaceTags(flightListDictionary));
                    ++b;
                }
            }
        }
        return flightsListBuilder.toString();
    }

    private String fetchContents(Pair<RouteMap, Airport> routeMapAirportPair) {
        RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
        Airport airport = (Airport)routeMapAirportPair.getRight();
        StringBuilder contentsBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("name", airport.getName());
        contentsDictionary.put("flightList", this.generateFlightsListHTML(routeMap, airport));
        contentsDictionary.put("addFlightURL", CoachUtils.generateAddFlightURL(routeMap, airport));
        contentsBuilder.append(ENGINE.replaceTags(contentsDictionary));
        return contentsBuilder.toString();
    }

    @Override
    public String grabPath() {
        return "/edit_airport";
    }

    private Pair<RouteMap, Airport> obtainRouteMapAirportPairFromPath(String remainingPath, Airline airline) throws NumberFormatException {
        RouteMap routeMap;
        Airport airport;
        String[] urlSplit = remainingPath.split("/");
        if (urlSplit.length == 3 && (routeMap = airline.obtainRouteMap(Integer.parseInt(urlSplit[1]))) != null && (airport = routeMap.obtainAirport(Integer.parseInt(urlSplit[2]))) != null) {
            return new ImmutablePair((Object)routeMap, (Object)airport);
        }
        return null;
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline member) {
        Pair<RouteMap, Airport> routeMapAirportPair;
        try {
            routeMapAirportPair = this.obtainRouteMapAirportPairFromPath(remainingPath, member);
            if (routeMapAirportPair == null) {
                return EditAirportCoach.obtainErrorResponse(400, "Bad URL");
            }
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), member);
        }
        return this.grabTemplateResponse("Edit Airport Data", this.fetchContents(routeMapAirportPair), member);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline airline) {
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.obtainRouteMapAirportPairFromPath(remainingPath, airline);
            if (routeMapAirportPair == null) {
                return EditAirportCoach.obtainErrorResponse(400, "Unable to parse the URL for route map and airport information.");
            }
            RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
            Airport airport = (Airport)routeMapAirportPair.getRight();
            Map<String, List<String>> data = MultipartHelper.pullMultipartValues(httpExchange, FIELD_NAMES);
            if (data.containsKey("name")) {
                this.handlePostHelp(airport, data);
            }
            if (data.containsKey("delete")) {
                this.handlePostEngine(routeMap, airport);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return EditAirportCoach.obtainErrorResponse(400, e.getMessage());
        }
        return EditAirportCoach.obtainDefaultRedirectResponse();
    }

    private void handlePostEngine(RouteMap routeMap, Airport airport) {
        routeMap.deleteAirport(airport);
    }

    private void handlePostHelp(Airport airport, Map<String, List<String>> data) {
        String newName = data.get("name").get(0);
        if (!newName.isEmpty()) {
            airport.setName(newName);
        }
    }

    static {
        FIELD_NAMES.add("name");
        FIELD_NAMES.add("delete");
        ENGINE = new TemplateEngineBuilder().setText("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <input type=\"submit\" value=\"Delete Airport\" name=\"delete\" id=\"delete\" /></form></br></br><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <label for=\"name\"> Airport Name: </label>    <input type=\"text\" name=\"name\" value=\"{{name}}\"/> <br/>    <input type=\"submit\" value=\"Submit airport\" name=\"submit\" id=\"submit\" />    <br/>    <ul>        {{flightList}}    </ul>    <p> <a href=\"{{addFlightURL}}\">Add an outgoing flight from this airport</a> </p></form>").makeTemplateEngine();
        FLIGHTS_LIST_ENGINE = new TemplateEngineBuilder().setText("<li> <a href=\"{{flightURL}}\"> Outgoing flight to <b>{{destination}}</b>, </a>   Distance: <b>{{distance}}</b>   Fuel cost: <b>{{cost}}</b>   Travel Time: <b>{{time}}</b>   Number of Crew Members: <b>{{crew}}</b></li>").makeTemplateEngine();
    }
}

