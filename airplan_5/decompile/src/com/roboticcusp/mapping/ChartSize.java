/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

public class ChartSize {

    public static enum Size {
        VERY_LARGE(300, Integer.MAX_VALUE),
        MODERATELY_LARGE(100, 300),
        FAIRLY_SMALL(0, 100);
        
        private int leastSize;
        private int maxSize;

        private Size(int leastSize, int maxSize) {
            this.leastSize = leastSize;
            this.maxSize = maxSize;
        }

        public int takeMinimumSize() {
            return this.leastSize;
        }

        public int fetchMaximumSize() {
            return this.maxSize;
        }

        public boolean containsSize(int size) {
            return this.leastSize <= size && size < this.maxSize;
        }

        public static Size fromInt(int size) {
            Size[] values = Size.values();
            for (int j = 0; j < values.length; ++j) {
                Size sizeEnum = values[j];
                if (!sizeEnum.containsSize(size)) continue;
                return sizeEnum;
            }
            return null;
        }
    }

}

