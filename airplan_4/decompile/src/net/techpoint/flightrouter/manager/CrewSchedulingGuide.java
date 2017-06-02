/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.SchemeAdapter;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.Airport;
import net.techpoint.flightrouter.prototype.Crew;
import net.techpoint.flightrouter.prototype.Flight;
import net.techpoint.flightrouter.prototype.FlightWeightType;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.template.TemplateEngine;

public class CrewSchedulingGuide
extends AirGuide {
    protected static final String TRAIL = "/crew_management";
    private static final String TITLE = "Find an Optimal Crew Assignment";
    private static final TemplateEngine CREW_ENGINE = new TemplateEngine("A crew of size {{crewsize}} is assigned to the following flight{{s_if_plural}}: <ul>\n {{flights}} </ul>\n");
    private static final TemplateEngine FLIGHT_ENGINE = new TemplateEngine("<li> Origin: {{origin}}, Destination: {{destination}} </li>");

    public CrewSchedulingGuide(AirDatabase airDatabase, WebSessionService sessionService) {
        super(airDatabase, sessionService);
    }

    @Override
    public String obtainTrail() {
        return "/crew_management";
    }

    private RouteMap getRouteMapFromTrail(String remainingTrail, Airline airline) {
        String[] parts = remainingTrail.split("/");
        return airline.grabRouteMap(Integer.parseInt(parts[1]));
    }

    private String obtainContent(List<Crew> crews) {
        StringBuilder contentBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        for (int q = 0; q < crews.size(); ++q) {
            this.grabContentHome(crews, contentBuilder, contentsDictionary, q);
        }
        return contentBuilder.toString();
    }

    private void grabContentHome(List<Crew> crews, StringBuilder contentBuilder, Map<String, String> contentsDictionary, int b) {
        Crew crew = crews.get(b);
        contentsDictionary.clear();
        contentsDictionary.put("crewsize", Integer.toString(crew.takeSize()));
        contentsDictionary.put("s_if_plural", crew.grabAssignedFlights().isEmpty() ? "" : "s");
        contentsDictionary.put("flights", this.obtainFlightsContent(crew));
        contentBuilder.append(CREW_ENGINE.replaceTags(contentsDictionary));
    }

    private String obtainFlightsContent(Crew crew) {
        StringBuilder flightsBuilder = new StringBuilder();
        HashMap<String, String> dictionary = new HashMap<String, String>();
        List<Flight> assignedFlights = crew.grabAssignedFlights();
        for (int q = 0; q < assignedFlights.size(); ++q) {
            Flight flight = assignedFlights.get(q);
            dictionary.clear();
            dictionary.put("origin", flight.getOrigin().obtainName());
            dictionary.put("destination", flight.pullDestination().obtainName());
            flightsBuilder.append(FLIGHT_ENGINE.replaceTags(dictionary));
        }
        return flightsBuilder.toString();
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap;
        try {
            routeMap = this.getRouteMapFromTrail(remainingTrail, airline);
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        if (routeMap == null) {
            return this.takeTemplateErrorResponse("Unable to parse the URL for route map.", airline);
        }
        try {
            SchemeAdapter schemeAdapter = new SchemeAdapter(routeMap, FlightWeightType.CREW_MEMBERS);
            List<Crew> crews = schemeAdapter.fetchCrewAssignments();
            return this.getTemplateResponse("Find an Optimal Crew Assignment", this.obtainContent(crews), airline);
        }
        catch (AirFailure e) {
            return this.takeTemplateErrorResponse(e.getMessage(), airline);
        }
    }
}

