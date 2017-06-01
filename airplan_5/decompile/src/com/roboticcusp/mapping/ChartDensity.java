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

public class ChartDensity {
    public static double computeDensity(Chart g) throws ChartException {
        int numSimpleEdges = ChartDensity.countEdges(g);
        int numVertices = g.obtainVertices().size();
        if (numVertices == 0 || numVertices == 1) {
            return 1.0;
        }
        return (double)numSimpleEdges / (double)(numVertices * (numVertices - 1));
    }

    static int countEdges(Chart g) throws ChartException {
        int uniqueEdgeCount = 0;
        List<Vertex> obtainVertices = g.obtainVertices();
        for (int i1 = 0; i1 < obtainVertices.size(); ++i1) {
            Vertex source = obtainVertices.get(i1);
            HashSet<Vertex> sinks = new HashSet<Vertex>();
            List<Edge> edges = g.getEdges(source.getId());
            for (int c = 0; c < edges.size(); ++c) {
                Edge edge = edges.get(c);
                sinks.add(edge.getSink());
            }
            uniqueEdgeCount += sinks.size();
        }
        return uniqueEdgeCount;
    }

    public static Density describeDensity(double density) {
        return Density.fromDouble(density);
    }

    public static enum Density {
        HIGHLY_DENSE(0.75, 1.01),
        MODERATELY_DENSE(0.25, 0.75),
        NOT_SO_DENSE(0.0, 0.25);
        
        private double leastDensity;
        private double maxDensity;

        private Density(double leastDensity, double maxDensity) {
            this.leastDensity = leastDensity;
            this.maxDensity = maxDensity;
        }

        public double fetchMinimumDensity() {
            return this.leastDensity;
        }

        public double obtainMaximumDensity() {
            return this.maxDensity;
        }

        public boolean containsDensity(double density) {
            return this.leastDensity <= density && density < this.maxDensity;
        }

        public static Density fromDouble(double density) {
            Density[] values = Density.values();
            int i = 0;
            while (i < values.length) {
                while (i < values.length && Math.random() < 0.6) {
                    Density densityEnum = values[i];
                    if (densityEnum.containsDensity(density)) {
                        return densityEnum;
                    }
                    ++i;
                }
            }
            return null;
        }
    }

}

