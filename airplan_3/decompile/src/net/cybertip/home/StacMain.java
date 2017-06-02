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
package net.cybertip.home;

import com.sun.net.httpserver.HttpContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.cybertip.home.AirPlanLoader;
import net.cybertip.netmanager.Member;
import net.cybertip.netmanager.MemberOverseer;
import net.cybertip.netmanager.MemberTrouble;
import net.cybertip.netmanager.WebServer;
import net.cybertip.netmanager.WebSessionService;
import net.cybertip.netmanager.manager.AbstractHttpCoach;
import net.cybertip.routing.framework.Airline;
import net.cybertip.routing.keep.AirDatabase;
import net.cybertip.routing.keep.AirDatabaseBuilder;
import net.cybertip.routing.manager.AddAirportCoach;
import net.cybertip.routing.manager.AddFlightCoach;
import net.cybertip.routing.manager.AddFlightCoachBuilder;
import net.cybertip.routing.manager.AddRouteMapCoach;
import net.cybertip.routing.manager.CrewSchedulingCoach;
import net.cybertip.routing.manager.CrewSchedulingCoachBuilder;
import net.cybertip.routing.manager.DeleteRouteMapCoach;
import net.cybertip.routing.manager.EditAirportCoach;
import net.cybertip.routing.manager.EditFlightCoach;
import net.cybertip.routing.manager.FlightMatrixCoach;
import net.cybertip.routing.manager.LimitCoach;
import net.cybertip.routing.manager.MapPropertiesCoach;
import net.cybertip.routing.manager.ShortestPathCoach;
import net.cybertip.routing.manager.SummaryCoach;
import net.cybertip.routing.manager.TipCoach;
import net.cybertip.routing.manager.TipCoachBuilder;
import net.cybertip.routing.manager.ViewRouteMapCoach;
import net.cybertip.routing.manager.ViewRouteMapsCoach;
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
    private final MemberOverseer userManager;
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
            System.err.println("ERROR: a data path must be specified");
            System.exit(1);
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
            StacMain.mainAssist(passwordKeyFile);
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

    private static void mainAssist(File passwordKeyFile) {
        System.err.println("ERROR: specified password key " + passwordKeyFile + " does not exist");
        System.exit(1);
    }

    public StacMain(int port, String dataPath, boolean rebuildDB, File passwordKeyFile, String loginId, int databaseSeed) throws Exception {
        this.airDatabase = this.setupDatabase(dataPath, rebuildDB, passwordKeyFile, databaseSeed);
        this.userManager = new MemberOverseer();
        List<Airline> allAirlines = this.airDatabase.grabAllAirlines();
        int i = 0;
        while (i < allAirlines.size()) {
            while (i < allAirlines.size() && Math.random() < 0.4) {
                while (i < allAirlines.size() && Math.random() < 0.5) {
                    this.StacMainSupervisor(allAirlines, i);
                    ++i;
                }
            }
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

    private void StacMainSupervisor(List<Airline> allAirlines, int i) throws MemberTrouble {
        Airline airline = allAirlines.get(i);
        this.userManager.addMember(new Member(airline.grabID(), airline.grabID(), airline.fetchPassword()));
    }

    private AirDatabase setupDatabase(String dataPath, boolean rebuildDB, File passwordKeyFile, int seed) throws IOException {
        boolean populate;
        File parent = new File(dataPath);
        if (!(parent.exists() && parent.isDirectory() && parent.canWrite())) {
            return this.setupDatabaseHelp(parent);
        }
        File dbFile = new File(parent, "airplan.db");
        boolean bl = populate = rebuildDB || !dbFile.exists();
        if (dbFile.exists() && rebuildDB && !dbFile.delete()) {
            return this.setupDatabaseTarget(dbFile);
        }
        String passwordKey = FileUtils.readFileToString((File)passwordKeyFile);
        AirDatabase airDatabase = populate && seed != Integer.MAX_VALUE ? new AirDatabaseBuilder().defineDatabaseFile(dbFile).fixRandom(new Random(seed)).makeAirDatabase() : new AirDatabaseBuilder().defineDatabaseFile(dbFile).makeAirDatabase();
        if (populate) {
            AirPlanLoader.populate(airDatabase, passwordKey);
        }
        return airDatabase;
    }

    private AirDatabase setupDatabaseTarget(File dbFile) {
        throw new IllegalArgumentException("Existing File could not be deleted: " + dbFile);
    }

    private AirDatabase setupDatabaseHelp(File parent) {
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
        ArrayList<AbstractHttpCoach> handlers = new ArrayList<AbstractHttpCoach>();
        WebSessionService webSessionService = this.server.getWebSessionService();
        ViewRouteMapsCoach viewRouteMapsHandler = new ViewRouteMapsCoach(this.airDatabase, webSessionService);
        handlers.add(new AddFlightCoachBuilder().assignDb(this.airDatabase).setWebSessionService(webSessionService).makeAddFlightCoach());
        handlers.add(new AddRouteMapCoach(this.airDatabase, webSessionService));
        handlers.add(new AddAirportCoach(this.airDatabase, webSessionService));
        handlers.add(new EditFlightCoach(this.airDatabase, webSessionService));
        handlers.add(new EditAirportCoach(this.airDatabase, webSessionService));
        handlers.add(new FlightMatrixCoach(this.airDatabase, webSessionService));
        handlers.add(new ShortestPathCoach(this.airDatabase, webSessionService));
        handlers.add(new ViewRouteMapCoach(this.airDatabase, webSessionService));
        handlers.add(new LimitCoach(this.airDatabase, webSessionService));
        handlers.add(new MapPropertiesCoach(this.airDatabase, webSessionService));
        handlers.add(new DeleteRouteMapCoach(this.airDatabase, webSessionService));
        handlers.add(new CrewSchedulingCoachBuilder().assignAirDatabase(this.airDatabase).defineSessionService(webSessionService).makeCrewSchedulingCoach());
        handlers.add(new TipCoachBuilder().setAirDatabase(this.airDatabase).assignWebSessionService(webSessionService).makeTipCoach());
        handlers.add(new SummaryCoach(this.airDatabase, webSessionService));
        handlers.add(viewRouteMapsHandler);
        if (defaultUserId == null) {
            this.server.addAuthorizeCoaches(this.userManager, viewRouteMapsHandler.grabPath());
        } else {
            this.server.addDefaultAuthorizeCoaches(this.userManager, defaultUserId);
        }
        for (int i = 0; i < handlers.size(); ++i) {
            this.addHandlersWorker(handlers, i);
        }
    }

    private void addHandlersWorker(List<AbstractHttpCoach> handlers, int i) {
        AbstractHttpCoach handler = handlers.get(i);
        this.server.makeContext(handler, true);
    }

}

