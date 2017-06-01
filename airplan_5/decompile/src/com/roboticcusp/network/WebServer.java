/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 */
package com.roboticcusp.network;

import com.roboticcusp.authorize.KeyExchangeServer;
import com.roboticcusp.network.ParticipantConductor;
import com.roboticcusp.network.WebServerFactory;
import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.AbstractHttpCoach;
import com.roboticcusp.network.coach.AuthenticationCoach;
import com.roboticcusp.network.coach.AuthenticationCoachBuilder;
import com.roboticcusp.network.coach.LoginCoach;
import com.roboticcusp.network.coach.LoginFilter;
import com.roboticcusp.network.coach.LogoutCoach;
import com.roboticcusp.network.coach.LogoutCoachBuilder;
import com.roboticcusp.network.coach.NoLoginFilter;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
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
        this.webSessionService = new WebSessionService(appBaseName, 1440);
        String string = this.passwordKey = passwordKeyFile == null ? null : FileUtils.readFileToString((File)passwordKeyFile);
        if (authorizeKeyFile != null) {
            String authorizeKey = FileUtils.readFileToString((File)authorizeKeyFile);
            this.keyExchangeServer = new KeyExchangeServer(authorizeKey);
        } else {
            this.keyExchangeServer = null;
        }
    }

    public HttpsServer takeServer() {
        return this.httpsServer;
    }

    public void addDefaultAuthorizeCoaches(ParticipantConductor participantConductor, String participantId) {
        this.loginFilter = new NoLoginFilter(participantConductor, this.webSessionService, participantId);
    }

    public void addAuthorizeCoaches(ParticipantConductor participantConductor, String loginDestinationTrail) {
        LoginCoach loginCoach = new LoginCoach(participantConductor, this.webSessionService, this.keyExchangeServer, loginDestinationTrail, this.passwordKey);
        this.composeContext(loginCoach, false);
        LogoutCoach logoutCoach = new LogoutCoachBuilder().setWebSessionService(this.webSessionService).composeLogoutCoach();
        this.composeContext(logoutCoach, false);
        String loginFilterTrail = loginCoach.getTrail();
        if (this.keyExchangeServer != null) {
            AuthenticationCoach authenticationCoach = new AuthenticationCoachBuilder().fixRedirectResponseTrail(loginCoach.getTrail()).composeAuthenticationCoach();
            this.composeContext(authenticationCoach, false);
            loginFilterTrail = authenticationCoach.getTrail();
        }
        this.loginFilter = new LoginFilter(participantConductor, this.webSessionService, loginFilterTrail);
    }

    public WebSessionService fetchWebSessionService() {
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

    public HttpContext composeContext(AbstractHttpCoach coach, boolean requireAuthorize) {
        HttpContext context = this.httpsServer.createContext(coach.getTrail(), coach);
        if (requireAuthorize) {
            context.getFilters().add(this.loginFilter);
        }
        return context;
    }
}

