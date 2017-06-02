/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager.manager;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import net.cybertip.netmanager.Member;
import net.cybertip.netmanager.MemberOverseer;
import net.cybertip.netmanager.WebSession;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.netmanager.manager.HttpCoachResponse;

public class LoginFilter
extends Filter {
    private final MemberOverseer memberOverseer;
    private final WebSessionService webSessionService;
    private final String authenticationCoachPath;

    public LoginFilter(MemberOverseer memberOverseer, WebSessionService webSessionService, String authenticationCoachPath) {
        this.memberOverseer = memberOverseer;
        this.webSessionService = webSessionService;
        this.authenticationCoachPath = authenticationCoachPath;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
        WebSession webSession = this.webSessionService.obtainSession(httpExchange);
        Member member = null;
        if (webSession != null) {
            member = this.memberOverseer.obtainMemberByIdentity(webSession.obtainMemberId());
        }
        if (member != null) {
            httpExchange.setAttribute("userId", webSession.obtainMemberId());
            chain.doFilter(httpExchange);
        } else {
            HttpCoachResponse response = AbstractHttpCoach.fetchRedirectResponse(this.authenticationCoachPath);
            response.transferResponse(httpExchange);
        }
    }

    @Override
    public String description() {
        return "Login Filter";
    }
}

