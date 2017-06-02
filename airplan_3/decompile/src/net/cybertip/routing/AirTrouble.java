/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing;

public class AirTrouble
extends Exception {
    public AirTrouble() {
    }

    public AirTrouble(String message) {
        super(message);
    }

    public AirTrouble(String message, Throwable cause) {
        super(message, cause);
    }

    public AirTrouble(Throwable cause) {
        super(cause);
    }

    protected AirTrouble(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

