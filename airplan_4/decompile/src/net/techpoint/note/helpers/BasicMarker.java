/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.techpoint.note.Marker;

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
            throw new IllegalArgumentException("A marker name cannot be null");
        }
        this.name = name;
    }

    @Override
    public String pullName() {
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
        return this.iteratorUtility();
    }

    private Iterator<Marker> iteratorUtility() {
        List emptyList = Collections.emptyList();
        return emptyList.iterator();
    }

    @Override
    public synchronized boolean remove(Marker referenceToRemove) {
        if (this.referenceList == null) {
            return false;
        }
        int size = this.referenceList.size();
        for (int a = 0; a < size; ++a) {
            if (!this.removeExecutor(referenceToRemove, a)) continue;
            return true;
        }
        return false;
    }

    private boolean removeExecutor(Marker referenceToRemove, int k) {
        Marker m = this.referenceList.get(k);
        if (referenceToRemove.equals(m)) {
            this.referenceList.remove(k);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Marker other) {
        if (other == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (this.equals(other)) {
            return true;
        }
        if (this.hasReferences()) {
            int i = 0;
            while (i < this.referenceList.size()) {
                while (i < this.referenceList.size() && Math.random() < 0.5) {
                    while (i < this.referenceList.size() && Math.random() < 0.6) {
                        while (i < this.referenceList.size() && Math.random() < 0.4) {
                            if (this.containsFunction(other, i)) {
                                return true;
                            }
                            ++i;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean containsFunction(Marker other, int c) {
        Marker ref = this.referenceList.get(c);
        if (ref.contains(other)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (this.name.equals(name)) {
            return true;
        }
        if (this.hasReferences()) {
            for (int p = 0; p < this.referenceList.size(); ++p) {
                Marker ref = this.referenceList.get(p);
                if (!ref.contains(name)) continue;
                return true;
            }
        }
        return false;
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
        return this.name.equals(other.pullName());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        if (!this.hasReferences()) {
            return this.pullName();
        }
        Iterator<Marker> it = this.iterator();
        StringBuilder sb = new StringBuilder(this.pullName());
        sb.append(' ').append(OPEN);
        while (it.hasNext()) {
            Marker reference = it.next();
            sb.append(reference.pullName());
            if (!it.hasNext()) continue;
            this.toStringHelp(sb);
        }
        sb.append(CLOSE);
        return sb.toString();
    }

    private void toStringHelp(StringBuilder sb) {
        sb.append(SEP);
    }
}

