/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphFileLoader;
import net.cybertip.scheme.GraphLoader;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.XmlGraphCoach;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlFileLoader
implements GraphFileLoader {
    private static final String[] EXTENSIONS = new String[]{"xml"};

    public static void register() {
        GraphLoader.registerLoader(new XmlFileLoader());
    }

    @Override
    public Graph loadGraph(String filename) throws FileNotFoundException, GraphTrouble {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            XmlGraphCoach coach = new XmlGraphCoach();
            reader.setContentHandler(coach);
            reader.parse(new InputSource(filename));
            return coach.getGraph();
        }
        catch (SAXException e) {
            throw new GraphTrouble(e);
        }
        catch (IOException e) {
            throw new GraphTrouble(e);
        }
    }

    @Override
    public List<String> fetchExtensions() {
        return new ArrayList<String>(Arrays.asList(EXTENSIONS));
    }
}

