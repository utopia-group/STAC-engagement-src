/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server.manager;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import net.techpoint.server.User;
import net.techpoint.server.UserManager;
import net.techpoint.server.WebSession;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.AbstractHttpGuide;
import net.techpoint.server.manager.HttpGuideResponse;

public class NoLoginFilter
extends Filter {
    private final UserManager userManager;
    private final WebSessionService webSessionService;
    private final String userId;

    public NoLoginFilter(UserManager userManager, WebSessionService webSessionService, String userId) {
        this.userManager = userManager;
        this.webSessionService = webSessionService;
        this.userId = userId;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
        WebSession webSession = this.webSessionService.takeSession(httpExchange);
        if (webSession == null) {
            User user = this.userManager.grabUserByIdentity(this.userId);
            webSession = new WebSession(this.userId);
            this.webSessionService.addSession(httpExchange, webSession);
            HttpGuideResponse response = AbstractHttpGuide.getRedirectResponse(httpExchange.getRequestURI().toString());
            response.sendResponse(httpExchange);
        } else {
            User user = this.userManager.grabUserByIdentity(webSession.takeUserId());
            if (user != null) {
                httpExchange.setAttribute("userId", webSession.takeUserId());
                chain.doFilter(httpExchange);
            } else {
                throw new IllegalArgumentException("No user associated with " + this.userId);
            }
        }
    }

    @Override
    public String description() {
        return "No Login Filter";
    }
}

