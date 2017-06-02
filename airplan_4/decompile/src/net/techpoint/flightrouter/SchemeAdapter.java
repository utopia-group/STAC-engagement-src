/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.CrewManager;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Crew;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.FlightWeightType;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.flightrouter.prototype.RouteMapDensity;
import net.techpoint.flightrouter.prototype.RouteMapSize;
import net.techpoint.graph.BasicData;
import net.techpoint.graph.BestTrail;
import net.techpoint.graph.BipartiteAlg;
import net.techpoint.graph.ConnectedAlg;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.EulerianAlg;
import net.techpoint.graph.KConnectedAlg;
import net.techpoint.graph.Limit;
import net.techpoint.graph.RegularAlg;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeDensity;
import net.techpoint.graph.SchemeFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.SchemeSize;
import net.techpoint.graph.Vertex;

public class SchemeAdapter {
    private final RouteMap routeMap;
    private final FlightWeightType weightType;
    private final Scheme routeMapScheme;
    private final BestTrail bestTrail;
    private final Limit limit;
    private final KConnectedAlg kConnectedAlg;
    private final BipartiteAlg bipartiteAlg;
    private final RegularAlg regularAlg;

    public SchemeAdapter(RouteMap routeMap, FlightWeightType weightType) throws AirFailure {
        this.routeMap = routeMap;
        this.weightType = weightType;
        this.routeMapScheme = SchemeAdapter.formSchemeFromRouteMap(routeMap, weightType);
        this.bestTrail = new BestTrail(this.routeMapScheme);
        this.limit = new Limit(this.routeMapScheme);
        this.kConnectedAlg = new KConnectedAlg(this.routeMapScheme);
        this.bipartiteAlg = new BipartiteAlg(this.routeMapScheme);
        this.regularAlg = new RegularAlg(this.routeMapScheme);
    }

