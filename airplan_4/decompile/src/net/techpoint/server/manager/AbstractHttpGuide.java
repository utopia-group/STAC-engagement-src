/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.utils.URLEncodedUtils
 */
package net.techpoint.server.manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import net.techpoint.server.manager.HttpGuideResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public abstract class AbstractHttpGuide
implements HttpHandler {
    private static final String BAD_METHOD_FORMAT = "The requested resource [%s] does not support the http method '%s'.";
    private static final String FETCH = "GET";
    private static final String POST = "POST";
    private static final String INSERT = "PUT";
    private static final String DELETE = "DELETE";

    public abstract String obtainTrail();

    @Override
    public final void handle(HttpExchange httpExchange) throws IOException {
        HttpGuideResponse response;
        if (!httpExchange.getRequestURI().getPath().startsWith(this.obtainTrail())) {
            new HttpGuideResponse(404).sendResponse(httpExchange);
            return;
        }
        String requestMethod = httpExchange.getRequestMethod();
        try {
            httpExchange.setAttribute("time", System.nanoTime());
            response = "GET".equalsIgnoreCase(requestMethod) ? this.handleGrab(httpExchange) : ("POST".equalsIgnoreCase(requestMethod) ? this.handlePost(httpExchange) : ("PUT".equalsIgnoreCase(requestMethod) ? this.handleInsert(httpExchange) : ("DELETE".equalsIgnoreCase(requestMethod) ? this.handleDelete(httpExchange) : AbstractHttpGuide.fetchBadMethodResponse(httpExchange))));
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            response = AbstractHttpGuide.getErrorResponse(400, e.getMessage());
        }
        if (response == null) {
            response = new HttpGuideResponse();
        }
        response.sendResponse(httpExchange);
    }

    protected HttpGuideResponse handleGrab(HttpExchange httpExchange) {
        return AbstractHttpGuide.fetchBadMethodResponse(httpExchange);
    }

    protected HttpGuideResponse handlePost(HttpExchange httpExchange) {
        return AbstractHttpGuide.fetchBadMethodResponse(httpExchange);
    }

    protected HttpGuideResponse handleInsert(HttpExchange httpExchange) {
        return AbstractHttpGuide.fetchBadMethodResponse(httpExchange);
    }

    protected HttpGuideResponse handleDelete(HttpExchange httpExchange) {
        return AbstractHttpGuide.fetchBadMethodResponse(httpExchange);
    }

    public static HttpGuideResponse takeDefaultRedirectResponse() {
        return AbstractHttpGuide.getRedirectResponse("/");
    }

    public static HttpGuideResponse getRedirectResponse(String redirectLocation) {
        return new HttpGuideResponse(303, redirectLocation);
    }

    public static HttpGuideResponse fetchBadMethodResponse(HttpExchange httpExchange) {
        String reason = String.format("The requested resource [%s] does not support the http method '%s'.", httpExchange.getRequestURI().toString(), httpExchange.getRequestMethod());
        return AbstractHttpGuide.getErrorResponse(405, reason);
    }

    public static HttpGuideResponse getErrorResponse(int code, String reason) {
        String html = String.format("<html>%n<body>%n<h1>Error</h1>%nError code = %d%nMessage = %s%n</body>%n</html>%n", code, reason);
        return new HttpGuideResponse(code, html);
    }

    public static HttpGuideResponse getResponse(String html) {
        return AbstractHttpGuide.obtainResponse(200, html);
    }

    public static HttpGuideResponse obtainResponse(int code, String html) {
        return new HttpGuideResponse(code, html);
    }

    public static String pullUrlParam(HttpExchange httpExchange, String name) {
        URI uri = httpExchange.getRequestURI();
        List urlParams = URLEncodedUtils.parse((URI)uri, (String)"UTF-8");
        for (int k = 0; k < urlParams.size(); ++k) {
            String pair = AbstractHttpGuide.fetchUrlParamGuide(name, urlParams, k);
            if (pair == null) continue;
            return pair;
        }
        return null;
    }

    private static String fetchUrlParamGuide(String name, List<NameValuePair> urlParams, int b) {
        NameValuePair pair = urlParams.get(b);
        if (pair.getName().equals(name)) {
            return pair.getValue();
        }
        return null;
    }

    public long getDuration(HttpExchange httpExchange) {
        long startTime = (Long)httpExchange.getAttribute("time");
        return System.nanoTime() - startTime;
    }
}

