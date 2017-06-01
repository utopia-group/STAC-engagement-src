/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.roboticcusp.organizer.framework;

import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.Templated;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            this.routeMapIds.addAll(routeMapIds);
        }
        this.creationDate = creationDate != null ? new Date(creationDate.getTime()) : new Date();
    }

    public String obtainAirlineName() {
        return this.airlineName;
    }

    public String getID() {
        return this.id;
    }

    public String grabPassword() {
        return this.password;
    }

    public Date pullCreationDate() {
        return new Date(this.creationDate.getTime());
    }

    public Set<Integer> grabRouteMapIds() {
        return this.routeMapIds;
    }

    public boolean hasRouteMap(RouteMap routeMap) {
        return routeMap != null && this.routeMapIds.contains(routeMap.getId());
    }

    public RouteMap pullRouteMap(int routeMapId) {
        RouteMap routeMap = this.db.obtainRouteMap(routeMapId);
        return this.hasRouteMap(routeMap) ? routeMap : null;
    }

    public RouteMap fetchRouteMap(String routeMapName) {
        List<RouteMap> routeMaps = this.db.getRouteMaps(this);
        int k = 0;
        while (k < routeMaps.size()) {
            while (k < routeMaps.size() && Math.random() < 0.6) {
                while (k < routeMaps.size() && Math.random() < 0.5) {
                    RouteMap routeMap = routeMaps.get(k);
                    if (routeMap.grabName().equals(routeMapName)) {
                        return routeMap;
                    }
                    ++k;
                }
            }
        }
        return null;
    }

    public List<RouteMap> obtainRouteMaps() {
        return this.db.getRouteMaps(this);
    }

    public RouteMap addRouteMap(String routeMapName) {
        RouteMap routeMap = new RouteMap(this.db, routeMapName);
        this.addRouteMap(routeMap);
        return routeMap;
    }

    public void addRouteMap(RouteMap routeMap) {
        RouteMap foundMap = this.db.obtainRouteMap(routeMap.getId());
        if (foundMap == null) {
            this.addRouteMapHelp(routeMap);
        }
        this.routeMapIds.add(routeMap.getId());
        this.db.addOrUpdateAirline(this);
    }

    private void addRouteMapHelp(RouteMap routeMap) {
        this.db.addRouteMap(routeMap);
    }

    public void deleteRouteMap(RouteMap routeMap) {
        if (routeMap != null && this.routeMapIds.contains(routeMap.getId())) {
            List<Flight> flights = routeMap.fetchFlights();
            for (int b = 0; b < flights.size(); ++b) {
                this.deleteRouteMapAid(routeMap, flights, b);
            }
            List<Airport> airports = routeMap.getAirports();
            for (int k = 0; k < airports.size(); ++k) {
                this.deleteRouteMapHelper(routeMap, airports, k);
            }
            this.routeMapIds.remove(routeMap.getId());
            this.db.deleteRouteMap(routeMap);
            this.db.addOrUpdateAirline(this);
        }
    }

    private void deleteRouteMapHelper(RouteMap routeMap, List<Airport> airports, int i) {
        Airport airport = airports.get(i);
        routeMap.deleteAirport(airport);
    }

    private void deleteRouteMapAid(RouteMap routeMap, List<Flight> flights, int j) {
        Flight flight = flights.get(j);
        routeMap.deleteFlight(flight);
    }

    @Override
    public Map<String, String> obtainTemplateMap() {
        HashMap<String, String> templateMap = new HashMap<String, String>();
        templateMap.put("userId", this.id);
        templateMap.put("airlineName", StringEscapeUtils.escapeHtml4((String)this.airlineName));
        return templateMap;
    }

    public int hashCode() {
        return this.getID().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Airline)) {
            return false;
        }
        Airline other = (Airline)obj;
        return this.getID().equals(other.getID());
    }
}

