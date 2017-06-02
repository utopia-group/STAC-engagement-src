/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import net.cybertip.parsing.simple.JACKArray;
import net.cybertip.parsing.simple.JACKObject;
import net.cybertip.parsing.simple.retriever.JACKExtractor;
import net.cybertip.parsing.simple.retriever.ParseTrouble;
import net.cybertip.scheme.BasicData;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphFactory;
import net.cybertip.scheme.GraphFileLoader;
import net.cybertip.scheme.GraphLoader;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class JackFileLoader
implements GraphFileLoader {
    private static final String[] EXTENSIONS = new String[]{"json"};

    public static void register() {
        GraphLoader.registerLoader(new JackFileLoader());
    }

    @Override
    public Graph loadGraph(String filename) throws FileNotFoundException, GraphTrouble {
        Graph graph = GraphFactory.newInstance();
        try {
            JACKExtractor extractor = new JACKExtractor();
            JACKObject jack = (JACKObject)extractor.parse(new FileReader(filename));
            JACKArray vertices = (JACKArray)jack.get("vertices");
            for (int i = 0; i < vertices.size(); ++i) {
                this.loadGraphHerder(graph, vertices, i);
            }
            JACKArray edges = (JACKArray)jack.get("edges");
            for (int c = 0; c < edges.size(); ++c) {
                Object oEdge = edges.get(c);
                JACKObject edge = (JACKObject)oEdge;
                int src = graph.fetchVertexIdByName((String)edge.get("src"));
                int dest = graph.fetchVertexIdByName((String)edge.get("dst"));
                BasicData data = new BasicData();
                data.place("weight", (String)edge.get("weight"));
                graph.addEdge(src, dest, data);
            }
        }
        catch (IOException e) {
            throw new GraphTrouble(e);
        }
        catch (ParseTrouble e) {
            throw new GraphTrouble(e);
        }
        return graph;
    }

    private void loadGraphHerder(Graph graph, JACKArray vertices, int j) throws GraphTrouble {
        Object oVertex = vertices.get(j);
        JACKObject vertex = (JACKObject)oVertex;
        String name = (String)vertex.get("name");
        if (!graph.containsVertexWithName(name)) {
            graph.addVertex(name);
        }
    }

    @Override
    public List<String> fetchExtensions() {
        return Arrays.asList(EXTENSIONS);
    }
}

