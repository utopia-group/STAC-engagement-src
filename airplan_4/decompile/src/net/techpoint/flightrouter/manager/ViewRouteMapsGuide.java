/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.GuideUtils;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.template.TemplateEngine;
import org.apache.commons.lang3.StringEscapeUtils;

public class ViewRouteMapsGuide
extends AirGuide {
    protected static final String TRAIL = "/";
    protected static final String TITLE = "Route Maps";
    private static final TemplateEngine ENGINE = new TemplateEngine("<ul>\n<li><a href=\"{{routeMapURL}}\"> {{routeMapName}} </a> </li>\n</ul>");

    public ViewRouteMapsGuide(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String generateRouteMapHTML(RouteMap routeMap) {
        HashMap<String, String> routeMapDictionary = new HashMap<String, String>();
        routeMapDictionary.put("routeMapName", StringEscapeUtils.escapeHtml4((String)routeMap.fetchName()));
        routeMapDictionary.put("routeMapURL", GuideUtils.generateRouteMapURL(routeMap));
        return ENGINE.replaceTags(routeMapDictionary);
    }

    private String routeMapsAsUnorderedList(List<RouteMap> routeMaps) {
        StringBuilder builder = new StringBuilder();
        int a = 0;
        while (a < routeMaps.size()) {
            while (a < routeMaps.size() && Math.random() < 0.5) {
                RouteMap routeMap = routeMaps.get(a);
                builder.append(this.generateRouteMapHTML(routeMap));
                ++a;
            }
        }
        return builder.toString();
    }

    @Override
    public String obtainTrail() {
        return "/";
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        if (!remainingTrail.equals("") && !remainingTrail.equals("/")) {
            return ViewRouteMapsGuide.getErrorResponse(404, "Page not found.");
        }
        List<RouteMap> routeMaps = airline.grabRouteMaps();
        return this.getTemplateResponse("Route Maps", this.routeMapsAsUnorderedList(routeMaps), airline);
    }
}

