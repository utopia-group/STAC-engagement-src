/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network;

import com.roboticcusp.network.WebSession;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        String sessionId = this.obtainSessionIdFromCookie(httpExchange);
        if (sessionId != null && this.sessions.containsKey(sessionId)) {
            return this.obtainSessionUtility(httpExchange, sessionId);
        }
        return null;
    }

    private WebSession obtainSessionUtility(HttpExchange httpExchange, String sessionId) {
        if (System.nanoTime() - this.times.get(sessionId) > this.sessionExpirationInNanos) {
            return this.obtainSessionUtilityEntity(httpExchange);
        }
        this.times.put(sessionId, System.nanoTime());
        this.setCookie(httpExchange, sessionId, "86400");
        return this.sessions.get(sessionId);
    }

    private WebSession obtainSessionUtilityEntity(HttpExchange httpExchange) {
        this.invalidateSession(httpExchange);
        return null;
    }

    private String obtainSessionIdFromCookie(HttpExchange httpExchange) {
        List<String> cookies = httpExchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            for (int i1 = 0; i1 < cookies.size(); ++i1) {
                String cookie = (String)cookies.get(i1);
                String[] cookiePieces = cookie.split(";");
                for (int i = 0; i < cookiePieces.length; ++i) {
                    String cookiePiece = cookiePieces[i];
                    String[] cookieNameValuePair = cookiePiece.split("=");
                    if (!this.cookieName.equals(cookieNameValuePair[0].trim())) continue;
                    return cookieNameValuePair[1].trim();
                }
            }
        }
        return null;
    }

    private void setCookie(HttpExchange httpExchange, String sessionId, String maxAge) {
        httpExchange.getResponseHeaders().set("Set-Cookie", this.cookieName + "=" + sessionId + "; path=/; HttpOnly " + "; max-age=" + maxAge + "; Secure; ");
    }

    public void invalidateSession(HttpExchange httpExchange) {
        String sessionId = this.obtainSessionIdFromCookie(httpExchange);
        if (sessionId != null) {
            this.invalidateSessionHelp(httpExchange, sessionId);
        }
    }

    private void invalidateSessionHelp(HttpExchange httpExchange, String sessionId) {
        this.sessions.remove(sessionId);
        this.times.remove(sessionId);
        this.setCookie(httpExchange, sessionId, "0");
    }
}

