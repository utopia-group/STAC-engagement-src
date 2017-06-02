/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;

public class DeleteRouteMapGuide
extends AirGuide {
    protected static final String TRAIL = "/delete_route_maps";
    protected static final String TITLE = "Delete Route Maps";
    private static final String FIELD = "routemap";
    private static final TemplateEngine ENGINE = new TemplateEngine("<form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">     <ul style=\"list-style: none;\">     {{routeMapCheckboxes}}     </ul>     <input type=\"submit\" value=\"Delete Route Maps\"></form>");
    private static final TemplateEngine CHECKBOX_ENGINE = new TemplateEngine("<li>    <input type=\"checkbox\" name=\"routemap\" value=\"{{routeMapId}}\">{{routeMapName}}<br /></li>");

    public DeleteRouteMapGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String obtainTrail() {
        return "/delete_route_maps";
    }

    private String getContents(List<RouteMap> routeMaps) {
        StringBuilder checkboxBuilder = new StringBuilder();
        HashMap<String, String> checkBoxDictionary = new HashMap<String, String>();
        for (int j = 0; j < routeMaps.size(); ++j) {
            this.pullContentsAid(routeMaps, checkboxBuilder, checkBoxDictionary, j);
        }
        return ENGINE.replaceTags(Collections.singletonMap("routeMapCheckboxes", checkboxBuilder.toString()));
    }

    private void pullContentsAid(List<RouteMap> routeMaps, StringBuilder checkboxBuilder, Map<String, String> checkBoxDictionary, int p) {
        new DeleteRouteMapGuideEngine(routeMaps, checkboxBuilder, checkBoxDictionary, p).invoke();
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        return this.getTemplateResponse("Delete Route Maps", this.getContents(airline.grabRouteMaps()), airline);
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        List<String> fieldItems = MultipartHelper.obtainMultipartFieldItems(httpExchange, "routemap");
        List<RouteMap> routeMaps = airline.grabRouteMaps();
        for (int j = 0; j < routeMaps.size(); ++j) {
            this.handlePostHerder(airline, fieldItems, routeMaps, j);
        }
        return DeleteRouteMapGuide.takeDefaultRedirectResponse();
    }

    private void handlePostHerder(Airline airline, List<String> fieldItems, List<RouteMap> routeMaps, int k) {
        RouteMap routeMap = routeMaps.get(k);
        String routeMapId = Integer.toString(routeMap.pullId());
        if (fieldItems.contains(routeMapId)) {
            airline.deleteRouteMap(routeMap);
        }
    }

    private class DeleteRouteMapGuideEngine {
        private List<RouteMap> routeMaps;
        private StringBuilder checkboxBuilder;
        private Map<String, String> checkBoxDictionary;
        private int i;

        public DeleteRouteMapGuideEngine(List<RouteMap> routeMaps, StringBuilder checkboxBuilder, Map<String, String> checkBoxDictionary, int p) {
            this.routeMaps = routeMaps;
            this.checkboxBuilder = checkboxBuilder;
            this.checkBoxDictionary = checkBoxDictionary;
            this.i = p;
        }

        public void invoke() {
            RouteMap routeMap = this.routeMaps.get(this.i);
            this.checkBoxDictionary.clear();
            this.checkBoxDictionary.put("routeMapId", Integer.toString(routeMap.pullId()));
            this.checkBoxDictionary.put("routeMapName", routeMap.fetchName());
            this.checkboxBuilder.append(CHECKBOX_ENGINE.replaceTags(this.checkBoxDictionary));
        }
    }

}

