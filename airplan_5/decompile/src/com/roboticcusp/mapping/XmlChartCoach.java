/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.BasicData;
import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.ChartFactory;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlChartCoach
extends DefaultHandler {
    private Chart chart;
    private String curVertexName;
    private Data curData;
    private DataElement curDataEntry;
    private EdgeElement curEdge;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("graph")) {
            if (this.chart != null) {
                this.startElementCoordinator();
            }
            this.chart = ChartFactory.newInstance();
        } else if (localName.equals("vertex")) {
            if (this.curVertexName != null) {
                throw new SAXException("We can't handle nested vertices");
            }
            if (this.chart == null) {
                this.startElementGateKeeper();
            }
            this.curVertexName = atts.getValue("name");
        } else if (localName.equals("data")) {
            if (this.curVertexName == null && this.curEdge == null) {
                throw new SAXException("Invalid <data> element; must be a child of vertex or edge");
            }
            this.curData = new BasicData();
        } else if (localName.equals("entry")) {
            if (this.curData == null) {
                this.startElementAdviser();
            }
            if (this.curDataEntry != null) {
                this.startElementUtility();
            }
            this.curDataEntry = new DataElement(atts);
        } else if (localName.equals("edge")) {
            this.startElementExecutor(atts);
        }
    }

    private void startElementExecutor(Attributes atts) throws SAXException {
        if (this.curEdge != null) {
            throw new SAXException("We can't handle nested edges");
        }
        if (this.chart == null) {
            throw new SAXException("Graph must be specified before an Edge");
        }
        this.curEdge = new EdgeElement(atts);
    }

    private void startElementUtility() throws SAXException {
        throw new SAXException("Entry tags may not be nested");
    }

    private void startElementAdviser() throws SAXException {
        throw new SAXException("Entry must be a child of a <data> element");
    }

    private void startElementGateKeeper() throws SAXException {
        throw new SAXException("Graph must be specified before a Vertex");
    }

    private void startElementCoordinator() throws SAXException {
        throw new SAXException("We can't handle nested graphs");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.curDataEntry != null) {
            this.curDataEntry.addCharacters(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("vertex")) {
            this.endElementEngine();
        } else if (localName.equals("entry")) {
            this.curData.put(this.curDataEntry.takeKey(), this.curDataEntry.grabValue());
            this.curDataEntry = null;
        } else if (localName.equals("edge")) {
            try {
                int sourceVertex = this.chart.obtainVertexIdByName(this.curEdge.pullSrc());
                int sinkVertex = this.chart.obtainVertexIdByName(this.curEdge.fetchDst());
                Data data = XmlChartCoach.obtainData(this.curEdge.getData(), this.curData);
                this.chart.addEdge(sourceVertex, sinkVertex, data);
                this.curData = null;
            }
            catch (ChartException e) {
                throw new SAXException(e);
            }
            this.curEdge = null;
        }
    }

    private void endElementEngine() throws SAXException {
        try {
            Vertex vertex = this.chart.addVertex(this.curVertexName);
            if (this.curData != null) {
                this.endElementEngineAid(vertex);
            }
        }
        catch (ChartException e) {
            throw new SAXException(e);
        }
        this.curVertexName = null;
    }

    private void endElementEngineAid(Vertex vertex) {
        vertex.setData(this.curData);
        this.curData = null;
    }

    public Chart takeChart() {
        return this.chart;
    }

    private static Data obtainData(Data edgeData, Data entryData) {
        Data data = entryData;
        if (entryData == null) {
            data = edgeData != null ? edgeData : new BasicData();
        } else if (edgeData != null) {
            XmlChartCoach.fetchDataHelp(edgeData, data);
        }
        return data;
    }

    private static void fetchDataHelp(Data edgeData, Data data) {
        for (String key : edgeData.keyDefine()) {
            data.put(key, edgeData.grab(key));
        }
    }

    private static class DataElement {
        private String key;
        private StringBuffer value = new StringBuffer();

        public DataElement(Attributes atts) {
            this.key = atts.getValue("key");
        }

        public String takeKey() {
            return this.key;
        }

        public void addCharacters(char[] ch, int start, int length) {
            this.value.append(ch, start, length);
        }

        public String grabValue() {
            return this.value.toString();
        }
    }

    private static class EdgeElement {
        private String src;
        private String dst;
        private String weight;

        EdgeElement(Attributes atts) {
            this.src = atts.getValue("src");
            this.dst = atts.getValue("dst");
            this.weight = atts.getValue("weight");
        }

        public String pullSrc() {
            return this.src;
        }

        public String fetchDst() {
            return this.dst;
        }

        public Data getData() {
            BasicData data = null;
            if (this.weight != null) {
                try {
                    double value = Double.parseDouble(this.weight);
                    data = new BasicData(value);
                }
                catch (NumberFormatException value) {
                    // empty catch block
                }
            }
            return data != null ? data : new BasicData();
        }
    }

}