    public BestTrailData getBestTrail(Airport origin, Airport destination) throws AirFailure {
        int sourceId = origin.pullId();
        int sinkId = destination.pullId();
        try {
            if (this.bestTrail.hasTrail(sourceId, sinkId)) {
                return this.obtainBestTrailSupervisor(sourceId, sinkId);
            }
            return new BestTrailData(this.routeMap, new ArrayList<Airport>(), Double.POSITIVE_INFINITY, this.weightType);
        }
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to find the shortest path between the two provided airports.", e);
        }
    }

    private BestTrailData obtainBestTrailSupervisor(int sourceId, int sinkId) throws SchemeFailure {
        double distance = this.bestTrail.bestTrail(sourceId, sinkId);
        List<Vertex> vertices = this.bestTrail.grabTrailVertices(sourceId, sinkId);
        List<Airport> airports = this.convertVerticesToAirports(vertices);
        return new BestTrailData(this.routeMap, airports, distance, this.weightType);
    }

    public double grabLimit(Airport origin, Airport destination) throws AirFailure {
        try {
            return this.limit.limit(origin.obtainName(), destination.obtainName());
        }
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to find the capacity between the two provided airports.");
        }
    }

    public String obtainBipartite() throws AirFailure {
        try {
            if (this.bipartiteAlg.isBipartite()) {
                return "Bipartite";
            }
            return "Not bipartite";
        }
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to determine if the route map is bipartite.");
        }
    }

    public String obtainConnected() throws AirFailure {
        try {
            if (ConnectedAlg.isConnected(this.routeMapScheme)) {
                return "Fully Connected";
            }
            return "Not Fully Connected";
        }
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to determine the connectedness of the route map.");
        }
    }

    public RouteMapDensity describeDensity() throws AirFailure {
        try {
            double density = SchemeDensity.computeDensity(this.routeMapScheme);
            SchemeDensity.Density schemeDensity = SchemeDensity.describeDensity(density);
            RouteMapDensity routeMapDensity = null;
            switch (schemeDensity) {
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
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to find the density of the route map.");
        }
    }

    public RouteMapSize describeSize() throws AirFailure {
        try {
            SchemeSize.Size size = SchemeSize.describeSize(this.routeMapScheme);
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
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to find the size of the route map.");
        }
    }

    public String kConnected(int k) throws AirFailure {
        if (this.routeMap.grabAirportIds().size() > 400 || this.routeMap.takeFlightIds().size() > 400) {
            return "Too large to tell";
        }
        try {
            if (this.kConnectedAlg.isKConnected(k)) {
                return "" + k + "-connected";
            }
            return "Not " + k + "-connected";
        }
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to determine the k-connectedness of the route map.");
        }
    }

    public String takeEulerian() throws AirFailure {
        try {
            Boolean isEulerian = EulerianAlg.isEulerian(this.routeMapScheme);
            return isEulerian.toString();
        }
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to determine whether route map is Eulerian");
        }
    }

    public String obtainRegular() throws AirFailure {
        try {
            boolean isRegular = this.regularAlg.isOutRegular();
            if (isRegular) {
                return "Regular of degree " + this.regularAlg.getOutDegree();
            }
            return "Not regular";
        }
        catch (SchemeFailure e) {
            throw new AirFailure("Unable to determine whether route map is Eulerian");
        }
    }

    private List<Airport> convertVerticesToAirports(List<Vertex> vertices) {
        ArrayList<Airport> airports = new ArrayList<Airport>();
        for (int j = 0; j < vertices.size(); ++j) {
            Vertex vertex = vertices.get(j);
            Airport airport = this.routeMap.obtainAirport(vertex.getId());
            airports.add(airport);
        }
        return airports;
    }

    private static Scheme formSchemeFromRouteMap(RouteMap routeMap, FlightWeightType weightType) throws AirFailure {
        Scheme scheme = SchemeFactory.newInstance();
        try {
            List<Airport> airports = routeMap.obtainAirports();
            for (int i = 0; i < airports.size(); ++i) {
                Airport airport = airports.get(i);
                Vertex vertex = new Vertex(airport.pullId(), airport.obtainName());
                scheme.addVertex(vertex);
            }
            List<Flight> flights = routeMap.obtainFlights();
            for (int i = 0; i < flights.size(); ++i) {
                BasicData data;
                Flight flight = flights.get(i);
                int sourceId = flight.getOrigin().pullId();
                int sinkId = flight.pullDestination().pullId();
                switch (weightType) {
                    case COST: {
                        data = new BasicData(flight.getFuelCosts());
                        break;
                    }
                    case DISTANCE: {
                        data = new BasicData(flight.obtainDistance());
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
                        data = new BasicData(flight.getWeightLimit());
                        break;
                    }
                    case PASSENGERS: {
                        data = new BasicData(flight.fetchPassengerLimit());
                        break;
                    }
                    default: {
                        data = new BasicData();
                    }
                }
                scheme.addEdge(sourceId, sinkId, data);
            }
        }
        catch (SchemeFailure e) {
            throw new AirFailure(e);
        }
        return scheme;
    }

    public List<Crew> fetchCrewAssignments() throws AirFailure {
        CrewManager crewManager = new CrewManager(this.routeMap);
        return crewManager.grabCrewAssignments();
    }

    public static class BestTrailData {
        private RouteMap routeMap;
        private List<Airport> airports;
        private double distance;
        private FlightWeightType weightType;

        public BestTrailData(RouteMap routeMap, List<Airport> airports, double distance, FlightWeightType weightType) {
            this.routeMap = routeMap;
            this.airports = airports;
            this.distance = distance;
            this.weightType = weightType;
        }

        public boolean hasTrail() {
            return this.airports.size() > 0;
        }

        public List<Airport> grabAirports() {
            return this.airports;
        }

        public RouteMap obtainRouteMap() {
            return this.routeMap;
        }

        public double pullDistance() {
            return this.distance;
        }

        public FlightWeightType pullWeightType() {
            return this.weightType;
        }
    }

}

