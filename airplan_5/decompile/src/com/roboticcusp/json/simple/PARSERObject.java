/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.json.simple;

import com.roboticcusp.json.simple.PARSERAware;
import com.roboticcusp.json.simple.PARSERStreamAware;
import com.roboticcusp.json.simple.PARSERValue;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PARSERObject
extends HashMap
implements Map,
PARSERAware,
PARSERStreamAware {
    private static final long serialVersionUID = -503443796854799292L;

    public PARSERObject() {
    }

    public PARSERObject(Map map) {
        super(map);
    }

    public static void writePARSERString(Map map, Writer out) throws IOException {
        if (map == null) {
            PARSERObject.writePARSERStringExecutor(out);
            return;
        }
        boolean first = true;
        Iterator<Entry> iter = map.entrySet().iterator();
        out.write(123);
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                out.write(44);
            }
            Map.Entry entry =  iter.next();
            out.write(34);
            out.write(PARSERObject.escape(String.valueOf(entry.getKey())));
            out.write(34);
            out.write(58);
            PARSERValue.writePARSERString(entry.getValue(), out);
        }
        out.write(125);
    }

    private static void writePARSERStringExecutor(Writer out) throws IOException {
        out.write("null");
    }

    @Override
    public void writePARSERString(Writer out) throws IOException {
        PARSERObject.writePARSERString(this, out);
    }

    public static String toPARSERString(Map map) {
        if (map == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator<Entry> iter = map.entrySet().iterator();
        sb.append('{');
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            Map.Entry entry = iter.next();
            PARSERObject.toPARSERString(String.valueOf(entry.getKey()), entry.getValue(), sb);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toPARSERString() {
        return PARSERObject.toPARSERString(this);
    }

    private static String toPARSERString(String key, Object value, StringBuffer sb) {
        sb.append('\"');
        if (key == null) {
            sb.append("null");
        } else {
            PARSERValue.escape(key, sb);
        }
        sb.append('\"').append(':');
        sb.append(PARSERValue.toPARSERString(value));
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toPARSERString();
    }

    public static String toString(String key, Object value) {
        StringBuffer sb = new StringBuffer();
        PARSERObject.toPARSERString(key, value, sb);
        return sb.toString();
    }

    public static String escape(String s) {
        return PARSERValue.escape(s);
    }
}

