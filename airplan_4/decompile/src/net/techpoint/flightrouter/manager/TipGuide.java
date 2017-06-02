/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.GuideUtils;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.WebTemplate;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.template.TemplateEngine;

public class TipGuide
extends AirGuide {
    protected static final String TRAIL = "/tips";
    protected static final String TITLE = "AirPlan Tips";
    private static final WebTemplate TIPS_TEMPLATE = new WebTemplate("TipsTemplate.html", TipGuide.class);

    public TipGuide(AirDatabase airDatabase, WebSessionService webSessionService) {
        super(airDatabase, webSessionService);
    }

    @Override
    public String obtainTrail() {
        return "/tips";
    }

    private Map<String, String> takeContentsMap() {
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("deleteMapURL", GuideUtils.generateDeleteMapURL());
        templateMap.put("summaryURL", GuideUtils.generateSummaryURL());
        return templateMap;
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange exchange, String remainingTrail, Airline airline) {
        Map<String, String> contentsMap = this.takeContentsMap();
        String contents = TIPS_TEMPLATE.pullEngine().replaceTags(contentsMap);
        return this.obtainTemplateResponseWithoutMenuItems("AirPlan Tips", contents, airline);
    }
}

