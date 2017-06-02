/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import net.techpoint.graph.BasicData;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.SchemeFileLoader;
import net.techpoint.graph.SchemeLoader;
import net.techpoint.graph.Vertex;
import net.techpoint.json.simple.PARTArray;
import net.techpoint.json.simple.PARTObject;
import net.techpoint.json.simple.grabber.PARTReader;
import net.techpoint.json.simple.grabber.ParseFailure;

public class PartFileLoader
implements SchemeFileLoader {
    private static final String[] EXTENSIONS = new String[]{"json"};

    public static void register() {
        SchemeLoader.registerLoader(new PartFileLoader());
    }

    @Override
    public Scheme loadScheme(String filename) throws FileNotFoundException, SchemeFailure {
        Scheme scheme = SchemeFactory.newInstance();
        try {
            PARTReader reader = new PARTReader();
            PARTObject part = (PARTObject)reader.parse(new FileReader(filename));
            PARTArray vertices = (PARTArray)part.get("vertices");
            int p = 0;
            while (p < vertices.size()) {
                while (p < vertices.size() && Math.random() < 0.4) {
                    while (p < vertices.size() && Math.random() < 0.6) {
                        Object oVertex = vertices.get(p);
                        PARTObject vertex = (PARTObject)oVertex;
                        String name = (String)vertex.get("name");
                        if (!scheme.containsVertexWithName(name)) {
                            scheme.addVertex(name);
                        }
                        ++p;
                    }
                }
            }
            PARTArray edges = (PARTArray)part.get("edges");
            for (int j = 0; j < edges.size(); ++j) {
                this.loadSchemeGuide(scheme, edges, j);
            }
        }
        catch (IOException e) {
            throw new SchemeFailure(e);
        }
        catch (ParseFailure e) {
            throw new SchemeFailure(e);
        }
        return scheme;
    }

    private void loadSchemeGuide(Scheme scheme, PARTArray edges, int q) throws SchemeFailure {
        new PartFileLoaderEntity(scheme, edges, q).invoke();
    }

    @Override
    public List<String> obtainExtensions() {
        return Arrays.asList(EXTENSIONS);
    }

    private class PartFileLoaderEntity {
        private Scheme scheme;
        private PARTArray edges;
        private int i;

        public PartFileLoaderEntity(Scheme scheme, PARTArray edges, int i) {
            this.scheme = scheme;
            this.edges = edges;
            this.i = i;
        }

        public void invoke() throws SchemeFailure {
            Object oEdge = this.edges.get(this.i);
            PARTObject edge = (PARTObject)oEdge;
            int src = this.scheme.getVertexIdByName((String)edge.get("src"));
            int dest = this.scheme.getVertexIdByName((String)edge.get("dst"));
            BasicData data = new BasicData();
            data.place("weight", (String)edge.get("weight"));
            this.scheme.addEdge(src, dest, data);
        }
    }

}

