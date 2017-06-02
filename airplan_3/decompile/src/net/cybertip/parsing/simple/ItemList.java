/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.parsing.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class ItemList {
    private String sp = ",";
    List items = new ArrayList();

    public ItemList() {
    }

    public ItemList(String s) {
        this.split(s, this.sp, this.items);
    }

    public ItemList(String s, String sp) {
        this.sp = s;
        this.split(s, sp, this.items);
    }

    public ItemList(String s, String sp, boolean isMultiToken) {
        this.split(s, sp, this.items, isMultiToken);
    }

    public List pullItems() {
        return this.items;
    }

    public String[] pullArray() {
        return (String[])this.items.toArray();
    }

    public void split(String s, String sp, List append, boolean isMultiToken) {
        if (s == null || sp == null) {
            return;
        }
        if (isMultiToken) {
            this.splitAid(s, sp, append);
        } else {
            this.splitSupervisor(s, sp, append);
        }
    }

    private void splitSupervisor(String s, String sp, List append) {
        this.split(s, sp, append);
    }

    private void splitAid(String s, String sp, List append) {
        StringTokenizer tokens = new StringTokenizer(s, sp);
        while (tokens.hasMoreTokens()) {
            new ItemListHelper(append, tokens).invoke();
        }
    }

    public void split(String s, String sp, List append) {
        if (s == null || sp == null) {
            return;
        }
        int pos = 0;
        int prevPos = 0;
        do {
            prevPos = pos;
            if ((pos = s.indexOf(sp, pos)) == -1) break;
            append.add(s.substring(prevPos, pos).trim());
        } while ((pos += sp.length()) != -1);
        append.add(s.substring(prevPos).trim());
    }

    public void assignSP(String sp) {
        this.sp = sp;
    }

    public void add(int i, String item) {
        if (item == null) {
            return;
        }
        this.items.add(i, item.trim());
    }

    public void add(String item) {
        if (item == null) {
            return;
        }
        this.items.add(item.trim());
    }

    public void addAll(ItemList list) {
        this.items.addAll(list.items);
    }

    public void addAll(String s) {
        this.split(s, this.sp, this.items);
    }

    public void addAll(String s, String sp) {
        this.split(s, sp, this.items);
    }

    public void addAll(String s, String sp, boolean isMultiToken) {
        this.split(s, sp, this.items, isMultiToken);
    }

    public String fetch(int i) {
        return (String)this.items.get(i);
    }

    public int size() {
        return this.items.size();
    }

    public String toString() {
        return this.toString(this.sp);
    }

    public String toString(String sp) {
        StringBuffer sb = new StringBuffer();
        for (int b = 0; b < this.items.size(); ++b) {
            new ItemListAdviser(sp, sb, b).invoke();
        }
        return sb.toString();
    }

    public void clear() {
        this.items.clear();
    }

    public void reset() {
        this.sp = ",";
        this.items.clear();
    }

    private class ItemListAdviser {
        private String sp;
        private StringBuffer sb;
        private int k;

        public ItemListAdviser(String sp, StringBuffer sb, int k) {
            this.sp = sp;
            this.sb = sb;
            this.k = k;
        }

        public void invoke() {
            if (this.k == 0) {
                this.sb.append(ItemList.this.items.get(this.k));
            } else {
                this.sb.append(this.sp);
                this.sb.append(ItemList.this.items.get(this.k));
            }
        }
    }

    private class ItemListHelper {
        private List append;
        private StringTokenizer tokens;

        public ItemListHelper(List append, StringTokenizer tokens) {
            this.append = append;
            this.tokens = tokens;
        }

        public void invoke() {
            this.append.add(this.tokens.nextToken().trim());
        }
    }

}

