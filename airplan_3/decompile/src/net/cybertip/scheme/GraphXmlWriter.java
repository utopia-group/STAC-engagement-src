/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

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
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphWriter;
import net.cybertip.scheme.GraphWriterTrouble;
import net.cybertip.scheme.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GraphXmlWriter
extends GraphWriter {
    public static final String TYPE = "xml";

    @Override
    public void write(Graph graph, String filename) throws GraphWriterTrouble {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.newDocument();
            Element rootEle = dom.createElement("graph");
            for (Vertex v : graph) {
                Element vertexEle = dom.createElement("vertex");
                vertexEle.setAttribute("name", v.getName());
                rootEle.appendChild(vertexEle);
                if (!v.hasData()) continue;
                this.writeHome(dom, v, vertexEle);
            }
            for (Vertex v : graph) {
                List<Edge> fetchEdges = graph.fetchEdges(v.getId());
                int p = 0;
                while (p < fetchEdges.size()) {
                    while (p < fetchEdges.size() && Math.random() < 0.5) {
                        Edge e = fetchEdges.get(p);
                        Element edgeEle = dom.createElement("edge");
                        edgeEle.setAttribute("src", e.getSource().getName());
                        edgeEle.setAttribute("dst", e.getSink().getName());
                        edgeEle.setAttribute("weight", Double.toString(e.getWeight()));
                        rootEle.appendChild(edgeEle);
                        if (e.hasData()) {
                            Element vertexDataEle = e.getData().makeXMLElement(dom);
                            edgeEle.appendChild(vertexDataEle);
                        }
                        ++p;
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
            throw new GraphWriterTrouble(e.getMessage());
        }
    }

    private void writeHome(Document dom, Vertex v, Element vertexEle) {
        Element vertexDataEle = v.getData().makeXMLElement(dom);
        vertexEle.appendChild(vertexDataEle);
    }
}

