/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.EditAirportGuide;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.note.Logger;
import net.techpoint.note.LoggerFactory;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;

public class AddAirportGuide
extends AirGuide {
    private static final Logger logger = LoggerFactory.takeLogger(EditAirportGuide.class);
    protected static final String TRAIL = "/add_airport";
    private static final String TITLE = "Add an Airport";
    private static final String FIELD = "name";
    private static final TemplateEngine ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <h2>Route map: {{mapName}} </h2>    <label for=\"name\"> Name: </label>    <input type=\"text\" name=\"name\" placeholder=\"name\"/>    <br/>    <input type=\"submit\" value=\"Submit airport\" name=\"submit\" id=\"submit\" />    <br/></form>");

    public AddAirportGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String grabContents(RouteMap routeMap) {
        StringBuilder contentsBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        contentsDictionary.put("mapName", routeMap.fetchName());
        contentsBuilder.append(ENGINE.replaceTags(contentsDictionary));
        return contentsBuilder.toString();
    }

    @Override
    public String obtainTrail() {
        return "/add_airport";
    }

    private RouteMap takeRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String[] splitUrl = remainingTrail.split("/");
        if (splitUrl.length == 2) {
            return airline.grabRouteMap(Integer.parseInt(splitUrl[1]));
        }
        return null;
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap;
        try {
            routeMap = this.takeRouteMapFromTrail(remainingTrail, airline);
            if (routeMap == null) {
                return AddAirportGuide.getErrorResponse(400, "Bad URL: " + remainingTrail + " is not associated with a route map belonging to you.");
            }
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        return this.getTemplateResponse("Add an Airport", this.grabContents(routeMap), airline);
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        block6 : {
            try {
                RouteMap routeMap = this.takeRouteMapFromTrail(remainingTrail, airline);
                if (routeMap == null) {
                    return AddAirportGuide.getErrorResponse(400, "Bad URL " + remainingTrail + " is not associated with a route map belonging to you.");
                }
                List<String> fieldItems = MultipartHelper.obtainMultipartFieldItems(httpExchange, "name");
                if (fieldItems.isEmpty()) {
                    return AddAirportGuide.getErrorResponse(400, "Bad Request for URL " + remainingTrail + " does not contain a valid value.");
                }
                String name = fieldItems.get(0).trim();
                if (routeMap.canAddAirport()) {
                    if (!Objects.equals(name, "")) {
                        routeMap.addAirport(name);
                        break block6;
                    }
                    return this.takeTemplateErrorResponse("Cannot add an airport without a name", airline);
                }
                return this.takeTemplateErrorResponse("This route map is not allowed to add additional airports.", airline);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                return AddAirportGuide.getErrorResponse(400, e.getMessage());
            }
        }
        return AddAirportGuide.takeDefaultRedirectResponse();
    }
}

