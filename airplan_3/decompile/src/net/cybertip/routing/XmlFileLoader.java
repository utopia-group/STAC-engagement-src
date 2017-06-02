/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.cybertip.routing.AirTrouble;
import net.cybertip.routing.RouteMapLoader;
import net.cybertip.routing.XmlRouteMapCoach;
import net.cybertip.routing.framework.RouteMap;
import net.cybertip.routing.keep.AirDatabase;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlFileLoader
implements RouteMapLoader {
    private static final int MAX_FILE_LENGTH = 30720;
    private static final String[] EXTENSIONS = new String[]{"xml"};

    @Override
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirTrouble {
        try {
            File file = new File(fileName);
            if (file.length() > 30720) {
                throw new AirTrouble("This route map is too large for the system.");
            }
            XMLReader reader = XMLReaderFactory.createXMLReader();
            XmlRouteMapCoach coach = new XmlRouteMapCoach(database);
            reader.setContentHandler(coach);
            reader.parse(new InputSource(fileName));
            return coach.pullRouteMap();
        }
        catch (SAXException e) {
            throw new AirTrouble(e);
        }
        catch (IOException e) {
            throw new AirTrouble(e);
        }
    }

    @Override
    public List<String> takeExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}

