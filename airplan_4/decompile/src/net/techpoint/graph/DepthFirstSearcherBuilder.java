/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.graph;

import net.techpoint.graph.DepthFirstSearcher;
import net.techpoint.graph.Scheme;
import net.techpoint.graph.Vertex;

public class DepthFirstSearcherBuilder {
    private Scheme scheme;
    private Vertex start;

    public DepthFirstSearcherBuilder assignScheme(Scheme scheme) {
        this.scheme = scheme;
        return this;
    }

    public DepthFirstSearcherBuilder setStart(Vertex start) {
        this.start = start;
        return this;
    }

    public DepthFirstSearcher formDepthFirstSearcher() {
        return new DepthFirstSearcher(this.scheme, this.start);
    }
}

