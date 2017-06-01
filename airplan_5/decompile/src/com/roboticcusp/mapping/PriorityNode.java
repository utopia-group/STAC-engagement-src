/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

class PriorityNode
implements Comparable<PriorityNode> {
    private int id;
    private double rank;
    private int goal;

    public PriorityNode(int nodeId, double rank, int goal) {
        this.id = nodeId;
        this.rank = rank;
        this.goal = goal;
    }

    public int takeId() {
        return this.id;
    }

    public double fetchRank() {
        return this.rank;
    }

    @Override
    public int compareTo(PriorityNode other) {
        int res = Double.compare(this.rank, other.rank);
        if (res == 0) {
            if (this.id == this.goal) {
                return 1;
            }
            if (other.takeId() == this.goal) {
                return -1;
            }
        }
        return res;
    }

    public String toString() {
        return "(id: " + this.id + ", rank " + this.rank + ")";
    }
}

