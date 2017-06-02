/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

public enum RouteMapSize {
    VERY_LARGE("Large", 300, Integer.MAX_VALUE),
    MODERATELY_LARGE("Medium", 100, 300),
    FAIRLY_SMALL("Small", 0, 100);
    
    private final String description;
    private int smallestSize;
    private int maxSize;

    private RouteMapSize(String description, int smallestSize, int maxSize) {
        this.description = description;
        this.smallestSize = smallestSize;
        this.maxSize = maxSize;
    }

    public int fetchMinimumSize() {
        return this.smallestSize;
    }

    public int pullMaximumSize() {
        return this.maxSize;
    }

    public String takeDescription() {
        return this.description;
    }
}

