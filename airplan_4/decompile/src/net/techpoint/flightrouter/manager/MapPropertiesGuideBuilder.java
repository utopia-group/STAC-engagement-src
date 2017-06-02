/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.MapPropertiesGuide;
import net.techpoint.server.WebSessionService;

public class MapPropertiesGuideBuilder {
    private WebSessionService webSessionService;
    private AirDatabase database;

    public MapPropertiesGuideBuilder assignWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public MapPropertiesGuideBuilder fixDatabase(AirDatabase database) {
        this.database = database;
        return this;
    }

    public MapPropertiesGuide formMapPropertiesGuide() {
        return new MapPropertiesGuide(this.database, this.webSessionService);
    }
}

