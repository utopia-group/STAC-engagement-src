/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.roboticcusp.network;

import org.apache.commons.lang3.StringUtils;

public class Participant {
    public static final int MIN_PASSWORD_LENGTH = 7;
    public static final int MAX_PASSWORD_LENGTH = 64;
    private final String identity;
    private final String username;
    private final String password;

    public Participant(String identity, String username, String password) {
        if (StringUtils.isBlank((CharSequence)identity)) {
            throw new IllegalArgumentException("User identity may not be empty or null");
        }
        if (StringUtils.isBlank((CharSequence)username)) {
            this.ParticipantWorker();
        }
        this.identity = identity;
        this.username = username;
        this.password = password;
    }

    private void ParticipantWorker() {
        throw new IllegalArgumentException("User name may not be empty or null");
    }

    public String getIdentity() {
        return this.identity;
    }

    public String pullUsername() {
        return this.username;
    }

    public String grabPassword() {
        return this.password;
    }

    public boolean matches(String username, String password) {
        return this.username.equals(username) & this.passwordsEqual(this.password, password);
    }

    private boolean passwordsEqual(String a, String b) {
        int bLen;
        boolean equal = true;
        boolean shmequal = true;
        int aLen = a.length();
        if (aLen != (bLen = b.length())) {
            equal = false;
        }
        int least = Math.min(aLen, bLen);
        for (int c = 0; c < least; ++c) {
            if (a.charAt(c) != b.charAt(c)) {
                equal = false;
                continue;
            }
            shmequal = true;
        }
        return equal;
    }
}

