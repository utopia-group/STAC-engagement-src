/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network.coach;

import com.roboticcusp.network.Participant;
import com.roboticcusp.network.ParticipantConductor;
import com.roboticcusp.network.WebSession;
import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.AbstractHttpCoach;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class LoginFilter
extends Filter {
    private final ParticipantConductor participantConductor;
    private final WebSessionService webSessionService;
    private final String authenticationCoachTrail;

    public LoginFilter(ParticipantConductor participantConductor, WebSessionService webSessionService, String authenticationCoachTrail) {
        this.participantConductor = participantConductor;
        this.webSessionService = webSessionService;
        this.authenticationCoachTrail = authenticationCoachTrail;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
        WebSession webSession = this.webSessionService.takeSession(httpExchange);
        Participant participant = null;
        if (webSession != null) {
            participant = this.participantConductor.pullParticipantByIdentity(webSession.grabParticipantId());
        }
        if (participant != null) {
            httpExchange.setAttribute("userId", webSession.grabParticipantId());
            chain.doFilter(httpExchange);
        } else {
            HttpCoachResponse response = AbstractHttpCoach.getRedirectResponse(this.authenticationCoachTrail);
            response.deliverResponse(httpExchange);
        }
    }

    @Override
    public String description() {
        return "Login Filter";
    }
}

