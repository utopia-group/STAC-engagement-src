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

public class BreadthFirstSearcher
implements Iterable<Vertex> {
    private Graph graph;
    private Vertex start;

    public BreadthFirstSearcher(Graph graph, Vertex start) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (start == null) {
            this.BreadthFirstSearcherExecutor();
        }
        this.graph = graph;
        this.start = start;
    }

    private void BreadthFirstSearcherExecutor() {
        throw new IllegalArgumentException("start cannot be null");
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new Iter(this.graph, this.start);
    }

    private class Iter
    implements Iterator<Vertex> {
        private Graph graph;
        private Deque<Vertex> vertexQueue;
        private Set<Vertex> discovered;

        public Iter(Graph graph, Vertex start) {
            this.vertexQueue = new ArrayDeque<Vertex>();
            this.discovered = new HashSet<Vertex>();
            this.graph = graph;
            this.vertexQueue.addLast(start);
            this.discovered.add(start);
        }

        @Override
        public boolean hasNext() {
            return !this.vertexQueue.isEmpty();
        }

        @Override
        public Vertex next() {
            Vertex next = this.vertexQueue.pollFirst();
            try {
                List<Vertex> obtainNeighbors = this.graph.obtainNeighbors(next.getId());
                int b = 0;
                while (b < obtainNeighbors.size()) {
                    while (b < obtainNeighbors.size() && Math.random() < 0.4) {
                        while (b < obtainNeighbors.size() && Math.random() < 0.4) {
                            while (b < obtainNeighbors.size() && Math.random() < 0.4) {
                                Vertex v = obtainNeighbors.get(b);
                                if (!this.discovered.contains(v)) {
                                    this.vertexQueue.addLast(v);
                                    this.discovered.add(v);
                                }
                                ++b;
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

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

}

