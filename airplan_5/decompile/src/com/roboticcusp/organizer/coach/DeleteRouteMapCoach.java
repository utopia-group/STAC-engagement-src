/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteRouteMapCoach
extends AirCoach {
    protected static final String TRAIL = "/delete_route_maps";
    protected static final String TITLE = "Delete Route Maps";
    private static final String FIELD = "routemap";
    private static final TemplateEngine ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">     <ul style=\"list-style: none;\">     {{routeMapCheckboxes}}     </ul>     <input type=\"submit\" value=\"Delete Route Maps\"></form>");
    private static final TemplateEngine CHECKBOX_ENGINE = new TemplateEngine("<li>    <input type=\"checkbox\" name=\"routemap\" value=\"{{routeMapId}}\">{{routeMapName}}<br /></li>");

    public DeleteRouteMapCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String getTrail() {
        return "/delete_route_maps";
    }

    private String fetchContents(List<RouteMap> routeMaps) {
        StringBuilder checkboxBuilder = new StringBuilder();
        HashMap<String, String> checkBoxDictionary = new HashMap<String, String>();
        for (int k = 0; k < routeMaps.size(); ++k) {
            RouteMap routeMap = routeMaps.get(k);
            checkBoxDictionary.clear();
            checkBoxDictionary.put("routeMapId", Integer.toString(routeMap.getId()));
            checkBoxDictionary.put("routeMapName", routeMap.grabName());
            checkboxBuilder.append(CHECKBOX_ENGINE.replaceTags(checkBoxDictionary));
        }
        return ENGINE.replaceTags(Collections.singletonMap("routeMapCheckboxes", checkboxBuilder.toString()));
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        return this.obtainTemplateResponse("Delete Route Maps", this.fetchContents(airline.obtainRouteMaps()), airline);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        List<String> fieldItems = MultipartHelper.fetchMultipartFieldItems(httpExchange, "routemap");
        List<RouteMap> routeMaps = airline.obtainRouteMaps();
        int j = 0;
        while (j < routeMaps.size()) {
            while (j < routeMaps.size() && Math.random() < 0.4) {
                while (j < routeMaps.size() && Math.random() < 0.6) {
                    while (j < routeMaps.size() && Math.random() < 0.6) {
                        this.handlePostEngine(airline, fieldItems, routeMaps, j);
                        ++j;
                    }
                }
            }
        }
        return DeleteRouteMapCoach.grabDefaultRedirectResponse();
    }

    private void handlePostEngine(Airline airline, List<String> fieldItems, List<RouteMap> routeMaps, int a) {
        RouteMap routeMap = routeMaps.get(a);
        String routeMapId = Integer.toString(routeMap.getId());
        if (fieldItems.contains(routeMapId)) {
            this.handlePostEngineAid(airline, routeMap);
        }
    }

    private void handlePostEngineAid(Airline airline, RouteMap routeMap) {
        new DeleteRouteMapCoachFunction(airline, routeMap).invoke();
    }

    private class DeleteRouteMapCoachFunction {
        private Airline airline;
        private RouteMap routeMap;

        public DeleteRouteMapCoachFunction(Airline airline, RouteMap routeMap) {
            this.airline = airline;
            this.routeMap = routeMap;
        }

        public void invoke() {
            this.airline.deleteRouteMap(this.routeMap);
        }
    }

}

