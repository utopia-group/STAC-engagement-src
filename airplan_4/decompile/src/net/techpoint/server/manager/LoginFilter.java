/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server.manager;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import net.techpoint.server.User;
import net.techpoint.server.UserManager;
import net.techpoint.server.WebSession;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.AbstractHttpGuide;
import net.techpoint.server.manager.HttpGuideResponse;

public class LoginFilter
extends Filter {
    private final UserManager userManager;
    private final WebSessionService webSessionService;
    private final String authenticationGuideTrail;

    public LoginFilter(UserManager userManager, WebSessionService webSessionService, String authenticationGuideTrail) {
        this.userManager = userManager;
        this.webSessionService = webSessionService;
        this.authenticationGuideTrail = authenticationGuideTrail;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
        WebSession webSession = this.webSessionService.takeSession(httpExchange);
        User user = null;
        if (webSession != null) {
            user = this.userManager.grabUserByIdentity(webSession.takeUserId());
        }
        if (user != null) {
            httpExchange.setAttribute("userId", webSession.takeUserId());
            chain.doFilter(httpExchange);
        } else {
            HttpGuideResponse response = AbstractHttpGuide.getRedirectResponse(this.authenticationGuideTrail);
            response.sendResponse(httpExchange);
        }
    }

    @Override
    public String description() {
        return "Login Filter";
    }
}

