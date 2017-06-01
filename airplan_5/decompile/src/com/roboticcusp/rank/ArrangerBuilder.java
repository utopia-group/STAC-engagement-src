/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.rank;

import com.roboticcusp.rank.Arranger;
import java.util.Comparator;

public class ArrangerBuilder<T> {
    private Comparator<T> comparator;

    public ArrangerBuilder defineComparator(Comparator<T> comparator) {
        this.comparator = comparator;
        return this;
    }

    public Arranger composeArranger() {
        return new Arranger<T>(this.comparator);
    }
}

