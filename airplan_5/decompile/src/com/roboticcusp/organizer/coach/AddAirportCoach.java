/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.EditAirportCoach;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.slf4j.Logger;
import com.roboticcusp.slf4j.LoggerFactory;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddAirportCoach
extends AirCoach {
    private static final Logger logger = LoggerFactory.fetchLogger(EditAirportCoach.class);
    protected static final String TRAIL = "/add_airport";
    private static final String TITLE = "Add an Airport";
    private static final String FIELD = "name";
    private static final TemplateEngine ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <h2>Route map: {{mapName}} </h2>    <label for=\"name\"> Name: </label>    <input type=\"text\" name=\"name\" placeholder=\"name\"/>    <br/>    <input type=\"submit\" value=\"Submit airport\" name=\"submit\" id=\"submit\" />    <br/></form>");

    public AddAirportCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String takeContents(RouteMap routeMap) {
        StringBuilder contentsBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("mapName", routeMap.grabName());
        contentsBuilder.append(ENGINE.replaceTags(contentsDictionary));
        return contentsBuilder.toString();
    }

    @Override
    public String getTrail() {
        return "/add_airport";
    }

    private RouteMap takeRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] splitUrl = remainingTrail.split("/");
        if (splitUrl.length == 2) {
            return airline.pullRouteMap(Integer.parseInt(splitUrl[1]));
        }
        return null;
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap;
        try {
            routeMap = this.takeRouteMapFromTrail(remainingTrail, airline);
            if (routeMap == null) {
                return AddAirportCoach.grabErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a route map belonging to you.");
            }
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        return this.obtainTemplateResponse("Add an Airport", this.takeContents(routeMap), airline);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        block6 : {
            try {
                RouteMap routeMap = this.takeRouteMapFromTrail(remainingTrail, airline);
                if (routeMap == null) {
                    return AddAirportCoach.grabErrorResponse(400, "Bad URL " + remainingTrail + " is not associated with a route map belonging to you.");
                }
                List<String> fieldItems = MultipartHelper.fetchMultipartFieldItems(httpExchange, "name");
                if (fieldItems.isEmpty()) {
                    return AddAirportCoach.grabErrorResponse(400, "Bad Request for URL " + remainingTrail + " does not contain a valid value.");
                }
                String name = fieldItems.get(0).trim();
                if (routeMap.canAddAirport()) {
                    if (this.handlePostSupervisor(routeMap, name)) {
                        return this.pullTemplateErrorResponse("Cannot add an airport without a name", airline);
                    }
                    break block6;
                }
                return this.pullTemplateErrorResponse("This route map is not allowed to add additional airports.", airline);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                return AddAirportCoach.grabErrorResponse(400, e.getMessage());
            }
        }
        return AddAirportCoach.grabDefaultRedirectResponse();
    }

    private boolean handlePostSupervisor(RouteMap routeMap, String name) throws AirException {
        if (Objects.equals(name, "")) {
            return true;
        }
        this.handlePostSupervisorGuide(routeMap, name);
        return false;
    }

    private void handlePostSupervisorGuide(RouteMap routeMap, String name) throws AirException {
        routeMap.addAirport(name);
    }
}

