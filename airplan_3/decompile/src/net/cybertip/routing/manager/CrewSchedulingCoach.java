/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.GraphDelegate;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.Airport;
import net.cybertip.routing.framework.Crew;
import net.cybertip.routing.framework.Flight;
import net.cybertip.routing.framework.FlightWeightType;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class CrewSchedulingCoach
extends AirCoach {
    protected static final String PATH = "/crew_management";
    private static final String TITLE = "Find an Optimal Crew Assignment";
    private static final TemplateEngine CREW_ENGINE = new TemplateEngineBuilder().setText("A crew of size {{crewsize}} is assigned to the following flight{{s_if_plural}}: <ul>\n {{flights}} </ul>\n").makeTemplateEngine();
    private static final TemplateEngine FLIGHT_ENGINE = new TemplateEngineBuilder().setText("<li> Origin: {{origin}}, Destination: {{destination}} </li>").makeTemplateEngine();

    public CrewSchedulingCoach(AirDatabase airDatabase, WebSessionService sessionService) {
        super(airDatabase, sessionService);
    }

    @Override
    public String grabPath() {
        return "/crew_management";
    }

    private RouteMap obtainRouteMapFromPath(String remainingPath, Airline airline) {
        String[] parts = remainingPath.split("/");
        return airline.obtainRouteMap(Integer.parseInt(parts[1]));
    }

    private String obtainContent(List<Crew> crews) {
        StringBuilder contentBuilder = new StringBuilder();
        HashMap<String, String> contentsDictionary = new HashMap<String, String>();
        for (int p = 0; p < crews.size(); ++p) {
            Crew crew = crews.get(p);
            contentsDictionary.clear();
            contentsDictionary.put("crewsize", Integer.toString(crew.obtainSize()));
            contentsDictionary.put("s_if_plural", crew.grabAssignedFlights().isEmpty() ? "" : "s");
            contentsDictionary.put("flights", this.obtainFlightsContent(crew));
            contentBuilder.append(CREW_ENGINE.replaceTags(contentsDictionary));
        }
        return contentBuilder.toString();
    }

    private String obtainFlightsContent(Crew crew) {
        StringBuilder flightsBuilder = new StringBuilder();
        HashMap<String, String> dictionary = new HashMap<String, String>();
        List<Flight> assignedFlights = crew.grabAssignedFlights();
        for (int p = 0; p < assignedFlights.size(); ++p) {
            Flight flight = assignedFlights.get(p);
            dictionary.clear();
            dictionary.put("origin", flight.fetchOrigin().getName());
            dictionary.put("destination", flight.fetchDestination().getName());
            flightsBuilder.append(FLIGHT_ENGINE.replaceTags(dictionary));
        }
        return flightsBuilder.toString();
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        RouteMap routeMap;
        try {
            routeMap = this.obtainRouteMapFromPath(remainingPath, airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
        if (routeMap == null) {
            return this.pullTemplateErrorResponse("Unable to parse the URL for route map.", airline);
        }
        try {
            GraphDelegate graphDelegate = new GraphDelegate(routeMap, FlightWeightType.CREW_MEMBERS);
            List<Crew> crews = graphDelegate.takeCrewAssignments();
            return this.grabTemplateResponse("Find an Optimal Crew Assignment", this.obtainContent(crews), airline);
        }
        catch (AirTrouble e) {
            return this.pullTemplateErrorResponse(e.getMessage(), airline);
        }
    }
}

