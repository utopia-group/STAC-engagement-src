/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.framework;

import java.util.ArrayList;
import java.util.List;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;

public class Crew
implements Comparable<Crew> {
    private final int crewId;
    private final List<Flight> assignedFlights;
    private int size;

    public Crew(int id) {
        this.crewId = id;
        this.assignedFlights = new ArrayList<Flight>();
        this.size = 0;
    }

    public int fetchId() {
        return this.crewId;
    }

    public int obtainSize() {
        return this.size;
    }

    public void assignFlight(Flight flight) {
        if (flight != null && !this.assignedFlights.contains(flight)) {
            this.assignFlightHerder(flight);
        }
    }

    private void assignFlightHerder(Flight flight) {
        this.assignedFlights.add(flight);
        int crewNeeded = flight.fetchNumCrewMembers();
        if (crewNeeded > this.size) {
            this.size = crewNeeded;
        }
    }

    public List<Flight> grabAssignedFlights() {
        return this.assignedFlights;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Crew crew = (Crew)obj;
        return this.crewId == crew.crewId;
    }

    public int hashCode() {
        return this.crewId;
    }

    public String toString() {
        String result = "Crew_" + this.crewId + " assigned to flights ";
        for (int a = 0; a < this.assignedFlights.size(); ++a) {
            Flight flight = this.assignedFlights.get(a);
            result = result + flight.fetchOrigin().getName() + "->" + flight.fetchDestination().getName() + "; ";
        }
        return result;
    }

    @Override
    public int compareTo(Crew crew) {
        return Integer.compare(this.crewId, crew.fetchId());
    }
}

