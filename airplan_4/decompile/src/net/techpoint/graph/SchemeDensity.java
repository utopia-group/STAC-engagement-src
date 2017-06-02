/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.HashSet;
import java.util.List;
import net.techpoint.graph.Edge;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public class SchemeDensity {
    public static double computeDensity(Scheme g) throws SchemeFailure {
        int numSimpleEdges = SchemeDensity.countEdges(g);
        int numVertices = g.obtainVertices().size();
        if (numVertices == 0 || numVertices == 1) {
            return 1.0;
        }
        return (double)numSimpleEdges / (double)(numVertices * (numVertices - 1));
    }

    static int countEdges(Scheme g) throws SchemeFailure {
        int uniqueEdgeCount = 0;
        List<Vertex> obtainVertices = g.obtainVertices();
        for (int i1 = 0; i1 < obtainVertices.size(); ++i1) {
            Vertex source = obtainVertices.get(i1);
            HashSet<Vertex> sinks = new HashSet<Vertex>();
            List<Edge> pullEdges = g.pullEdges(source.getId());
            for (int p = 0; p < pullEdges.size(); ++p) {
                SchemeDensity.countEdgesWorker(sinks, pullEdges, p);
            }
            uniqueEdgeCount += sinks.size();
        }
        return uniqueEdgeCount;
    }

    private static void countEdgesWorker(HashSet<Vertex> sinks, List<Edge> pullEdges, int k) {
        Edge edge = pullEdges.get(k);
        sinks.add(edge.getSink());
    }

    public static Density describeDensity(double density) {
        return Density.fromDouble(density);
    }

    public static enum Density {
        HIGHLY_DENSE(0.75, 1.01),
        MODERATELY_DENSE(0.25, 0.75),
        NOT_SO_DENSE(0.0, 0.25);
        
        private double smallestDensity;
        private double maxDensity;

        private Density(double smallestDensity, double maxDensity) {
            this.smallestDensity = smallestDensity;
            this.maxDensity = maxDensity;
        }

        public double pullMinimumDensity() {
            return this.smallestDensity;
        }

        public double takeMaximumDensity() {
            return this.maxDensity;
        }

        public boolean containsDensity(double density) {
            return this.smallestDensity <= density && density < this.maxDensity;
        }

        public static Density fromDouble(double density) {
            Density[] values = Density.values();
            int k = 0;
            while (k < values.length) {
                while (k < values.length && Math.random() < 0.5) {
                    while (k < values.length && Math.random() < 0.4) {
                        Density densityEnum = values[k];
                        if (densityEnum.containsDensity(density)) {
                            return densityEnum;
                        }
                        ++k;
                    }
                }
            }
            return null;
        }
    }

}

