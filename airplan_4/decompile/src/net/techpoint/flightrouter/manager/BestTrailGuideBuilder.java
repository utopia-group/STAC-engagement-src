/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.BestTrailGuide;
import net.techpoint.server.WebSessionService;

public class BestTrailGuideBuilder {
    private AirDatabase db;
    private WebSessionService webSessionService;

    public BestTrailGuideBuilder setDb(AirDatabase db) {
        this.db = db;
        return this;
    }

    public BestTrailGuideBuilder fixWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public BestTrailGuide formBestTrailGuide() {
        return new BestTrailGuide(this.db, this.webSessionService);
    }
}

