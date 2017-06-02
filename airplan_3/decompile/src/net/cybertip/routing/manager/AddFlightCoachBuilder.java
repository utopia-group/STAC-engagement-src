/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import net.cybertip.netmanager.WebSessionService;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AddFlightCoach;

public class AddFlightCoachBuilder {
    private AirDatabase db;
    private WebSessionService webSessionService;

    public AddFlightCoachBuilder assignDb(AirDatabase db) {
        this.db = db;
        return this;
    }

    public AddFlightCoachBuilder setWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public AddFlightCoach makeAddFlightCoach() {
        return new AddFlightCoach(this.db, this.webSessionService);
    }
}

