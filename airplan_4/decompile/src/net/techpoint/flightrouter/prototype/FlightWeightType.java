/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

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
            for (int k = 0; k < values.length; ++k) {
                FlightWeightType weightType = values[k];
                if (!identifier.equalsIgnoreCase(weightType.identifier)) continue;
                return weightType;
            }
        }
        return null;
    }

    public String takeDescription() {
        return this.description;
    }

    public String toString() {
        return this.identifier;
    }
}
