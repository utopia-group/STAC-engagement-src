/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import net.cybertip.netmanager.WebSessionService;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.TipCoach;

public class TipCoachBuilder {
    private AirDatabase airDatabase;
    private WebSessionService webSessionService;

    public TipCoachBuilder setAirDatabase(AirDatabase airDatabase) {
        this.airDatabase = airDatabase;
        return this;
    }

    public TipCoachBuilder assignWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public TipCoach makeTipCoach() {
        return new TipCoach(this.airDatabase, this.webSessionService);
    }
}

