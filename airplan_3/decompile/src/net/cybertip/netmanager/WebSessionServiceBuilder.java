/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import net.cybertip.netmanager.WebSessionService;

public class WebSessionServiceBuilder {
    private long sessionExpirationInMinutes;
    private String applicationBaseName;

    public WebSessionServiceBuilder defineSessionExpirationInMinutes(long sessionExpirationInMinutes) {
        this.sessionExpirationInMinutes = sessionExpirationInMinutes;
        return this;
    }

    public WebSessionServiceBuilder defineApplicationBaseName(String applicationBaseName) {
        this.applicationBaseName = applicationBaseName;
        return this;
    }

    public WebSessionService makeWebSessionService() {
        return new WebSessionService(this.applicationBaseName, this.sessionExpirationInMinutes);
    }
}

