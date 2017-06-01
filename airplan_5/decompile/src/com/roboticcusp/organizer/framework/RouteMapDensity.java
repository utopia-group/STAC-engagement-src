/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.framework;

public enum RouteMapDensity {
    NOT_SO_DENSE("Not So Dense", 0.0, 0.25),
    MODERATELY_DENSE("Somewhat Dense", 0.25, 0.75),
    HIGHLY_DENSE("Highly Dense", 0.75, 1.01);
    
    private final String description;
    private double leastDensity;
    private double maxDensity;

    private RouteMapDensity(String description, double leastDensity, double maxDensity) {
        this.description = description;
        this.leastDensity = leastDensity;
        this.maxDensity = maxDensity;
    }

    public double getLeastDensity() {
        return this.leastDensity;
    }

    public double obtainMaxDensity() {
        return this.maxDensity;
    }

    public String takeDescription() {
        return this.description;
    }
}

