/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package net.techpoint.flightrouter.manager;

import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.SchemeAdapter;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AirGuide;
import net.techpoint.flightrouter.manager.CellFormatter;
import net.techpoint.flightrouter.manager.CellFormatterBuilder;
import net.techpoint.flightrouter.manager.GuideUtils;
import net.techpoint.flightrouter.manager.ParameterManager;
import net.techpoint.flightrouter.manager.ParameterManagerBuilder;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.flightrouter.prototype.FlightWeightType;
import net.techpoint.flightrouter.prototype.RouteMap;
import net.techpoint.flightrouter.prototype.RouteMapDensity;
import net.techpoint.flightrouter.prototype.RouteMapSize;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.HttpGuideResponse;
import net.techpoint.server.manager.MultipartHelper;
import net.techpoint.template.TemplateEngine;
import org.apache.commons.lang3.math.NumberUtils;

public class MapPropertiesGuide
extends AirGuide {
    protected static final String TRAIL = "/map_properties";
    private static final String TITLE = "Map Properties";
    private static final String WEIGHT_TYPE_FIELD = "weight-type";
    private static final TemplateEngine INPUT_PAGE_ENGINE = new TemplateEngine("<h2>The following pages allow you to verify the correctness of the uploaded map.</h2><h2>{{routeMapName}}</h2><p>Select a weight type to be used in the properties calculation.</p><form action=\"#\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\"><label for=\"weight-type\"> Select a Weight Type: </label><select name=\"weight-type\">{{weightTypeChoices}}</select> <br /><input type=\"submit\" value=\"Get the List of Properties\" name=\"submit\" id=\"submit\" /></form>");
    private static final TemplateEngine RESULTS_ENGINE = new TemplateEngine("<h4>This page allows you to verify the correctness of the uploaded map. You may delete this route map if it is not as intended.</h4><a href=\"{{deleteMapURL}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Delete the Map\" name=\"submit\"></a><a href=\"{{graphMatrix}}\" style=\"text-decoration:none\"> <input type=\"button\" value=\"Next\" name=\"submit\"></a><h2>{{routeMapName}}'s Properties</h2><p>These properties are related to the \"{{lowerCaseWeightLabel}}\" weight type. </p><ul>   {{properties}}</ul>");
    private static final TemplateEngine PROPERTY_ENGINE = new TemplateEngine("<li>{{propertyLabel}}: {{propertyValue}} </li>");
    private CellFormatter formatter = new CellFormatterBuilder().assignLength(10).formCellFormatter();
    private ParameterManager paramGuide = new ParameterManagerBuilder().formParameterManager();
    private int valueLength = 19;

    @Override
    public String obtainTrail() {
        return "/map_properties";
    }

    public MapPropertiesGuide(AirDatabase database, WebSessionService webSessionService) {
        super(database, webSessionService);
        this.paramGuide.fix("shou1d_adjust", false);
        this.paramGuide.fix("adjustment_factor", 2);
    }

    private RouteMap grabRouteMapFromTrail(String remainingTrail, Airline airline) throws NumberFormatException {
        String idStr;
        String[] trailParts = remainingTrail.split("/");
        if (trailParts.length == 2 && NumberUtils.isNumber((String)(idStr = trailParts[1]))) {
            return airline.grabRouteMap(Integer.parseInt(idStr));
        }
        return null;
    }

    private String grabContentsForPull(RouteMap routeMap) {
        HashMap<String, String> choicesDict = new HashMap<String, String>();
        choicesDict.put("routeMapName", routeMap.fetchName());
        choicesDict.put("weightTypeChoices", GuideUtils.FLIGHT_WEIGHT_TYPE_OPTIONS);
        return INPUT_PAGE_ENGINE.replaceTags(choicesDict);
    }

    @Override
    protected HttpGuideResponse handleObtain(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        try {
            RouteMap routeMap = this.grabRouteMapFromTrail(remainingTrail, airline);
            if (routeMap == null) {
                return MapPropertiesGuide.getErrorResponse(400, "This route map does not exist.");
            }
            return this.obtainTemplateResponseWithoutMenuItems("Map Properties", this.grabContentsForPull(routeMap), airline);
        }
        catch (NumberFormatException e) {
            return this.takeTemplateErrorResponse("Error while parsing the route map id. " + e.getMessage(), airline);
        }
    }

    private String grabContentsForPost(RouteMap routeMap, FlightWeightType weightType) throws AirFailure {
        this.valueLength = 19;
        StringBuilder properties = new StringBuilder();
        SchemeAdapter schemeAdapter = new SchemeAdapter(routeMap, weightType);
        this.paramGuide.fix("adjustment_factor", 1);
        String connectedString = schemeAdapter.obtainConnected();
        properties.append(this.formHTMLForProperty("Connected", this.format(connectedString)));
        String bipartiteString = schemeAdapter.obtainBipartite();
        properties.append(this.formHTMLForProperty("Bipartite", this.format(bipartiteString)));
        RouteMapSize size = schemeAdapter.describeSize();
        properties.append(this.formHTMLForProperty("Size", this.format(size.takeDescription())));
        RouteMapDensity density = schemeAdapter.describeDensity();
        properties.append(this.formHTMLForProperty("Density", this.format(density.takeDescription())));
        String twoConnected = schemeAdapter.kConnected(2);
        properties.append(this.formHTMLForProperty("Is 2-connected", this.format(twoConnected)));
        String threeConnected = schemeAdapter.kConnected(3);
        properties.append(this.formHTMLForProperty("Is 3-connected", this.format(threeConnected)));
        String fourConnected = schemeAdapter.kConnected(4);
        properties.append(this.formHTMLForProperty("Is 4-connected", this.format(fourConnected)));
        String fiveConnected = schemeAdapter.kConnected(5);
        properties.append(this.formHTMLForProperty("Is 5-connected", this.format(fiveConnected)));
        String regular = schemeAdapter.obtainRegular();
        properties.append(this.formHTMLForProperty("Regular", this.format(regular)));
        String eulerian = schemeAdapter.takeEulerian();
        properties.append(this.formHTMLForProperty("Eulerian", this.format(eulerian)));
        HashMap<String, String> results = new HashMap<String, String>();
        String formattedRouteName = this.formatter.format(routeMap.fetchName(), 10, CellFormatter.Justification.CENTER, false);
        results.put("routeMapName", formattedRouteName);
        String formattedWeightLabel = this.formatter.format(weightType.takeDescription().toLowerCase(), 20, CellFormatter.Justification.CENTER, false);
        results.put("lowerCaseWeightLabel", formattedWeightLabel);
        results.put("properties", properties.toString());
        results.put("graphMatrix", GuideUtils.generateRouteMapMatrixURL(routeMap));
        results.put("deleteMapURL", GuideUtils.generateDeleteMapURL());
        return RESULTS_ENGINE.replaceTags(results);
    }

    private String format(String value) {
        String formatted = value;
        formatted = value.length() < this.valueLength ? this.formatter.format(value, this.valueLength, CellFormatter.Justification.CENTER, (Boolean)this.paramGuide.get("should_adjust")) : this.formatter.format(value, this.valueLength *= ((Integer)this.paramGuide.get("adjustment_factor")).intValue(), CellFormatter.Justification.CENTER, (Boolean)this.paramGuide.get("should_adjust"));
        return formatted;
    }

    private String formHTMLForProperty(String propertyLabel, String propertyValue) {
        HashMap<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put("propertyLabel", propertyLabel);
        propertiesMap.put("propertyValue", propertyValue);
        return PROPERTY_ENGINE.replaceTags(propertiesMap);
    }

    @Override
    protected HttpGuideResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        RouteMap routeMap = this.grabRouteMapFromTrail(remainingTrail, airline);
        if (routeMap == null) {
            return MapPropertiesGuide.getErrorResponse(400, "This route map does not exist.");
        }
        String data = MultipartHelper.pullMultipartFieldContent(httpExchange, "weight-type");
        if (data == null || data.isEmpty()) {
            return MapPropertiesGuide.getErrorResponse(400, "A weight type was not selected.");
        }
        FlightWeightType weightType = FlightWeightType.fromString(data);
        if (weightType == null) {
            return MapPropertiesGuide.getErrorResponse(400, "The weight type selected is not a known weight type.");
        }
        try {
            HttpGuideResponse response = this.obtainTemplateResponseWithoutMenuItems("Map Properties", this.grabContentsForPost(routeMap, weightType), airline);
            return response;
        }
        catch (AirFailure e) {
            return MapPropertiesGuide.getErrorResponse(400, "Unable to create a list of properties for this route map.");
        }
    }

    @Override
    protected String getDisplayName(Airline user) {
        return this.format(user.grabAirlineName());
    }
}

