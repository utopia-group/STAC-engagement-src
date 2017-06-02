/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.cybertip.netmanager.WebSession;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.WebTemplate;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.Link;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.Templated;

public abstract class AirCoach
extends AbstractHttpCoach {
    private final WebSessionService webSessionService;
    private final AirDatabase db;
    private final WebTemplate masterTemplate;
    private final WebTemplate menuTemplate;

    protected AirCoach(AirDatabase db, WebSessionService webSessionService) {
        this.db = db;
        this.webSessionService = webSessionService;
        this.masterTemplate = new WebTemplate("basiccontenttemplate.html", this.getClass());
        this.menuTemplate = new WebTemplate("MenuItemTemplate.html", this.getClass());
    }

    @Override
    protected HttpCoachResponse handleTake(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        if (!path.startsWith(this.grabPath())) {
            return AirCoach.obtainErrorResponse(403, "Invalid Path: " + path);
        }
        String remainingPath = path.substring(this.grabPath().length());
        String memberId = this.webSessionService.obtainSession(httpExchange).obtainMemberId();
        return this.handleObtain(httpExchange, remainingPath, this.db.grabAirline(memberId));
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        if (!path.startsWith(this.grabPath())) {
            return AirCoach.obtainErrorResponse(403, "Invalid Path: " + path);
        }
        String remainingPath = path.substring(this.grabPath().length());
        String memberId = this.webSessionService.obtainSession(httpExchange).obtainMemberId();
        return this.handlePost(httpExchange, remainingPath, this.db.grabAirline(memberId));
    }

    @Override
    protected HttpCoachResponse handleInsert(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        if (!path.startsWith(this.grabPath())) {
            return AirCoach.obtainErrorResponse(403, "Invalid Path: " + path);
        }
        String remainingPath = path.substring(this.grabPath().length());
        String memberId = this.webSessionService.obtainSession(httpExchange).obtainMemberId();
        return this.handlePut(httpExchange, remainingPath, this.db.grabAirline(memberId));
    }

    @Override
    protected HttpCoachResponse handleDelete(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        if (!path.startsWith(this.grabPath())) {
            return AirCoach.obtainErrorResponse(403, "Invalid Path: " + path);
        }
        String remainingPath = path.substring(this.grabPath().length());
        String memberId = this.webSessionService.obtainSession(httpExchange).obtainMemberId();
        return this.handleDelete(httpExchange, remainingPath, this.db.grabAirline(memberId));
    }

    protected HttpCoachResponse grabTemplateResponse(String title, String contents, Airline member) {
        List<Link> finalMenuItems = this.takeFirstMenuItems();
        finalMenuItems.addAll(this.grabLastMenuItems());
        return this.getTemplateResponse(title, contents, member, finalMenuItems);
    }

    protected HttpCoachResponse getTemplateResponse(String title, String contents, Airline member, List<Link> menuItems) {
        Map<String, String> templateMap = member.takeTemplateMap();
        templateMap.put("contents", contents);
        templateMap.put("title", title);
        templateMap.put("displayName", this.grabDisplayName(member));
        templateMap.put("main_menu", this.menuTemplate.getEngine().replaceTags(menuItems));
        return AirCoach.grabResponse(this.masterTemplate.getEngine().replaceTags(templateMap));
    }

    protected HttpCoachResponse fetchTemplateResponseWithoutMenuItems(String title, String contents, Airline member) {
        return this.getTemplateResponse(title, contents, member, Collections.emptyList());
    }

    protected HttpCoachResponse pullTemplateErrorResponse(String message, Airline member) {
        return this.grabTemplateResponse("ERROR", message, member);
    }

    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline member) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline member) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handlePut(HttpExchange httpExchange, String remainingPath, Airline member) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handleDelete(HttpExchange httpExchange, String remainingPath, Airline member) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected List<Link> takeFirstMenuItems() {
        LinkedList<Link> items = new LinkedList<Link>();
        items.add(new Link("/", "Route Maps"));
        items.add(new Link("/add_route_map", "Add Route Map"));
        items.add(new Link("/delete_route_maps", "Delete Route Maps"));
        return items;
    }

    protected List<Link> grabLastMenuItems() {
        LinkedList<Link> items = new LinkedList<Link>();
        items.add(new Link("/logout", "Logout"));
        return items;
    }

    protected AirDatabase getDb() {
        return this.db;
    }

    protected WebSessionService takeWebSessionService() {
        return this.webSessionService;
    }

    protected String grabDisplayName(Airline member) {
        return member.grabAirlineName();
    }
}

