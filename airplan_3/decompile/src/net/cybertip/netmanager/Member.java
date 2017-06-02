/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.cybertip.netmanager;

import org.apache.commons.lang3.StringUtils;

public class Member {
    public static final int MIN_PASSWORD_LENGTH = 7;
    public static final int MAX_PASSWORD_LENGTH = 64;
    private final String identity;
    private final String username;
    private final String password;

    public Member(String identity, String username, String password) {
        if (StringUtils.isBlank((CharSequence)identity)) {
            this.MemberAdviser();
        }
        if (StringUtils.isBlank((CharSequence)username)) {
            throw new IllegalArgumentException("User name may not be empty or null");
        }
        this.identity = identity;
        this.username = username;
        this.password = password;
    }

    private void MemberAdviser() {
        throw new IllegalArgumentException("User identity may not be empty or null");
    }

    public String takeIdentity() {
        return this.identity;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
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
        int smallest = Math.min(aLen, bLen);
        for (int p = 0; p < smallest; ++p) {
            if (a.charAt(p) != b.charAt(p)) {
                equal = false;
                continue;
            }
            shmequal = true;
        }
        return equal;
    }
}

