/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.prototype.Crew;
import net.techpoint.flightrouter.prototype.CrewBuilder;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.graph.BasicData;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Limit;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public class CrewManager {
    private RouteMap routeMap;
    private Scheme crewSchedulingScheme;

    public CrewManager(RouteMap routeMap) throws AirFailure {
        this.routeMap = routeMap;
        this.crewSchedulingScheme = this.formCrewSchedulingScheme();
    }

    public List<Crew> grabCrewAssignments() throws AirFailure {
        try {
            Limit limit = new Limit(this.crewSchedulingScheme);
            Map<Vertex, Map<Vertex, Double>> edgeFlows = limit.fetchLimitTrails("source", "sink");
            HashMap<Flight, Crew> flightToCrewMap = new HashMap<Flight, Crew>();
            int currCrewNum = 1;
            for (Flight flight : this.routeMap.obtainFlights()) {
                Crew crew = new CrewBuilder().fixId(currCrewNum++).formCrew();
                crew.assignFlight(flight);
                flightToCrewMap.put(flight, crew);
            }
            for (Vertex v1 : edgeFlows.keySet()) {
                for (Vertex v2 : edgeFlows.get(v1).keySet()) {
                    this.grabCrewAssignmentsGuide(edgeFlows, flightToCrewMap, v1, v2);
                }
            }
            ArrayList<Crew> uniqueCrews = new ArrayList<Crew>(new LinkedHashSet(flightToCrewMap.values()));
            Collections.sort(uniqueCrews);
            return uniqueCrews;
        }
        catch (SchemeFailure e) {
            throw new AirFailure(e);
        }
    }

    private void grabCrewAssignmentsGuide(Map<Vertex, Map<Vertex, Double>> edgeFlows, Map<Flight, Crew> flightToCrewMap, Vertex v1, Vertex v2) {
        new CrewManagerEngine(edgeFlows, flightToCrewMap, v1, v2).invoke();
    }

    private Scheme formCrewSchedulingScheme() throws AirFailure {
        Scheme scheme = SchemeFactory.newInstance();
        Map<Flight, List<Flight>> complementaryFlightsMap = CrewManager.generateComplementaryFlightsMap(this.routeMap.obtainFlights());
        try {
            LinkedHashSet<Integer> basicVertexIds = new LinkedHashSet<Integer>();
            LinkedHashSet<Integer> compVertexIds = new LinkedHashSet<Integer>();
            for (Flight flight : this.routeMap.obtainFlights()) {
                String name = this.grabFlightBasicVertexName(flight);
                Vertex vertex = scheme.addVertex(name);
                vertex.setData(new BasicData(flight.pullId()));
                basicVertexIds.add(vertex.getId());
                Vertex compVertex = scheme.addVertex(name + "Complementary");
                compVertex.setData(new BasicData(flight.pullId()));
                compVertexIds.add(compVertex.getId());
            }
            for (Flight flight : this.routeMap.obtainFlights()) {
                String vertexName = this.grabFlightBasicVertexName(flight);
                Vertex basicVertex = scheme.grabVertex(scheme.getVertexIdByName(vertexName));
                for (Flight compFlight : complementaryFlightsMap.get(flight)) {
                    this.formCrewSchedulingSchemeSupervisor(scheme, basicVertex, compFlight);
                }
            }
            Vertex source = scheme.addVertex("source");
            Vertex sink = scheme.addVertex("sink");
            for (Integer compVertexId : compVertexIds) {
                scheme.addEdge(source.getId(), compVertexId, new BasicData(1));
            }
            for (Integer basicVertexId : basicVertexIds) {
                this.formCrewSchedulingSchemeFunction(scheme, sink, basicVertexId);
            }
        }
        catch (SchemeFailure e) {
            throw new AirFailure(e);
        }
        return scheme;
    }

    private void formCrewSchedulingSchemeFunction(Scheme scheme, Vertex sink, Integer basicVertexId) throws SchemeFailure {
        new CrewManagerEntity(scheme, sink, basicVertexId).invoke();
    }

    private void formCrewSchedulingSchemeSupervisor(Scheme scheme, Vertex basicVertex, Flight compFlight) throws SchemeFailure {
        String compName = this.grabFlightBasicVertexName(compFlight) + "Complementary";
        Vertex compVertex = scheme.grabVertex(scheme.getVertexIdByName(compName));
        scheme.addEdge(compVertex.getId(), basicVertex.getId(), new BasicData(1));
    }

    private String grabFlightBasicVertexName(Flight flight) {
        return Integer.toString(flight.pullId());
    }

    private static Map<Flight, List<Flight>> generateComplementaryFlightsMap(List<Flight> flights) {
        HashMap<Flight, List<Flight>> complementaryFlights = new HashMap<Flight, List<Flight>>();
        for (Flight flight : flights) {
            complementaryFlights.put(flight, new ArrayList());
        }
        for (int k = 0; k < flights.size(); ++k) {
            Flight flight;
            flight = flights.get(k);
            for (int j = k; j < flights.size(); ++j) {
                CrewManager.generateComplementaryFlightsMapAid(flights, complementaryFlights, flight, j);
            }
        }
        return complementaryFlights;
    }

    private static void generateComplementaryFlightsMapAid(List<Flight> flights, Map<Flight, List<Flight>> complementaryFlights, Flight flight, int j) {
        Flight other = flights.get(j);
        if (other.canUseSameCrew(flight) && !complementaryFlights.get(other).contains(flight)) {
            CrewManager.generateComplementaryFlightsMapAidExecutor(complementaryFlights, flight, other);
        }
    }

    private static void generateComplementaryFlightsMapAidExecutor(Map<Flight, List<Flight>> complementaryFlights, Flight flight, Flight other) {
        complementaryFlights.get(other).add(flight);
    }

    private class CrewManagerEntity {
        private Scheme scheme;
        private Vertex sink;
        private Integer basicVertexId;

        public CrewManagerEntity(Scheme scheme, Vertex sink, Integer basicVertexId) {
            this.scheme = scheme;
            this.sink = sink;
            this.basicVertexId = basicVertexId;
        }

        public void invoke() throws SchemeFailure {
            this.scheme.addEdge(this.basicVertexId, this.sink.getId(), new BasicData(1));
        }
    }

    private class CrewManagerEngine {
        private Map<Vertex, Map<Vertex, Double>> edgeFlows;
        private Map<Flight, Crew> flightToCrewMap;
        private Vertex v1;
        private Vertex v2;

        public CrewManagerEngine(Map<Vertex, Map<Vertex, Double>> edgeFlows, Map<Flight, Crew> flightToCrewMap, Vertex v1, Vertex v2) {
            this.edgeFlows = edgeFlows;
            this.flightToCrewMap = flightToCrewMap;
            this.v1 = v1;
            this.v2 = v2;
        }

        public void invoke() {
            if (this.edgeFlows.get(this.v1).get(this.v2) == 0.0) {
                return;
            }
            if (this.representsFlight(this.v1) && this.representsFlight(this.v2)) {
                this.invokeTarget();
            }
        }

        private void invokeTarget() {
            Flight flight1 = this.pullFlightFromVertexName(this.v1);
            Flight flight2 = this.pullFlightFromVertexName(this.v2);
            Crew crew1 = this.flightToCrewMap.get(flight1);
            Crew crew2 = this.flightToCrewMap.get(flight2);
            if (crew1.getId() != crew2.getId()) {
                for (Flight flight : crew2.grabAssignedFlights()) {
                    this.invokeTargetTarget(crew1, flight);
                }
            }
        }

        private void invokeTargetTarget(Crew crew1, Flight flight) {
            crew1.assignFlight(flight);
            this.flightToCrewMap.put(flight, crew1);
        }

        private boolean representsFlight(Vertex v) {
            return !v.getName().equals("source") && !v.getName().equals("sink");
        }

        private Flight pullFlightFromVertexName(Vertex crewMapVertex) {
            String flight1Name = crewMapVertex.getName().replace("Complementary", "");
            return CrewManager.this.routeMap.getFlight(Integer.parseInt(flight1Name));
        }
    }

}

