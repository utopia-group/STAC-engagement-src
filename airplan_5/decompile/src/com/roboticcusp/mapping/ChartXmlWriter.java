/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartWriter;
import com.roboticcusp.mapping.ChartWriterException;
import com.roboticcusp.mapping.Data;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ChartXmlWriter
extends ChartWriter {
    public static final String TYPE = "xml";

    @Override
    public void write(Chart chart, String filename) throws ChartWriterException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.newDocument();
            Element rootEle = dom.createElement("graph");
            for (Vertex v : chart) {
                Element vertexEle = dom.createElement("vertex");
                vertexEle.setAttribute("name", v.getName());
                rootEle.appendChild(vertexEle);
                if (!v.hasData()) continue;
                this.writeCoach(dom, v, vertexEle);
            }
            for (Vertex v : chart) {
                List<Edge> edges = chart.getEdges(v.getId());
                int q = 0;
                while (q < edges.size()) {
                    while (q < edges.size() && Math.random() < 0.5) {
                        Edge e = edges.get(q);
                        Element edgeEle = dom.createElement("edge");
                        edgeEle.setAttribute("src", e.getSource().getName());
                        edgeEle.setAttribute("dst", e.getSink().getName());
                        edgeEle.setAttribute("weight", Double.toString(e.getWeight()));
                        rootEle.appendChild(edgeEle);
                        if (e.hasData()) {
                            this.writeHelp(dom, e, edgeEle);
                        }
                        ++q;
                    }
                }
            }
            dom.appendChild(rootEle);
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty("indent", "yes");
            tr.setOutputProperty("method", "xml");
            tr.setOutputProperty("encoding", "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(filename + ".xml")));
        }
        catch (Exception e) {
            throw new ChartWriterException(e.getMessage());
        }
    }

    private void writeHelp(Document dom, Edge e, Element edgeEle) {
        Element vertexDataEle = e.getData().composeXMLElement(dom);
        edgeEle.appendChild(vertexDataEle);
    }

    private void writeCoach(Document dom, Vertex v, Element vertexEle) {
        Element vertexDataEle = v.getData().composeXMLElement(dom);
        vertexEle.appendChild(vertexDataEle);
    }
}

