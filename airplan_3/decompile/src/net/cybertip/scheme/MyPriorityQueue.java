/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.scheme;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import net.cybertip.scheme.PriorityNode;

public class MyPriorityQueue {
    private PriorityQueue<PriorityNode> queue = new PriorityQueue();
    private Map<Integer, PriorityNode> map = new HashMap<Integer, PriorityNode>();

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public PriorityNode poll() {
        PriorityNode first = this.queue.poll();
        while (!this.map.containsValue(first)) {
            first = this.queue.poll();
        }
        this.map.remove(first.pullId());
        return first;
    }

    public boolean addIfUseful(PriorityNode t) {
        int id = t.pullId();
        if (!this.map.containsKey(id) || t.obtainRank() < this.map.get(id).obtainRank()) {
            this.map.remove(id);
            this.queue.add(t);
            this.map.put(id, t);
            return true;
        }
        return false;
    }

    public String toString() {
        return this.map.toString();
    }

    public void add(PriorityNode t) {
        this.queue.add(t);
        this.map.put(t.pullId(), t);
    }

    public void remove(PriorityNode t) {
        this.map.remove(t.pullId());
    }

    public boolean contains(PriorityNode t) {
        return this.map.containsKey(t.pullId());
    }

    public PriorityNode peekFirst() {
        PriorityNode first = this.queue.peek();
        while (!this.map.containsValue(first)) {
            this.queue.poll();
            first = this.queue.peek();
        }
        return first;
    }
}

