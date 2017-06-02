/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.cybertip.netmanager.manager;

import com.sun.net.httpserver.HttpExchange;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.cybertip.DESHelper;
import net.cybertip.auth.KeyExchangeServer;
import net.cybertip.netmanager.Member;
import net.cybertip.netmanager.MemberOverseer;
import net.cybertip.netmanager.WebSession;
import net.cybertip.netmanager.WebSessionBuilder;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.WebTemplate;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.template.TemplateEngine;
import org.apache.commons.lang3.StringUtils;

public class LoginCoach
extends AbstractHttpCoach {
    private static final String PATH = "/login";
    private static final String TITLE = "Login";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String AUTH_LOGIN_TEMPLATE_FILE = "logintemplate.html";
    private static final String LOGIN_TEMPLATE_FILE = "simplelogintemplate.html";
    private final MemberOverseer memberOverseer;
    private final WebSessionService webSessionService;
    private final KeyExchangeServer keyExchangeServer;
    private final WebTemplate template;
    private final String destinationPath;
    private final String passwordKey;

    public LoginCoach(MemberOverseer memberOverseer, WebSessionService webSessionService, KeyExchangeServer keyExchangeServer, String destinationPath, String passwordKey) {
        this.memberOverseer = Objects.requireNonNull(memberOverseer, "UserManager must be specified");
        this.webSessionService = Objects.requireNonNull(webSessionService, "WebSessionService must be specified");
        this.keyExchangeServer = keyExchangeServer;
        this.template = new WebTemplate(keyExchangeServer != null ? "logintemplate.html" : "simplelogintemplate.html", this.getClass());
        this.destinationPath = destinationPath;
        this.passwordKey = passwordKey;
    }

    @Override
    public String grabPath() {
        return "/login";
    }

    @Override
    protected HttpCoachResponse handleTake(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI().getPath();
        if (path.startsWith(this.grabPath()) && (path = path.substring(this.grabPath().length())).length() > 0 && path.startsWith("/")) {
            path = path.substring(1);
        }
        TemplateEngine templateEngine = this.template.getEngine();
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("title", "Login");
        templateMap.put("path", this.grabPath());
        if (this.keyExchangeServer != null && path.length() > 0 && path.length() < 10000) {
            BigInteger membersPublicKey;
            try {
                membersPublicKey = path.startsWith("0x") ? new BigInteger(path.substring(2), 16) : new BigInteger(path);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error: key must be hexadecimal or decimal");
            }
            BigInteger masterSecret = this.keyExchangeServer.generateMasterSecret(membersPublicKey);
            templateMap.put("masterSecret", masterSecret.toString());
        } else {
            templateMap.put("masterSecret", "Null");
        }
        String suppressTimeStamp = LoginCoach.takeUrlParam(httpExchange, "suppressTimestamp");
        if (StringUtils.isBlank((CharSequence)suppressTimeStamp) || !suppressTimeStamp.equalsIgnoreCase("true")) {
            templateMap.put("duration", String.valueOf(this.obtainDuration(httpExchange)));
            templateMap.put("timestamp", new Date().toString());
        }
        return LoginCoach.grabResponse(templateEngine.replaceTags(templateMap));
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange) {
        HashSet<String> fieldNames = new HashSet<String>();
        fieldNames.add("username");
        fieldNames.add("password");
        Map<String, List<String>> loginCredentials = MultipartHelper.pullMultipartValues(httpExchange, fieldNames);
        List<String> usernames = loginCredentials.get("username");
        List<String> passwords = loginCredentials.get("password");
        if (usernames != null && usernames.size() == 1 && passwords != null && passwords.size() == 1) {
            String password;
            Member currentMember;
            String username = usernames.get(0);
            String encryptedPw = password = passwords.get(0);
            if (this.passwordKey != null) {
                encryptedPw = DESHelper.takeEncryptedString(password, this.passwordKey);
            }
            if ((currentMember = this.memberOverseer.getMemberByUsername(username)) != null && currentMember.matches(username, encryptedPw)) {
                return this.handlePostCoach(httpExchange, currentMember);
            }
        }
        throw new IllegalArgumentException("Invalid username or password (or both)");
    }

    private HttpCoachResponse handlePostCoach(HttpExchange httpExchange, Member currentMember) {
        WebSession webSession = new WebSessionBuilder().assignMemberId(currentMember.takeIdentity()).makeWebSession();
        this.webSessionService.addSession(httpExchange, webSession);
        if (this.destinationPath == null) {
            return LoginCoach.obtainDefaultRedirectResponse();
        }
        return LoginCoach.fetchRedirectResponse(this.destinationPath);
    }
}

