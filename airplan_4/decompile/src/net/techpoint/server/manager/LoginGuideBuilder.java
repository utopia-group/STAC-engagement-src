/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server.manager;

import net.techpoint.authenticate.KeyExchangeServer;
import net.techpoint.server.UserManager;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.LoginGuide;

public class LoginGuideBuilder {
    private WebSessionService webSessionService;
    private KeyExchangeServer keyExchangeServer;
    private UserManager userManager;
    private String destinationTrail;
    private String passwordKey;

    public LoginGuideBuilder assignWebSessionService(WebSessionService webSessionService) {
        this.webSessionService = webSessionService;
        return this;
    }

    public LoginGuideBuilder setKeyExchangeServer(KeyExchangeServer keyExchangeServer) {
        this.keyExchangeServer = keyExchangeServer;
        return this;
    }

    public LoginGuideBuilder defineUserManager(UserManager userManager) {
        this.userManager = userManager;
        return this;
    }

    public LoginGuideBuilder fixDestinationTrail(String destinationTrail) {
        this.destinationTrail = destinationTrail;
        return this;
    }

    public LoginGuideBuilder definePasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
        return this;
    }

    public LoginGuide formLoginGuide() {
        return new LoginGuide(this.userManager, this.webSessionService, this.keyExchangeServer, this.destinationTrail, this.passwordKey);
    }
}

