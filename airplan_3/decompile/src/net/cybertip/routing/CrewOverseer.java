/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.framework.Crew;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphFactory;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Limit;
import net.cybertip.scheme.Vertex;

public class CrewOverseer {
    private RouteMap routeMap;
    private Graph crewSchedulingGraph;

    public CrewOverseer(RouteMap routeMap) throws AirTrouble {
        this.routeMap = routeMap;
        this.crewSchedulingGraph = this.makeCrewSchedulingGraph();
    }

    public List<Crew> obtainCrewAssignments() throws AirTrouble {
        try {
            Limit limit = new Limit(this.crewSchedulingGraph);
            Map<Vertex, Map<Vertex, Double>> edgeFlows = limit.pullLimitPaths("source", "sink");
            HashMap<Flight, Crew> flightToCrewMap = new HashMap<Flight, Crew>();
            int currCrewNum = 1;
            for (Flight flight : this.routeMap.pullFlights()) {
                Crew crew = new Crew(currCrewNum++);
                crew.assignFlight(flight);
                flightToCrewMap.put(flight, crew);
            }
            for (Vertex v1 : edgeFlows.keySet()) {
                for (Vertex v2 : edgeFlows.get(v1).keySet()) {
                    if (edgeFlows.get(v1).get(v2) == 0.0 || !this.representsFlight(v1) || !this.representsFlight(v2)) continue;
                    Flight flight1 = this.obtainFlightFromVertexName(v1);
                    Flight flight2 = this.obtainFlightFromVertexName(v2);
                    Crew crew1 = (Crew)flightToCrewMap.get(flight1);
                    Crew crew2 = (Crew)flightToCrewMap.get(flight2);
                    if (crew1.fetchId() == crew2.fetchId()) continue;
                    this.grabCrewAssignmentsGateKeeper(flightToCrewMap, crew1, crew2);
                }
            }
            ArrayList<Crew> uniqueCrews = new ArrayList<Crew>(new LinkedHashSet(flightToCrewMap.values()));
            Collections.sort(uniqueCrews);
            return uniqueCrews;
        }
        catch (GraphTrouble e) {
            throw new AirTrouble(e);
        }
    }

    private void grabCrewAssignmentsGateKeeper(Map<Flight, Crew> flightToCrewMap, Crew crew1, Crew crew2) {
        for (Flight flight : crew2.grabAssignedFlights()) {
            this.fetchCrewAssignmentsGateKeeperExecutor(flightToCrewMap, crew1, flight);
        }
    }

    private void fetchCrewAssignmentsGateKeeperExecutor(Map<Flight, Crew> flightToCrewMap, Crew crew1, Flight flight) {
        crew1.assignFlight(flight);
        flightToCrewMap.put(flight, crew1);
    }

    private Graph makeCrewSchedulingGraph() throws AirTrouble {
        Graph graph = GraphFactory.newInstance();
        Map<Flight, List<Flight>> complementaryFlightsMap = CrewOverseer.generateComplementaryFlightsMap(this.routeMap.pullFlights());
        try {
            LinkedHashSet<Integer> basicVertexIds = new LinkedHashSet<Integer>();
            LinkedHashSet<Integer> compVertexIds = new LinkedHashSet<Integer>();
            for (Flight flight : this.routeMap.pullFlights()) {
                this.makeCrewSchedulingGraphTarget(graph, basicVertexIds, compVertexIds, flight);
            }
            for (Flight flight : this.routeMap.pullFlights()) {
                this.makeCrewSchedulingGraphWorker(graph, complementaryFlightsMap, flight);
            }
            Vertex source = graph.addVertex("source");
            Vertex sink = graph.addVertex("sink");
            for (Integer compVertexId : compVertexIds) {
                this.makeCrewSchedulingGraphHome(graph, source, compVertexId);
            }
            for (Integer basicVertexId : basicVertexIds) {
                graph.addEdge(basicVertexId, sink.getId(), new BasicData(1));
            }
        }
        catch (GraphTrouble e) {
            throw new AirTrouble(e);
        }
        return graph;
    }

    private void makeCrewSchedulingGraphHome(Graph graph, Vertex source, Integer compVertexId) throws GraphTrouble {
        graph.addEdge(source.getId(), compVertexId, new BasicData(1));
    }

    private void makeCrewSchedulingGraphWorker(Graph graph, Map<Flight, List<Flight>> complementaryFlightsMap, Flight flight) throws GraphTrouble {
        String vertexName = this.pullFlightBasicVertexName(flight);
        Vertex basicVertex = graph.getVertex(graph.fetchVertexIdByName(vertexName));
        for (Flight compFlight : complementaryFlightsMap.get(flight)) {
            this.makeCrewSchedulingGraphWorkerHelp(graph, basicVertex, compFlight);
        }
    }

    private void makeCrewSchedulingGraphWorkerHelp(Graph graph, Vertex basicVertex, Flight compFlight) throws GraphTrouble {
        String compName = this.pullFlightBasicVertexName(compFlight) + "Complementary";
        Vertex compVertex = graph.getVertex(graph.fetchVertexIdByName(compName));
        graph.addEdge(compVertex.getId(), basicVertex.getId(), new BasicData(1));
    }

    private void makeCrewSchedulingGraphTarget(Graph graph, Set<Integer> basicVertexIds, Set<Integer> compVertexIds, Flight flight) throws GraphTrouble {
        String name = this.pullFlightBasicVertexName(flight);
        Vertex vertex = graph.addVertex(name);
        vertex.setData(new BasicData(flight.grabId()));
        basicVertexIds.add(vertex.getId());
        Vertex compVertex = graph.addVertex(name + "Complementary");
        compVertex.setData(new BasicData(flight.grabId()));
        compVertexIds.add(compVertex.getId());
    }

    private boolean representsFlight(Vertex v) {
        return !v.getName().equals("source") && !v.getName().equals("sink");
    }

    private String pullFlightBasicVertexName(Flight flight) {
        return Integer.toString(flight.grabId());
    }

    private Flight obtainFlightFromVertexName(Vertex crewMapVertex) {
        String flight1Name = crewMapVertex.getName().replace("Complementary", "");
        return this.routeMap.takeFlight(Integer.parseInt(flight1Name));
    }

    private static Map<Flight, List<Flight>> generateComplementaryFlightsMap(List<Flight> flights) {
        HashMap<Flight, List<Flight>> complementaryFlights = new HashMap<Flight, List<Flight>>();
        for (Flight flight : flights) {
            CrewOverseer.generateComplementaryFlightsMapAid(complementaryFlights, flight);
        }
        for (int k = 0; k < flights.size(); ++k) {
            Flight flight;
            flight = flights.get(k);
            for (int j = k; j < flights.size(); ++j) {
                Flight other = flights.get(j);
                if (!other.canUseSameCrew(flight) || complementaryFlights.get(other).contains(flight)) continue;
                CrewOverseer.generateComplementaryFlightsMapUtility(complementaryFlights, flight, other);
            }
        }
        return complementaryFlights;
    }

    private static void generateComplementaryFlightsMapUtility(Map<Flight, List<Flight>> complementaryFlights, Flight flight, Flight other) {
        complementaryFlights.get(other).add(flight);
    }

    private static void generateComplementaryFlightsMapAid(Map<Flight, List<Flight>> complementaryFlights, Flight flight) {
        complementaryFlights.put(flight, new ArrayList());
    }
}

