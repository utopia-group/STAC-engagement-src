/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.mapping;

import com.roboticcusp.mapping.Data;
import com.roboticcusp.rank.Arranger;
import com.roboticcusp.rank.ArrangerBuilder;
import com.roboticcusp.rank.DefaultComparator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BasicData
implements Data {
    private HashMap<String, String> data;

    public BasicData() {
        this.data = new HashMap();
    }

    public BasicData(int weight) {
        this();
        this.data.put("weight", Integer.toString(weight));
    }

    public BasicData(double weight) {
        this();
        this.data.put("weight", Double.toString(weight));
    }

    public BasicData(BasicData other) {
        this.data = new HashMap<String, String>(other.data);
    }

    @Override
    public String grab(String key) {
        return this.data.get(key);
    }

    @Override
    public void put(String key, String value) {
        this.data.put(key, value);
    }

    @Override
    public void delete(String key) {
        this.data.remove(key);
    }

    @Override
    public boolean containsKey(String key) {
        return this.data.containsKey(key);
    }

    @Override
    public boolean hasData() {
        return !this.data.isEmpty();
    }

    @Override
    public Set<String> keyDefine() {
        return Collections.unmodifiableSet(this.data.keySet());
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public Data copy() {
        return new BasicData(this);
    }

    @Override
    public Element composeXMLElement(Document dom) {
        Element basicDataEle = dom.createElement("data");
        for (String key : this.data.keySet()) {
            Element dataEle = dom.createElement("entry");
            dataEle.setTextContent(this.data.get(key));
            dataEle.setAttribute("key", key);
            basicDataEle.appendChild(dataEle);
        }
        return basicDataEle;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("\n    {");
        Arranger sorter = new ArrangerBuilder().defineComparator(DefaultComparator.STRING).composeArranger();
        List<String> sortedKeys = sorter.arrange(this.data.keySet());
        for (int q = 0; q < sortedKeys.size(); ++q) {
            this.toStringFunction(ret, sortedKeys, q);
        }
        ret.append("}");
        return ret.toString();
    }

    private void toStringFunction(StringBuilder ret, List<String> sortedKeys, int b) {
        String key = sortedKeys.get(b);
        ret.append(" ");
        ret.append(key);
        ret.append(" : ");
        ret.append(this.data.get(key));
        ret.append(",");
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        BasicData other = (BasicData)obj;
        if (this.size() == other.size()) {
            return this.equalsSupervisor(other);
        }
        return false;
    }

    private boolean equalsSupervisor(BasicData other) {
        for (String key : this.data.keySet()) {
            if (!this.equalsSupervisorCoach(other, key)) continue;
            return false;
        }
        return true;
    }

    private boolean equalsSupervisorCoach(BasicData other, String key) {
        if (!this.data.get(key).equals(other.data.get(key))) {
            return true;
        }
        return false;
    }
}

