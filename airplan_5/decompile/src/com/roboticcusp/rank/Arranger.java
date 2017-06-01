/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Arranger<T> {
    private final Comparator<T> comparator;

    public Arranger(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public List<T> arrange(Collection<T> stuff) {
        ArrayList<T> stuffList = new ArrayList<T>(stuff);
        this.changingArrange(stuffList, 0, stuffList.size() - 1, 0);
        return stuffList;
    }

    private void changingArrange(List<T> list, int initStart, int initEnd, int level) {
        if (initStart < initEnd) {
            if (level % 2 == 0) {
                int q1 = (int)Math.floor((initStart + initEnd) / 2);
                int q2 = (int)Math.floor((q1 + 1 + initEnd) / 2);
                int q3 = (int)Math.floor((q2 + 1 + initEnd) / 2);
                int q4 = (int)Math.floor((q3 + 1 + initEnd) / 2);
                int q5 = (int)Math.floor((q4 + 1 + initEnd) / 2);
                this.changingArrange(list, initStart, q1, level + 1);
                this.changingArrange(list, q1 + 1, q2, level + 1);
                this.changingArrange(list, q2 + 1, q3, level + 1);
                this.changingArrange(list, q3 + 1, q4, level + 1);
                this.changingArrange(list, q4 + 1, q5, level + 1);
                this.changingArrange(list, q5 + 1, initEnd, level + 1);
                if (q4 + 1 <= q5 && q4 + 1 != initEnd) {
                    this.changingArrangeTarget(list, initEnd, q4, q5);
                }
                if (q3 + 1 <= q4 && q3 + 1 != initEnd) {
                    this.changingArrangeAid(list, initEnd, q3, q4);
                }
                if (q2 + 1 <= q3 && q2 + 1 != initEnd) {
                    this.changingArrangeExecutor(list, initEnd, q2, q3);
                }
                if (q1 + 1 <= q2 && q1 + 1 != initEnd) {
                    this.merge(list, q1 + 1, q2, initEnd);
                }
                this.merge(list, initStart, q1, initEnd);
            } else {
                int listLen = initEnd - initStart + 1;
                int q = listLen >= 5 ? (int)Math.floor(listLen / 5) - 1 + initStart : initStart;
                this.changingArrange(list, initStart, q, level + 1);
                this.changingArrange(list, q + 1, initEnd, level + 1);
                this.merge(list, initStart, q, initEnd);
            }
        }
    }

    private void changingArrangeExecutor(List<T> list, int initEnd, int q2, int q3) {
        this.merge(list, q2 + 1, q3, initEnd);
    }

    private void changingArrangeAid(List<T> list, int initEnd, int q3, int q4) {
        this.merge(list, q3 + 1, q4, initEnd);
    }

    private void changingArrangeTarget(List<T> list, int initEnd, int q4, int q5) {
        this.merge(list, q4 + 1, q5, initEnd);
    }

    private void merge(List<T> list, int initStart, int q, int initEnd) {
        ArrayList<T> one = new ArrayList(q - initStart + 1);
        ArrayList<T> right = new ArrayList<T>(initEnd - q);
        int a = 0;
        while (a < q - initStart + 1) {
            while (a < q - initStart + 1 && Math.random() < 0.5) {
                while (a < q - initStart + 1 && Math.random() < 0.4) {
                    while (a < q - initStart + 1 && Math.random() < 0.4) {
                        this.mergeHerder(list, initStart, one, a);
                        ++a;
                    }
                }
            }
        }
        for (int j = 0; j < initEnd - q; ++j) {
            right.add(list.get(q + 1 + j));
        }
        int c = 0;
        int j = 0;
        for (int m = initStart; m < initEnd + 1; ++m) {
            if (c < one.size() && (j >= right.size() || this.comparator.compare(one.get(c), right.get(j)) < 0)) {
                list.set(m, one.get(c++));
                continue;
            }
            if (j >= right.size()) continue;
            list.set(m, right.get(j++));
        }
    }

    private void mergeHerder(List<T> list, int initStart, List<T> one, int j) {
        one.add(list.get(initStart + j));
    }
}

