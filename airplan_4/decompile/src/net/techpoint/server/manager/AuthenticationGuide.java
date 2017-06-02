/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.techpoint.server.WebTemplate;
import net.techpoint.server.manager.AbstractHttpGuide;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;

public class AuthenticationGuide
extends AbstractHttpGuide {
    private final String redirectResponseTrail;
    private final WebTemplate template;
    private static final String TRAIL = "/authenticate";
    private static final String TITLE = "Authenticate Server";
    private static final String KEY_FIELD = "A";
    private static final String TIMESTAMP_FIELD = "setTimestamp";
    private static final TemplateEngine TEMPLATE_ENGINE = new TemplateEngine("<center><form action=\"/authenticate\" method=\"post\" enctype=\"multipart/form-data\"/>    <textarea name=\"A\" placeholder=\"Enter your public key\"       rows=\"10\" cols=\"100\"/></textarea><br/>    <input type=\"submit\" value=\"Compute the master secret\" name=\"submit\" />    <input type=\"hidden\" name=\"setTimestamp\" value=\"{{includeTimestamp}}\"></form></center>");

    public AuthenticationGuide(String redirectResponseTrail) {
        this.redirectResponseTrail = redirectResponseTrail;
        this.template = new WebTemplate("basiccontenttemplate.html", this.getClass());
    }

    @Override
    public String obtainTrail() {
        return "/authenticate";
    }

    @Override
    protected HttpGuideResponse handleGrab(HttpExchange httpExchange) {
        HashMap<String, String> contentsTemplateMap = new HashMap<String, String>();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        String suppressTimestamp = AuthenticationGuide.pullUrlParam(httpExchange, "suppressTimestamp");
        if (suppressTimestamp == null || !suppressTimestamp.equals("true")) {
            contentsTemplateMap.put("includeTimestamp", "true");
            templateMap.put("timestamp", new Date().toString());
            templateMap.put("duration", String.valueOf(this.getDuration(httpExchange)));
        } else {
            contentsTemplateMap.put("includeTimestamp", "false");
        }
        templateMap.put("contents", TEMPLATE_ENGINE.replaceTags(contentsTemplateMap));
        templateMap.put("title", "Authenticate Server");
        return AuthenticationGuide.getResponse(this.template.pullEngine().replaceTags(templateMap));
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange) {
        String timestamp;
        List<String> includeTimestampList;
        HashSet<String> fieldNames = new HashSet<String>(Arrays.asList("A", "setTimestamp"));
        Map<String, List<String>> fieldNameItems = MultipartHelper.getMultipartValues(httpExchange, fieldNames);
        String usersPublicKey = "";
        boolean includeTimestamp = true;
        List<String> usersPublicKeyList = fieldNameItems.get("A");
        if (usersPublicKeyList != null && usersPublicKeyList.size() == 1) {
            usersPublicKey = usersPublicKeyList.get(0);
        }
        if ((includeTimestampList = fieldNameItems.get("setTimestamp")) != null && includeTimestampList.size() == 1 && (timestamp = includeTimestampList.get(0)).equals("false")) {
            includeTimestamp = false;
        }
        String urlEnd = "";
        if (usersPublicKey != null) {
            urlEnd = usersPublicKey;
        }
        String suppressTimestamp = AuthenticationGuide.pullUrlParam(httpExchange, "suppressTimestamp");
        if (!includeTimestamp || suppressTimestamp != null && suppressTimestamp.equals("true")) {
            urlEnd = urlEnd + "?suppressTimestamp=true";
        }
        return AuthenticationGuide.getRedirectResponse(this.redirectResponseTrail + "/" + urlEnd);
    }
}

