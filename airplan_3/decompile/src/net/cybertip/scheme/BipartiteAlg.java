/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.UndirectGraph;
import net.cybertip.scheme.Vertex;

public class BipartiteAlg {
    private final Graph graph;

    public BipartiteAlg(Graph graph) {
        this.graph = graph;
    }

    public boolean isBipartite() throws GraphTrouble {
        HashMap<Vertex, String> coloredVertices = new HashMap<Vertex, String>();
        Graph undirected = UndirectGraph.undirect(this.graph);
        Vertex startVertex = undirected.grabVertices().get(0);
        if (!this.colorGraph(startVertex, coloredVertices)) {
            return false;
        }
        List<Vertex> grabVertices = undirected.grabVertices();
        for (int i = 0; i < grabVertices.size(); ++i) {
            Vertex vertex = grabVertices.get(i);
            if (coloredVertices.containsKey(vertex) || this.colorGraph(vertex, coloredVertices)) continue;
            return false;
        }
        return true;
    }

    private boolean colorGraph(Vertex startVertex, Map<Vertex, String> coloredVertices) {
        Stack<Vertex> vertexStack = new Stack<Vertex>();
        Stack<String> colorStack = new Stack<String>();
        coloredVertices.put(startVertex, "red");
        vertexStack.push(startVertex);
        colorStack.push("red");
        colorStack.push("blue");
        while (!vertexStack.empty()) {
            String otherColor = (String)colorStack.pop();
            String currentColor = (String)colorStack.pop();
            Vertex vertex = (Vertex)vertexStack.pop();
            List<Edge> edges = vertex.getEdges();
            for (int p = 0; p < edges.size(); ++p) {
                Edge edge = edges.get(p);
                Vertex sink = edge.getSink();
                if (coloredVertices.containsKey(sink) && coloredVertices.get(sink).equals(currentColor)) {
                    return false;
                }
                if (coloredVertices.containsKey(sink)) continue;
                coloredVertices.put(sink, otherColor);
                vertexStack.push(sink);
                colorStack.push(otherColor);
                colorStack.push(currentColor);
            }
        }
        return true;
    }
}

