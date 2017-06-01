/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CoachUtils;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

public class ViewRouteMapsCoach
extends AirCoach {
    protected static final String TRAIL = "/";
    protected static final String TITLE = "Route Maps";
    private static final TemplateEngine ENGINE = new TemplateEngine("<ul>\n<li><a href=\"{{routeMapURL}}\"> {{routeMapName}} </a> </li>\n</ul>");

    public ViewRouteMapsCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String generateRouteMapHTML(RouteMap routeMap) {
        HashMap<String, String> routeMapDictionary = new HashMap<String, String>();
        routeMapDictionary.put("routeMapName", StringEscapeUtils.escapeHtml4((String)routeMap.grabName()));
        routeMapDictionary.put("routeMapURL", CoachUtils.generateRouteMapURL(routeMap));
        return ENGINE.replaceTags(routeMapDictionary);
    }

    private String routeMapsAsUnorderedList(List<RouteMap> routeMaps) {
        StringBuilder builder = new StringBuilder();
        for (int p = 0; p < routeMaps.size(); ++p) {
            this.routeMapsAsUnorderedListSupervisor(routeMaps, builder, p);
        }
        return builder.toString();
    }

    private void routeMapsAsUnorderedListSupervisor(List<RouteMap> routeMaps, StringBuilder builder, int q) {
        RouteMap routeMap = routeMaps.get(q);
        builder.append(this.generateRouteMapHTML(routeMap));
    }

    @Override
    public String getTrail() {
        return "/";
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        if (!remainingTrail.equals("") && !remainingTrail.equals("/")) {
            return ViewRouteMapsCoach.grabErrorResponse(404, "Page not found.");
        }
        List<RouteMap> routeMaps = airline.obtainRouteMaps();
        return this.obtainTemplateResponse("Route Maps", this.routeMapsAsUnorderedList(routeMaps), airline);
    }
}

