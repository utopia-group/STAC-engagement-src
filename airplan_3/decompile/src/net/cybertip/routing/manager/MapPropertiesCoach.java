/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package net.cybertip.routing.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.HttpCoachResponse;
import net.cybertip.netmanager.manager.MultipartHelper;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.GraphDelegate;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.framework.FlightWeightType;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.framework.RouteMapDensity;
import net.cybertip.routing.framework.RouteMapSize;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.manager.AirCoach;
import net.cybertip.routing.manager.CellFormatter;
import net.cybertip.routing.manager.CoachUtils;
import net.cybertip.routing.manager.ParameterOverseer;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;
import org.apache.commons.lang3.math.NumberUtils;

public class MapPropertiesCoach
extends AirCoach {
    protected static final String PATH = "/map_properties";
    private static final String TITLE = "Map Properties";
    private static final String WEIGHT_TYPE_FIELD = "weight-type";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngineBuilder().setText("<h2>The following pages allow you to verify the correctness of the uploaded map.</h2><h2>{{routeMapName}}</h2><p>Select a weight type to be used in the properties calculation.</p><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\"><label for=\"weight-type\"> Select a Weight Type: </label><select name=\"weight-type\">{{weightTypeChoices}}</select> <br /><input type=\"submit\" value=\"Get the List of Properties\" name=\"submit\" id=\"submit\" /></form>").makeTemplateEngine();
    private static final TemplateEngine RESULTS_ENGINE = new TemplateEngineBuilder().setText("<h4>This page allows you to verify the correctness of the uploaded map. You may delete this route map if it is not as intended.</h4><a href=\"{{deleteMapURL}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Delete the Map\" name=\"submit\"></a><a href=\"{{graphMatrix}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Next\" name=\"submit\"></a><h2>{{routeMapName}}'s Properties</h2><p>These properties are related to the \"{{lowerCaseWeightLabel}}\" weight type. </p><ul>   {{properties}}</ul>").makeTemplateEngine();
    private static final TemplateEngine PROPERTY_ENGINE = new TemplateEngineBuilder().setText("<li>{{propertyLabel}}: {{propertyValue}} </li>").makeTemplateEngine();
    private CellFormatter formatter = new CellFormatter(10);
    private ParameterOverseer paramCoach = new ParameterOverseer();
    private int valueLength = 19;

    @Override
    public String grabPath() {
        return "/map_properties";
    }

    public MapPropertiesCoach(AirDatabase database, WebSessionService webSessionService) {
        super(database, webSessionService);
        this.paramCoach.assign("shou1d_adjust", false);
        this.paramCoach.assign("adjustment_factor", 2);
    }

    private RouteMap fetchRouteMapFromPath(String remainingPath, Airline airline) throws NumberFormatException {
        String idStr;
        String[] pathParts = remainingPath.split("/");
        if (pathParts.length == 2 && NumberUtils.isNumber((String)(idStr = pathParts[1]))) {
            return airline.obtainRouteMap(Integer.parseInt(idStr));
        }
        return null;
    }

    private String getContentsForGrab(RouteMap routeMap) {
        HashMap<String, String> choicesDict = new HashMap<String, String>();
        choicesDict.put("routeMapName", routeMap.pullName());
        choicesDict.put("weightTypeChoices", CoachUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(choicesDict);
    }

    @Override
    protected HttpCoachResponse handleObtain(HttpExchange httpExchange, String remainingPath, Airline airline) {
        try {
            RouteMap routeMap = this.fetchRouteMapFromPath(remainingPath, airline);
            if (routeMap == null) {
                return MapPropertiesCoach.obtainErrorResponse(400, "This route map does not exist.");
            }
            return this.fetchTemplateResponseWithoutMenuItems("Map Properties", this.getContentsForGrab(routeMap), airline);
        }
        catch (NumberFormatException e) {
            return this.pullTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
    }

    private String getContentsForPost(RouteMap routeMap, FlightWeightType weightType) throws AirTrouble {
        this.valueLength = 19;
        StringBuilder properties = new StringBuilder();
        GraphDelegate graphDelegate = new GraphDelegate(routeMap, weightType);
        String connectedString = graphDelegate.takeConnected();
        properties.append(this.makeHTMLForProperty("Connected", this.format(connectedString)));
        String bipartiteString = graphDelegate.fetchBipartite();
        properties.append(this.makeHTMLForProperty("Bipartite", this.format(bipartiteString)));
        RouteMapSize size = graphDelegate.describeSize();
        properties.append(this.makeHTMLForProperty("Size", this.format(size.obtainDescription())));
        RouteMapDensity density = graphDelegate.describeDensity();
        properties.append(this.makeHTMLForProperty("Density", this.format(density.grabDescription())));
        String twoConnected = graphDelegate.kConnected(2);
        properties.append(this.makeHTMLForProperty("Is 2-connected", this.format(twoConnected)));
        String threeConnected = graphDelegate.kConnected(3);
        properties.append(this.makeHTMLForProperty("Is 3-connected", this.format(threeConnected)));
        String fourConnected = graphDelegate.kConnected(4);
        properties.append(this.makeHTMLForProperty("Is 4-connected", this.format(fourConnected)));
        String fiveConnected = graphDelegate.kConnected(5);
        properties.append(this.makeHTMLForProperty("Is 5-connected", this.format(fiveConnected)));
        String regular = graphDelegate.obtainRegular();
        properties.append(this.makeHTMLForProperty("Regular", this.format(regular)));
        String eulerian = graphDelegate.grabEulerian();
        properties.append(this.makeHTMLForProperty("Eulerian", this.format(eulerian)));
        HashMap<String, String> results = new HashMap<String, String>();
        String formattedRouteName = this.formatter.format(routeMap.pullName(), 10, CellFormatter.Justification.CENTER, false);
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

    private String makeHTMLForProperty(String propertyLabel, String propertyValue) {
        HashMap<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put("propertyLabel", propertyLabel);
        propertiesMap.put("propertyValue", propertyValue);
        return PROPERTY_ENGINE.replaceTags(propertiesMap);
    }

    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingPath, Airline airline) {
        RouteMap routeMap = this.fetchRouteMapFromPath(remainingPath, airline);
        if (routeMap == null) {
            return MapPropertiesCoach.obtainErrorResponse(400, "This route map does not exist.");
        }
        String data = MultipartHelper.grabMultipartFieldContent(httpExchange, "weight-type");
        if (data == null || data.isEmpty()) {
            return MapPropertiesCoach.obtainErrorResponse(400, "A weight type was not selected.");
        }
        FlightWeightType weightType = FlightWeightType.fromString(data);
        if (weightType == null) {
            return MapPropertiesCoach.obtainErrorResponse(400, "The weight type selected is not a known weight type.");
        }
        try {
            HttpCoachResponse response = this.fetchTemplateResponseWithoutMenuItems("Map Properties", this.getContentsForPost(routeMap, weightType), airline);
            return response;
        }
        catch (AirTrouble e) {
            return MapPropertiesCoach.obtainErrorResponse(400, "Unable to create a list of properties for this route map.");
        }
    }

    @Override
    protected String grabDisplayName(Airline member) {
        return this.format(member.grabAirlineName());
    }
}

