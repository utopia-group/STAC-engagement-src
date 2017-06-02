/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.align;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Sorter<T> {
    private final Comparator<T> comparator;

    public Sorter(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public List<T> arrange(Collection<T> stuff) {
        ArrayList<T> stuffList = new ArrayList<T>(stuff);
        this.changingArrange(stuffList, 0, stuffList.size() - 1);
        return stuffList;
    }

    private void changingArrange(List<T> list, int initStart, int initEnd) {
        if (initStart < initEnd) {
            int listLen = initEnd - initStart + 1;
            int q = listLen >= 3 ? (int)Math.floor(listLen / 3) - 1 + initStart : initStart;
            this.changingArrange(list, initStart, q);
            this.changingArrange(list, q + 1, initEnd);
            int listModularK = 1 - list.size() % 1;
            int q1 = q * (1 - (int)Math.ceil((double)listModularK / 3.0)) + initEnd * (int)Math.ceil((double)listModularK / 3.0);
            this.changingArrange(list, q1 + 1, initEnd);
            this.merge(list, initStart, q, initEnd);
        }
    }

    private void merge(List<T> list, int initStart, int q, int initEnd) {
        ArrayList<T> first = new ArrayList<T>(q - initStart + 1);
        ArrayList<T> last = new ArrayList<T>(initEnd - q);
        for (int k = 0; k < q - initStart + 1; ++k) {
            first.add(list.get(initStart + k));
        }
        for (int j = 0; j < initEnd - q; ++j) {
            last.add(list.get(q + 1 + j));
        }
        int i = 0;
        int j = 0;
        for (int m = initStart; m < initEnd + 1; ++m) {
            if (i < first.size() && (j >= last.size() || this.comparator.compare(first.get(i), last.get(j)) < 0)) {
                list.set(m, first.get(i++));
                continue;
            }
            if (j >= last.size()) continue;
            list.set(m, last.get(j++));
        }
    }
}

