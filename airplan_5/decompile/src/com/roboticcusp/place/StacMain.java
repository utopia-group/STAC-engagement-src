/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.cli.CommandLine
 *  org.apache.commons.cli.DefaultParser
 *  org.apache.commons.cli.HelpFormatter
 *  org.apache.commons.cli.Option
 *  org.apache.commons.cli.Options
 *  org.apache.commons.cli.ParseException
 *  org.apache.commons.io.FileUtils
 */
package com.roboticcusp.place;

import com.roboticcusp.network.Participant;
import com.roboticcusp.network.ParticipantConductor;
import com.roboticcusp.network.ParticipantException;
import com.roboticcusp.network.WebServer;
import com.roboticcusp.network.WebSessionService;
import com.roboticcusp.network.coach.AbstractHttpCoach;
import com.roboticcusp.organizer.coach.AccommodationCoach;
import com.roboticcusp.organizer.coach.AddAirportCoach;
import com.roboticcusp.organizer.coach.AddAirportCoachBuilder;
import com.roboticcusp.organizer.coach.AddFlightCoach;
import com.roboticcusp.organizer.coach.AddRouteMapCoach;
import com.roboticcusp.organizer.coach.AirCoach;
import com.roboticcusp.organizer.coach.CrewSchedulingCoach;
import com.roboticcusp.organizer.coach.DeleteRouteMapCoach;
import com.roboticcusp.organizer.coach.EditAirportCoach;
import com.roboticcusp.organizer.coach.EditFlightCoach;
import com.roboticcusp.organizer.coach.FlightMatrixCoach;
import com.roboticcusp.organizer.coach.MapPropertiesCoach;
import com.roboticcusp.organizer.coach.ShortestTrailCoach;
import com.roboticcusp.organizer.coach.SummaryCoach;
import com.roboticcusp.organizer.coach.TipCoach;
import com.roboticcusp.organizer.coach.ViewRouteMapCoach;
import com.roboticcusp.organizer.coach.ViewRouteMapCoachBuilder;
import com.roboticcusp.organizer.coach.ViewRouteMapsCoach;
import com.roboticcusp.organizer.framework.Airline;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.place.AirPlanLoader;
import com.sun.net.httpserver.HttpContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

public class StacMain {
    private static final int DEFAULT_PORT = 8443;
    private static final String CONTEXT_RESOURCE = "/airplan.jks";
    private static final String CONTEXT_RESOURCE_PASSWORD = "airplan";
    private static final int SECONDS_TO_WAIT_TO_CLOSE = 0;
    private static final String MAPDB_FILE = "airplan.db";
    private static final int DEFAULT_DATABASE_SEED = Integer.MAX_VALUE;
    private final WebServer server;
    private final ParticipantConductor userManager;
    private final AirDatabase airDatabase;

