/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.cybertip.netmanager.WebTemplate;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class AuthenticationCoach
extends AbstractHttpCoach {
    private final String redirectResponsePath;
    private final WebTemplate template;
    private static final String PATH = "/authenticate";
    private static final String TITLE = "Authenticate Server";
    private static final String KEY_FIELD = "A";
    private static final String TIMESTAMP_FIELD = "setTimestamp";
    private static final TemplateEngine TEMPLATE_ENGINE = new TemplateEngineBuilder().setText("<center><form action=\"/authenticate\" method=\"post\" enctype=\"multipart/form-data\"/>    <textarea name=\"A\" placeholder=\"Enter your public key\"       rows=\"10\" cols=\"100\"/></textarea><br/>    <input type=\"submit\" value=\"Compute the master secret\" name=\"submit\" />    <input type=\"hidden\" name=\"setTimestamp\" value=\"{{includeTimestamp}}\"></form></center>").makeTemplateEngine();

    public AuthenticationCoach(String redirectResponsePath) {
        this.redirectResponsePath = redirectResponsePath;
        this.template = new WebTemplate("basiccontenttemplate.html", this.getClass());
    }

    @Override
    public String grabPath() {
        return "/authenticate";
    }

    @Override
    protected HttpCoachResponse handleTake(HttpExchange httpExchange) {
        HashMap<String, String> contentsTemplateMap = new HashMap<String, String>();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        String suppressTimestamp = AuthenticationCoach.takeUrlParam(httpExchange, "suppressTimestamp");
        if (suppressTimestamp == null || !suppressTimestamp.equals("true")) {
            this.handleTakeAssist(httpExchange, contentsTemplateMap, templateMap);
        } else {
            this.handleTakeHerder(contentsTemplateMap);
        }
        templateMap.put("contents", TEMPLATE_ENGINE.replaceTags(contentsTemplateMap));
        templateMap.put("title", "Authenticate Server");
        return AuthenticationCoach.grabResponse(this.template.getEngine().replaceTags(templateMap));
    }

    private void handleTakeHerder(Map<String, String> contentsTemplateMap) {
        contentsTemplateMap.put("includeTimestamp", "false");
    }

    private void handleTakeAssist(HttpExchange httpExchange, Map<String, String> contentsTemplateMap, Map<String, String> templateMap) {
        contentsTemplateMap.put("includeTimestamp", "true");
        templateMap.put("timestamp", new Date().toString());
        templateMap.put("duration", String.valueOf(this.obtainDuration(httpExchange)));
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange) {
        String timestamp;
        List<String> includeTimestampList;
        HashSet<String> fieldNames = new HashSet<String>(Arrays.asList("A", "setTimestamp"));
        Map<String, List<String>> fieldNameItems = MultipartHelper.pullMultipartValues(httpExchange, fieldNames);
        String membersPublicKey = "";
        boolean includeTimestamp = true;
        List<String> membersPublicKeyList = fieldNameItems.get("A");
        if (membersPublicKeyList != null && membersPublicKeyList.size() == 1) {
            membersPublicKey = membersPublicKeyList.get(0);
        }
        if ((includeTimestampList = fieldNameItems.get("setTimestamp")) != null && includeTimestampList.size() == 1 && (timestamp = includeTimestampList.get(0)).equals("false")) {
            includeTimestamp = false;
        }
        String urlEnd = "";
        if (membersPublicKey != null) {
            urlEnd = membersPublicKey;
        }
        String suppressTimestamp = AuthenticationCoach.takeUrlParam(httpExchange, "suppressTimestamp");
        if (!includeTimestamp || suppressTimestamp != null && suppressTimestamp.equals("true")) {
            urlEnd = urlEnd + "?suppressTimestamp=true";
        }
        return AuthenticationCoach.fetchRedirectResponse(this.redirectResponsePath + "/" + urlEnd);
    }
}

