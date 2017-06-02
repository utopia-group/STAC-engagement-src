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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.GuideUtils;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.note.Logger;
import net.techpoint.note.LoggerFactory;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class EditAirportGuide
extends AirGuide {
    private static final Logger logger = LoggerFactory.takeLogger(EditAirportGuide.class);
    protected static final String TRAIL = "/edit_airport";
    private static final String TITLE = "Edit Airport Data";
    private static final String NAME_FIELD = "name";
    private static final String DELETE_FIELD = "delete";
    private static final Set<String> FIELD_NAMES = new LinkedHashSet<String>();
    private static final TemplateEngine ENGINE;
    private static final TemplateEngine FLIGHTS_LIST_ENGINE;

    public EditAirportGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String generateFlightsListHTML(RouteMap routeMap, Airport airport) {
        StringBuilder flightsListBuilder = new StringBuilder();
        HashMap<String, String> flightListDictionary = new HashMap<String, String>();
        List<Flight> originFlights = airport.takeOriginFlights();
        int a = 0;
        while (a < originFlights.size()) {
            while (a < originFlights.size() && Math.random() < 0.4) {
                Flight flight = originFlights.get(a);
                flightListDictionary.put("flightURL", GuideUtils.generateFlightURL(routeMap, airport, flight));
                flightListDictionary.put("destination", flight.pullDestination().obtainName());
                flightListDictionary.put("distance", Integer.toString(flight.obtainDistance()));
                flightListDictionary.put("cost", Integer.toString(flight.getFuelCosts()));
                flightListDictionary.put("time", Integer.toString(flight.obtainTravelTime()));
                flightListDictionary.put("crew", Integer.toString(flight.takeNumCrewMembers()));
                flightsListBuilder.append(FLIGHTS_LIST_ENGINE.replaceTags(flightListDictionary));
                ++a;
            }
        }
        return flightsListBuilder.toString();
    }

    private String grabContents(Pair<RouteMap, Airport> routeMapAirportPair) {
        RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
        Airport airport = (Airport)routeMapAirportPair.getRight();
        StringBuilder contentsBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("name", airport.obtainName());
        contentsDictionary.put("flightList", this.generateFlightsListHTML(routeMap, airport));
        contentsDictionary.put("addFlightURL", GuideUtils.generateAddFlightURL(routeMap, airport));
        contentsBuilder.append(ENGINE.replaceTags(contentsDictionary));
        return contentsBuilder.toString();
    }

    @Override
    public String obtainTrail() {
        return "/edit_airport";
    }

    private Pair<RouteMap, Airport> takeRouteMapAirportPairFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        RouteMap routeMap;
        Airport airport;
        String[] urlSplit = remainingTrail.split("/");
        if (urlSplit.length == 3 && (routeMap = airline.grabRouteMap(Integer.parseInt(urlSplit[1]))) != null && (airport = routeMap.obtainAirport(Integer.parseInt(urlSplit[2]))) != null) {
            return new ImmutablePair((Object)routeMap, (Object)airport);
        }
        return null;
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline user) {
        Pair<RouteMap, Airport> routeMapAirportPair;
        try {
            routeMapAirportPair = this.takeRouteMapAirportPairFromTrail(remainingTrail, user);
            if (routeMapAirportPair == null) {
                return EditAirportGuide.getErrorResponse(400, "Bad URL");
            }
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), user);
        }
        return this.getTemplateResponse("Edit Airport Data", this.grabContents(routeMapAirportPair), user);
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            Pair<RouteMap, Airport> routeMapAirportPair = this.takeRouteMapAirportPairFromTrail(remainingTrail, airline);
            if (routeMapAirportPair == null) {
                return EditAirportGuide.getErrorResponse(400, "Unable to parse the URL for route map and airport information.");
            }
            RouteMap routeMap = (RouteMap)routeMapAirportPair.getLeft();
            Airport airport = (Airport)routeMapAirportPair.getRight();
            Map<String, List<String>> data = MultipartHelper.getMultipartValues(httpExchange, FIELD_NAMES);
            if (data.containsKey("name")) {
                new EditAirportGuideAssist(airport, data).invoke();
            }
            if (data.containsKey("delete")) {
                new EditAirportGuideAid(routeMap, airport).invoke();
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return EditAirportGuide.getErrorResponse(400, e.getMessage());
        }
        return EditAirportGuide.takeDefaultRedirectResponse();
    }

    static {
        FIELD_NAMES.add("name");
        FIELD_NAMES.add("delete");
        ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <input type=\"submit\" value=\"Delete Airport\" name=\"delete\" id=\"delete\" /></form></br></br><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <label for=\"name\"> Airport Name: </label>    <input type=\"text\" name=\"name\" value=\"{{name}}\"/> <br/>    <input type=\"submit\" value=\"Submit airport\" name=\"submit\" id=\"submit\" />    <br/>    <ul>        {{flightList}}    </ul>    <p> <a href=\"{{addFlightURL}}\">Add an outgoing flight from this airport</a> </p></form>");
        FLIGHTS_LIST_ENGINE = new TemplateEngine("<li> <a href=\"{{flightURL}}\"> Outgoing flight to <b>{{destination}}</b>, </a>   Distance: <b>{{distance}}</b>   Fuel cost: <b>{{cost}}</b>   Travel Time: <b>{{time}}</b>   Number of Crew Members: <b>{{crew}}</b></li>");
    }

    private class EditAirportGuideAid {
        private RouteMap routeMap;
        private Airport airport;

        public EditAirportGuideAid(RouteMap routeMap, Airport airport) {
            this.routeMap = routeMap;
            this.airport = airport;
        }

        public void invoke() {
            this.routeMap.deleteAirport(this.airport);
        }
    }

    private class EditAirportGuideAssist {
        private Airport airport;
        private Map<String, List<String>> data;

        public EditAirportGuideAssist(Airport airport, Map<String, List<String>> data) {
            this.airport = airport;
            this.data = data;
        }

        public void invoke() {
            String newName = this.data.get("name").get(0);
            if (!newName.isEmpty()) {
                this.invokeWorker(newName);
            }
        }

        private void invokeWorker(String newName) {
            this.airport.setName(newName);
        }
    }

}

