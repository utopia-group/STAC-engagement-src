/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.utils.URLEncodedUtils
 */
package net.cybertip.netmanager.manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public abstract class AbstractHttpCoach
implements HttpHandler {
    private static final String BAD_METHOD_FORMAT = "The requested resource [%s] does not support the http method '%s'.";
    private static final String PULL = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    public abstract String grabPath();

    @Override
    public final void handle(HttpExchange httpExchange) throws IOException {
        HttpCoachResponse response;
        if (!httpExchange.getRequestURI().getPath().startsWith(this.grabPath())) {
            new HttpCoachResponse(404).transferResponse(httpExchange);
            return;
        }
        String requestMethod = httpExchange.getRequestMethod();
        try {
            httpExchange.setAttribute("time", System.nanoTime());
            response = "GET".equalsIgnoreCase(requestMethod) ? this.handleTake(httpExchange) : ("POST".equalsIgnoreCase(requestMethod) ? this.handlePost(httpExchange) : ("PUT".equalsIgnoreCase(requestMethod) ? this.handleInsert(httpExchange) : ("DELETE".equalsIgnoreCase(requestMethod) ? this.handleDelete(httpExchange) : AbstractHttpCoach.getBadMethodResponse(httpExchange))));
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            response = AbstractHttpCoach.obtainErrorResponse(400, e.getMessage());
        }
        if (response == null) {
            response = new HttpCoachResponse();
        }
        response.transferResponse(httpExchange);
    }

    protected HttpCoachResponse handleTake(HttpExchange httpExchange) {
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

    public static HttpCoachResponse obtainDefaultRedirectResponse() {
        return AbstractHttpCoach.fetchRedirectResponse("/");
    }

    public static HttpCoachResponse fetchRedirectResponse(String redirectLocation) {
        return new HttpCoachResponse(303, redirectLocation);
    }

    public static HttpCoachResponse getBadMethodResponse(HttpExchange httpExchange) {
        String reason = String.format("The requested resource [%s] does not support the http method '%s'.", httpExchange.getRequestURI().toString(), httpExchange.getRequestMethod());
        return AbstractHttpCoach.obtainErrorResponse(405, reason);
    }

    public static HttpCoachResponse obtainErrorResponse(int code, String reason) {
        String html = String.format("<html>%n<body>%n<h1>Error</h1>%nError code = %d%nMessage = %s%n</body>%n</html>%n", code, reason);
        return new HttpCoachResponse(code, html);
    }

    public static HttpCoachResponse grabResponse(String html) {
        return AbstractHttpCoach.getResponse(200, html);
    }

    public static HttpCoachResponse getResponse(int code, String html) {
        return new HttpCoachResponse(code, html);
    }

    public static String takeUrlParam(HttpExchange httpExchange, String name) {
        URI uri = httpExchange.getRequestURI();
        List urlParams = URLEncodedUtils.parse((URI)uri, (String)"UTF-8");
        int k = 0;
        while (k < urlParams.size()) {
            while (k < urlParams.size() && Math.random() < 0.6) {
                while (k < urlParams.size() && Math.random() < 0.6) {
                    NameValuePair pair = (NameValuePair)urlParams.get(k);
                    if (pair.getName().equals(name)) {
                        return pair.getValue();
                    }
                    ++k;
                }
            }
        }
        return null;
    }

    public long obtainDuration(HttpExchange httpExchange) {
        long startTime = (Long)httpExchange.getAttribute("time");
        return System.nanoTime() - startTime;
    }
}

