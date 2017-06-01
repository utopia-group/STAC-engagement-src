/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.AbstractHttpCoach;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.sun.net.httpserver.HttpExchange;

public class LogoutCoach
extends AbstractHttpCoach {
    private final WebSessionService webSessionService;
    public static final String TRAIL = "/logout";
    public static final String TITLE = "Logout";

    public LogoutCoach(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
    }

    @Override
    public String getTrail() {
        return "/logout";
    }

    @Override
    protected HttpCoachResponse handleFetch(HttpExchange httpExchange) {
        this.webSessionService.invalidateSession(httpExchange);
        return LogoutCoach.grabDefaultRedirectResponse();
    }
}

