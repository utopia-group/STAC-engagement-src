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
package net.techpoint.place;

import com.sun.net.httpserver.HttpContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.manager.AddAirportGuide;
import net.techpoint.flightrouter.manager.AddFlightGuide;
import net.techpoint.flightrouter.manager.AddRouteMapGuide;
import net.techpoint.flightrouter.manager.BestTrailGuide;
import net.techpoint.flightrouter.manager.BestTrailGuideBuilder;
import net.techpoint.flightrouter.manager.CrewSchedulingGuide;
import net.techpoint.flightrouter.manager.DeleteRouteMapGuide;
import net.techpoint.flightrouter.manager.EditAirportGuide;
import net.techpoint.flightrouter.manager.EditFlightGuide;
import net.techpoint.flightrouter.manager.FlightMatrixGuide;
import net.techpoint.flightrouter.manager.LimitGuide;
import net.techpoint.flightrouter.manager.MapPropertiesGuide;
import net.techpoint.flightrouter.manager.MapPropertiesGuideBuilder;
import net.techpoint.flightrouter.manager.SummaryGuide;
import net.techpoint.flightrouter.manager.TipGuide;
import net.techpoint.flightrouter.manager.ViewRouteMapGuide;
import net.techpoint.flightrouter.manager.ViewRouteMapsGuide;
import net.techpoint.flightrouter.manager.ViewRouteMapsGuideBuilder;
import net.techpoint.flightrouter.prototype.Airline;
import net.techpoint.place.AirPlanLoader;
import net.techpoint.server.User;
import net.techpoint.server.UserFailure;
import net.techpoint.server.UserManager;
import net.techpoint.server.WebServer;
import net.techpoint.server.WebSessionService;
import net.techpoint.server.manager.AbstractHttpGuide;
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
    private final UserManager userManager;
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
                StacMain.mainAdviser(options);
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
            System.err.println("ERROR: a data path must be specified");
            System.exit(1);
        }
        if (!(dataPathFile = new File(dataPath)).exists() || !dataPathFile.isDirectory()) {
            StacMain.mainEngine(dataPath);
        }
        if (passwordKeyPath == null) {
            StacMainHelper.invoke();
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

    private static void mainEngine(String dataPath) {
        System.err.println("ERROR: specified datapath " + dataPath + " does not exist or is not a directory");
        System.exit(1);
    }

    private static void mainAdviser(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("AirPlan <options>", options);
        System.exit(0);
    }

    public StacMain(int port, String dataPath, boolean rebuildDB, File passwordKeyFile, String loginId, int databaseSeed) throws Exception {
        this.airDatabase = this.setupDatabase(dataPath, rebuildDB, passwordKeyFile, databaseSeed);
        this.userManager = new UserManager();
        List<Airline> allAirlines = this.airDatabase.takeAllAirlines();
        for (int i = 0; i < allAirlines.size(); ++i) {
            this.StacMainEngine(allAirlines, i);
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

    private void StacMainEngine(List<Airline> allAirlines, int i) throws UserFailure {
        new StacMainWorker(allAirlines, i).invoke();
    }

    private AirDatabase setupDatabase(String dataPath, boolean rebuildDB, File passwordKeyFile, int seed) throws IOException {
        boolean populate;
        File parent = new File(dataPath);
        if (!(parent.exists() && parent.isDirectory() && parent.canWrite())) {
            return this.setupDatabaseSupervisor(parent);
        }
        File dbFile = new File(parent, "airplan.db");
        boolean bl = populate = rebuildDB || !dbFile.exists();
        if (dbFile.exists() && rebuildDB && !dbFile.delete()) {
            throw new IllegalArgumentException("Existing File could not be deleted: " + dbFile);
        }
        String passwordKey = FileUtils.readFileToString((File)passwordKeyFile);
        AirDatabase airDatabase = populate && seed != Integer.MAX_VALUE ? new AirDatabase(dbFile, new Random(seed)) : new AirDatabase(dbFile);
        if (populate) {
            this.setupDatabaseService(passwordKey, airDatabase);
        }
        return airDatabase;
    }

    private void setupDatabaseService(String passwordKey, AirDatabase airDatabase) throws IOException {
        AirPlanLoader.populate(airDatabase, passwordKey);
    }

    private AirDatabase setupDatabaseSupervisor(File parent) {
        throw new IllegalArgumentException("Parent directory " + parent + " must exist, be a directory, and be writable");
    }

    public void start() {
        this.server.start();
    }

    public void stop() {
        this.server.stop(0);
        this.airDatabase.close();
    }

    private void addHandlers(String defaultUserId) throws IOException {
        ArrayList<AbstractHttpGuide> handlers = new ArrayList<AbstractHttpGuide>();
        WebSessionService webSessionService = this.server.getWebSessionService();
        ViewRouteMapsGuide viewRouteMapsHandler = new ViewRouteMapsGuideBuilder().setDb(this.airDatabase).fixWebSessionService(webSessionService).formViewRouteMapsGuide();
        handlers.add(new AddFlightGuide(this.airDatabase, webSessionService));
        handlers.add(new AddRouteMapGuide(this.airDatabase, webSessionService));
        handlers.add(new AddAirportGuide(this.airDatabase, webSessionService));
        handlers.add(new EditFlightGuide(this.airDatabase, webSessionService));
        handlers.add(new EditAirportGuide(this.airDatabase, webSessionService));
        handlers.add(new FlightMatrixGuide(this.airDatabase, webSessionService));
        handlers.add(new BestTrailGuideBuilder().setDb(this.airDatabase).fixWebSessionService(webSessionService).formBestTrailGuide());
        handlers.add(new ViewRouteMapGuide(this.airDatabase, webSessionService));
        handlers.add(new LimitGuide(this.airDatabase, webSessionService));
        handlers.add(new MapPropertiesGuideBuilder().fixDatabase(this.airDatabase).assignWebSessionService(webSessionService).formMapPropertiesGuide());
        handlers.add(new DeleteRouteMapGuide(this.airDatabase, webSessionService));
        handlers.add(new CrewSchedulingGuide(this.airDatabase, webSessionService));
        handlers.add(new TipGuide(this.airDatabase, webSessionService));
        handlers.add(new SummaryGuide(this.airDatabase, webSessionService));
        handlers.add(viewRouteMapsHandler);
        if (defaultUserId == null) {
            this.server.addPermissionGuides(this.userManager, viewRouteMapsHandler.obtainTrail());
        } else {
            this.server.addDefaultPermissionGuides(this.userManager, defaultUserId);
        }
        int i = 0;
        while (i < handlers.size()) {
            while (i < handlers.size() && Math.random() < 0.6) {
                while (i < handlers.size() && Math.random() < 0.6) {
                    while (i < handlers.size() && Math.random() < 0.5) {
                        this.addHandlersEngine(handlers, i);
                        ++i;
                    }
                }
            }
        }
    }

    private void addHandlersEngine(List<AbstractHttpGuide> handlers, int i) {
        AbstractHttpGuide handler = handlers.get(i);
        this.server.formContext(handler, true);
    }

    private class StacMainWorker {
        private List<Airline> allAirlines;
        private int i;

        public StacMainWorker(List<Airline> allAirlines, int i) {
            this.allAirlines = allAirlines;
            this.i = i;
        }

        public void invoke() throws UserFailure {
            Airline airline = this.allAirlines.get(this.i);
            StacMain.this.userManager.addUser(new User(airline.obtainID(), airline.obtainID(), airline.takePassword()));
        }
    }

    private static class StacMainHelper {
        private StacMainHelper() {
        }

        private static void invoke() {
            System.err.println("ERROR: a password key must be specified");
            System.exit(1);
        }
    }

}

