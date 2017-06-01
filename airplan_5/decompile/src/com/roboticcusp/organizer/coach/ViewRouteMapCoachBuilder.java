/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.organizer.coach.ViewRouteMapCoach;
import com.roboticcusp.organizer.save.AirDatabase;

public class ViewRouteMapCoachBuilder {
    private AirDatabase db;
    private WebSessionService webSessionService;

    public ViewRouteMapCoachBuilder assignDb(AirDatabase db) {
        this.db = db;
        return this;
    }

    public ViewRouteMapCoachBuilder assignWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public ViewRouteMapCoach composeViewRouteMapCoach() {
        return new ViewRouteMapCoach(this.db, this.webSessionService);
    }
}

