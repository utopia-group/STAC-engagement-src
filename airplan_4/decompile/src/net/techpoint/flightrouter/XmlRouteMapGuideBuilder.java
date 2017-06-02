/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import net.techpoint.flightrouter.XmlRouteMapGuide;
import net.techpoint.flightrouter.keep.AirDatabase;

public class XmlRouteMapGuideBuilder {
    private AirDatabase database;

    public XmlRouteMapGuideBuilder fixDatabase(AirDatabase database) {
        this.database = database;
        return this;
    }

    public XmlRouteMapGuide formXmlRouteMapGuide() {
        return new XmlRouteMapGuide(this.database);
    }
}

