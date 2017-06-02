/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.CrewOverseer;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Crew;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.FlightWeightType;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.framework.RouteMapDensity;
import net.cybertip.routing.framework.RouteMapSize;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.BipartiteAlg;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphDensity;
import net.cybertip.scheme.GraphFactory;
import net.cybertip.scheme.GraphSize;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.KConnectedAlg;
import net.cybertip.scheme.Limit;
import net.cybertip.scheme.RegularAlg;
import net.cybertip.scheme.RegularAlgBuilder;
import net.cybertip.scheme.ShortestPath;
import net.cybertip.scheme.Vertex;

public class GraphDelegate {
    private final RouteMap routeMap;
    private final FlightWeightType weightType;
    private final Graph routeMapGraph;
    private final ShortestPath shortestPath;
    private final Limit limit;
    private final KConnectedAlg kConnectedAlg;
    private final BipartiteAlg bipartiteAlg;
    private final RegularAlg regularAlg;

    public GraphDelegate(RouteMap routeMap, FlightWeightType weightType) throws AirTrouble {
        this.routeMap = routeMap;
        this.weightType = weightType;
        this.routeMapGraph = GraphDelegate.makeGraphFromRouteMap(routeMap, weightType);
        this.shortestPath = new ShortestPath(this.routeMapGraph);
        this.limit = new Limit(this.routeMapGraph);
        this.kConnectedAlg = new KConnectedAlg(this.routeMapGraph);
        this.bipartiteAlg = new BipartiteAlg(this.routeMapGraph);
        this.regularAlg = new RegularAlgBuilder().assignG(this.routeMapGraph).makeRegularAlg();
    }

