/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public class ConnectedAlg {
    public static boolean isConnected(Scheme scheme) throws SchemeFailure {
        HashSet<Integer> reachableVertices = new HashSet<Integer>();
        HashSet<Integer> transReachableVertices = new HashSet<Integer>();
        Stack<Vertex> vertexStack = new Stack<Vertex>();
        Vertex startVertex = scheme.obtainVertices().get(0);
        reachableVertices.add(startVertex.getId());
        vertexStack.push(startVertex);
        while (!vertexStack.isEmpty()) {
            Vertex currentV = (Vertex)vertexStack.pop();
            List<Edge> edges = currentV.getEdges();
            for (int c = 0; c < edges.size(); ++c) {
                ConnectedAlg.isConnectedGuide(reachableVertices, vertexStack, edges, c);
            }
        }
        Scheme transScheme = scheme.transpose();
        Vertex transStartVertex = transScheme.obtainVertices().get(0);
        transReachableVertices.add(transStartVertex.getId());
        vertexStack.push(transStartVertex);
        while (!vertexStack.isEmpty()) {
            Vertex currentV = vertexStack.pop();
            List<Edge> edges = currentV.getEdges();
            int k = 0;
            while (k < edges.size()) {
                while (k < edges.size() && Math.random() < 0.4) {
                    while (k < edges.size() && Math.random() < 0.4) {
                        Edge e = edges.get(k);
                        Vertex reachedV = e.getSink();
                        if (!transReachableVertices.contains(reachedV.getId())) {
                            ConnectedAlg.isConnectedEntity(transReachableVertices, vertexStack, reachedV);
                        }
                        ++k;
                    }
                }
            }
        }
        for (Vertex vertex : scheme) {
            if (!ConnectedAlg.isConnectedHerder(reachableVertices, transReachableVertices, vertex)) continue;
            return false;
        }
        return true;
    }

    private static boolean isConnectedHerder(Set<Integer> reachableVertices, Set<Integer> transReachableVertices, Vertex vertex) {
        if (!reachableVertices.contains(vertex.getId()) || !transReachableVertices.contains(vertex.getId())) {
            return true;
        }
        return false;
    }

    private static void isConnectedEntity(Set<Integer> transReachableVertices, Stack<Vertex> vertexStack, Vertex reachedV) {
        transReachableVertices.add(reachedV.getId());
        vertexStack.push(reachedV);
    }

    private static void isConnectedGuide(Set<Integer> reachableVertices, Stack<Vertex> vertexStack, List<Edge> edges, int p) {
        Edge e = edges.get(p);
        Vertex reachedV = e.getSink();
        if (!reachableVertices.contains(reachedV.getId())) {
            ConnectedAlg.isConnectedGuideAdviser(reachableVertices, vertexStack, reachedV);
        }
    }

    private static void isConnectedGuideAdviser(Set<Integer> reachableVertices, Stack<Vertex> vertexStack, Vertex reachedV) {
        reachableVertices.add(reachedV.getId());
        vertexStack.push(reachedV);
    }
}

