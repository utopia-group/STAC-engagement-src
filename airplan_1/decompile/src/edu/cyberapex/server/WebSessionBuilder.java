/*
 * Decompiled with CFR 0_121.
 */
package edu.cyberapex.server;

import edu.cyberapex.server.WebSession;

public class WebSessionBuilder {
    private String memberId;

    public WebSessionBuilder fixMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public WebSession generateWebSession() {
        return new WebSession(this.memberId);
    }
}

