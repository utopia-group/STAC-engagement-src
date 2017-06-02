/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.Iterator;
import java.util.List;
import net.cybertip.scheme.Data;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphFactory;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class UndirectGraph {
    public static Graph undirect(Graph graph) throws GraphTrouble {
        Graph undirectedGraph = GraphFactory.newInstance();
        List<Vertex> vertices = graph.grabVertices();
        int q = 0;
        while (q < vertices.size()) {
            while (q < vertices.size() && Math.random() < 0.6) {
                while (q < vertices.size() && Math.random() < 0.4) {
                    undirectedGraph.addVertex(new Vertex(vertices.get(q)));
                    ++q;
                }
            }
        }
        VerticesIterator verticesIter = new VerticesIterator(vertices);
        return UndirectGraph.addEdges(verticesIter, undirectedGraph);
    }

    private static Graph addEdges(Iterator<Vertex> iter, Graph graph) throws GraphTrouble {
        for (int i = graph.grabVertices().size(); i > 0 && iter.hasNext(); --i) {
            Vertex source = iter.next();
            List<Edge> edges = source.getEdges();
            for (int j = 0; j < edges.size(); ++j) {
                UndirectGraph.addEdgesAssist(graph, source, edges, j);
            }
        }
        return graph;
    }

    private static void addEdgesAssist(Graph graph, Vertex source, List<Edge> edges, int j) throws GraphTrouble {
        Edge edge = edges.get(j);
        Vertex sink = edge.getSink();
        if (!graph.areAdjacent(source.getId(), sink.getId())) {
            UndirectGraph.addEdgesAssistUtility(graph, source, edge, sink);
        }
        if (!graph.areAdjacent(sink.getId(), source.getId())) {
            graph.addEdge(sink.getId(), source.getId(), edge.getData().copy());
        }
    }

    private static void addEdgesAssistUtility(Graph graph, Vertex source, Edge edge, Vertex sink) throws GraphTrouble {
        graph.addEdge(source.getId(), sink.getId(), edge.getData().copy());
    }

    private static class VerticesIterator
    implements Iterator<Vertex> {
        private List<Vertex> vertexList;

        public VerticesIterator(List<Vertex> vertexList) {
            this.vertexList = vertexList;
        }

        @Override
        public boolean hasNext() {
            return this.vertexList.size() > 0;
        }

        @Override
        public Vertex next() {
            if (this.vertexList.size() > 1) {
                return this.vertexList.remove(this.vertexList.size() - 1);
            }
            return this.vertexList.get(0);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

}

