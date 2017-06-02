/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.techpoint.server.manager;

import com.sun.net.httpserver.HttpExchange;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.techpoint.DESHelper;
import net.techpoint.authenticate.KeyExchangeServer;
import net.techpoint.server.User;
import net.techpoint.server.UserManager;
import net.techpoint.server.WebSession;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.WebTemplate;
import net.techpoint.server.manager.AbstractHttpGuide;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;
import org.apache.commons.lang3.StringUtils;

public class LoginGuide
extends AbstractHttpGuide {
    private static final String TRAIL = "/login";
    private static final String TITLE = "Login";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String AUTH_LOGIN_TEMPLATE_FILE = "logintemplate.html";
    private static final String LOGIN_TEMPLATE_FILE = "simplelogintemplate.html";
    private final UserManager userManager;
    private final WebSessionService webSessionService;
    private final KeyExchangeServer keyExchangeServer;
    private final WebTemplate template;
    private final String destinationTrail;
    private final String passwordKey;

    public LoginGuide(UserManager userManager, WebSessionService webSessionService, KeyExchangeServer keyExchangeServer, String destinationTrail, String passwordKey) {
        this.userManager = Objects.requireNonNull(userManager, "UserManager must be specified");
        this.webSessionService = Objects.requireNonNull(webSessionService, "WebSessionService must be specified");
        this.keyExchangeServer = keyExchangeServer;
        this.template = new WebTemplate(keyExchangeServer != null ? "logintemplate.html" : "simplelogintemplate.html", this.getClass());
        this.destinationTrail = destinationTrail;
        this.passwordKey = passwordKey;
    }

    @Override
    public String obtainTrail() {
        return "/login";
    }

    @Override
    protected HttpGuideResponse handleGrab(HttpExchange httpExchange) {
        String trail = httpExchange.getRequestURI().getPath();
        if (trail.startsWith(this.obtainTrail()) && (trail = trail.substring(this.obtainTrail().length())).length() > 0 && trail.startsWith("/")) {
            trail = trail.substring(1);
        }
        TemplateEngine templateEngine = this.template.pullEngine();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("title", "Login");
        templateMap.put("path", this.obtainTrail());
        if (this.keyExchangeServer != null && trail.length() > 0 && trail.length() < 10000) {
            BigInteger usersPublicKey;
            try {
                usersPublicKey = trail.startsWith("0x") ? new BigInteger(trail.substring(2), 16) : new BigInteger(trail);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error: key must be hexadecimal or decimal");
            }
            BigInteger masterSecret = this.keyExchangeServer.generateMasterSecret(usersPublicKey);
            templateMap.put("masterSecret", masterSecret.toString());
        } else {
            templateMap.put("masterSecret", "Null");
        }
        String suppressTimeStamp = LoginGuide.pullUrlParam(httpExchange, "suppressTimestamp");
        if (StringUtils.isBlank((CharSequence)suppressTimeStamp) || !suppressTimeStamp.equalsIgnoreCase("true")) {
            templateMap.put("duration", String.valueOf(this.getDuration(httpExchange)));
            templateMap.put("timestamp", new Date().toString());
        }
        return LoginGuide.getResponse(templateEngine.replaceTags(templateMap));
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange) {
        HashSet<String> fieldNames = new HashSet<String>();
        fieldNames.add("username");
        fieldNames.add("password");
        Map<String, List<String>> loginCredentials = MultipartHelper.getMultipartValues(httpExchange, fieldNames);
        List<String> usernames = loginCredentials.get("username");
        List<String> passwords = loginCredentials.get("password");
        if (usernames != null && usernames.size() == 1 && passwords != null && passwords.size() == 1) {
            String password;
            User currentUser;
            String username = usernames.get(0);
            String encryptedPw = password = passwords.get(0);
            if (this.passwordKey != null) {
                encryptedPw = DESHelper.pullEncryptedString(password, this.passwordKey);
            }
            if ((currentUser = this.userManager.fetchUserByUsername(username)) != null && currentUser.matches(username, encryptedPw)) {
                WebSession webSession = new WebSession(currentUser.takeIdentity());
                this.webSessionService.addSession(httpExchange, webSession);
                if (this.destinationTrail == null) {
                    return LoginGuide.takeDefaultRedirectResponse();
                }
                return LoginGuide.getRedirectResponse(this.destinationTrail);
            }
        }
        throw new IllegalArgumentException("Invalid username or password (or both)");
    }
}