    public static void main(String[] args) throws Exception {
        File dataPathFile;
        File passwordKeyFile;
        Options options = new Options();
        Option portOption = new Option("p", "port", true, "Specifies the port the server will use; defaults to 8443");
        portOption.setType(Integer.class);
        options.addOption(portOption);
        options.addOption("d", "datapath", true, "Path to the existing data storage directory");
        options.addOption("r", "rebuild", false, "Removes any existing persistence and reloads initial model data");
        options.addOption("w", "passwordkey", true, "File containing a key used to encrypt passwords");
        options.addOption("l", "loginid", true, "All connections will be automatically logged in as this user.");
        options.addOption("s", "seed", true, "Seed for the random id creation in the database.");
        options.addOption("h", false, "Display this help message");
        int port = 8443;
        String dataPath = null;
        boolean rebuildDB = false;
        String passwordKeyPath = null;
        String loginId = null;
        int seed = Integer.MAX_VALUE;
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("p")) {
                String optionValue = commandLine.getOptionValue("p");
                try {
                    port = Integer.valueOf(optionValue.trim());
                }
                catch (Exception e) {
                    System.err.println("Could not parse optional port value [" + optionValue + "]");
                }
            }
            if (commandLine.hasOption("d")) {
                dataPath = commandLine.getOptionValue("d");
            }
            if (commandLine.hasOption("r")) {
                rebuildDB = true;
            }
            if (commandLine.hasOption("w")) {
                passwordKeyPath = commandLine.getOptionValue("w");
            }
            if (commandLine.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("AirPlan <options>", options);
                System.exit(0);
            }
            if (commandLine.hasOption("l")) {
                loginId = commandLine.getOptionValue("l");
            }
            if (commandLine.hasOption("s")) {
                seed = Integer.parseInt(commandLine.getOptionValue("s"));
            }
        }
        catch (ParseException e) {
            System.err.println("Command line parsing failed.  Reason: " + e.getMessage());
            System.exit(1);
        }
        if (dataPath == null) {
            StacMainHerder.invoke();
        }
        if (!(dataPathFile = new File(dataPath)).exists() || !dataPathFile.isDirectory()) {
            System.err.println("ERROR: specified datapath " + dataPath + " does not exist or is not a directory");
            System.exit(1);
        }
        if (passwordKeyPath == null) {
            System.err.println("ERROR: a password key must be specified");
            System.exit(1);
        }
        if (!(passwordKeyFile = new File(passwordKeyPath)).exists()) {
            System.err.println("ERROR: specified password key " + passwordKeyFile + " does not exist");
            System.exit(1);
        }
        final StacMain main = new StacMain(port, dataPath, rebuildDB, passwordKeyFile, loginId, seed);
        main.start();
        System.out.println("Server started on port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){

            @Override
            public void run() {
                System.out.println("Stopping the server...");
                main.stop();
            }
        }));
    }

    public StacMain(int port, String dataPath, boolean rebuildDB, File passwordKeyFile, String loginId, int databaseSeed) throws Exception {
        this.airDatabase = this.setupDatabase(dataPath, rebuildDB, passwordKeyFile, databaseSeed);
        this.userManager = new ParticipantConductor();
        List<Airline> allAirlines = this.airDatabase.getAllAirlines();
        for (int i = 0; i < allAirlines.size(); ++i) {
            new StacMainSupervisor(allAirlines, i).invoke();
        }
        InputStream inputStream = this.getClass().getResourceAsStream("/airplan.jks");
        Throwable throwable = null;
        try {
            this.server = new WebServer("airplan", port, inputStream, "airplan", passwordKeyFile);
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (inputStream != null) {
                if (throwable != null) {
                    try {
                        inputStream.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    inputStream.close();
                }
            }
        }
        this.addHandlers(loginId);
    }

    private AirDatabase setupDatabase(String dataPath, boolean rebuildDB, File passwordKeyFile, int seed) throws IOException {
        boolean populate;
        File parent = new File(dataPath);
        if (!(parent.exists() && parent.isDirectory() && parent.canWrite())) {
            throw new IllegalArgumentException("Parent directory " + parent + " must exist, be a directory, and be writable");
        }
        File dbFile = new File(parent, "airplan.db");
        boolean bl = populate = rebuildDB || !dbFile.exists();
        if (dbFile.exists() && rebuildDB && !dbFile.delete()) {
            throw new IllegalArgumentException("Existing File could not be deleted: " + dbFile);
        }
        String passwordKey = FileUtils.readFileToString((File)passwordKeyFile);
        AirDatabase airDatabase = populate && seed != Integer.MAX_VALUE ? new AirDatabase(dbFile, new Random(seed)) : new AirDatabase(dbFile);
        if (populate) {
            AirPlanLoader.populate(airDatabase, passwordKey);
        }
        return airDatabase;
    }

    public void start() {
        this.server.start();
    }

    public void stop() {
        this.server.stop(0);
        this.airDatabase.close();
    }

    private void addHandlers(String defaultUserId) throws IOException {
        ArrayList<AirCoach> handlers = new ArrayList<AirCoach>();
        WebSessionService webSessionService = this.server.fetchWebSessionService();
        ViewRouteMapsCoach viewRouteMapsHandler = new ViewRouteMapsCoach(this.airDatabase, webSessionService);
        handlers.add(new AddFlightCoach(this.airDatabase, webSessionService));
        handlers.add(new AddRouteMapCoach(this.airDatabase, webSessionService));
        handlers.add(new AddAirportCoachBuilder().assignDb(this.airDatabase).defineWebSessionService(webSessionService).composeAddAirportCoach());
        handlers.add(new EditFlightCoach(this.airDatabase, webSessionService));
        handlers.add(new EditAirportCoach(this.airDatabase, webSessionService));
        handlers.add(new FlightMatrixCoach(this.airDatabase, webSessionService));
        handlers.add(new ShortestTrailCoach(this.airDatabase, webSessionService));
        handlers.add(new ViewRouteMapCoachBuilder().assignDb(this.airDatabase).assignWebSessionService(webSessionService).composeViewRouteMapCoach());
        handlers.add(new AccommodationCoach(this.airDatabase, webSessionService));
        handlers.add(new MapPropertiesCoach(this.airDatabase, webSessionService));
        handlers.add(new DeleteRouteMapCoach(this.airDatabase, webSessionService));
        handlers.add(new CrewSchedulingCoach(this.airDatabase, webSessionService));
        handlers.add(new TipCoach(this.airDatabase, webSessionService));
        handlers.add(new SummaryCoach(this.airDatabase, webSessionService));
        handlers.add(viewRouteMapsHandler);
        if (defaultUserId == null) {
            this.server.addAuthorizeCoaches(this.userManager, viewRouteMapsHandler.getTrail());
        } else {
            this.server.addDefaultAuthorizeCoaches(this.userManager, defaultUserId);
        }
        for (int i = 0; i < handlers.size(); ++i) {
            AbstractHttpCoach handler = (AbstractHttpCoach)handlers.get(i);
            this.server.composeContext(handler, true);
        }
    }

    private class StacMainSupervisor {
        private List<Airline> allAirlines;
        private int i;

        public StacMainSupervisor(List<Airline> allAirlines, int i) {
            this.allAirlines = allAirlines;
            this.i = i;
        }

        public void invoke() throws ParticipantException {
            Airline airline = this.allAirlines.get(this.i);
            StacMain.this.userManager.addParticipant(new Participant(airline.getID(), airline.getID(), airline.grabPassword()));
        }
    }

    private static class StacMainHerder {
        private StacMainHerder() {
        }

        private static void invoke() {
            System.err.println("ERROR: a data path must be specified");
            System.exit(1);
        }
    }

}

