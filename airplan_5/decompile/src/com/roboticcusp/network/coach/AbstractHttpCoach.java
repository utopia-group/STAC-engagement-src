/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.utils.URLEncodedUtils
 */
package com.roboticcusp.network.coach;

import com.roboticcusp.network.coach.HttpCoachResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public abstract class AbstractHttpCoach
implements HttpHandler {
    private static final String BAD_METHOD_FORMAT = "The requested resource [%s] does not support the http method '%s'.";
    private static final String FETCH = "GET";
    private static final String POST = "POST";
    private static final String PLACE = "PUT";
    private static final String DELETE = "DELETE";

    public abstract String getTrail();

    @Override
    public final void handle(HttpExchange httpExchange) throws IOException {
        HttpCoachResponse response;
        if (!httpExchange.getRequestURI().getPath().startsWith(this.getTrail())) {
            new HttpCoachResponse(404).deliverResponse(httpExchange);
            return;
        }
        String requestMethod = httpExchange.getRequestMethod();
        try {
            httpExchange.setAttribute("time", System.nanoTime());
            response = "GET".equalsIgnoreCase(requestMethod) ? this.handleFetch(httpExchange) : ("POST".equalsIgnoreCase(requestMethod) ? this.handlePost(httpExchange) : ("PUT".equalsIgnoreCase(requestMethod) ? this.handleInsert(httpExchange) : ("DELETE".equalsIgnoreCase(requestMethod) ? this.handleDelete(httpExchange) : AbstractHttpCoach.getBadMethodResponse(httpExchange))));
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            response = AbstractHttpCoach.grabErrorResponse(400, e.getMessage());
        }
        if (response == null) {
            response = new HttpCoachResponse();
        }
        response.deliverResponse(httpExchange);
    }

    protected HttpCoachResponse handleFetch(HttpExchange httpExchange) {
        return AbstractHttpCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handlePost(HttpExchange httpExchange) {
        return AbstractHttpCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handleInsert(HttpExchange httpExchange) {
        return AbstractHttpCoach.getBadMethodResponse(httpExchange);
    }

    protected HttpCoachResponse handleDelete(HttpExchange httpExchange) {
        return AbstractHttpCoach.getBadMethodResponse(httpExchange);
    }

    public static HttpCoachResponse grabDefaultRedirectResponse() {
        return AbstractHttpCoach.getRedirectResponse("/");
    }

    public static HttpCoachResponse getRedirectResponse(String redirectLocation) {
        return new HttpCoachResponse(303, redirectLocation);
    }

    public static HttpCoachResponse getBadMethodResponse(HttpExchange httpExchange) {
        String reason = String.format("The requested resource [%s] does not support the http method '%s'.", httpExchange.getRequestURI().toString(), httpExchange.getRequestMethod());
        return AbstractHttpCoach.grabErrorResponse(405, reason);
    }

    public static HttpCoachResponse grabErrorResponse(int code, String reason) {
        String html = String.format("<html>%n<body>%n<h1>Error</h1>%nError code = %d%nMessage = %s%n</body>%n</html>%n", code, reason);
        return new HttpCoachResponse(code, html);
    }

    public static HttpCoachResponse pullResponse(String html) {
        return AbstractHttpCoach.grabResponse(200, html);
    }

    public static HttpCoachResponse grabResponse(int code, String html) {
        return new HttpCoachResponse(code, html);
    }

    public static String fetchUrlParam(HttpExchange httpExchange, String name) {
        URI uri = httpExchange.getRequestURI();
        List urlParams = URLEncodedUtils.parse((URI)uri, (String)"UTF-8");
        for (int k = 0; k < urlParams.size(); ++k) {
            NameValuePair pair = (NameValuePair)urlParams.get(k);
            if (!pair.getName().equals(name)) continue;
            return pair.getValue();
        }
        return null;
    }

    public long takeDuration(HttpExchange httpExchange) {
        long startTime = (Long)httpExchange.getAttribute("time");
        return System.nanoTime() - startTime;
    }
}

