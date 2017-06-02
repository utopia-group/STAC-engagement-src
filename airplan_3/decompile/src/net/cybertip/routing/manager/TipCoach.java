/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.WebTemplate;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.CoachUtils;
import net.cybertip.template.TemplateEngine;

public class TipCoach
extends AirCoach {
    protected static final String PATH = "/tips";
    protected static final String TITLE = "AirPlan Tips";
    private static final WebTemplate TIPS_TEMPLATE = new WebTemplate("TipsTemplate.html", TipCoach.class);

    public TipCoach(AirDatabase airDatabase, WebSessionService webSessionService) {
        super(airDatabase, webSessionService);
    }

    @Override
    public String grabPath() {
        return "/tips";
    }

    private String grabContents() {
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("deleteMapURL", CoachUtils.generateDeleteMapURL());
        templateMap.put("summaryURL", CoachUtils.generateSummaryURL());
        return TIPS_TEMPLATE.getEngine().replaceTags(templateMap);
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange exchange, String remainingPath, Airline airline) {
        return this.fetchTemplateResponseWithoutMenuItems("AirPlan Tips", this.grabContents(), airline);
    }
}

