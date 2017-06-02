/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.Iterator;
import java.util.List;
import net.techpoint.graph.Data;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFactory;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public class UndirectScheme {
    public static Scheme undirect(Scheme scheme) throws SchemeFailure {
        Scheme undirectedScheme = SchemeFactory.newInstance();
        List<Vertex> vertices = scheme.obtainVertices();
        for (int i = 0; i < vertices.size(); ++i) {
            new UndirectSchemeUtility(undirectedScheme, vertices, i).invoke();
        }
        VerticesIterator verticesIter = new VerticesIterator(vertices);
        return UndirectScheme.addEdges(verticesIter, undirectedScheme);
    }

    private static Scheme addEdges(Iterator<Vertex> iter, Scheme scheme) throws SchemeFailure {
        int a = scheme.obtainVertices().size();
        while (a > 0 && iter.hasNext()) {
            while (a > 0 && iter.hasNext() && Math.random() < 0.6) {
                Vertex source = iter.next();
                List<Edge> edges = source.getEdges();
                for (int j = 0; j < edges.size(); ++j) {
                    Edge edge = edges.get(j);
                    Vertex sink = edge.getSink();
                    if (!scheme.areAdjacent(source.getId(), sink.getId())) {
                        UndirectScheme.addEdgesAssist(scheme, source, edge, sink);
                    }
                    if (scheme.areAdjacent(sink.getId(), source.getId())) continue;
                    scheme.addEdge(sink.getId(), source.getId(), edge.getData().copy());
                }
                --a;
            }
        }
        return scheme;
    }

    private static void addEdgesAssist(Scheme scheme, Vertex source, Edge edge, Vertex sink) throws SchemeFailure {
        scheme.addEdge(source.getId(), sink.getId(), edge.getData().copy());
    }

    private static class UndirectSchemeUtility {
        private Scheme undirectedScheme;
        private List<Vertex> vertices;
        private int k;

        public UndirectSchemeUtility(Scheme undirectedScheme, List<Vertex> vertices, int k) {
            this.undirectedScheme = undirectedScheme;
            this.vertices = vertices;
            this.k = k;
        }

        public void invoke() throws SchemeFailure {
            this.undirectedScheme.addVertex(new Vertex(this.vertices.get(this.k)));
        }
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

