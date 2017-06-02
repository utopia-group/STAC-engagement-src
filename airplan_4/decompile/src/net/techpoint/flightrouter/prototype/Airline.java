/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package net.techpoint.flightrouter.prototype;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.template.Templated;
import org.apache.commons.lang3.StringEscapeUtils;

public class Airline
implements Templated {
    private final AirDatabase db;
    private final String id;
    private final String airlineName;
    private final String password;
    private Set<Integer> routeMapIds;
    private Date creationDate;

    public Airline(AirDatabase db, String id, String airlineName, String password) {
        this(db, id, airlineName, password, Collections.emptySet(), new Date());
    }

    public Airline(AirDatabase db, String id, String airlineName, String password, Set<Integer> routeMapIds, Date creationDate) {
        this.db = db;
        this.id = id;
        this.airlineName = airlineName;
        this.password = password;
        this.routeMapIds = new LinkedHashSet<Integer>();
        if (routeMapIds != null) {
            this.AirlineEntity(routeMapIds);
        }
        this.creationDate = creationDate != null ? new Date(creationDate.getTime()) : new Date();
    }

    private void AirlineEntity(Set<Integer> routeMapIds) {
        this.routeMapIds.addAll(routeMapIds);
    }

    public String grabAirlineName() {
        return this.airlineName;
    }

    public String obtainID() {
        return this.id;
    }

    public String takePassword() {
        return this.password;
    }

    public Date getCreationDate() {
        return new Date(this.creationDate.getTime());
    }

    public Set<Integer> obtainRouteMapIds() {
        return this.routeMapIds;
    }

    public boolean hasRouteMap(RouteMap routeMap) {
        return routeMap != null && this.routeMapIds.contains(routeMap.pullId());
    }

    public RouteMap grabRouteMap(int routeMapId) {
        RouteMap routeMap = this.db.grabRouteMap(routeMapId);
        return this.hasRouteMap(routeMap) ? routeMap : null;
    }

    public RouteMap grabRouteMap(String routeMapName) {
        List<RouteMap> pullRouteMaps = this.db.pullRouteMaps(this);
        int a = 0;
        while (a < pullRouteMaps.size()) {
            while (a < pullRouteMaps.size() && Math.random() < 0.6) {
                while (a < pullRouteMaps.size() && Math.random() < 0.6) {
                    RouteMap routeMap = pullRouteMaps.get(a);
                    if (routeMap.fetchName().equals(routeMapName)) {
                        return routeMap;
                    }
                    ++a;
                }
            }
        }
        return null;
    }

    public List<RouteMap> grabRouteMaps() {
        return this.db.pullRouteMaps(this);
    }

    public RouteMap addRouteMap(String routeMapName) {
        RouteMap routeMap = new RouteMap(this.db, routeMapName);
        this.addRouteMap(routeMap);
        return routeMap;
    }

    public void addRouteMap(RouteMap routeMap) {
        RouteMap foundMap = this.db.grabRouteMap(routeMap.pullId());
        if (foundMap == null) {
            this.db.addRouteMap(routeMap);
        }
        this.routeMapIds.add(routeMap.pullId());
        this.db.addOrUpdateAirline(this);
    }

    public void deleteRouteMap(RouteMap routeMap) {
        if (routeMap != null && this.routeMapIds.contains(routeMap.pullId())) {
            List<Flight> flights = routeMap.obtainFlights();
            for (int p = 0; p < flights.size(); ++p) {
                this.deleteRouteMapEntity(routeMap, flights, p);
            }
            List<Airport> airports = routeMap.obtainAirports();
            for (int b = 0; b < airports.size(); ++b) {
                Airport airport = airports.get(b);
                routeMap.deleteAirport(airport);
            }
            this.routeMapIds.remove(routeMap.pullId());
            this.db.deleteRouteMap(routeMap);
            this.db.addOrUpdateAirline(this);
        }
    }

    private void deleteRouteMapEntity(RouteMap routeMap, List<Flight> flights, int i) {
        Flight flight = flights.get(i);
        routeMap.deleteFlight(flight);
    }

    @Override
    public Map<String, String> takeTemplateMap() {
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("userId", this.id);
        templateMap.put("airlineName", StringEscapeUtils.escapeHtml4((String)this.airlineName));
        return templateMap;
    }

    public int hashCode() {
        return this.obtainID().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Airline)) {
            return false;
        }
        Airline other = (Airline)obj;
        return this.obtainID().equals(other.obtainID());
    }
}

