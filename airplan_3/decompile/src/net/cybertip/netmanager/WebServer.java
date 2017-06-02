/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 */
package net.cybertip.netmanager;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import net.cybertip.auth.KeyExchangeServer;
import net.cybertip.netmanager.MemberOverseer;
import net.cybertip.netmanager.WebServerFactory;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.WebSessionServiceBuilder;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.netmanager.manager.AuthenticationCoach;
import net.cybertip.netmanager.manager.AuthenticationCoachBuilder;
import net.cybertip.netmanager.manager.LoginCoach;
import net.cybertip.netmanager.manager.LoginFilter;
import net.cybertip.netmanager.manager.LogoutCoach;
import net.cybertip.netmanager.manager.NoLoginFilter;
import org.apache.commons.io.FileUtils;

public class WebServer {
    private static final long DEFAULT_SESSION_TIMEOUT_IN_MINUTES = 1440;
    private final HttpsServer httpsServer;
    private final WebSessionService webSessionService;
    private final KeyExchangeServer keyExchangeServer;
    private final String passwordKey;
    private static final int SECONDS_TO_WAIT_TO_CLOSE = 0;
    private Filter loginFilter;

    public WebServer(String appBaseName, int port, InputStream resourceStream, String resourcePassword, File passwordKeyFile) throws IOException, GeneralSecurityException {
        this(appBaseName, port, resourceStream, resourcePassword, passwordKeyFile, null);
    }

    public WebServer(String appBaseName, int port, InputStream resourceStream, String resourcePassword, File passwordKeyFile, File authorizeKeyFile) throws IOException, GeneralSecurityException {
        this.httpsServer = WebServerFactory.createServer(port, resourceStream, resourcePassword);
        this.webSessionService = new WebSessionServiceBuilder().defineApplicationBaseName(appBaseName).defineSessionExpirationInMinutes(1440).makeWebSessionService();
        String string = this.passwordKey = passwordKeyFile == null ? null : FileUtils.readFileToString((File)passwordKeyFile);
        if (authorizeKeyFile != null) {
            String authorizeKey = FileUtils.readFileToString((File)authorizeKeyFile);
            this.keyExchangeServer = new KeyExchangeServer(authorizeKey);
        } else {
            this.keyExchangeServer = null;
        }
    }

    public HttpsServer obtainServer() {
        return this.httpsServer;
    }

    public void addDefaultAuthorizeCoaches(MemberOverseer memberOverseer, String memberId) {
        this.loginFilter = new NoLoginFilter(memberOverseer, this.webSessionService, memberId);
    }

    public void addAuthorizeCoaches(MemberOverseer memberOverseer, String loginDestinationPath) {
        LoginCoach loginCoach = new LoginCoach(memberOverseer, this.webSessionService, this.keyExchangeServer, loginDestinationPath, this.passwordKey);
        this.makeContext(loginCoach, false);
        LogoutCoach logoutCoach = new LogoutCoach(this.webSessionService);
        this.makeContext(logoutCoach, false);
        String loginFilterPath = loginCoach.grabPath();
        if (this.keyExchangeServer != null) {
            AuthenticationCoach authenticationCoach = new AuthenticationCoachBuilder().assignRedirectResponsePath(loginCoach.grabPath()).makeAuthenticationCoach();
            this.makeContext(authenticationCoach, false);
            loginFilterPath = authenticationCoach.grabPath();
        }
        this.loginFilter = new LoginFilter(memberOverseer, this.webSessionService, loginFilterPath);
    }

    public WebSessionService getWebSessionService() {
        return this.webSessionService;
    }

    public void stop() {
        this.httpsServer.stop(0);
    }

    public void stop(int secondsToWaitToClose) {
        this.httpsServer.stop(secondsToWaitToClose);
    }

    public void start() {
        this.httpsServer.start();
    }

    public HttpContext makeContext(AbstractHttpCoach coach, boolean requireAuthorize) {
        HttpContext context = this.httpsServer.createContext(coach.grabPath(), coach);
        if (requireAuthorize) {
            context.getFilters().add(this.loginFilter);
        }
        return context;
    }
}

