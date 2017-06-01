/*
 * Decompiled with CFR 0_121.
 */
package com.example.linalg.util;

public class Pair<K, V> {
    private final K element0;
    private final V element1;

    public static <K, V> Pair<K, V> createPair(K element0, V element1) {
        return new Pair<K, V>(element0, element1);
    }

    public Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K getElement0() {
        return this.element0;
    }

    public V getElement1() {
        return this.element1;
    }

    public String toString() {
        return "<" + this.element0 + "," + this.element1 + ">";
    }
}

