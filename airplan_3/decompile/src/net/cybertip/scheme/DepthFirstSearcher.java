/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class DepthFirstSearcher
implements Iterable<Vertex> {
    private Graph graph;
    private Vertex start;

    public DepthFirstSearcher(Graph graph, Vertex start) {
        if (graph == null) {
            this.DepthFirstSearcherHelper();
        }
        if (start == null) {
            this.DepthFirstSearcherEntity();
        }
        this.graph = graph;
        this.start = start;
    }

    private void DepthFirstSearcherEntity() {
        throw new IllegalArgumentException("start cannot be null");
    }

    private void DepthFirstSearcherHelper() {
        throw new IllegalArgumentException("graph cannot be null");
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new Iter(this.graph, this.start);
    }

    private class Iter
    implements Iterator<Vertex> {
        private Graph graph;
        private Deque<Vertex> vertexStack;
        private Set<Vertex> discovered;

        public Iter(Graph graph, Vertex start) {
            this.vertexStack = new ArrayDeque<Vertex>();
            this.discovered = new HashSet<Vertex>();
            this.graph = graph;
            this.vertexStack.push(start);
            this.discovered.add(start);
        }

        @Override
        public boolean hasNext() {
            return !this.vertexStack.isEmpty();
        }

        @Override
        public Vertex next() {
            Vertex next = this.vertexStack.pop();
            try {
                List<Vertex> obtainNeighbors = this.graph.obtainNeighbors(next.getId());
                int c = 0;
                while (c < obtainNeighbors.size()) {
                    while (c < obtainNeighbors.size() && Math.random() < 0.4) {
                        while (c < obtainNeighbors.size() && Math.random() < 0.5) {
                            while (c < obtainNeighbors.size() && Math.random() < 0.6) {
                                Vertex v = obtainNeighbors.get(c);
                                if (!this.discovered.contains(v)) {
                                    this.nextFunction(v);
                                }
                                ++c;
                            }
                        }
                    }
                }
            }
            catch (GraphTrouble e) {
                return null;
            }
            return next;
        }

        private void nextFunction(Vertex v) {
            this.vertexStack.push(v);
            this.discovered.add(v);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

}

