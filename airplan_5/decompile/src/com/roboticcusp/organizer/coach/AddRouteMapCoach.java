/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer.coach;

import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.HttpCoachResponse;
import com.roboticcusp.network.coach.MultipartHelper;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.ParserFileLoader;
import com.roboticcusp.organizer.TextFileLoader;
import com.roboticcusp.organizer.XmlFileLoader;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddRouteMapCoach
extends AirCoach {
    protected static final String TRAIL = "/add_route_map";
    protected static final String TITLE = "Add Route Map";
    private static final String FILE_FIELD_NAME = "file";
    private static final String ROUTE_MAP_FIELD_NAME = "route_map_name";
    private static final String CONTENT = "<form action=\"/add_route_map\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <label for=\"file\"> Route Map: </label>    <input type=\"file\" id=\"file\" name=\"file\" autofocus required accept=\"*/*\"/>    <br/>    <label for=\"map\"> Name your route map: </label>    <input type=\"text\" id=\"map\" name=\"route_map_name\" placeholder=\"Route Map\" /> <br/>    <input type=\"submit\" value=\"Submit Route Map\" name=\"submit\" id=\"submit\" /></form>";
    private static final Set<String> ALL_FIELDS = new HashSet<String>();
    private static Path tempDirectory;
    private final ParserFileLoader parserFileLoader = new ParserFileLoader();
    private final TextFileLoader textFileLoader = new TextFileLoader();
    private final XmlFileLoader xmlFileLoader = new XmlFileLoader();

    public AddRouteMapCoach(AirDatabase db, WebSessionService webSessionService) {
        super(db, webSessionService);
    }

    @Override
    public String getTrail() {
        return "/add_route_map";
    }

    @Override
    protected HttpCoachResponse handleGrab(HttpExchange httpExchange, String remainingTrail, Airline participant) {
        if (!remainingTrail.equals("") && !remainingTrail.equals("/")) {
            return AddRouteMapCoach.grabErrorResponse(404, "Page not found.");
        }
        return this.obtainTemplateResponse("Add Route Map", "<form action=\"/add_route_map\" method=\"post\" enctype=\"multipart/form-data\" acceptcharset=\"UTF-8\">    <label for=\"file\"> Route Map: </label>    <input type=\"file\" id=\"file\" name=\"file\" autofocus required accept=\"*/*\"/>    <br/>    <label for=\"map\"> Name your route map: </label>    <input type=\"text\" id=\"map\" name=\"route_map_name\" placeholder=\"Route Map\" /> <br/>    <input type=\"submit\" value=\"Submit Route Map\" name=\"submit\" id=\"submit\" /></form>", participant);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    protected HttpCoachResponse handlePost(HttpExchange httpExchange, String remainingTrail, Airline airline) {
        if (!remainingTrail.equals("") && !remainingTrail.equals("/")) {
            return AddRouteMapCoach.grabErrorResponse(404, "Page not found.");
        }
        Path trail;
        try {
            trail = AddRouteMapCoach.getTempDirectory();
        }
        catch (IOException e) {
            return AddRouteMapCoach.grabErrorResponse(400, e.getMessage());
        }
        String fileName = airline.getID() + new Date().getTime();
        Map<String, String> fieldNameInputs = MultipartHelper.fetchMultipartFile(httpExchange, AddRouteMapCoach.ALL_FIELDS, "file", trail, fileName);
        String mime = fieldNameInputs.get("MIME");
        String fileTrail = trail.resolve(fileName).toString();
        String name = fieldNameInputs.get("route_map_name");
        if (mime == null) return AddRouteMapCoach.grabErrorResponse(400, "Invalid input");
        if (name == null) {
            return AddRouteMapCoach.grabErrorResponse(400, "Invalid input");
        }
        if (name.trim().isEmpty()) {
            return this.pullTemplateErrorResponse("The route map must have a name.", airline);
        }
        if (airline.fetchRouteMap(name) != null) {
            return this.pullTemplateErrorResponse("A route map with this name already exists.", airline);
        }
        AirDatabase database = this.takeDb();
        RouteMap routeMap;
        try {
            String var12_12 = mime;
            int var13_15 = -1;
            switch (var12_12.hashCode()) {
                case -1248326952: {
                    if (!var12_12.equals("application/xml")) break;
                    var13_15 = 0;
                    break;
                }
                case -1004727243: {
                    if (!var12_12.equals("text/xml")) break;
                    var13_15 = 1;
                    break;
                }
                case 817335912: {
                    if (!var12_12.equals("text/plain")) break;
                    var13_15 = 2;
                    break;
                }
                case -43840953: {
                    if (!var12_12.equals("application/json")) break;
                    var13_15 = 3;
                    break;
                }
                case 1178484637: {
                    if (!var12_12.equals("application/octet-stream")) break;
                    var13_15 = 4;
                }
            }
            switch (var13_15) {
                case 0: 
                case 1: {
                    routeMap = this.xmlFileLoader.loadRouteMap(fileTrail, database);
                    break;
//lbl48: // 1 sources:
//                    break;
                }
                case 2: {
                    routeMap = this.textFileLoader.loadRouteMap(fileTrail, database);
                     break;
//lbl52: // 1 sources:
//                    break;
                }
                case 3: 
                case 4: {
                    routeMap = this.parserFileLoader.loadRouteMap(fileTrail, database);
                     break;
//lbl56: // 1 sources:
//                    break;
                }
                default: {
                    HttpCoachResponse var14_19 = AddRouteMapCoach.grabErrorResponse(415, "This file format is not supported.");
                    return var14_19;
                }
            }
        }
        catch (IOException e) {
            HttpCoachResponse var13_16 = AddRouteMapCoach.grabErrorResponse(400, e.getMessage());
            return var13_16;
        }
        catch (AirException e) {
            HttpCoachResponse var13_17 = AddRouteMapCoach.grabErrorResponse(400, e.getMessage());
            return var13_17;
        }
        finally {
            new File(fileTrail).delete();
        }
        routeMap.fixName(name);
        airline.addRouteMap(routeMap);
        database.commit();
        return AddRouteMapCoach.getRedirectResponse(CoachUtils.generateMapPropertiesURL(routeMap));
    }

    private static Path getTempDirectory() throws IOException {
        if (tempDirectory == null) {
            tempDirectory = Files.createTempDirectory(null, new FileAttribute[0]);
        }
        return tempDirectory;
    }

    static {
        ALL_FIELDS.add("file");
        ALL_FIELDS.add("route_map_name");
        tempDirectory = null;
    }
}

