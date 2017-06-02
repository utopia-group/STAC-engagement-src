/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public class BreadthFirstSearcher
implements Iterable<Vertex> {
    private Scheme scheme;
    private Vertex start;

    public BreadthFirstSearcher(Scheme scheme, Vertex start) {
        if (scheme == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null");
        }
        this.scheme = scheme;
        this.start = start;
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new Iter(this.scheme, this.start);
    }

    private class Iter
    implements Iterator<Vertex> {
        private Scheme scheme;
        private Deque<Vertex> vertexQueue;
        private Set<Vertex> discovered;

        public Iter(Scheme scheme, Vertex start) {
            this.vertexQueue = new ArrayDeque<Vertex>();
            this.discovered = new HashSet<Vertex>();
            this.scheme = scheme;
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
                List<Vertex> grabNeighbors = this.scheme.grabNeighbors(next.getId());
                for (int b = 0; b < grabNeighbors.size(); ++b) {
                    this.nextGateKeeper(grabNeighbors, b);
                }
            }
            catch (SchemeFailure e) {
                return null;
            }
            return next;
        }

        private void nextGateKeeper(List<Vertex> grabNeighbors, int q) {
            Vertex v = grabNeighbors.get(q);
            if (!this.discovered.contains(v)) {
                this.vertexQueue.addLast(v);
                this.discovered.add(v);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

}

