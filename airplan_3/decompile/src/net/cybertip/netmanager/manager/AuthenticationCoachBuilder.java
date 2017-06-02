/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager.manager;

import net.cybertip.netmanager.manager.AuthenticationCoach;

public class AuthenticationCoachBuilder {
    private String redirectResponsePath;

    public AuthenticationCoachBuilder assignRedirectResponsePath(String redirectResponsePath) {
        this.redirectResponsePath = redirectResponsePath;
        return this;
    }

    public AuthenticationCoach makeAuthenticationCoach() {
        return new AuthenticationCoach(this.redirectResponsePath);
    }
}

