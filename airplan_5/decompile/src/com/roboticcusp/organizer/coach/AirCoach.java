/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSession;
import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.WebTemplate;
import com.roboticcusp.network.coach.AbstractHttpCoach;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.organizer.coach.Link;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.roboticcusp.template.Templated;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    protected HttpCoachResponse handleFetch(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.getTrail())) {
            return AirCoach.grabErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.getTrail().length());
        String participantId = this.webSessionService.takeSession(httpExchange).grabParticipantId();
        return this.handleGrab(httpExchange, remainingTrail, this.db.obtainAirline(participantId));
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.getTrail())) {
            return AirCoach.grabErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.getTrail().length());
        String participantId = this.webSessionService.takeSession(httpExchange).grabParticipantId();
        return this.handlePost(httpExchange, remainingTrail, this.db.obtainAirline(participantId));
    }

    @Override
    protected HttpCoachResponse handleInsert(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.getTrail())) {
            return AirCoach.grabErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.getTrail().length());
        String participantId = this.webSessionService.takeSession(httpExchange).grabParticipantId();
        return this.handlePlace(httpExchange, remainingTrail, this.db.obtainAirline(participantId));
    }

    @Override
    protected HttpCoachResponse handleDelete(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (!trail.startsWith(this.getTrail())) {
            return AirCoach.grabErrorResponse(403, "Invalid Path: " + trail);
        }
        String remainingTrail = trail.substring(this.getTrail().length());
        String participantId = this.webSessionService.takeSession(httpExchange).grabParticipantId();
        return this.handleDelete(httpExchange, remainingTrail, this.db.obtainAirline(participantId));
    }

    protected HttpCoachResponse obtainTemplateResponse(String title, String contents, Airline participant) {
        List<Link> finalMenuItems = this.fetchOneMenuItems();
        finalMenuItems.addAll(this.takeRightMenuItems());
        return this.getTemplateResponse(title, contents, participant, finalMenuItems);
    }

    protected HttpCoachResponse getTemplateResponse(String title, String contents, Airline participant, List<Link> menuItems) {
        Map<String, String> templateMap = participant.obtainTemplateMap();
        templateMap.put("contents", contents);
        templateMap.put("title", title);
        templateMap.put("displayName", this.grabDisplayName(participant));
        templateMap.put("main_menu", this.menuTemplate.getEngine().replaceTags(menuItems));
        return AirCoach.pullResponse(this.masterTemplate.getEngine().replaceTags(templateMap));
    }

    protected HttpCoachResponse takeTemplateResponseWithoutMenuItems(String title, String contents, Airline participant) {
        return this.getTemplateResponse(title, contents, participant, Collections.emptyList());
    }

    protected HttpCoachResponse pullTemplateErrorResponse(String message, Airline participant) {
        return this.obtainTemplateResponse("ERROR", message, participant);
    }

    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handlePlace(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handleDelete(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        return AirCoach.getBadMethodResponse(httpExchange);
    }

    protected List<Link> fetchOneMenuItems() {
        LinkedList<Link> items = new LinkedList<Link>();
        items.add(new Link("/", "Route Maps"));
        items.add(new Link("/add_route_map", "Add Route Map"));
        items.add(new Link("/delete_route_maps", "Delete Route Maps"));
        return items;
    }

    protected List<Link> takeRightMenuItems() {
        LinkedList<Link> items = new LinkedList<Link>();
        items.add(new Link("/logout", "Logout"));
        return items;
    }

    protected AirDatabase takeDb() {
        return this.db;
    }

    protected WebSessionService grabWebSessionService() {
        return this.webSessionService;
    }

    protected String grabDisplayName(Airline participant) {
        return participant.obtainAirlineName();
    }
}

