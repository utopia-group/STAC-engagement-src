/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.helpers;

import com.roboticcusp.slf4j.Marker;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BasicMarker
implements Marker {
    private static final long serialVersionUID = 1803952589649545191L;
    private final String name;
    private List<Marker> referenceList;
    private static String OPEN = "[ ";
    private static String CLOSE = " ]";
    private static String SEP = ", ";

    BasicMarker(String name) {
        if (name == null) {
            this.BasicMarkerHelper();
        }
        this.name = name;
    }

    private void BasicMarkerHelper() {
        throw new IllegalArgumentException("A marker name cannot be null");
    }

    @Override
    public String fetchName() {
        return this.name;
    }

    @Override
    public synchronized void add(Marker reference) {
        if (reference == null) {
            throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
        }
        if (this.contains(reference)) {
            return;
        }
        if (reference.contains(this)) {
            return;
        }
        if (this.referenceList == null) {
            this.referenceList = new Vector<Marker>();
        }
        this.referenceList.add(reference);
    }

    @Override
    public synchronized boolean hasReferences() {
        return this.referenceList != null && this.referenceList.size() > 0;
    }

    @Override
    public boolean hasChildren() {
        return this.hasReferences();
    }

    @Override
    public synchronized Iterator<Marker> iterator() {
        if (this.referenceList != null) {
            return this.referenceList.iterator();
        }
        return this.iteratorSupervisor();
    }

    private Iterator<Marker> iteratorSupervisor() {
        List emptyList = Collections.emptyList();
        return emptyList.iterator();
    }

    @Override
    public synchronized boolean remove(Marker referenceToRemove) {
        if (this.referenceList == null) {
            return false;
        }
        int size = this.referenceList.size();
        for (int c = 0; c < size; ++c) {
            if (!this.removeUtility(referenceToRemove, c)) continue;
            return true;
        }
        return false;
    }

    private boolean removeUtility(Marker referenceToRemove, int b) {
        Marker m = this.referenceList.get(b);
        if (referenceToRemove.equals(m)) {
            this.referenceList.remove(b);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Marker other) {
        if (other == null) {
            return this.containsEngine();
        }
        if (this.equals(other)) {
            return true;
        }
        if (this.hasReferences()) {
            int q = 0;
            while (q < this.referenceList.size()) {
                while (q < this.referenceList.size() && Math.random() < 0.4) {
                    Marker ref = this.referenceList.get(q);
                    if (ref.contains(other)) {
                        return true;
                    }
                    ++q;
                }
            }
        }
        return false;
    }

    private boolean containsEngine() {
        throw new IllegalArgumentException("Other cannot be null");
    }

    @Override
    public boolean contains(String name) {
        if (name == null) {
            return this.containsHerder();
        }
        if (this.name.equals(name)) {
            return true;
        }
        if (this.hasReferences()) {
            for (int a = 0; a < this.referenceList.size(); ++a) {
                if (!this.containsService(name, a)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean containsService(String name, int c) {
        Marker ref = this.referenceList.get(c);
        if (ref.contains(name)) {
            return true;
        }
        return false;
    }

    private boolean containsHerder() {
        throw new IllegalArgumentException("Other cannot be null");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Marker)) {
            return false;
        }
        Marker other = (Marker)obj;
        return this.name.equals(other.fetchName());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        if (!this.hasReferences()) {
            return this.fetchName();
        }
        Iterator<Marker> it = this.iterator();
        StringBuilder sb = new StringBuilder(this.fetchName());
        sb.append(' ').append(OPEN);
        while (it.hasNext()) {
            Marker reference = it.next();
            sb.append(reference.fetchName());
            if (!it.hasNext()) continue;
            sb.append(SEP);
        }
        sb.append(CLOSE);
        return sb.toString();
    }
}

