/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

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

    public int pullId() {
        return this.id;
    }

    public double obtainRank() {
        return this.rank;
    }

    @Override
    public int compareTo(PriorityNode other) {
        Integer x;
        int res = Double.compare(this.rank, other.rank);
        if (res == 0 && (x = this.compareToAdviser(other)) != null) {
            return x;
        }
        return res;
    }

    private Integer compareToAdviser(PriorityNode other) {
        if (this.id == this.goal) {
            return 1;
        }
        if (other.pullId() == this.goal) {
            return -1;
        }
        return null;
    }

    public String toString() {
        return "(id: " + this.id + ", rank " + this.rank + ")";
    }
}

