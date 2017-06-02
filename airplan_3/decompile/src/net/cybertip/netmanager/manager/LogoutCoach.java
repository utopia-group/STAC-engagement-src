/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager.manager;

import com.sun.net.httpserver.HttpExchange;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.netmanager.manager.HttpCoachResponse;

public class LogoutCoach
extends AbstractHttpCoach {
    private final WebSessionService webSessionService;
    public static final String PATH = "/logout";
    public static final String TITLE = "Logout";

    public LogoutCoach(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
    }

    @Override
    public String grabPath() {
        return "/logout";
    }

    @Override
    protected HttpCoachResponse handleTake(HttpExchange httpExchange) {
        this.webSessionService.invalidateSession(httpExchange);
        return LogoutCoach.obtainDefaultRedirectResponse();
    }
}

