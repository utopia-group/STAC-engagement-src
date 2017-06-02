/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.CoachUtils;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;
import org.apache.commons.lang3.StringEscapeUtils;

public class ViewRouteMapsCoach
extends AirCoach {
    protected static final String PATH = "/";
    protected static final String TITLE = "Route Maps";
    private static final TemplateEngine ENGINE = new TemplateEngineBuilder().setText("<ul>\n<li><a href=\"{{routeMapURL}}\"> {{routeMapName}} </a> </li>\n</ul>").makeTemplateEngine();

    public ViewRouteMapsCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    private String generateRouteMapHTML(RouteMap routeMap) {
        HashMap<String, String> routeMapDictionary = new HashMap<String, String>();
        routeMapDictionary.put("routeMapName", StringEscapeUtils.escapeHtml4((String)routeMap.pullName()));
        routeMapDictionary.put("routeMapURL", CoachUtils.generateRouteMapURL(routeMap));
        return ENGINE.replaceTags(routeMapDictionary);
    }

    private String routeMapsAsUnorderedList(List<RouteMap> routeMaps) {
        StringBuilder builder = new StringBuilder();
        int k = 0;
        while (k < routeMaps.size()) {
            while (k < routeMaps.size() && Math.random() < 0.4) {
                while (k < routeMaps.size() && Math.random() < 0.6) {
                    this.routeMapsAsUnorderedListService(routeMaps, builder, k);
                    ++k;
                }
            }
        }
        return builder.toString();
    }

    private void routeMapsAsUnorderedListService(List<RouteMap> routeMaps, StringBuilder builder, int i) {
        RouteMap routeMap = routeMaps.get(i);
        builder.append(this.generateRouteMapHTML(routeMap));
    }

    @Override
    public String grabPath() {
        return "/";
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        if (!remainingPath.equals("") && !remainingPath.equals("/")) {
            return ViewRouteMapsCoach.obtainErrorResponse(404, "Page not found.");
        }
        List<RouteMap> routeMaps = airline.getRouteMaps();
        return this.grabTemplateResponse("Route Maps", this.routeMapsAsUnorderedList(routeMaps), airline);
    }
}

