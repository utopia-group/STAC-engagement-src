/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 */
package net.techpoint.server;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import net.techpoint.authenticate.KeyExchangeServer;
import net.techpoint.server.UserManager;
import net.techpoint.server.WebServerFactory;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.AbstractHttpGuide;
import net.techpoint.server.manager.AuthenticationGuide;
import net.techpoint.server.manager.LoginFilter;
import net.techpoint.server.manager.LoginGuide;
import net.techpoint.server.manager.LoginGuideBuilder;
import net.techpoint.server.manager.LogoutGuide;
import net.techpoint.server.manager.NoLoginFilter;
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

    public WebServer(String appBaseName, int port, InputStream resourceStream, String resourcePassword, File passwordKeyFile, File permissionKeyFile) throws IOException, GeneralSecurityException {
        this.httpsServer = WebServerFactory.createServer(port, resourceStream, resourcePassword);
        this.webSessionService = new WebSessionService(appBaseName, 1440);
        String string = this.passwordKey = passwordKeyFile == null ? null : FileUtils.readFileToString((File)passwordKeyFile);
        if (permissionKeyFile != null) {
            String permissionKey = FileUtils.readFileToString((File)permissionKeyFile);
            this.keyExchangeServer = new KeyExchangeServer(permissionKey);
        } else {
            this.keyExchangeServer = null;
        }
    }

    public HttpsServer takeServer() {
        return this.httpsServer;
    }

    public void addDefaultPermissionGuides(UserManager userManager, String userId) {
        this.loginFilter = new NoLoginFilter(userManager, this.webSessionService, userId);
    }

    public void addPermissionGuides(UserManager userManager, String loginDestinationTrail) {
        LoginGuide loginGuide = new LoginGuideBuilder().defineUserManager(userManager).assignWebSessionService(this.webSessionService).setKeyExchangeServer(this.keyExchangeServer).fixDestinationTrail(loginDestinationTrail).definePasswordKey(this.passwordKey).formLoginGuide();
        this.formContext(loginGuide, false);
        LogoutGuide logoutGuide = new LogoutGuide(this.webSessionService);
        this.formContext(logoutGuide, false);
        String loginFilterTrail = loginGuide.obtainTrail();
        if (this.keyExchangeServer != null) {
            AuthenticationGuide authenticationGuide = new AuthenticationGuide(loginGuide.obtainTrail());
            this.formContext(authenticationGuide, false);
            loginFilterTrail = authenticationGuide.obtainTrail();
        }
        this.loginFilter = new LoginFilter(userManager, this.webSessionService, loginFilterTrail);
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

    public HttpContext formContext(AbstractHttpGuide guide, boolean requirePermission) {
        HttpContext context = this.httpsServer.createContext(guide.obtainTrail(), guide);
        if (requirePermission) {
            this.formContextService(context);
        }
        return context;
    }

    private void formContextService(HttpContext context) {
        new WebServerUtility(context).invoke();
    }

    private class WebServerUtility {
        private HttpContext context;

        public WebServerUtility(HttpContext context) {
            this.context = context;
        }

        public void invoke() {
            this.context.getFilters().add(WebServer.this.loginFilter);
        }
    }

}

