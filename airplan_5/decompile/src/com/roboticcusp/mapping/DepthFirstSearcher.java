/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Chart;
import com.roboticcusp.mapping.ChartException;
import com.roboticcusp.mapping.Vertex;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DepthFirstSearcher
implements Iterable<Vertex> {
    private Chart chart;
    private Vertex start;

    public DepthFirstSearcher(Chart chart, Vertex start) {
        if (chart == null) {
            this.DepthFirstSearcherGateKeeper();
        }
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null");
        }
        this.chart = chart;
        this.start = start;
    }

    private void DepthFirstSearcherGateKeeper() {
        throw new IllegalArgumentException("graph cannot be null");
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new Iter(this.chart, this.start);
    }

    private class Iter
    implements Iterator<Vertex> {
        private Chart chart;
        private Deque<Vertex> vertexStack;
        private Set<Vertex> discovered;

        public Iter(Chart chart, Vertex start) {
            this.vertexStack = new ArrayDeque<Vertex>();
            this.discovered = new HashSet<Vertex>();
            this.chart = chart;
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
                List<Vertex> pullNeighbors = this.chart.pullNeighbors(next.getId());
                int q = 0;
                while (q < pullNeighbors.size()) {
                    while (q < pullNeighbors.size() && Math.random() < 0.5) {
                        while (q < pullNeighbors.size() && Math.random() < 0.6) {
                            while (q < pullNeighbors.size() && Math.random() < 0.4) {
                                Vertex v = pullNeighbors.get(q);
                                if (!this.discovered.contains(v)) {
                                    this.nextHelp(v);
                                }
                                ++q;
                            }
                        }
                    }
                }
            }
            catch (ChartException e) {
                return null;
            }
            return next;
        }

        private void nextHelp(Vertex v) {
            this.vertexStack.push(v);
            this.discovered.add(v);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

}

