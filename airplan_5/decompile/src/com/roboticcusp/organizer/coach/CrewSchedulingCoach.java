/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.ChartProxy;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.Airport;
import com.roboticcusp.organizer.framework.Crew;
import com.roboticcusp.organizer.framework.Flight;
import com.roboticcusp.organizer.framework.FlightWeightType;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrewSchedulingCoach
extends AirCoach {
    protected static final String TRAIL = "/crew_management";
    private static final String TITLE = "Find an Optimal Crew Assignment";
    private static final TemplateEngine CREW_ENGINE = new TemplateEngine("A crew of size {{crewsize}} is assigned to the following flight{{s_if_plural}}: <ul>\n {{flights}} </ul>\n");
    private static final TemplateEngine FLIGHT_ENGINE = new TemplateEngine("<li> Origin: {{origin}}, Destination: {{destination}} </li>");

    public CrewSchedulingCoach(AirDatabase airDatabase, WebSessionService sessionService) {
        super(airDatabase, sessionService);
    }

    @Override
    public String getTrail() {
        return "/crew_management";
    }

    private RouteMap obtainRouteMapFromTrail(String remainingTrail, Airline airline) {
        String[] parts = remainingTrail.split("/");
        return airline.pullRouteMap(Integer.parseInt(parts[1]));
    }

    private String grabContent(List<Crew> crews) {
        StringBuilder contentBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        for (int p = 0; p < crews.size(); ++p) {
            this.grabContentHerder(crews, contentBuilder, contentsDictionary, p);
        }
        return contentBuilder.toString();
    }

    private void grabContentHerder(List<Crew> crews, StringBuilder contentBuilder, Map<String, String> contentsDictionary, int p) {
        Crew crew = crews.get(p);
        contentsDictionary.clear();
        contentsDictionary.put("crewsize", Integer.toString(crew.pullSize()));
        contentsDictionary.put("s_if_plural", crew.obtainAssignedFlights().isEmpty() ? "" : "s");
        contentsDictionary.put("flights", this.fetchFlightsContent(crew));
        contentBuilder.append(CREW_ENGINE.replaceTags(contentsDictionary));
    }

    private String fetchFlightsContent(Crew crew) {
        StringBuilder flightsBuilder = new StringBuilder();
        HashMap<String, String> dictionary = new HashMap<String, String>();
        List<Flight> assignedFlights = crew.obtainAssignedFlights();
        for (int q = 0; q < assignedFlights.size(); ++q) {
            this.grabFlightsContentCoordinator(flightsBuilder, dictionary, assignedFlights, q);
        }
        return flightsBuilder.toString();
    }

    private void grabFlightsContentCoordinator(StringBuilder flightsBuilder, Map<String, String> dictionary, List<Flight> assignedFlights, int i) {
        Flight flight = assignedFlights.get(i);
        dictionary.clear();
        dictionary.put("origin", flight.obtainOrigin().takeName());
        dictionary.put("destination", flight.fetchDestination().takeName());
        flightsBuilder.append(FLIGHT_ENGINE.replaceTags(dictionary));
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap;
        try {
            routeMap = this.obtainRouteMapFromTrail(remainingTrail, airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        if (routeMap == null) {
            return this.pullTemplateErrorResponse("Unable to parse the URL for route map.", airline);
        }
        try {
            ChartProxy chartProxy = new ChartProxy(routeMap, FlightWeightType.CREW_MEMBERS);
            List<Crew> crews = chartProxy.pullCrewAssignments();
            return this.obtainTemplateResponse("Find an Optimal Crew Assignment", this.grabContent(crews), airline);
        }
        catch (AirException e) {
            return this.pullTemplateErrorResponse(e.getMessage(), airline);
        }
    }
}

