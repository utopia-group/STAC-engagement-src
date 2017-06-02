/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.flightrouter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.techpoint.flightrouter.AirFailure;
import net.techpoint.flightrouter.RouteMapLoader;
import net.techpoint.flightrouter.XmlRouteMapGuide;
import net.techpoint.flightrouter.XmlRouteMapGuideBuilder;
import net.techpoint.flightrouter.keep.AirDatabase;
import net.techpoint.flightrouter.prototype.RouteMap;
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
    public RouteMap loadRouteMap(String fileName, AirDatabase database) throws FileNotFoundException, AirFailure {
        try {
            File file = new File(fileName);
            if (file.length() > 30720) {
                return this.loadRouteMapGuide();
            }
            XMLReader reader = XMLReaderFactory.createXMLReader();
            XmlRouteMapGuide guide = new XmlRouteMapGuideBuilder().fixDatabase(database).formXmlRouteMapGuide();
            reader.setContentHandler(guide);
            reader.parse(new InputSource(fileName));
            return guide.takeRouteMap();
        }
        catch (SAXException e) {
            throw new AirFailure(e);
        }
        catch (IOException e) {
            throw new AirFailure(e);
        }
    }

    private RouteMap loadRouteMapGuide() throws AirFailure {
        throw new AirFailure("This route map is too large for the system.");
    }

    @Override
    public List<String> takeExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}

