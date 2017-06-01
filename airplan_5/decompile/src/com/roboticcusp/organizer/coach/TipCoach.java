/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.WebTemplate;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CoachUtils;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;

public class TipCoach
extends AirCoach {
    protected static final String TRAIL = "/tips";
    protected static final String TITLE = "AirPlan Tips";
    private static final WebTemplate TIPS_TEMPLATE = new WebTemplate("TipsTemplate.html", TipCoach.class);

    public TipCoach(AirDatabase airDatabase, WebSessionService webSessionService) {
        super(airDatabase, webSessionService);
    }

    @Override
    public String getTrail() {
        return "/tips";
    }

    private Map<String, String> obtainContentsMap() {
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("deleteMapURL", CoachUtils.generateDeleteMapURL());
        templateMap.put("summaryURL", CoachUtils.generateSummaryURL());
        return templateMap;
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange exchange, String remainingTrail, Airline airline) {
        Map<String, String> contentsMap = this.obtainContentsMap();
        String contents = TIPS_TEMPLATE.getEngine().replaceTags(contentsMap);
        return this.takeTemplateResponseWithoutMenuItems("AirPlan Tips", contents, airline);
    }
}

