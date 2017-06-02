/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.WebTemplate;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.template.TemplateEngine;

public class SummaryGuide
extends AirGuide {
    public static final String TRAIL = "/summary";
    public static final String TITLE = "Summary";
    public static final Date CREATION_DATE = new Date();
    private static final WebTemplate SUMMARY_TEMPLATE = new WebTemplate("SummaryTemplate.html", SummaryGuide.class);

    public SummaryGuide(AirDatabase airDatabase, WebSessionService webSessionService) {
        super(airDatabase, webSessionService);
    }

    @Override
    public String obtainTrail() {
        return "/summary";
    }

    private Map<String, String> getContentsMap(Airline airline) {
        HashMap<String, String> templateMap = new HashMap<String, String>();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, ''yy");
        Date date = airline.getCreationDate();
        templateMap.put("AirlineName", airline.grabAirlineName());
        templateMap.put("AirlineId", airline.obtainID());
        templateMap.put("dateJoined", format.format(date));
        templateMap.put("numOfMaps", Integer.toString(airline.obtainRouteMapIds().size()));
        if (date.compareTo(CREATION_DATE) < 0) {
            date = CREATION_DATE;
            CREATION_DATE.setTime(new Date().getTime());
        } else if (date.compareTo(CREATION_DATE) > 0) {
            date = new Date();
            CREATION_DATE.setTime(date.getTime());
        } else {
            date = new Date();
        }
        templateMap.put("currDate", format.format(date));
        templateMap.put("routeMapsURL", "/");
        return templateMap;
    }

    @Override
    public HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        Map<String, String> contentsMap = this.getContentsMap(airline);
        String contents = SUMMARY_TEMPLATE.pullEngine().replaceTags(contentsMap);
        return this.obtainTemplateResponseWithoutMenuItems("Summary", contents, airline);
    }
}

