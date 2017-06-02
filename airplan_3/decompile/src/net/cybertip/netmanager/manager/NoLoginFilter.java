/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager.manager;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import net.cybertip.netmanager.Member;
import net.cybertip.netmanager.MemberOverseer;
import net.cybertip.netmanager.WebSession;
import net.cybertip.netmanager.WebSessionBuilder;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.netmanager.manager.HttpCoachResponse;

public class NoLoginFilter
extends Filter {
    private final MemberOverseer memberOverseer;
    private final WebSessionService webSessionService;
    private final String memberId;

    public NoLoginFilter(MemberOverseer memberOverseer, WebSessionService webSessionService, String memberId) {
        this.memberOverseer = memberOverseer;
        this.webSessionService = webSessionService;
        this.memberId = memberId;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
        WebSession webSession = this.webSessionService.obtainSession(httpExchange);
        if (webSession == null) {
            Member member = this.memberOverseer.obtainMemberByIdentity(this.memberId);
            webSession = new WebSessionBuilder().assignMemberId(this.memberId).makeWebSession();
            this.webSessionService.addSession(httpExchange, webSession);
            HttpCoachResponse response = AbstractHttpCoach.fetchRedirectResponse(httpExchange.getRequestURI().toString());
            response.transferResponse(httpExchange);
        } else {
            Member member = this.memberOverseer.obtainMemberByIdentity(webSession.obtainMemberId());
            if (member != null) {
                new NoLoginFilterSupervisor(httpExchange, chain, webSession).invoke();
            } else {
                new NoLoginFilterService().invoke();
            }
        }
    }

    @Override
    public String description() {
        return "No Login Filter";
    }

    private class NoLoginFilterService {
        private NoLoginFilterService() {
        }

        public void invoke() {
            throw new IllegalArgumentException("No user associated with " + NoLoginFilter.this.memberId);
        }
    }

    private class NoLoginFilterSupervisor {
        private HttpExchange httpExchange;
        private Filter.Chain chain;
        private WebSession webSession;

        public NoLoginFilterSupervisor(HttpExchange httpExchange, Filter.Chain chain, WebSession webSession) {
            this.httpExchange = httpExchange;
            this.chain = chain;
            this.webSession = webSession;
        }

        public void invoke() throws IOException {
            this.httpExchange.setAttribute("userId", this.webSession.obtainMemberId());
            this.chain.doFilter(this.httpExchange);
        }
    }

}