    public ShortestPathData takeShortestPath(Airport origin, Airport destination) throws AirTrouble {
        int sourceId = origin.pullId();
        int sinkId = destination.pullId();
        try {
            if (this.shortestPath.hasPath(sourceId, sinkId)) {
                return new GraphDelegateEngine(sourceId, sinkId).invoke();
            }
            return new ShortestPathData(this.routeMap, new ArrayList<Airport>(), Double.POSITIVE_INFINITY, this.weightType);
        }
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to find the shortest path between the two provided airports.", e);
        }
    }

    public double fetchLimit(Airport origin, Airport destination) throws AirTrouble {
        try {
            return this.limit.limit(origin.getName(), destination.getName());
        }
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to find the capacity between the two provided airports.");
        }
    }

    public String fetchBipartite() throws AirTrouble {
        try {
            if (this.bipartiteAlg.isBipartite()) {
                return "Bipartite";
            }
            return "Not bipartite";
        }
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to determine if the route map is bipartite.");
        }
    }

    public String takeConnected() throws AirTrouble {
        try {
            if (this.routeMapGraph.isConnected()) {
                return "Fully Connected";
            }
            return "Not Fully Connected";
        }
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to determine the connectedness of the route map.");
        }
    }

    public RouteMapDensity describeDensity() throws AirTrouble {
        try {
            double density = this.routeMapGraph.computeDensity();
            GraphDensity.Density graphDensity = GraphDensity.describeDensity(density);
            RouteMapDensity routeMapDensity = null;
            switch (graphDensity) {
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
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to find the density of the route map.");
        }
    }

    public RouteMapSize describeSize() throws AirTrouble {
        try {
            GraphSize.Size size = GraphSize.describeSize(this.routeMapGraph);
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
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to find the size of the route map.");
        }
    }

    public String kConnected(int k) throws AirTrouble {
        if (this.routeMap.fetchAirportIds().size() > 400 || this.routeMap.pullFlightIds().size() > 400) {
            return "Too large to tell";
        }
        try {
            if (this.kConnectedAlg.isKConnected(k)) {
                return "" + k + "-connected";
            }
            return "Not " + k + "-connected";
        }
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to determine the k-connectedness of the route map.");
        }
    }

    public String grabEulerian() throws AirTrouble {
        try {
            Boolean isEulerian = this.routeMapGraph.isEulerian();
            return isEulerian.toString();
        }
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to determine whether route map is Eulerian");
        }
    }

    public String obtainRegular() throws AirTrouble {
        try {
            boolean isRegular = this.regularAlg.isOutRegular();
            if (isRegular) {
                return "Regular of degree " + this.regularAlg.grabOutDegree();
            }
            return "Not regular";
        }
        catch (GraphTrouble e) {
            throw new AirTrouble("Unable to determine whether route map is Eulerian");
        }
    }

    private static Graph makeGraphFromRouteMap(RouteMap routeMap, FlightWeightType weightType) throws AirTrouble {
        Graph graph = GraphFactory.newInstance();
        try {
            List<Airport> airports = routeMap.takeAirports();
            for (int k = 0; k < airports.size(); ++k) {
                Airport airport = airports.get(k);
                Vertex vertex = new Vertex(airport.pullId(), airport.getName());
                graph.addVertex(vertex);
            }
            List<Flight> flights = routeMap.pullFlights();
            for (int k = 0; k < flights.size(); ++k) {
                BasicData data;
                Flight flight = flights.get(k);
                int sourceId = flight.fetchOrigin().pullId();
                int sinkId = flight.fetchDestination().pullId();
                switch (weightType) {
                    case COST: {
                        data = new BasicData(flight.fetchFuelCosts());
                        break;
                    }
                    case DISTANCE: {
                        data = new BasicData(flight.takeDistance());
                        break;
                    }
                    case TIME: {
                        data = new BasicData(flight.takeTravelTime());
                        break;
                    }
                    case CREW_MEMBERS: {
                        data = new BasicData(flight.fetchNumCrewMembers());
                        break;
                    }
                    case WEIGHT: {
                        data = new BasicData(flight.fetchWeightLimit());
                        break;
                    }
                    case PASSENGERS: {
                        data = new BasicData(flight.getPassengerLimit());
                        break;
                    }
                    default: {
                        data = new BasicData();
                    }
                }
                graph.addEdge(sourceId, sinkId, data);
            }
        }
        catch (GraphTrouble e) {
            throw new AirTrouble(e);
        }
        return graph;
    }

    public List<Crew> takeCrewAssignments() throws AirTrouble {
        CrewOverseer crewOverseer = new CrewOverseer(this.routeMap);
        return crewOverseer.obtainCrewAssignments();
    }

    private class GraphDelegateEngine {
        private int sourceId;
        private int sinkId;

        public GraphDelegateEngine(int sourceId, int sinkId) {
            this.sourceId = sourceId;
            this.sinkId = sinkId;
        }

        public ShortestPathData invoke() throws GraphTrouble {
            double distance = GraphDelegate.this.shortestPath.shortestPath(this.sourceId, this.sinkId);
            List<Vertex> vertices = GraphDelegate.this.shortestPath.pullPathVertices(this.sourceId, this.sinkId);
            List<Airport> airports = this.convertVerticesToAirports(vertices);
            return new ShortestPathData(GraphDelegate.this.routeMap, airports, distance, GraphDelegate.this.weightType);
        }

        private List<Airport> convertVerticesToAirports(List<Vertex> vertices) {
            ArrayList<Airport> airports = new ArrayList<Airport>();
            for (int k = 0; k < vertices.size(); ++k) {
                Vertex vertex = vertices.get(k);
                Airport airport = GraphDelegate.this.routeMap.obtainAirport(vertex.getId());
                airports.add(airport);
            }
            return airports;
        }
    }

    public static class ShortestPathData {
        private RouteMap routeMap;
        private List<Airport> airports;
        private double distance;
        private FlightWeightType weightType;

        public ShortestPathData(RouteMap routeMap, List<Airport> airports, double distance, FlightWeightType weightType) {
            this.routeMap = routeMap;
            this.airports = airports;
            this.distance = distance;
            this.weightType = weightType;
        }

        public boolean hasPath() {
            return this.airports.size() > 0;
        }

        public List<Airport> obtainAirports() {
            return this.airports;
        }

        public RouteMap getRouteMap() {
            return this.routeMap;
        }

        public double getDistance() {
            return this.distance;
        }

        public FlightWeightType pullWeightType() {
            return this.weightType;
        }
    }

}

