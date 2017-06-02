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

public class DepthFirstSearcher
implements Iterable<Vertex> {
    private Scheme scheme;
    private Vertex start;

    public DepthFirstSearcher(Scheme scheme, Vertex start) {
        if (scheme == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (start == null) {
            this.DepthFirstSearcherHelp();
        }
        this.scheme = scheme;
        this.start = start;
    }

    private void DepthFirstSearcherHelp() {
        throw new IllegalArgumentException("start cannot be null");
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new Iter(this.scheme, this.start);
    }

    private class Iter
    implements Iterator<Vertex> {
        private Scheme scheme;
        private Deque<Vertex> vertexStack;
        private Set<Vertex> discovered;

        public Iter(Scheme scheme, Vertex start) {
            this.vertexStack = new ArrayDeque<Vertex>();
            this.discovered = new HashSet<Vertex>();
            this.scheme = scheme;
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
                List<Vertex> grabNeighbors = this.scheme.grabNeighbors(next.getId());
                for (int i = 0; i < grabNeighbors.size(); ++i) {
                    Vertex v = grabNeighbors.get(i);
                    if (this.discovered.contains(v)) continue;
                    this.vertexStack.push(v);
                    this.discovered.add(v);
                }
            }
            catch (SchemeFailure e) {
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

