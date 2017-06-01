/*
 * Decompiled with CFR 0_121.
 */
package edu.cyberapex.server.guide;

import edu.cyberapex.server.WebSessionService;
import edu.cyberapex.server.guide.LogoutGuide;

public class LogoutGuideBuilder {
    private WebSessionService webSessionService;

    public LogoutGuideBuilder fixWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public LogoutGuide generateLogoutGuide() {
        return new LogoutGuide(this.webSessionService);
    }
}

