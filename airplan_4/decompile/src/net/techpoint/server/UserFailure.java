/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server;

import net.techpoint.server.User;

public class UserFailure
extends Exception {
    public UserFailure(User user, String message) {
        super(String.format("user: %s: %s", user, message));
    }
}

