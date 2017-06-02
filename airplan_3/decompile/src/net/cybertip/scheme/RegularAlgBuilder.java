/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import net.cybertip.scheme.Graph;
import net.cybertip.scheme.RegularAlg;

public class RegularAlgBuilder {
    private Graph g;

    public RegularAlgBuilder assignG(Graph g) {
        this.g = g;
        return this;
    }

    public RegularAlg makeRegularAlg() {
        return new RegularAlg(this.g);
    }
}

