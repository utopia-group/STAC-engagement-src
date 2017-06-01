/*
 * Decompiled with CFR 0_121.
 */
package edu.cyberapex.home;

import edu.cyberapex.DESHelper;
import edu.cyberapex.flightplanner.framework.Airline;
import edu.cyberapex.flightplanner.store.AirDatabase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AirPlanLoader {
    private static final String AIRLINE_RESOURCE = "airplan_airlines.csv";

    public static void populate(AirDatabase database, String passwordKey) throws IOException {
        Map<String, Airline> airlines;
        InputStream inputStream = AirPlanLoader.class.getResourceAsStream("airplan_airlines.csv");
        Throwable throwable = null;
        try {
            airlines = AirPlanLoader.obtainAirlines(inputStream, database, passwordKey);
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
        for (Airline airline : airlines.values()) {
            new AirPlanLoaderHelp(database, airline).invoke();
        }
    }

    private static Map<String, Airline> obtainAirlines(InputStream inputStream, AirDatabase database, String passwordKey) throws IOException {
        HashMap<String, Airline> airlines;
        airlines = new HashMap<String, Airline>();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        Throwable throwable = null;
        try {
            String line;
            while ((line = br.readLine()) != null) {
                AirPlanLoader.grabAirlinesExecutor(database, passwordKey, airlines, line);
            }
        }
        catch (Throwable x2) {
            throwable = x2;
            throw x2;
        }
        finally {
            if (br != null) {
                if (throwable != null) {
                    try {
                        br.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    br.close();
                }
            }
        }
        return airlines;
    }

    private static void grabAirlinesExecutor(AirDatabase database, String passwordKey, Map<String, Airline> airlines, String line) {
        String[] parts = line.split(",", 3);
        String id = parts[0];
        String airlineName = parts[1];
        String password = parts[2];
        String encryptedPw = DESHelper.obtainEncryptedString(password, passwordKey);
        Airline airline = new Airline(database, id, airlineName, encryptedPw);
        airlines.put(id, airline);
    }

    private static class AirPlanLoaderHelp {
        private AirDatabase database;
        private Airline airline;

        public AirPlanLoaderHelp(AirDatabase database, Airline airline) {
            this.database = database;
            this.airline = airline;
        }

        public void invoke() {
            this.database.addOrUpdateAirline(this.airline);
        }
    }

}

