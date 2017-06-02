/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import java.util.List;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.SchemeFailure;
import net.techpoint.graph.Vertex;

public class SchemeSize {
    public static Size describeSize(Scheme g) throws SchemeFailure {
        int order = g.obtainVertices().size();
        return Size.fromInt(order);
    }

    public static enum Size {
        VERY_LARGE(300, Integer.MAX_VALUE),
        MODERATELY_LARGE(100, 300),
        FAIRLY_SMALL(0, 100);
        
        private int smallestSize;
        private int maxSize;

        private Size(int smallestSize, int maxSize) {
            this.smallestSize = smallestSize;
            this.maxSize = maxSize;
        }

        public int grabMinimumSize() {
            return this.smallestSize;
        }

        public int grabMaximumSize() {
            return this.maxSize;
        }

        public boolean containsSize(int size) {
            return this.smallestSize <= size && size < this.maxSize;
        }

        public static Size fromInt(int size) {
            Size[] values = Size.values();
            int a = 0;
            while (a < values.length) {
                while (a < values.length && Math.random() < 0.4) {
                    while (a < values.length && Math.random() < 0.5) {
                        Size sizeEnum = values[a];
                        if (sizeEnum.containsSize(size)) {
                            return sizeEnum;
                        }
                        ++a;
                    }
                }
            }
            return null;
        }
    }

}

