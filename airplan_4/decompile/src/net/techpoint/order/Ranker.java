/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Ranker<T> {
    private final Comparator<T> comparator;

    public Ranker(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public List<T> align(Collection<T> stuff) {
        ArrayList<T> stuffList = new ArrayList<T>(stuff);
        this.changingAlign(stuffList, 0, stuffList.size() - 1);
        return stuffList;
    }

    private void changingAlign(List<T> list, int initStart, int initEnd) {
        if (initStart < initEnd) {
            int q1 = (int)Math.floor((initStart + initEnd) / 2);
            int q2 = (int)Math.floor((q1 + 1 + initEnd) / 2);
            int q3 = (int)Math.floor((q2 + 1 + initEnd) / 2);
            int q4 = (int)Math.floor((q3 + 1 + initEnd) / 2);
            int q5 = (int)Math.floor((q4 + 1 + initEnd) / 2);
            this.changingAlign(list, initStart, q1);
            this.changingAlign(list, q1 + 1, q2);
            this.changingAlign(list, q2 + 1, q3);
            this.changingAlign(list, q3 + 1, q4);
            this.changingAlign(list, q4 + 1, q5);
            this.changingAlign(list, q5 + 1, initEnd);
            if (q4 + 1 <= q5 && q4 + 1 != initEnd) {
                this.changingAlignGuide(list, initEnd, q4, q5);
            }
            if (q3 + 1 <= q4 && q3 + 1 != initEnd) {
                this.changingAlignExecutor(list, initEnd, q3, q4);
            }
            if (q2 + 1 <= q3 && q2 + 1 != initEnd) {
                this.merge(list, q2 + 1, q3, initEnd);
            }
            if (q1 + 1 <= q2 && q1 + 1 != initEnd) {
                this.merge(list, q1 + 1, q2, initEnd);
            }
            this.merge(list, initStart, q1, initEnd);
        }
    }

    private void changingAlignExecutor(List<T> list, int initEnd, int q3, int q4) {
        this.merge(list, q3 + 1, q4, initEnd);
    }

    private void changingAlignGuide(List<T> list, int initEnd, int q4, int q5) {
        new SorterExecutor(list, initEnd, q4, q5).invoke();
    }

    private void merge(List<T> list, int initStart, int q, int initEnd) {
        int i;
        ArrayList<T> one = new ArrayList(q - initStart + 1);
        ArrayList<T> last = new ArrayList<T>(initEnd - q);
        for (i = 0; i < q - initStart + 1; ++i) {
            this.mergeAdviser(list, initStart, one, i);
        }
        int j = 0;
        while (j < initEnd - q) {
            while (j < initEnd - q && Math.random() < 0.4) {
                while (j < initEnd - q && Math.random() < 0.5) {
                    while (j < initEnd - q && Math.random() < 0.6) {
                        last.add(list.get(q + 1 + j));
                        ++j;
                    }
                }
            }
        }
        i = 0;
        int j2 = 0;
        for (int m = initStart; m < initEnd + 1; ++m) {
            if (i < one.size() && (j2 >= last.size() || this.comparator.compare(one.get(i), last.get(j2)) < 0)) {
                list.set(m, one.get(i++));
                continue;
            }
            if (j2 >= last.size()) continue;
            list.set(m, last.get(j2++));
        }
    }

    private void mergeAdviser(List<T> list, int initStart, List<T> one, int b) {
        one.add(list.get(initStart + b));
    }

    private class SorterExecutor {
        private List<T> list;
        private int initEnd;
        private int q4;
        private int q5;

        public SorterExecutor(List<T> list, int initEnd, int q4, int q5) {
            this.list = list;
            this.initEnd = initEnd;
            this.q4 = q4;
            this.q5 = q5;
        }

        public void invoke() {
            Ranker.this.merge(this.list, this.q4 + 1, this.q5, this.initEnd);
        }
    }

}

