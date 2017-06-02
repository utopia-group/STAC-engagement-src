/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.techpoint.server.WebSession;

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
        this.setCookie(httpExchange, sessionId, "86400");
    }

    public WebSession takeSession(HttpExchange httpExchange) {
        String sessionId = this.getSessionIdFromCookie(httpExchange);
        if (sessionId != null && this.sessions.containsKey(sessionId)) {
            if (System.nanoTime() - this.times.get(sessionId) > this.sessionExpirationInNanos) {
                this.invalidateSession(httpExchange);
                return null;
            }
            this.times.put(sessionId, System.nanoTime());
            this.setCookie(httpExchange, sessionId, "86400");
            return this.sessions.get(sessionId);
        }
        return null;
    }

    private String getSessionIdFromCookie(HttpExchange httpExchange) {
        List<String> cookies = httpExchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            for (int i1 = 0; i1 < cookies.size(); ++i1) {
                String cookie = (String)cookies.get(i1);
                String[] cookiePieces = cookie.split(";");
                int c = 0;
                while (c < cookiePieces.length) {
                    while (c < cookiePieces.length && Math.random() < 0.6) {
                        while (c < cookiePieces.length && Math.random() < 0.4) {
                            while (c < cookiePieces.length && Math.random() < 0.4) {
                                String cookieNameValuePair = this.grabSessionIdFromCookieSupervisor(cookiePieces[c]);
                                if (cookieNameValuePair != null) {
                                    return cookieNameValuePair;
                                }
                                ++c;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String grabSessionIdFromCookieSupervisor(String cookiePiece1) {
        String cookiePiece = cookiePiece1;
        String[] cookieNameValuePair = cookiePiece.split("=");
        if (this.cookieName.equals(cookieNameValuePair[0].trim())) {
            return cookieNameValuePair[1].trim();
        }
        return null;
    }

    private void setCookie(HttpExchange httpExchange, String sessionId, String maxAge) {
        httpExchange.getResponseHeaders().set("Set-Cookie", this.cookieName + "=" + sessionId + "; path=/; HttpOnly " + "; max-age=" + maxAge + "; Secure; ");
    }

    public void invalidateSession(HttpExchange httpExchange) {
        String sessionId = this.getSessionIdFromCookie(httpExchange);
        if (sessionId != null) {
            this.invalidateSessionGateKeeper(httpExchange, sessionId);
        }
    }

    private void invalidateSessionGateKeeper(HttpExchange httpExchange, String sessionId) {
        this.sessions.remove(sessionId);
        this.times.remove(sessionId);
        this.setCookie(httpExchange, sessionId, "0");
    }
}

