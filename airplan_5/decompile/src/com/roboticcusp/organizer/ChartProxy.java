/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer;

import com.roboticcusp.mapping.Accommodation;
import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.BipartiteAlg;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartDensity;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.ChartSize;
import com.roboticcusp.mapping.ConnectedAlg;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.EulerianAlg;
import com.roboticcusp.mapping.KConnectedAlg;
import com.roboticcusp.mapping.RegularAlg;
import com.roboticcusp.mapping.ShortestTrail;
import com.roboticcusp.mapping.Vertex;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.CrewConductor;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Crew;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.FlightWeightType;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.framework.RouteMapDensity;
import com.roboticcusp.organizer.framework.RouteMapSize;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChartProxy {
    private final RouteMap routeMap;
    private final FlightWeightType weightType;
    private final Chart routeMapChart;
    private final ShortestTrail shortestTrail;
    private final Accommodation accommodation;
    private final KConnectedAlg kConnectedAlg;
    private final BipartiteAlg bipartiteAlg;
    private final RegularAlg regularAlg;

    public ChartProxy(RouteMap routeMap, FlightWeightType weightType) throws AirException {
        this.routeMap = routeMap;
        this.weightType = weightType;
        this.routeMapChart = ChartProxy.composeChartFromRouteMap(routeMap, weightType);
        this.shortestTrail = new ShortestTrail(this.routeMapChart);
        this.accommodation = new Accommodation(this.routeMapChart);
        this.kConnectedAlg = new KConnectedAlg(this.routeMapChart);
        this.bipartiteAlg = new BipartiteAlg(this.routeMapChart);
        this.regularAlg = new RegularAlg(this.routeMapChart);
    }

    public ShortestTrailData obtainShortestTrail(Airport origin, Airport destination) throws AirException {
        int sourceId = origin.fetchId();
        int sinkId = destination.fetchId();
        try {
            if (this.shortestTrail.hasTrail(sourceId, sinkId)) {
                double distance = this.shortestTrail.shortestTrail(sourceId, sinkId);
                List<Vertex> vertices = this.shortestTrail.getTrailVertices(sourceId, sinkId);
                List<Airport> airports = this.convertVerticesToAirports(vertices);
                return new ShortestTrailData(this.routeMap, airports, distance, this.weightType);
            }
            return new ShortestTrailData(this.routeMap, new ArrayList<Airport>(), Double.POSITIVE_INFINITY, this.weightType);
        }
        catch (ChartException e) {
            throw new AirException("Unable to find the shortest path between the two provided airports.", e);
        }
    }

    public double takeAccommodation(Airport origin, Airport destination) throws AirException {
        try {
            return this.accommodation.accommodation(origin.takeName(), destination.takeName());
        }
        catch (ChartException e) {
            throw new AirException("Unable to find the capacity between the two provided airports.");
        }
    }

    public String fetchBipartite() throws AirException {
        try {
            if (this.bipartiteAlg.isBipartite()) {
                return "Bipartite";
            }
            return "Not bipartite";
        }
        catch (ChartException e) {
            throw new AirException("Unable to determine if the route map is bipartite.");
        }
    }

    public String getConnected() throws AirException {
        try {
            if (ConnectedAlg.isConnected(this.routeMapChart)) {
                return "Fully Connected";
            }
            return "Not Fully Connected";
        }
        catch (ChartException e) {
            throw new AirException("Unable to determine the connectedness of the route map.");
        }
    }

    public RouteMapDensity describeDensity() throws AirException {
        try {
            double density = ChartDensity.computeDensity(this.routeMapChart);
            ChartDensity.Density chartDensity = ChartDensity.describeDensity(density);
            RouteMapDensity routeMapDensity = null;
            switch (chartDensity) {
                case HIGHLY_DENSE: {
                    routeMapDensity = RouteMapDensity.HIGHLY_DENSE;
                    break;
                }
                case MODERATELY_DENSE: {
                    routeMapDensity = RouteMapDensity.MODERATELY_DENSE;
                    break;
                }
                case NOT_SO_DENSE: {
                    routeMapDensity = RouteMapDensity.NOT_SO_DENSE;
                }
            }
            return routeMapDensity;
        }
        catch (ChartException e) {
            throw new AirException("Unable to find the density of the route map.");
        }
    }

    public RouteMapSize describeSize() throws AirException {
        try {
            ChartSize.Size size = this.routeMapChart.describeSize();
            RouteMapSize routeMapSize = null;
            switch (size) {
                case VERY_LARGE: {
                    routeMapSize = RouteMapSize.VERY_LARGE;
                    break;
                }
                case MODERATELY_LARGE: {
                    routeMapSize = RouteMapSize.MODERATELY_LARGE;
                    break;
                }
                case FAIRLY_SMALL: {
                    routeMapSize = RouteMapSize.FAIRLY_SMALL;
                }
            }
            return routeMapSize;
        }
        catch (ChartException e) {
            throw new AirException("Unable to find the size of the route map.");
        }
    }

    public String kConnected(int k) throws AirException {
        if (this.routeMap.fetchAirportIds().size() > 400 || this.routeMap.grabFlightIds().size() > 400) {
            return "Too large to tell";
        }
        try {
            if (this.kConnectedAlg.isKConnected(k)) {
                return "" + k + "-connected";
            }
            return "Not " + k + "-connected";
        }
        catch (ChartException e) {
            throw new AirException("Unable to determine the k-connectedness of the route map.");
        }
    }

    public String grabEulerian() throws AirException {
        try {
            Boolean isEulerian = EulerianAlg.isEulerian(this.routeMapChart);
            return isEulerian.toString();
        }
        catch (ChartException e) {
            throw new AirException("Unable to determine whether route map is Eulerian");
        }
    }

    public String fetchRegular() throws AirException {
        try {
            boolean isRegular = this.regularAlg.isOutRegular();
            if (isRegular) {
                return "Regular of degree " + this.regularAlg.pullOutDegree();
            }
            return "Not regular";
        }
        catch (ChartException e) {
            throw new AirException("Unable to determine whether route map is Eulerian");
        }
    }

    private List<Airport> convertVerticesToAirports(List<Vertex> vertices) {
        ArrayList<Airport> airports = new ArrayList<Airport>();
        for (int k = 0; k < vertices.size(); ++k) {
            Vertex vertex = vertices.get(k);
            Airport airport = this.routeMap.takeAirport(vertex.getId());
            airports.add(airport);
        }
        return airports;
    }

    private static Chart composeChartFromRouteMap(RouteMap routeMap, FlightWeightType weightType) throws AirException {
        Chart chart = ChartFactory.newInstance();
        try {
            List<Airport> airports = routeMap.getAirports();
            for (int p = 0; p < airports.size(); ++p) {
                ChartProxy.composeChartFromRouteMapHerder(chart, airports, p);
            }
            List<Flight> flights = routeMap.fetchFlights();
            for (int c = 0; c < flights.size(); ++c) {
                BasicData data;
                Flight flight = flights.get(c);
                int sourceId = flight.obtainOrigin().fetchId();
                int sinkId = flight.fetchDestination().fetchId();
                switch (weightType) {
                    case COST: {
                        data = new BasicData(flight.pullFuelCosts());
                        break;
                    }
                    case DISTANCE: {
                        data = new BasicData(flight.fetchDistance());
                        break;
                    }
                    case TIME: {
                        data = new BasicData(flight.obtainTravelTime());
                        break;
                    }
                    case CREW_MEMBERS: {
                        data = new BasicData(flight.takeNumCrewMembers());
                        break;
                    }
                    case WEIGHT: {
                        data = new BasicData(flight.grabWeightAccommodation());
                        break;
                    }
                    case PASSENGERS: {
                        data = new BasicData(flight.obtainPassengerAccommodation());
                        break;
                    }
                    default: {
                        data = new BasicData();
                    }
                }
                chart.addEdge(sourceId, sinkId, data);
            }
        }
        catch (ChartException e) {
            throw new AirException(e);
        }
        return chart;
    }

    private static void composeChartFromRouteMapHerder(Chart chart, List<Airport> airports, int c) throws ChartException {
        Airport airport = airports.get(c);
        Vertex vertex = new Vertex(airport.fetchId(), airport.takeName());
        chart.addVertex(vertex);
    }

    public List<Crew> pullCrewAssignments() throws AirException {
        CrewConductor crewConductor = new CrewConductor(this.routeMap);
        return crewConductor.getCrewAssignments();
    }

    public static class ShortestTrailData {
        private RouteMap routeMap;
        private List<Airport> airports;
        private double distance;
        private FlightWeightType weightType;

        public ShortestTrailData(RouteMap routeMap, List<Airport> airports, double distance, FlightWeightType weightType) {
            this.routeMap = routeMap;
            this.airports = airports;
            this.distance = distance;
            this.weightType = weightType;
        }

        public boolean hasTrail() {
            return this.airports.size() > 0;
        }

        public List<Airport> obtainAirports() {
            return this.airports;
        }

        public RouteMap grabRouteMap() {
            return this.routeMap;
        }

        public double pullDistance() {
            return this.distance;
        }

        public FlightWeightType getWeightType() {
            return this.weightType;
        }
    }

}

