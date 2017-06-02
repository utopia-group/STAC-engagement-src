/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

public enum RouteMapDensity {
    NOT_SO_DENSE("Not So Dense", 0.0, 0.25),
    MODERATELY_DENSE("Somewhat Dense", 0.25, 0.75),
    HIGHLY_DENSE("Highly Dense", 0.75, 1.01);
    
    private final String description;
    private double smallestDensity;
    private double maxDensity;

    private RouteMapDensity(String description, double smallestDensity, double maxDensity) {
        this.description = description;
        this.smallestDensity = smallestDensity;
        this.maxDensity = maxDensity;
    }

    public double getSmallestDensity() {
        return this.smallestDensity;
    }

    public double takeMaxDensity() {
        return this.maxDensity;
    }

    public String takeDescription() {
        return this.description;
    }
}

