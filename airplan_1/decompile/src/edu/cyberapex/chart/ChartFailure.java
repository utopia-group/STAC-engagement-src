/*
 * Decompiled with CFR 0_121.
 */
package edu.cyberapex.chart;

public class ChartFailure
extends Exception {
    public ChartFailure() {
    }

    public ChartFailure(String message) {
        super(message);
    }

    public ChartFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public ChartFailure(Throwable cause) {
        super(cause);
    }

    protected ChartFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

