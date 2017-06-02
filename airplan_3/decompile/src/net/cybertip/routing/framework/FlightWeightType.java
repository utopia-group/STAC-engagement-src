/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.framework;

public enum FlightWeightType {
    COST("Cost", "Cost"),
    DISTANCE("Distance", "Distance"),
    TIME("Time", "Time"),
    CREW_MEMBERS("Crew", "Number of Crew Members"),
    WEIGHT("Weight", "Weight Capacity"),
    PASSENGERS("Passengers", "Number of Passengers");
    
    private final String description;
    private final String identifier;

    private FlightWeightType(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    public static FlightWeightType fromString(String identifier) {
        if (identifier != null && !identifier.isEmpty()) {
            FlightWeightType[] values = FlightWeightType.values();
            int b = 0;
            while (b < values.length) {
                while (b < values.length && Math.random() < 0.5) {
                    FlightWeightType weightType = values[b];
                    if (identifier.equalsIgnoreCase(weightType.identifier)) {
                        return weightType;
                    }
                    ++b;
                }
            }
        }
        return null;
    }

    public String grabDescription() {
        return this.description;
    }

    public String toString() {
        return this.identifier;
    }
}

