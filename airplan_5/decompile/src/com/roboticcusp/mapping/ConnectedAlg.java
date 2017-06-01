/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.Edge;
import com.roboticcusp.mapping.Vertex;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ConnectedAlg {
    public static boolean isConnected(Chart chart) throws ChartException {
        HashSet<Integer> reachableVertices = new HashSet<Integer>();
        HashSet<Integer> transReachableVertices = new HashSet<Integer>();
        Stack<Vertex> vertexStack = new Stack<Vertex>();
        Vertex startVertex = chart.obtainVertices().get(0);
        reachableVertices.add(startVertex.getId());
        vertexStack.push(startVertex);
        while (!vertexStack.isEmpty()) {
            Vertex currentV = (Vertex)vertexStack.pop();
            List<Edge> edges = currentV.getEdges();
            for (int k = 0; k < edges.size(); ++k) {
                Edge e = edges.get(k);
                Vertex reachedV = e.getSink();
                if (reachableVertices.contains(reachedV.getId())) continue;
                reachableVertices.add(reachedV.getId());
                vertexStack.push(reachedV);
            }
        }
        Chart transChart = chart.transpose();
        Vertex transStartVertex = transChart.obtainVertices().get(0);
        transReachableVertices.add(transStartVertex.getId());
        vertexStack.push(transStartVertex);
        while (!vertexStack.isEmpty()) {
            Vertex currentV = (Vertex)vertexStack.pop();
            List<Edge> edges = currentV.getEdges();
            for (int q = 0; q < edges.size(); ++q) {
                ConnectedAlg.isConnectedService(transReachableVertices, vertexStack, edges, q);
            }
        }
        for (Vertex vertex : chart) {
            if (!ConnectedAlg.isConnectedGuide(reachableVertices, transReachableVertices, vertex)) continue;
            return false;
        }
        return true;
    }

    private static boolean isConnectedGuide(Set<Integer> reachableVertices, Set<Integer> transReachableVertices, Vertex vertex) {
        if (!reachableVertices.contains(vertex.getId()) || !transReachableVertices.contains(vertex.getId())) {
            return true;
        }
        return false;
    }

    private static void isConnectedService(Set<Integer> transReachableVertices, Stack<Vertex> vertexStack, List<Edge> edges, int q) {
        Edge e = edges.get(q);
        Vertex reachedV = e.getSink();
        if (!transReachableVertices.contains(reachedV.getId())) {
            new ConnectedAlgWorker(transReachableVertices, vertexStack, reachedV).invoke();
        }
    }

    private static class ConnectedAlgWorker {
        private Set<Integer> transReachableVertices;
        private Stack<Vertex> vertexStack;
        private Vertex reachedV;

        public ConnectedAlgWorker(Set<Integer> transReachableVertices, Stack<Vertex> vertexStack, Vertex reachedV) {
            this.transReachableVertices = transReachableVertices;
            this.vertexStack = vertexStack;
            this.reachedV = reachedV;
        }

        public void invoke() {
            this.transReachableVertices.add(this.reachedV.getId());
            this.vertexStack.push(this.reachedV);
        }
    }

}

