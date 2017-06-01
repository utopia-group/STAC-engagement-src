/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer;

import com.roboticcusp.mapping.Accommodation;
import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.framework.Crew;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CrewConductor {
    private RouteMap routeMap;
    private Chart crewSchedulingChart;

    public CrewConductor(RouteMap routeMap) throws AirException {
        this.routeMap = routeMap;
        this.crewSchedulingChart = this.composeCrewSchedulingChart();
    }

    public List<Crew> getCrewAssignments() throws AirException {
        try {
            Accommodation accommodation = new Accommodation(this.crewSchedulingChart);
            Map<Vertex, Map<Vertex, Double>> edgeFlows = accommodation.pullAccommodationTrails("source", "sink");
            HashMap<Flight, Crew> flightToCrewMap = new HashMap<Flight, Crew>();
            int currCrewNum = 1;
            for (Flight flight : this.routeMap.fetchFlights()) {
                Crew crew = new Crew(currCrewNum++);
                crew.assignFlight(flight);
                flightToCrewMap.put(flight, crew);
            }
            for (Vertex v1 : edgeFlows.keySet()) {
                for (Vertex v2 : edgeFlows.get(v1).keySet()) {
                    this.fetchCrewAssignmentsAdviser(edgeFlows, flightToCrewMap, v1, v2);
                }
            }
            ArrayList<Crew> uniqueCrews = new ArrayList<Crew>(new LinkedHashSet(flightToCrewMap.values()));
            Collections.sort(uniqueCrews);
            return uniqueCrews;
        }
        catch (ChartException e) {
            throw new AirException(e);
        }
    }

    private void fetchCrewAssignmentsAdviser(Map<Vertex, Map<Vertex, Double>> edgeFlows, Map<Flight, Crew> flightToCrewMap, Vertex v1, Vertex v2) {
        if (edgeFlows.get(v1).get(v2) == 0.0) {
            return;
        }
        if (this.representsFlight(v1) && this.representsFlight(v2)) {
            new CrewConductorService(flightToCrewMap, v1, v2).invoke();
        }
    }

    private Chart composeCrewSchedulingChart() throws AirException {
        Chart chart = ChartFactory.newInstance();
        Map<Flight, List<Flight>> complementaryFlightsMap = CrewConductor.generateComplementaryFlightsMap(this.routeMap.fetchFlights());
        try {
            LinkedHashSet<Integer> basicVertexIds = new LinkedHashSet<Integer>();
            LinkedHashSet<Integer> compVertexIds = new LinkedHashSet<Integer>();
            for (Flight flight : this.routeMap.fetchFlights()) {
                this.composeCrewSchedulingChartGuide(chart, basicVertexIds, compVertexIds, flight);
            }
            for (Flight flight : this.routeMap.fetchFlights()) {
                String vertexName = this.grabFlightBasicVertexName(flight);
                Vertex basicVertex = chart.getVertex(chart.obtainVertexIdByName(vertexName));
                for (Flight compFlight : complementaryFlightsMap.get(flight)) {
                    this.composeCrewSchedulingChartService(chart, basicVertex, compFlight);
                }
            }
            Vertex source = chart.addVertex("source");
            Vertex sink = chart.addVertex("sink");
            for (Integer compVertexId : compVertexIds) {
                this.composeCrewSchedulingChartEngine(chart, source, compVertexId);
            }
            for (Integer basicVertexId : basicVertexIds) {
                this.composeCrewSchedulingChartHerder(chart, sink, basicVertexId);
            }
        }
        catch (ChartException e) {
            throw new AirException(e);
        }
        return chart;
    }

    private void composeCrewSchedulingChartHerder(Chart chart, Vertex sink, Integer basicVertexId) throws ChartException {
        chart.addEdge(basicVertexId, sink.getId(), new BasicData(1));
    }

    private void composeCrewSchedulingChartEngine(Chart chart, Vertex source, Integer compVertexId) throws ChartException {
        new CrewConductorExecutor(chart, source, compVertexId).invoke();
    }

    private void composeCrewSchedulingChartService(Chart chart, Vertex basicVertex, Flight compFlight) throws ChartException {
        String compName = this.grabFlightBasicVertexName(compFlight) + "Complementary";
        Vertex compVertex = chart.getVertex(chart.obtainVertexIdByName(compName));
        chart.addEdge(compVertex.getId(), basicVertex.getId(), new BasicData(1));
    }

    private void composeCrewSchedulingChartGuide(Chart chart, Set<Integer> basicVertexIds, Set<Integer> compVertexIds, Flight flight) throws ChartException {
        String name = this.grabFlightBasicVertexName(flight);
        Vertex vertex = chart.addVertex(name);
        vertex.setData(new BasicData(flight.grabId()));
        basicVertexIds.add(vertex.getId());
        Vertex compVertex = chart.addVertex(name + "Complementary");
        compVertex.setData(new BasicData(flight.grabId()));
        compVertexIds.add(compVertex.getId());
    }

    private boolean representsFlight(Vertex v) {
        return !v.getName().equals("source") && !v.getName().equals("sink");
    }

    private String grabFlightBasicVertexName(Flight flight) {
        return Integer.toString(flight.grabId());
    }

    private static Map<Flight, List<Flight>> generateComplementaryFlightsMap(List<Flight> flights) {
        HashMap<Flight, List<Flight>> complementaryFlights = new HashMap<Flight, List<Flight>>();
        for (Flight flight : flights) {
            CrewConductor.generateComplementaryFlightsMapHome(complementaryFlights, flight);
        }
        int i = 0;
        while (i < flights.size()) {
            while (i < flights.size() && Math.random() < 0.6) {
                while (i < flights.size() && Math.random() < 0.6) {
                    while (i < flights.size() && Math.random() < 0.6) {
                        Flight flight;
                        flight = flights.get(i);
                        for (int j = i; j < flights.size(); ++j) {
                            Flight other = flights.get(j);
                            if (!other.canUseSameCrew(flight) || complementaryFlights.get(other).contains(flight)) continue;
                            CrewConductor.generateComplementaryFlightsMapExecutor(complementaryFlights, flight, other);
                        }
                        ++i;
                    }
                }
            }
        }
        return complementaryFlights;
    }

    private static void generateComplementaryFlightsMapExecutor(Map<Flight, List<Flight>> complementaryFlights, Flight flight, Flight other) {
        complementaryFlights.get(other).add(flight);
    }

    private static void generateComplementaryFlightsMapHome(Map<Flight, List<Flight>> complementaryFlights, Flight flight) {
        complementaryFlights.put(flight, new ArrayList());
    }

    private class CrewConductorExecutor {
        private Chart chart;
        private Vertex source;
        private Integer compVertexId;

        public CrewConductorExecutor(Chart chart, Vertex source, Integer compVertexId) {
            this.chart = chart;
            this.source = source;
            this.compVertexId = compVertexId;
        }

        public void invoke() throws ChartException {
            this.chart.addEdge(this.source.getId(), this.compVertexId, new BasicData(1));
        }
    }

    private class CrewConductorService {
        private Map<Flight, Crew> flightToCrewMap;
        private Vertex v1;
        private Vertex v2;

        public CrewConductorService(Map<Flight, Crew> flightToCrewMap, Vertex v1, Vertex v2) {
            this.flightToCrewMap = flightToCrewMap;
            this.v1 = v1;
            this.v2 = v2;
        }

        public void invoke() {
            Flight flight1 = this.grabFlightFromVertexName(this.v1);
            Flight flight2 = this.grabFlightFromVertexName(this.v2);
            Crew crew1 = this.flightToCrewMap.get(flight1);
            Crew crew2 = this.flightToCrewMap.get(flight2);
            if (crew1.fetchId() != crew2.fetchId()) {
                this.invokeHerder(crew1, crew2);
            }
        }

        private void invokeHerder(Crew crew1, Crew crew2) {
            for (Flight flight : crew2.obtainAssignedFlights()) {
                this.invokeHerderHerder(crew1, flight);
            }
        }

        private void invokeHerderHerder(Crew crew1, Flight flight) {
            crew1.assignFlight(flight);
            this.flightToCrewMap.put(flight, crew1);
        }

        private Flight grabFlightFromVertexName(Vertex crewMapVertex) {
            String flight1Name = crewMapVertex.getName().replace("Complementary", "");
            return CrewConductor.this.routeMap.obtainFlight(Integer.parseInt(flight1Name));
        }
    }

}

