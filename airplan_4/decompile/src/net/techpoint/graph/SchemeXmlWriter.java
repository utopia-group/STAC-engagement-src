/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

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
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeWriter;
import net.techpoint.graph.SchemeWriterFailure;
import net.techpoint.graph.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SchemeXmlWriter
extends SchemeWriter {
    public static final String TYPE = "xml";

    @Override
    public void write(Scheme scheme, String filename) throws SchemeWriterFailure {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.newDocument();
            Element rootEle = dom.createElement("graph");
            for (Vertex v : scheme) {
                this.writeTarget(dom, rootEle, v);
            }
            for (Vertex v : scheme) {
                List<Edge> pullEdges = scheme.pullEdges(v.getId());
                for (int j = 0; j < pullEdges.size(); ++j) {
                    this.writeHelp(dom, rootEle, pullEdges, j);
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
            throw new SchemeWriterFailure(e.getMessage());
        }
    }

    private void writeHelp(Document dom, Element rootEle, List<Edge> pullEdges, int p) {
        Edge e = pullEdges.get(p);
        Element edgeEle = dom.createElement("edge");
        edgeEle.setAttribute("src", e.getSource().getName());
        edgeEle.setAttribute("dst", e.getSink().getName());
        edgeEle.setAttribute("weight", Double.toString(e.getWeight()));
        rootEle.appendChild(edgeEle);
        if (e.hasData()) {
            Element vertexDataEle = e.getData().formXMLElement(dom);
            edgeEle.appendChild(vertexDataEle);
        }
    }

    private void writeTarget(Document dom, Element rootEle, Vertex v) {
        Element vertexEle = dom.createElement("vertex");
        vertexEle.setAttribute("name", v.getName());
        rootEle.appendChild(vertexEle);
        if (v.hasData()) {
            this.writeTargetGuide(dom, v, vertexEle);
        }
    }

    private void writeTargetGuide(Document dom, Vertex v, Element vertexEle) {
        Element vertexDataEle = v.getData().formXMLElement(dom);
        vertexEle.appendChild(vertexDataEle);
    }
}

