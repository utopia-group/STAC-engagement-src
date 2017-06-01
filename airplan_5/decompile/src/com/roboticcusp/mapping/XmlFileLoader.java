/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFileLoader;
import com.roboticcusp.mapping.ChartLoader;
import com.roboticcusp.mapping.XmlChartCoach;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlFileLoader
implements ChartFileLoader {
    private static final String[] EXTENSIONS = new String[]{"xml"};

    public static void register() {
        ChartLoader.registerLoader(new XmlFileLoader());
    }

    @Override
    public Chart loadChart(String filename) throws FileNotFoundException, ChartException {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            XmlChartCoach coach = new XmlChartCoach();
            reader.setContentHandler(coach);
            reader.parse(new InputSource(filename));
            return coach.takeChart();
        }
        catch (SAXException e) {
            throw new ChartException(e);
        }
        catch (IOException e) {
            throw new ChartException(e);
        }
    }

    @Override
    public List<String> obtainExtensions() {
        return new ArrayList<String>(Arrays.asList(EXTENSIONS));
    }
}

