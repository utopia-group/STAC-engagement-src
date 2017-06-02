/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.Link;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.server.WebSession;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.WebTemplate;
import net.techpoint.server.manager.AbstractHttpGuide;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.template.TemplateEngine;
import net.techpoint.template.Templated;

public abstract class AirGuide
extends AbstractHttpGuide {
    private final WebSessionService webSessionService;
    private final AirDatabase db;
    private final WebTemplate masterTemplate;
    private final WebTemplate menuTemplate;

    protected AirGuide(AirDatabase db, WebSessionService webSessionService) {
        this.db = db;
        this.webSessionService = webSessionService;
        this.masterTemplate = new WebTemplate("basiccontenttemplate.html", this.getClass());
        this.menuTemplate = new WebTemplate("MenuItemTemplate.html", this.getClass());
    }

    @Override
    protected HttpGuideResponse handleGrab(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.obtainTrail())) {
            return AirGuide.getErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.obtainTrail().length());
        String userId = this.webSessionService.takeSession(httpExchange).takeUserId();
        return this.handleObtain(httpExchange, remainingTrail, this.db.fetchAirline(userId));
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.obtainTrail())) {
            return AirGuide.getErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.obtainTrail().length());
        String userId = this.webSessionService.takeSession(httpExchange).takeUserId();
        return this.handlePost(httpExchange, remainingTrail, this.db.fetchAirline(userId));
    }

    @Override
    protected HttpGuideResponse handleInsert(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.obtainTrail())) {
            return AirGuide.getErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.obtainTrail().length());
        String userId = this.webSessionService.takeSession(httpExchange).takeUserId();
        return this.handlePut(httpExchange, remainingTrail, this.db.fetchAirline(userId));
    }

    @Override
    protected HttpGuideResponse handleDelete(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.obtainTrail())) {
            return AirGuide.getErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.obtainTrail().length());
        String userId = this.webSessionService.takeSession(httpExchange).takeUserId();
        return this.handleDelete(httpExchange, remainingTrail, this.db.fetchAirline(userId));
    }

    protected HttpGuideResponse getTemplateResponse(String title, String contents, Airline user) {
        List<Link> finalMenuItems = this.takeOneMenuItems();
        finalMenuItems.addAll(this.takeLastMenuItems());
        return this.obtainTemplateResponse(title, contents, user, finalMenuItems);
    }

    protected HttpGuideResponse obtainTemplateResponse(String title, String contents, Airline user, List<Link> menuItems) {
        Map<String, String> templateMap = user.takeTemplateMap();
        templateMap.put("contents", contents);
        templateMap.put("title", title);
        templateMap.put("displayName", this.getDisplayName(user));
        templateMap.put("main_menu", this.menuTemplate.pullEngine().replaceTags(menuItems));
        return AirGuide.getResponse(this.masterTemplate.pullEngine().replaceTags(templateMap));
    }

    protected HttpGuideResponse obtainTemplateResponseWithoutMenuItems(String title, String contents, Airline user) {
        return this.obtainTemplateResponse(title, contents, user, Collections.emptyList());
    }

    protected HttpGuideResponse takeTemplateErrorResponse(String message, Airline user) {
        return this.getTemplateResponse("ERROR", message, user);
    }

    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline user) {
        return AirGuide.fetchBadMethodResponse(httpExchange);
    }

    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline user) {
        return AirGuide.fetchBadMethodResponse(httpExchange);
    }

    protected HttpGuideResponse handlePut(HttpExchange httpExchange, String remainingTrail, Airline user) {
        return AirGuide.fetchBadMethodResponse(httpExchange);
    }

    protected HttpGuideResponse handleDelete(HttpExchange httpExchange, String remainingTrail, Airline user) {
        return AirGuide.fetchBadMethodResponse(httpExchange);
    }

    protected List<Link> takeOneMenuItems() {
        LinkedList<Link> items = new LinkedList<Link>();
        items.add(new Link("/", "Route Maps"));
        items.add(new Link("/add_route_map", "Add Route Map"));
        items.add(new Link("/delete_route_maps", "Delete Route Maps"));
        return items;
    }

    protected List<Link> takeLastMenuItems() {
        LinkedList<Link> items = new LinkedList<Link>();
        items.add(new Link("/logout", "Logout"));
        return items;
    }

    protected AirDatabase takeDb() {
        return this.db;
    }

    protected WebSessionService takeWebSessionService() {
        return this.webSessionService;
    }

    protected String getDisplayName(Airline user) {
        return user.grabAirlineName();
    }
}

