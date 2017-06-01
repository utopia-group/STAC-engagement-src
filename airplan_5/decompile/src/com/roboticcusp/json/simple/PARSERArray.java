/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.json.simple;

import com.roboticcusp.json.simple.PARSERAware;
import com.roboticcusp.json.simple.PARSERStreamAware;
import com.roboticcusp.json.simple.PARSERValue;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PARSERArray
extends ArrayList
implements List,
PARSERAware,
PARSERStreamAware {
    private static final long serialVersionUID = 3957988303675231981L;

    public static void writePARSERString(List list, Writer out) throws IOException {
        if (list == null) {
            out.write("null");
            return;
        }
        boolean first = true;
        Iterator iter = list.iterator();
        out.write(91);
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                out.write(44);
            }
            Object value = iter.next();
            if (value == null) {
                PARSERArray.writePARSERStringHerder(out);
                continue;
            }
            PARSERValue.writePARSERString(value, out);
        }
        out.write(93);
    }

    private static void writePARSERStringHerder(Writer out) throws IOException {
        out.write("null");
    }

    @Override
    public void writePARSERString(Writer out) throws IOException {
        PARSERArray.writePARSERString(this, out);
    }

    public static String toPARSERString(List list) {
        if (list == null) {
            return "null";
        }
        boolean first = true;
        StringBuffer sb = new StringBuffer();
        Iterator iter = list.iterator();
        sb.append('[');
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            Object value = iter.next();
            if (value == null) {
                sb.append("null");
                continue;
            }
            sb.append(PARSERValue.toPARSERString(value));
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public String toPARSERString() {
        return PARSERArray.toPARSERString(this);
    }

    @Override
    public String toString() {
        return this.toPARSERString();
    }
}

