/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.Set;
import net.techpoint.graph.BasicData;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSchemeGuide
extends DefaultHandler {
    private Scheme scheme;
    private String curVertexName;
    private Data curData;
    private DataElement curDataEntry;
    private EdgeElement curEdge;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("graph")) {
            if (this.scheme != null) {
                throw new SAXException("We can't handle nested graphs");
            }
            this.scheme = SchemeFactory.newInstance();
        } else if (localName.equals("vertex")) {
            if (this.curVertexName != null) {
                throw new SAXException("We can't handle nested vertices");
            }
            if (this.scheme == null) {
                this.startElementHerder();
            }
            this.curVertexName = atts.getValue("name");
        } else if (localName.equals("data")) {
            this.startElementGateKeeper();
        } else if (localName.equals("entry")) {
            this.startElementEngine(atts);
        } else if (localName.equals("edge")) {
            if (this.curEdge != null) {
                this.startElementUtility();
            }
            if (this.scheme == null) {
                this.startElementGuide();
            }
            this.curEdge = new EdgeElement(atts);
        }
    }

    private void startElementGuide() throws SAXException {
        throw new SAXException("Graph must be specified before an Edge");
    }

    private void startElementUtility() throws SAXException {
        throw new SAXException("We can't handle nested edges");
    }

    private void startElementEngine(Attributes atts) throws SAXException {
        if (this.curData == null) {
            throw new SAXException("Entry must be a child of a <data> element");
        }
        if (this.curDataEntry != null) {
            this.startElementEngineFunction();
        }
        this.curDataEntry = new DataElement(atts);
    }

    private void startElementEngineFunction() throws SAXException {
        new XmlSchemeGuideHome().invoke();
    }

    private void startElementGateKeeper() throws SAXException {
        new XmlSchemeGuideUtility().invoke();
    }

    private void startElementHerder() throws SAXException {
        throw new SAXException("Graph must be specified before a Vertex");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.curDataEntry != null) {
            this.charactersExecutor(ch, start, length);
        }
    }

    private void charactersExecutor(char[] ch, int start, int length) {
        this.curDataEntry.addCharacters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("vertex")) {
            this.endElementEngine();
        } else if (localName.equals("entry")) {
            this.endElementHome();
        } else if (localName.equals("edge")) {
            try {
                int sourceVertex = this.scheme.getVertexIdByName(this.curEdge.takeSrc());
                int sinkVertex = this.scheme.getVertexIdByName(this.curEdge.getDst());
                Data data = XmlSchemeGuide.fetchData(this.curEdge.grabData(), this.curData);
                this.scheme.addEdge(sourceVertex, sinkVertex, data);
                this.curData = null;
            }
            catch (SchemeFailure e) {
                throw new SAXException(e);
            }
            this.curEdge = null;
        }
    }

    private void endElementHome() {
        this.curData.place(this.curDataEntry.fetchKey(), this.curDataEntry.pullValue());
        this.curDataEntry = null;
    }

    private void endElementEngine() throws SAXException {
        new XmlSchemeGuideHelper().invoke();
    }

    public Scheme getScheme() {
        return this.scheme;
    }

    private static Data fetchData(Data edgeData, Data entryData) {
        Data data = entryData;
        if (entryData == null) {
            data = edgeData != null ? edgeData : new BasicData();
        } else if (edgeData != null) {
            XmlSchemeGuide.fetchDataHelp(edgeData, data);
        }
        return data;
    }

    private static void fetchDataHelp(Data edgeData, Data data) {
        for (String key : edgeData.keyAssign()) {
            XmlSchemeGuide.grabDataHelpTarget(edgeData, data, key);
        }
    }

    private static void grabDataHelpTarget(Data edgeData, Data data, String key) {
        data.place(key, edgeData.obtain(key));
    }

    private class XmlSchemeGuideHelper {
        private XmlSchemeGuideHelper() {
        }

        public void invoke() throws SAXException {
            try {
                Vertex vertex = XmlSchemeGuide.this.scheme.addVertex(XmlSchemeGuide.this.curVertexName);
                if (XmlSchemeGuide.this.curData != null) {
                    vertex.setData(XmlSchemeGuide.this.curData);
                    XmlSchemeGuide.this.curData = null;
                }
            }
            catch (SchemeFailure e) {
                throw new SAXException(e);
            }
            XmlSchemeGuide.this.curVertexName = null;
        }
    }

    private class XmlSchemeGuideHome {
        private XmlSchemeGuideHome() {
        }

        public void invoke() throws SAXException {
            throw new SAXException("Entry tags may not be nested");
        }
    }

    private class XmlSchemeGuideUtility {
        private XmlSchemeGuideUtility() {
        }

        public void invoke() throws SAXException {
            if (XmlSchemeGuide.this.curVertexName == null && XmlSchemeGuide.this.curEdge == null) {
                throw new SAXException("Invalid <data> element; must be a child of vertex or edge");
            }
            XmlSchemeGuide.this.curData = new BasicData();
        }
    }

    private static class DataElement {
        private String key;
        private StringBuffer value = new StringBuffer();

        public DataElement(Attributes atts) {
            this.key = atts.getValue("key");
        }

        public String fetchKey() {
            return this.key;
        }

        public void addCharacters(char[] ch, int start, int length) {
            this.value.append(ch, start, length);
        }

        public String pullValue() {
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

        public String takeSrc() {
            return this.src;
        }

        public String getDst() {
            return this.dst;
        }

        public Data grabData() {
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

