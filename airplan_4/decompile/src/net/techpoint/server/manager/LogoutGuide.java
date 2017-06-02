/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server.manager;

import com.sun.net.httpserver.HttpExchange;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.AbstractHttpGuide;
import net.techpoint.server.manager.HttpGuideResponse;

public class LogoutGuide
extends AbstractHttpGuide {
    private final WebSessionService webSessionService;
    public static final String TRAIL = "/logout";
    public static final String TITLE = "Logout";

    public LogoutGuide(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
    }

    @Override
    public String obtainTrail() {
        return "/logout";
    }

    @Override
    protected HttpGuideResponse handleGrab(HttpExchange httpExchange) {
        this.webSessionService.invalidateSession(httpExchange);
        return LogoutGuide.takeDefaultRedirectResponse();
    }
}

