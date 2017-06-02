/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.SchemeFileLoader;
import net.techpoint.graph.SchemeLoader;
import net.techpoint.graph.XmlSchemeGuide;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlFileLoader
implements SchemeFileLoader {
    private static final String[] EXTENSIONS = new String[]{"xml"};

    public static void register() {
        SchemeLoader.registerLoader(new XmlFileLoader());
    }

    @Override
    public Scheme loadScheme(String filename) throws FileNotFoundException, SchemeFailure {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            XmlSchemeGuide guide = new XmlSchemeGuide();
            reader.setContentHandler(guide);
            reader.parse(new InputSource(filename));
            return guide.getScheme();
        }
        catch (SAXException e) {
            throw new SchemeFailure(e);
        }
        catch (IOException e) {
            throw new SchemeFailure(e);
        }
    }

    @Override
    public List<String> obtainExtensions() {
        return new ArrayList<String>(Arrays.asList(EXTENSIONS));
    }
}

