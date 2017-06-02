/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.Set;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphFactory;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlGraphCoach
extends DefaultHandler {
    private Graph graph;
    private String curVertexName;
    private Data curData;
    private DataElement curDataEntry;
    private EdgeElement curEdge;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("graph")) {
            if (this.graph != null) {
                throw new SAXException("We can't handle nested graphs");
            }
            this.graph = GraphFactory.newInstance();
        } else if (localName.equals("vertex")) {
            this.startElementEntity(atts);
        } else if (localName.equals("data")) {
            if (this.curVertexName == null && this.curEdge == null) {
                this.startElementAdviser();
            }
            this.curData = new BasicData();
        } else if (localName.equals("entry")) {
            if (this.curData == null) {
                throw new SAXException("Entry must be a child of a <data> element");
            }
            if (this.curDataEntry != null) {
                new XmlGraphCoachEngine().invoke();
            }
            this.curDataEntry = new DataElement(atts);
        } else if (localName.equals("edge")) {
            if (this.curEdge != null) {
                this.startElementEngine();
            }
            if (this.graph == null) {
                throw new SAXException("Graph must be specified before an Edge");
            }
            this.curEdge = new EdgeElement(atts);
        }
    }

    private void startElementEngine() throws SAXException {
        throw new SAXException("We can't handle nested edges");
    }

    private void startElementAdviser() throws SAXException {
        new XmlGraphCoachAid().invoke();
    }

    private void startElementEntity(Attributes atts) throws SAXException {
        if (this.curVertexName != null) {
            throw new SAXException("We can't handle nested vertices");
        }
        if (this.graph == null) {
            this.startElementEntityEngine();
        }
        this.curVertexName = atts.getValue("name");
    }

    private void startElementEntityEngine() throws SAXException {
        throw new SAXException("Graph must be specified before a Vertex");
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
            try {
                Vertex vertex = this.graph.addVertex(this.curVertexName);
                if (this.curData != null) {
                    this.endElementEngine(vertex);
                }
            }
            catch (GraphTrouble e) {
                throw new SAXException(e);
            }
            this.curVertexName = null;
        } else if (localName.equals("entry")) {
            this.endElementAssist();
        } else if (localName.equals("edge")) {
            this.endElementAdviser();
        }
    }

    private void endElementAdviser() throws SAXException {
        try {
            int sourceVertex = this.graph.fetchVertexIdByName(this.curEdge.pullSrc());
            int sinkVertex = this.graph.fetchVertexIdByName(this.curEdge.grabDst());
            Data data = XmlGraphCoach.grabData(this.curEdge.obtainData(), this.curData);
            this.graph.addEdge(sourceVertex, sinkVertex, data);
            this.curData = null;
        }
        catch (GraphTrouble e) {
            throw new SAXException(e);
        }
        this.curEdge = null;
    }

    private void endElementAssist() {
        new XmlGraphCoachGateKeeper().invoke();
    }

    private void endElementEngine(Vertex vertex) {
        vertex.setData(this.curData);
        this.curData = null;
    }

    public Graph getGraph() {
        return this.graph;
    }

    private static Data grabData(Data edgeData, Data entryData) {
        Data data = entryData;
        if (entryData == null) {
            data = edgeData != null ? edgeData : new BasicData();
        } else if (edgeData != null) {
            XmlGraphCoach.obtainDataCoordinator(edgeData, data);
        }
        return data;
    }

    private static void obtainDataCoordinator(Data edgeData, Data data) {
        for (String key : edgeData.keySet()) {
            data.place(key, edgeData.pull(key));
        }
    }

    private class XmlGraphCoachGateKeeper {
        private XmlGraphCoachGateKeeper() {
        }

        public void invoke() {
            XmlGraphCoach.this.curData.place(XmlGraphCoach.this.curDataEntry.takeKey(), XmlGraphCoach.this.curDataEntry.takeValue());
            XmlGraphCoach.this.curDataEntry = null;
        }
    }

    private class XmlGraphCoachEngine {
        private XmlGraphCoachEngine() {
        }

        public void invoke() throws SAXException {
            throw new SAXException("Entry tags may not be nested");
        }
    }

    private class XmlGraphCoachAid {
        private XmlGraphCoachAid() {
        }

        public void invoke() throws SAXException {
            throw new SAXException("Invalid <data> element; must be a child of vertex or edge");
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

        public String takeValue() {
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

        public String grabDst() {
            return this.dst;
        }

        public Data obtainData() {
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

