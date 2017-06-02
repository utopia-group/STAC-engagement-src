/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.ViewRouteMapsGuide;
import net.techpoint.server.WebSessionService;

public class ViewRouteMapsGuideBuilder {
    private AirDatabase db;
    private WebSessionService webSessionService;

    public ViewRouteMapsGuideBuilder setDb(AirDatabase db) {
        this.db = db;
        return this;
    }

    public ViewRouteMapsGuideBuilder fixWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public ViewRouteMapsGuide formViewRouteMapsGuide() {
        return new ViewRouteMapsGuide(this.db, this.webSessionService);
    }
}

