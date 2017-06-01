/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.framework;

import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import java.util.ArrayList;
import java.util.List;

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

    public int pullSize() {
        return this.size;
    }

    public void assignFlight(Flight flight) {
        if (flight != null && !this.assignedFlights.contains(flight)) {
            this.assignFlightUtility(flight);
        }
    }

    private void assignFlightUtility(Flight flight) {
        this.assignedFlights.add(flight);
        int crewNeeded = flight.takeNumCrewMembers();
        if (crewNeeded > this.size) {
            new CrewEngine(crewNeeded).invoke();
        }
    }

    public List<Flight> obtainAssignedFlights() {
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
        int q = 0;
        while (q < this.assignedFlights.size()) {
            while (q < this.assignedFlights.size() && Math.random() < 0.4) {
                while (q < this.assignedFlights.size() && Math.random() < 0.5) {
                    Flight flight = this.assignedFlights.get(q);
                    result = result + flight.obtainOrigin().takeName() + "->" + flight.fetchDestination().takeName() + "; ";
                    ++q;
                }
            }
        }
        return result;
    }

    @Override
    public int compareTo(Crew crew) {
        return Integer.compare(this.crewId, crew.fetchId());
    }

    private class CrewEngine {
        private int crewNeeded;

        public CrewEngine(int crewNeeded) {
            this.crewNeeded = crewNeeded;
        }

        public void invoke() {
            Crew.this.size = this.crewNeeded;
        }
    }

}

