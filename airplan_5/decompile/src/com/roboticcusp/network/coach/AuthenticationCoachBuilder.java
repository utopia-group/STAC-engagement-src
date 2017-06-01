/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network.coach;

import com.roboticcusp.network.coach.AuthenticationCoach;

public class AuthenticationCoachBuilder {
    private String redirectResponseTrail;

    public AuthenticationCoachBuilder fixRedirectResponseTrail(String redirectResponseTrail) {
        this.redirectResponseTrail = redirectResponseTrail;
        return this;
    }

    public AuthenticationCoach composeAuthenticationCoach() {
        return new AuthenticationCoach(this.redirectResponseTrail);
    }
}

