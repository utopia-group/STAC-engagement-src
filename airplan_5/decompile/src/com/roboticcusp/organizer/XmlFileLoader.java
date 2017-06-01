/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.organizer;

import com.roboticcusp.kxml2.io.KXmlGrabber;
import com.roboticcusp.organizer.AirException;
import com.roboticcusp.organizer.RouteMapLoader;
import com.roboticcusp.organizer.XmlRouteMapCoach;
import com.roboticcusp.organizer.framework.RouteMap;
import com.roboticcusp.organizer.save.AirDatabase;
import com.roboticcusp.xmlpull.v1.XmlPullGrabber;
import com.roboticcusp.xmlpull.v1.XmlPullGrabberException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class XmlFileLoader
implements RouteMapLoader {
    private static final int MAX_FILE_LENGTH = 30720;
    private static final String[] EXTENSIONS = new String[]{"xml"};

    @Override
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirException {
        try {
            File file = new File(fileName);
            if (file.length() > 30720) {
                return this.loadRouteMapGuide();
            }
            XmlRouteMapCoach coach = new XmlRouteMapCoach(database);
            KXmlGrabber xpp = new KXmlGrabber();
            xpp.fixFeature("http://xmlpull.org/v1/doc/features.html#process-docdecl", true);
            xpp.setInput(new FileReader(file));
            int eventType = xpp.obtainEventType();
            while (eventType != 1) {
                if (eventType == 2) {
                    coach.startElement(xpp.grabName(), xpp);
                }
                eventType = xpp.next();
            }
            return coach.pullRouteMap();
        }
        catch (XmlPullGrabberException e) {
            throw new AirException(e);
        }
        catch (IOException e) {
            throw new AirException(e);
        }
    }

    private RouteMap loadRouteMapGuide() throws AirException {
        throw new AirException("This route map is too large for the system.");
    }

    @Override
    public List<String> fetchExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}

