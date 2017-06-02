/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import net.cybertip.netmanager.WebSessionService;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.CrewSchedulingCoach;

public class CrewSchedulingCoachBuilder {
    private AirDatabase airDatabase;
    private WebSessionService sessionService;

    public CrewSchedulingCoachBuilder assignAirDatabase(AirDatabase airDatabase) {
        this.airDatabase = airDatabase;
        return this;
    }

    public CrewSchedulingCoachBuilder defineSessionService(WebSessionService sessionService) {
        this.sessionService = sessionService;
        return this;
    }

    public CrewSchedulingCoach makeCrewSchedulingCoach() {
        return new CrewSchedulingCoach(this.airDatabase, this.sessionService);
    }
}

