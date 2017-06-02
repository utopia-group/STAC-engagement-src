/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.cybertip.netmanager.WebSession;

public class WebSessionService {
    private static final String COOKIE_EXPIRATION = "86400";
    private final Map<String, Long> times = new HashMap<String, Long>();
    private final Map<String, WebSession> sessions = new HashMap<String, WebSession>();
    private final long sessionExpirationInNanos;
    private final String cookieName;

    public WebSessionService(String applicationBaseName, long sessionExpirationInMinutes) {
        this.cookieName = applicationBaseName + "_sessionId";
        this.sessionExpirationInNanos = TimeUnit.NANOSECONDS.convert(sessionExpirationInMinutes, TimeUnit.MINUTES);
    }

    public void addSession(HttpExchange httpExchange, WebSession session) {
        String sessionId = UUID.randomUUID().toString();
        this.sessions.put(sessionId, session);
        this.times.put(sessionId, System.nanoTime());
        this.defineCookie(httpExchange, sessionId, "86400");
    }

    public WebSession obtainSession(HttpExchange httpExchange) {
        String sessionId = this.pullSessionIdFromCookie(httpExchange);
        if (sessionId != null && this.sessions.containsKey(sessionId)) {
            if (System.nanoTime() - this.times.get(sessionId) > this.sessionExpirationInNanos) {
                return this.takeSessionCoordinator(httpExchange);
            }
            this.times.put(sessionId, System.nanoTime());
            this.defineCookie(httpExchange, sessionId, "86400");
            return this.sessions.get(sessionId);
        }
        return null;
    }

    private WebSession takeSessionCoordinator(HttpExchange httpExchange) {
        this.invalidateSession(httpExchange);
        return null;
    }

    private String pullSessionIdFromCookie(HttpExchange httpExchange) {
        List<String> cookies = httpExchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            int i1 = 0;
            while (i1 < cookies.size()) {
                while (i1 < cookies.size() && Math.random() < 0.4) {
                    while (i1 < cookies.size() && Math.random() < 0.4) {
                        String cookie = (String)cookies.get(i1);
                        String[] cookiePieces = cookie.split(";");
                        for (int j = 0; j < cookiePieces.length; ++j) {
                            String cookiePiece = cookiePieces[j];
                            String[] cookieNameValuePair = cookiePiece.split("=");
                            if (!this.cookieName.equals(cookieNameValuePair[0].trim())) continue;
                            return cookieNameValuePair[1].trim();
                        }
                        ++i1;
                    }
                }
            }
        }
        return null;
    }

    private void defineCookie(HttpExchange httpExchange, String sessionId, String maxAge) {
        httpExchange.getResponseHeaders().set("Set-Cookie", this.cookieName + "=" + sessionId + "; path=/; HttpOnly " + "; max-age=" + maxAge + "; Secure; ");
    }

    public void invalidateSession(HttpExchange httpExchange) {
        String sessionId = this.pullSessionIdFromCookie(httpExchange);
        if (sessionId != null) {
            this.sessions.remove(sessionId);
            this.times.remove(sessionId);
            this.defineCookie(httpExchange, sessionId, "0");
        }
    }
}

