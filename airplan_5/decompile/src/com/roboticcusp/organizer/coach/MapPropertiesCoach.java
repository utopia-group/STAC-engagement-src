/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.ChartProxy;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CellFormatter;
import com.roboticcusp.organizer.coach.CoachUtils;
import com.roboticcusp.organizer.coach.ParameterConductor;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.FlightWeightType;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.framework.RouteMapDensity;
import com.roboticcusp.organizer.framework.RouteMapSize;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.template.TemplateEngine;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;

public class MapPropertiesCoach
extends AirCoach {
    protected static final String TRAIL = "/map_properties";
    private static final String TITLE = "Map Properties";
    private static final String WEIGHT_TYPE_FIELD = "weight-type";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngine("<h2>The following pages allow you to verify the correctness of the uploaded map.</h2><h2>{{routeMapName}}</h2><p>Select a weight type to be used in the properties calculation.</p><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\"><label for=\"weight-type\"> Select a Weight Type: </label><select name=\"weight-type\">{{weightTypeChoices}}</select> <br /><input type=\"submit\" value=\"Get the List of Properties\" name=\"submit\" id=\"submit\" /></form>");
    private static final TemplateEngine RESULTS_ENGINE = new TemplateEngine("<h4>This page allows you to verify the correctness of the uploaded map. You may delete this route map if it is not as intended.</h4><a href=\"{{deleteMapURL}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Delete the Map\" name=\"submit\"></a><a href=\"{{graphMatrix}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Next\" name=\"submit\"></a><h2>{{routeMapName}}'s Properties</h2><p>These properties are related to the \"{{lowerCaseWeightLabel}}\" weight type. </p><ul>   {{properties}}</ul>");
    private static final TemplateEngine PROPERTY_ENGINE = new TemplateEngine("<li>{{propertyLabel}}: {{propertyValue}} </li>");
    private CellFormatter formatter = new CellFormatter(10);
    private ParameterConductor paramCoach = new ParameterConductor();
    private int valueLength = 19;

    @Override
    public String getTrail() {
        return "/map_properties";
    }

    public MapPropertiesCoach(AirDatabase database, WebSessionService webSessionService) {
        super(database, webSessionService);
        this.paramCoach.set("shou1d_adjust", false);
        this.paramCoach.set("adjustment_factor", 2);
    }

    private RouteMap pullRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String idStr;
        String[] trailParts = remainingTrail.split("/");
        if (trailParts.length == 2 && NumberUtils.isNumber((String)(idStr = trailParts[1]))) {
            return airline.pullRouteMap(Integer.parseInt(idStr));
        }
        return null;
    }

    private String getContentsForObtain(RouteMap routeMap) {
        HashMap<String, String> choicesDict = new HashMap<String, String>();
        choicesDict.put("routeMapName", routeMap.grabName());
        choicesDict.put("weightTypeChoices", CoachUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(choicesDict);
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            RouteMap routeMap = this.pullRouteMapFromTrail(remainingTrail, airline);
            if (routeMap == null) {
                return MapPropertiesCoach.grabErrorResponse(400, "This route map does not exist.");
            }
            return this.takeTemplateResponseWithoutMenuItems("Map Properties", this.getContentsForObtain(routeMap), airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
    }

    private String fetchContentsForPost(RouteMap routeMap, FlightWeightType weightType) throws AirException {
        this.valueLength = 19;
        StringBuilder properties = new StringBuilder();
        ChartProxy chartProxy = new ChartProxy(routeMap, weightType);
        String connectedString = chartProxy.getConnected();
        properties.append(this.composeHTMLForProperty("Connected", this.format(connectedString)));
        String bipartiteString = chartProxy.fetchBipartite();
        properties.append(this.composeHTMLForProperty("Bipartite", this.format(bipartiteString)));
        RouteMapSize size = chartProxy.describeSize();
        properties.append(this.composeHTMLForProperty("Size", this.format(size.grabDescription())));
        RouteMapDensity density = chartProxy.describeDensity();
        properties.append(this.composeHTMLForProperty("Density", this.format(density.takeDescription())));
        String twoConnected = chartProxy.kConnected(2);
        properties.append(this.composeHTMLForProperty("Is 2-connected", this.format(twoConnected)));
        String threeConnected = chartProxy.kConnected(3);
        properties.append(this.composeHTMLForProperty("Is 3-connected", this.format(threeConnected)));
        String fourConnected = chartProxy.kConnected(4);
        properties.append(this.composeHTMLForProperty("Is 4-connected", this.format(fourConnected)));
        String fiveConnected = chartProxy.kConnected(5);
        properties.append(this.composeHTMLForProperty("Is 5-connected", this.format(fiveConnected)));
        String regular = chartProxy.fetchRegular();
        properties.append(this.composeHTMLForProperty("Regular", this.format(regular)));
        String eulerian = chartProxy.grabEulerian();
        properties.append(this.composeHTMLForProperty("Eulerian", this.format(eulerian)));
        HashMap<String, String> results = new HashMap<String, String>();
        String formattedRouteName = this.formatter.format(routeMap.grabName(), 10, CellFormatter.Justification.CENTER, false);
        results.put("routeMapName", formattedRouteName);
        String formattedWeightLabel = this.formatter.format(weightType.grabDescription().toLowerCase(), 20, CellFormatter.Justification.CENTER, false);
        results.put("lowerCaseWeightLabel", formattedWeightLabel);
        results.put("properties", properties.toString());
        results.put("graphMatrix", CoachUtils.generateRouteMapMatrixURL(routeMap));
        results.put("deleteMapURL", CoachUtils.generateDeleteMapURL());
        return RESULTS_ENGINE.replaceTags(results);
    }

    private String format(String value) {
        String formatted = value;
        formatted = value.length() < this.valueLength ? this.formatter.format(value, this.valueLength, CellFormatter.Justification.CENTER, (Boolean)this.paramCoach.obtain("should_adjust")) : this.formatter.format(value, this.valueLength *= ((Integer)this.paramCoach.obtain("adjustment_factor")).intValue(), CellFormatter.Justification.CENTER, (Boolean)this.paramCoach.obtain("should_adjust"));
        return formatted;
    }

    private String composeHTMLForProperty(String propertyLabel, String propertyValue) {
        HashMap<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put("propertyLabel", propertyLabel);
        propertiesMap.put("propertyValue", propertyValue);
        return PROPERTY_ENGINE.replaceTags(propertiesMap);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap = this.pullRouteMapFromTrail(remainingTrail, airline);
        if (routeMap == null) {
            return MapPropertiesCoach.grabErrorResponse(400, "This route map does not exist.");
        }
        String data = MultipartHelper.fetchMultipartFieldContent(httpExchange, "weight-type");
        if (data == null || data.isEmpty()) {
            return MapPropertiesCoach.grabErrorResponse(400, "A weight type was not selected.");
        }
        FlightWeightType weightType = FlightWeightType.fromString(data);
        if (weightType == null) {
            return MapPropertiesCoach.grabErrorResponse(400, "The weight type selected is not a known weight type.");
        }
        try {
            HttpCoachResponse response = this.takeTemplateResponseWithoutMenuItems("Map Properties", this.fetchContentsForPost(routeMap, weightType), airline);
            return response;
        }
        catch (AirException e) {
            return MapPropertiesCoach.grabErrorResponse(400, "Unable to create a list of properties for this route map.");
        }
    }

    @Override
    protected String grabDisplayName(Airline participant) {
        return this.format(participant.obtainAirlineName());
    }
}

