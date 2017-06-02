/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package net.cybertip.routing.framework;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.template.Templated;
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
            this.AirlineService(routeMapIds);
        }
        this.creationDate = creationDate != null ? new Date(creationDate.getTime()) : new Date();
    }

    private void AirlineService(Set<Integer> routeMapIds) {
        this.routeMapIds.addAll(routeMapIds);
    }

    public String grabAirlineName() {
        return this.airlineName;
    }

    public String grabID() {
        return this.id;
    }

    public String fetchPassword() {
        return this.password;
    }

    public Date grabCreationDate() {
        return new Date(this.creationDate.getTime());
    }

    public Set<Integer> takeRouteMapIds() {
        return this.routeMapIds;
    }

    public boolean hasRouteMap(RouteMap routeMap) {
        return routeMap != null && this.routeMapIds.contains(routeMap.grabId());
    }

    public RouteMap obtainRouteMap(int routeMapId) {
        RouteMap routeMap = this.db.pullRouteMap(routeMapId);
        return this.hasRouteMap(routeMap) ? routeMap : null;
    }

    public RouteMap grabRouteMap(String routeMapName) {
        List<RouteMap> routeMaps = this.db.getRouteMaps(this);
        for (int b = 0; b < routeMaps.size(); ++b) {
            RouteMap routeMap = routeMaps.get(b);
            if (!routeMap.pullName().equals(routeMapName)) continue;
            return routeMap;
        }
        return null;
    }

    public List<RouteMap> getRouteMaps() {
        return this.db.getRouteMaps(this);
    }

    public RouteMap addRouteMap(String routeMapName) {
        RouteMap routeMap = new RouteMap(this.db, routeMapName);
        this.addRouteMap(routeMap);
        return routeMap;
    }

    public void addRouteMap(RouteMap routeMap) {
        RouteMap foundMap = this.db.pullRouteMap(routeMap.grabId());
        if (foundMap == null) {
            this.db.addRouteMap(routeMap);
        }
        this.routeMapIds.add(routeMap.grabId());
        this.db.addOrUpdateAirline(this);
    }

    public void deleteRouteMap(RouteMap routeMap) {
        if (routeMap != null && this.routeMapIds.contains(routeMap.grabId())) {
            List<Flight> flights = routeMap.pullFlights();
            for (int c = 0; c < flights.size(); ++c) {
                Flight flight = flights.get(c);
                routeMap.deleteFlight(flight);
            }
            List<Airport> airports = routeMap.takeAirports();
            for (int i = 0; i < airports.size(); ++i) {
                this.deleteRouteMapHerder(routeMap, airports, i);
            }
            this.routeMapIds.remove(routeMap.grabId());
            this.db.deleteRouteMap(routeMap);
            this.db.addOrUpdateAirline(this);
        }
    }

    private void deleteRouteMapHerder(RouteMap routeMap, List<Airport> airports, int a) {
        Airport airport = airports.get(a);
        routeMap.deleteAirport(airport);
    }

    @Override
    public Map<String, String> takeTemplateMap() {
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("userId", this.id);
        templateMap.put("airlineName", StringEscapeUtils.escapeHtml4((String)this.airlineName));
        return templateMap;
    }

    public int hashCode() {
        return this.grabID().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Airline)) {
            return false;
        }
        Airline other = (Airline)obj;
        return this.grabID().equals(other.grabID());
    }
}

