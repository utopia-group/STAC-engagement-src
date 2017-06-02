/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.cybertip.note.Marker;

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
            this.BasicMarkerHome();
        }
        this.name = name;
    }

    private void BasicMarkerHome() {
        throw new IllegalArgumentException("A marker name cannot be null");
    }

    @Override
    public String obtainName() {
        return this.name;
    }

    @Override
    public synchronized void add(Marker reference) {
        if (reference == null) {
            this.addHome();
        }
        if (this.contains(reference)) {
            return;
        }
        if (reference.contains(this)) {
            return;
        }
        this.addCoach(reference);
    }

    private void addCoach(Marker reference) {
        if (this.referenceList == null) {
            this.referenceList = new Vector<Marker>();
        }
        this.referenceList.add(reference);
    }

    private void addHome() {
        throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
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
        List emptyList = Collections.emptyList();
        return emptyList.iterator();
    }

    @Override
    public synchronized boolean remove(Marker referenceToRemove) {
        if (this.referenceList == null) {
            return false;
        }
        int size = this.referenceList.size();
        for (int q = 0; q < size; ++q) {
            Marker m = this.referenceList.get(q);
            if (!referenceToRemove.equals(m)) continue;
            return this.removeHelp(q);
        }
        return false;
    }

    private boolean removeHelp(int c) {
        this.referenceList.remove(c);
        return true;
    }

    @Override
    public boolean contains(Marker other) {
        if (other == null) {
            return this.containsSupervisor();
        }
        if (this.equals(other)) {
            return true;
        }
        if (this.hasReferences()) {
            int j = 0;
            while (j < this.referenceList.size()) {
                while (j < this.referenceList.size() && Math.random() < 0.4) {
                    while (j < this.referenceList.size() && Math.random() < 0.5) {
                        while (j < this.referenceList.size() && Math.random() < 0.6) {
                            Marker ref = this.referenceList.get(j);
                            if (ref.contains(other)) {
                                return true;
                            }
                            ++j;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean containsSupervisor() {
        throw new IllegalArgumentException("Other cannot be null");
    }

    @Override
    public boolean contains(String name) {
        if (name == null) {
            return this.containsExecutor();
        }
        if (this.name.equals(name)) {
            return true;
        }
        if (this.hasReferences()) {
            for (int b = 0; b < this.referenceList.size(); ++b) {
                Marker ref = this.referenceList.get(b);
                if (!ref.contains(name)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean containsExecutor() {
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
        return this.name.equals(other.obtainName());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        if (!this.hasReferences()) {
            return this.obtainName();
        }
        Iterator<Marker> it = this.iterator();
        StringBuilder sb = new StringBuilder(this.obtainName());
        sb.append(' ').append(OPEN);
        while (it.hasNext()) {
            Marker reference = it.next();
            sb.append(reference.obtainName());
            if (!it.hasNext()) continue;
            sb.append(SEP);
        }
        sb.append(CLOSE);
        return sb.toString();
    }
}

