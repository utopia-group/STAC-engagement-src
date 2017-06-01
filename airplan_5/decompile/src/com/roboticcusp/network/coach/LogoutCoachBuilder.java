/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.LogoutCoach;

public class LogoutCoachBuilder {
    private WebSessionService webSessionService;

    public LogoutCoachBuilder setWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public LogoutCoach composeLogoutCoach() {
        return new LogoutCoach(this.webSessionService);
    }
}

