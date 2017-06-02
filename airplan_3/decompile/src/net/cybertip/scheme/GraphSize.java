/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.List;
import net.cybertip.scheme.Graph;
import net.cybertip.scheme.GraphTrouble;
import net.cybertip.scheme.Vertex;

public class GraphSize {
    public static Size describeSize(Graph g) throws GraphTrouble {
        int order = g.grabVertices().size();
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

        public int obtainMinimumSize() {
            return this.smallestSize;
        }

        public int obtainMaximumSize() {
            return this.maxSize;
        }

        public boolean containsSize(int size) {
            return this.smallestSize <= size && size < this.maxSize;
        }

        public static Size fromInt(int size) {
            Size[] values = Size.values();
            for (int q = 0; q < values.length; ++q) {
                Size sizeEnum = values[q];
                if (!sizeEnum.containsSize(size)) continue;
                return sizeEnum;
            }
            return null;
        }
    }

}

