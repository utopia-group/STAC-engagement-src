/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.roboticcusp.network.coach;

import com.roboticcusp.DESHelper;
import com.roboticcusp.authorize.KeyExchangeServer;
import com.roboticcusp.network.Participant;
import com.roboticcusp.network.ParticipantConductor;
import com.roboticcusp.network.WebSession;
import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.WebTemplate;
import com.roboticcusp.network.coach.AbstractHttpCoach;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class LoginCoach
extends AbstractHttpCoach {
    private static final String TRAIL = "/login";
    private static final String TITLE = "Login";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String AUTH_LOGIN_TEMPLATE_FILE = "logintemplate.html";
    private static final String LOGIN_TEMPLATE_FILE = "simplelogintemplate.html";
    private final ParticipantConductor participantConductor;
    private final WebSessionService webSessionService;
    private final KeyExchangeServer keyExchangeServer;
    private final WebTemplate template;
    private final String destinationTrail;
    private final String passwordKey;

    public LoginCoach(ParticipantConductor participantConductor, WebSessionService webSessionService, KeyExchangeServer keyExchangeServer, String destinationTrail, String passwordKey) {
        this.participantConductor = Objects.requireNonNull(participantConductor, "UserManager must be specified");
        this.webSessionService = Objects.requireNonNull(webSessionService, "WebSessionService must be specified");
        this.keyExchangeServer = keyExchangeServer;
        this.template = new WebTemplate(keyExchangeServer != null ? "logintemplate.html" : "simplelogintemplate.html", this.getClass());
        this.destinationTrail = destinationTrail;
        this.passwordKey = passwordKey;
    }

    @Override
    public String getTrail() {
        return "/login";
    }

    @Override
    protected HttpCoachResponse handleFetch(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (trail.startsWith(this.getTrail()) && (trail = trail.substring(this.getTrail().length())).length() > 0 && trail.startsWith("/")) {
            trail = trail.substring(1);
        }
        TemplateEngine templateEngine = this.template.getEngine();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("title", "Login");
        templateMap.put("path", this.getTrail());
        if (this.keyExchangeServer != null && trail.length() > 0 && trail.length() < 10000) {
            BigInteger participantsPublicKey;
            try {
                participantsPublicKey = trail.startsWith("0x") ? new BigInteger(trail.substring(2), 16) : new BigInteger(trail);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error: key must be hexadecimal or decimal");
            }
            BigInteger masterSecret = this.keyExchangeServer.generateMasterSecret(participantsPublicKey);
            templateMap.put("masterSecret", masterSecret.toString());
        } else {
            templateMap.put("masterSecret", "Null");
        }
        String suppressTimeStamp = LoginCoach.fetchUrlParam(httpExchange, "suppressTimestamp");
        if (StringUtils.isBlank((CharSequence)suppressTimeStamp) || !suppressTimeStamp.equalsIgnoreCase("true")) {
            templateMap.put("duration", String.valueOf(this.takeDuration(httpExchange)));
            templateMap.put("timestamp", new Date().toString());
        }
        return LoginCoach.pullResponse(templateEngine.replaceTags(templateMap));
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange) {
        HashSet<String> fieldNames = new HashSet<String>();
        fieldNames.add("username");
        fieldNames.add("password");
        Map<String, List<String>> loginCredentials = MultipartHelper.getMultipartValues(httpExchange, fieldNames);
        List<String> usernames = loginCredentials.get("username");
        List<String> passwords = loginCredentials.get("password");
        if (usernames != null && usernames.size() == 1 && passwords != null && passwords.size() == 1) {
            String password;
            Participant currentParticipant;
            String username = usernames.get(0);
            String encryptedPw = password = passwords.get(0);
            if (this.passwordKey != null) {
                encryptedPw = DESHelper.getEncryptedString(password, this.passwordKey);
            }
            if ((currentParticipant = this.participantConductor.getParticipantByUsername(username)) != null && currentParticipant.matches(username, encryptedPw)) {
                WebSession webSession = new WebSession(currentParticipant.getIdentity());
                this.webSessionService.addSession(httpExchange, webSession);
                if (this.destinationTrail == null) {
                    return LoginCoach.grabDefaultRedirectResponse();
                }
                return LoginCoach.getRedirectResponse(this.destinationTrail);
            }
        }
        throw new IllegalArgumentException("Invalid username or password (or both)");
    }
}

