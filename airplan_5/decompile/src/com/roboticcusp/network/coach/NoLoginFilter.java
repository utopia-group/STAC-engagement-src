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
import java.net.URI;

public class NoLoginFilter
extends Filter {
    private final ParticipantConductor participantConductor;
    private final WebSessionService webSessionService;
    private final String participantId;

    public NoLoginFilter(ParticipantConductor participantConductor, WebSessionService webSessionService, String participantId) {
        this.participantConductor = participantConductor;
        this.webSessionService = webSessionService;
        this.participantId = participantId;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
        WebSession webSession = this.webSessionService.takeSession(httpExchange);
        if (webSession == null) {
            Participant participant = this.participantConductor.pullParticipantByIdentity(this.participantId);
            webSession = new WebSession(this.participantId);
            this.webSessionService.addSession(httpExchange, webSession);
            HttpCoachResponse response = AbstractHttpCoach.getRedirectResponse(httpExchange.getRequestURI().toString());
            response.deliverResponse(httpExchange);
        } else {
            Participant participant = this.participantConductor.pullParticipantByIdentity(webSession.grabParticipantId());
            if (participant != null) {
                this.doFilterWorker(httpExchange, chain, webSession);
            } else {
                throw new IllegalArgumentException("No user associated with " + this.participantId);
            }
        }
    }

    private void doFilterWorker(HttpExchange httpExchange, Filter.Chain chain, WebSession webSession) throws IOException {
        httpExchange.setAttribute("userId", webSession.grabParticipantId());
        chain.doFilter(httpExchange);
    }

    @Override
    public String description() {
        return "No Login Filter";
    }
}

