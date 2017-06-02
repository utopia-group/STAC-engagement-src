/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.HashSet;
import java.util.List;
import net.cybertip.scheme.Edge;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class GraphDensity {
    static int countEdges(Graph g) throws GraphTrouble {
        int uniqueEdgeCount = 0;
        List<Vertex> grabVertices = g.grabVertices();
        for (int i1 = 0; i1 < grabVertices.size(); ++i1) {
            Vertex source = grabVertices.get(i1);
            HashSet<Vertex> sinks = new HashSet<Vertex>();
            List<Edge> fetchEdges = g.fetchEdges(source.getId());
            for (int j = 0; j < fetchEdges.size(); ++j) {
                Edge edge = fetchEdges.get(j);
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
        
        private double smallestDensity;
        private double maxDensity;

        private Density(double smallestDensity, double maxDensity) {
            this.smallestDensity = smallestDensity;
            this.maxDensity = maxDensity;
        }

        public double fetchMinimumDensity() {
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
            int q = 0;
            while (q < values.length) {
                while (q < values.length && Math.random() < 0.5) {
                    while (q < values.length && Math.random() < 0.6) {
                        Density densityEnum = values[q];
                        if (densityEnum.containsDensity(density)) {
                            return densityEnum;
                        }
                        ++q;
                    }
                }
            }
            return null;
        }
    }

}

