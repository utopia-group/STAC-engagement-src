/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network.coach;

import com.roboticcusp.network.WebTemplate;
import com.roboticcusp.network.coach.AbstractHttpCoach;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AuthenticationCoach
extends AbstractHttpCoach {
    private final String redirectResponseTrail;
    private final WebTemplate template;
    private static final String TRAIL = "/authenticate";
    private static final String TITLE = "Authenticate Server";
    private static final String KEY_FIELD = "A";
    private static final String TIMESTAMP_FIELD = "setTimestamp";
    private static final TemplateEngine TEMPLATE_ENGINE = new TemplateEngine("<center><form action=\"/authenticate\" method=\"post\" enctype=\"multipart/form-data\"/>    <textarea name=\"A\" placeholder=\"Enter your public key\"       rows=\"10\" cols=\"100\"/></textarea><br/>    <input type=\"submit\" value=\"Compute the master secret\" name=\"submit\" />    <input type=\"hidden\" name=\"setTimestamp\" value=\"{{includeTimestamp}}\"></form></center>");

    public AuthenticationCoach(String redirectResponseTrail) {
        this.redirectResponseTrail = redirectResponseTrail;
        this.template = new WebTemplate("basiccontenttemplate.html", this.getClass());
    }

    @Override
    public String getTrail() {
        return "/authenticate";
    }

    @Override
    protected HttpCoachResponse handleFetch(HttpExchange httpExchange) {
        HashMap<String, String> contentsTemplateMap = new HashMap<String, String>();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        String suppressTimestamp = AuthenticationCoach.fetchUrlParam(httpExchange, "suppressTimestamp");
        if (suppressTimestamp == null || !suppressTimestamp.equals("true")) {
            this.handleFetchGuide(httpExchange, contentsTemplateMap, templateMap);
        } else {
            contentsTemplateMap.put("includeTimestamp", "false");
        }
        templateMap.put("contents", TEMPLATE_ENGINE.replaceTags(contentsTemplateMap));
        templateMap.put("title", "Authenticate Server");
        return AuthenticationCoach.pullResponse(this.template.getEngine().replaceTags(templateMap));
    }

    private void handleFetchGuide(HttpExchange httpExchange, Map<String, String> contentsTemplateMap, Map<String, String> templateMap) {
        contentsTemplateMap.put("includeTimestamp", "true");
        templateMap.put("timestamp", new Date().toString());
        templateMap.put("duration", String.valueOf(this.takeDuration(httpExchange)));
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange) {
        String timestamp;
        List<String> includeTimestampList;
        HashSet<String> fieldNames = new HashSet<String>(Arrays.asList("A", "setTimestamp"));
        Map<String, List<String>> fieldNameItems = MultipartHelper.getMultipartValues(httpExchange, fieldNames);
        String participantsPublicKey = "";
        boolean includeTimestamp = true;
        List<String> participantsPublicKeyList = fieldNameItems.get("A");
        if (participantsPublicKeyList != null && participantsPublicKeyList.size() == 1) {
            participantsPublicKey = participantsPublicKeyList.get(0);
        }
        if ((includeTimestampList = fieldNameItems.get("setTimestamp")) != null && includeTimestampList.size() == 1 && (timestamp = includeTimestampList.get(0)).equals("false")) {
            includeTimestamp = false;
        }
        String urlEnd = "";
        if (participantsPublicKey != null) {
            urlEnd = participantsPublicKey;
        }
        String suppressTimestamp = AuthenticationCoach.fetchUrlParam(httpExchange, "suppressTimestamp");
        if (!includeTimestamp || suppressTimestamp != null && suppressTimestamp.equals("true")) {
            urlEnd = urlEnd + "?suppressTimestamp=true";
        }
        return AuthenticationCoach.getRedirectResponse(this.redirectResponseTrail + "/" + urlEnd);
    }
}

