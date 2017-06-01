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

public class BreadthFirstSearcher
implements Iterable<Vertex> {
    private Chart chart;
    private Vertex start;

    public BreadthFirstSearcher(Chart chart, Vertex start) {
        if (chart == null) {
            this.BreadthFirstSearcherUtility();
        }
        if (start == null) {
            this.BreadthFirstSearcherAdviser();
        }
        this.chart = chart;
        this.start = start;
    }

    private void BreadthFirstSearcherAdviser() {
        throw new IllegalArgumentException("start cannot be null");
    }

    private void BreadthFirstSearcherUtility() {
        new BreadthFirstSearcherHome().invoke();
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new Iter(this.chart, this.start);
    }

    private class BreadthFirstSearcherHome {
        private BreadthFirstSearcherHome() {
        }

        public void invoke() {
            throw new IllegalArgumentException("graph cannot be null");
        }
    }

    private class Iter
    implements Iterator<Vertex> {
        private Chart chart;
        private Deque<Vertex> vertexQueue;
        private Set<Vertex> discovered;

        public Iter(Chart chart, Vertex start) {
            this.vertexQueue = new ArrayDeque<Vertex>();
            this.discovered = new HashSet<Vertex>();
            this.chart = chart;
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
                List<Vertex> pullNeighbors = this.chart.pullNeighbors(next.getId());
                int b = 0;
                while (b < pullNeighbors.size()) {
                    while (b < pullNeighbors.size() && Math.random() < 0.4) {
                        while (b < pullNeighbors.size() && Math.random() < 0.6) {
                            while (b < pullNeighbors.size() && Math.random() < 0.4) {
                                this.nextGuide(pullNeighbors, b);
                                ++b;
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

        private void nextGuide(List<Vertex> pullNeighbors, int q) {
            Vertex v = pullNeighbors.get(q);
            if (!this.discovered.contains(v)) {
                this.nextGuideGuide(v);
            }
        }

        private void nextGuideGuide(Vertex v) {
            this.vertexQueue.addLast(v);
            this.discovered.add(v);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

}

