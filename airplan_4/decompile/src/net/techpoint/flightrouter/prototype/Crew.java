/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.prototype;

import java.util.ArrayList;
import java.util.List;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;

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

    public int getId() {
        return this.crewId;
    }

    public int takeSize() {
        return this.size;
    }

    public void assignFlight(Flight flight) {
        if (flight != null && !this.assignedFlights.contains(flight)) {
            this.assignFlightEngine(flight);
        }
    }

    private void assignFlightEngine(Flight flight) {
        new CrewHelp(flight).invoke();
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
        for (int j = 0; j < this.assignedFlights.size(); ++j) {
            Flight flight = this.assignedFlights.get(j);
            result = result + flight.getOrigin().obtainName() + "->" + flight.pullDestination().obtainName() + "; ";
        }
        return result;
    }

    @Override
    public int compareTo(Crew crew) {
        return Integer.compare(this.crewId, crew.getId());
    }

    private class CrewHelp {
        private Flight flight;

        public CrewHelp(Flight flight) {
            this.flight = flight;
        }

        public void invoke() {
            Crew.this.assignedFlights.add(this.flight);
            int crewNeeded = this.flight.takeNumCrewMembers();
            if (crewNeeded > Crew.this.size) {
                Crew.this.size = crewNeeded;
            }
        }
    }

}

