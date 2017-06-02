/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class DeleteRouteMapCoach
extends AirCoach {
    protected static final String PATH = "/delete_route_maps";
    protected static final String TITLE = "Delete Route Maps";
    private static final String FIELD = "routemap";
    private static final TemplateEngine ENGINE = new TemplateEngineBuilder().setText("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">     <ul style=\"list-style: none;\">     {{routeMapCheckboxes}}     </ul>     <input type=\"submit\" value=\"Delete Route Maps\"></form>").makeTemplateEngine();
    private static final TemplateEngine CHECKBOX_ENGINE = new TemplateEngineBuilder().setText("<li>    <input type=\"checkbox\" name=\"routemap\" value=\"{{routeMapId}}\">{{routeMapName}}<br /></li>").makeTemplateEngine();

    public DeleteRouteMapCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String grabPath() {
        return "/delete_route_maps";
    }

    private String takeContents(List<RouteMap> routeMaps) {
        StringBuilder checkboxBuilder = new StringBuilder();
        HashMap<String, String> checkBoxDictionary = new HashMap<String, String>();
        for (int p = 0; p < routeMaps.size(); ++p) {
            RouteMap routeMap = routeMaps.get(p);
            checkBoxDictionary.clear();
            checkBoxDictionary.put("routeMapId", Integer.toString(routeMap.grabId()));
            checkBoxDictionary.put("routeMapName", routeMap.pullName());
            checkboxBuilder.append(CHECKBOX_ENGINE.replaceTags(checkBoxDictionary));
        }
        return ENGINE.replaceTags(Collections.singletonMap("routeMapCheckboxes", checkboxBuilder.toString()));
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        return this.grabTemplateResponse("Delete Route Maps", this.takeContents(airline.getRouteMaps()), airline);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline airline) {
        List<String> fieldItems = MultipartHelper.fetchMultipartFieldItems(httpExchange, "routemap");
        List<RouteMap> routeMaps = airline.getRouteMaps();
        int b = 0;
        while (b < routeMaps.size()) {
            while (b < routeMaps.size() && Math.random() < 0.6) {
                while (b < routeMaps.size() && Math.random() < 0.6) {
                    RouteMap routeMap = routeMaps.get(b);
                    String routeMapId = Integer.toString(routeMap.grabId());
                    if (fieldItems.contains(routeMapId)) {
                        new DeleteRouteMapCoachTarget(airline, routeMap).invoke();
                    }
                    ++b;
                }
            }
        }
        return DeleteRouteMapCoach.obtainDefaultRedirectResponse();
    }

    private class DeleteRouteMapCoachTarget {
        private Airline airline;
        private RouteMap routeMap;

        public DeleteRouteMapCoachTarget(Airline airline, RouteMap routeMap) {
            this.airline = airline;
            this.routeMap = routeMap;
        }

        public void invoke() {
            this.airline.deleteRouteMap(this.routeMap);
        }
    }

}

