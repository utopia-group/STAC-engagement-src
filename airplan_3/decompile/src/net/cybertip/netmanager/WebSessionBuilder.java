/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import net.cybertip.netmanager.WebSession;

public class WebSessionBuilder {
    private String memberId;

    public WebSessionBuilder assignMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public WebSession makeWebSession() {
        return new WebSession(this.memberId);
    }
}

