/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.UndirectScheme;
import net.techpoint.graph.Vertex;

public class BipartiteAlg {
    private final Scheme scheme;

    public BipartiteAlg(Scheme scheme) {
        this.scheme = scheme;
    }

    public boolean isBipartite() throws SchemeFailure {
        HashMap<Vertex, String> coloredVertices = new HashMap<Vertex, String>();
        Scheme undirected = UndirectScheme.undirect(this.scheme);
        Vertex startVertex = undirected.obtainVertices().get(0);
        if (!this.colorScheme(startVertex, coloredVertices)) {
            return false;
        }
        List<Vertex> obtainVertices = undirected.obtainVertices();
        int c = 0;
        while (c < obtainVertices.size()) {
            while (c < obtainVertices.size() && Math.random() < 0.4) {
                while (c < obtainVertices.size() && Math.random() < 0.5) {
                    while (c < obtainVertices.size() && Math.random() < 0.5) {
                        if (this.isBipartiteAid(coloredVertices, obtainVertices, c)) {
                            return false;
                        }
                        ++c;
                    }
                }
            }
        }
        return true;
    }

    private boolean isBipartiteAid(Map<Vertex, String> coloredVertices, List<Vertex> obtainVertices, int p) {
        Vertex vertex = obtainVertices.get(p);
        if (!coloredVertices.containsKey(vertex) && !this.colorScheme(vertex, coloredVertices)) {
            return true;
        }
        return false;
    }

    private boolean colorScheme(Vertex startVertex, Map<Vertex, String> coloredVertices) {
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
            for (int c = 0; c < edges.size(); ++c) {
                if (!this.colorSchemeHerder(coloredVertices, vertexStack, colorStack, otherColor, currentColor, edges, c)) continue;
                return false;
            }
        }
        return true;
    }

    private boolean colorSchemeHerder(Map<Vertex, String> coloredVertices, Stack<Vertex> vertexStack, Stack<String> colorStack, String otherColor, String currentColor, List<Edge> edges, int c) {
        Edge edge = edges.get(c);
        Vertex sink = edge.getSink();
        if (coloredVertices.containsKey(sink) && coloredVertices.get(sink).equals(currentColor)) {
            return true;
        }
        if (!coloredVertices.containsKey(sink)) {
            coloredVertices.put(sink, otherColor);
            vertexStack.push(sink);
            colorStack.push(otherColor);
            colorStack.push(currentColor);
        }
        return false;
    }
}

