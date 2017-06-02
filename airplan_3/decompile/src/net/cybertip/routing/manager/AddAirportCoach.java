/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.note.Logger;
import net.cybertip.note.LoggerFactory;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.EditAirportCoach;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class AddAirportCoach
extends AirCoach {
    private static final Logger logger = LoggerFactory.takeLogger(EditAirportCoach.class);
    protected static final String PATH = "/add_airport";
    private static final String TITLE = "Add an Airport";
    private static final String FIELD = "name";
    private static final TemplateEngine ENGINE = new TemplateEngineBuilder().setText("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <h2>Route map: {{mapName}} </h2>    <label for=\"name\"> Name: </label>    <input type=\"text\" name=\"name\" placeholder=\"name\"/>    <br/>    <input type=\"submit\" value=\"Submit airport\" name=\"submit\" id=\"submit\" />    <br/></form>").makeTemplateEngine();

    public AddAirportCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String obtainContents(RouteMap routeMap) {
        StringBuilder contentsBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("mapName", routeMap.pullName());
        contentsBuilder.append(ENGINE.replaceTags(contentsDictionary));
        return contentsBuilder.toString();
    }

    @Override
    public String grabPath() {
        return "/add_airport";
    }

    private RouteMap grabRouteMapFromPath(String remainingPath, Airline airline) throws NumberFormatException {
        String[] splitUrl = remainingPath.split("/");
        if (splitUrl.length == 2) {
            return airline.obtainRouteMap(Integer.parseInt(splitUrl[1]));
        }
        return null;
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        RouteMap routeMap;
        try {
            routeMap = this.grabRouteMapFromPath(remainingPath, airline);
            if (routeMap == null) {
                return AddAirportCoach.obtainErrorResponse(400, "Bad URL: " + remainingPath + " is not associated with a route map belonging to you.");
            }
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        return this.grabTemplateResponse("Add an Airport", this.obtainContents(routeMap), airline);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline airline) {
        block6 : {
            try {
                RouteMap routeMap = this.grabRouteMapFromPath(remainingPath, airline);
                if (routeMap == null) {
                    return AddAirportCoach.obtainErrorResponse(400, "Bad URL " + remainingPath + " is not associated with a route map belonging to you.");
                }
                List<String> fieldItems = MultipartHelper.fetchMultipartFieldItems(httpExchange, "name");
                if (fieldItems.isEmpty()) {
                    return AddAirportCoach.obtainErrorResponse(400, "Bad Request for URL " + remainingPath + " does not contain a valid value.");
                }
                String name = fieldItems.get(0).trim();
                if (routeMap.canAddAirport()) {
                    if (!Objects.equals(name, "")) {
                        new AddAirportCoachHelp(routeMap, name).invoke();
                        break block6;
                    }
                    return this.pullTemplateErrorResponse("Cannot add an airport without a name", airline);
                }
                return this.pullTemplateErrorResponse("This route map is not allowed to add additional airports.", airline);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                return AddAirportCoach.obtainErrorResponse(400, e.getMessage());
            }
        }
        return AddAirportCoach.obtainDefaultRedirectResponse();
    }

    private class AddAirportCoachHelp {
        private RouteMap routeMap;
        private String name;

        public AddAirportCoachHelp(RouteMap routeMap, String name) {
            this.routeMap = routeMap;
            this.name = name;
        }

        public void invoke() throws AirTrouble {
            this.routeMap.addAirport(this.name);
        }
    }

}

