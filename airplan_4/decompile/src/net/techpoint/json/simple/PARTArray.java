/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.json.simple;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.techpoint.json.simple.PARTAware;
import net.techpoint.json.simple.PARTStreamAware;
import net.techpoint.json.simple.PARTValue;

public class PARTArray
extends ArrayList
implements List,
PARTAware,
PARTStreamAware {
    private static final long serialVersionUID = 3957988303675231981L;

    public static void writePARTString(List list, Writer out) throws IOException {
        if (list == null) {
            PARTArray.writePARTStringUtility(out);
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
                out.write("null");
                continue;
            }
            PARTValue.writePARTString(value, out);
        }
        out.write(93);
    }

    private static void writePARTStringUtility(Writer out) throws IOException {
        new PARTArrayHome(out).invoke();
    }

    @Override
    public void writePARTString(Writer out) throws IOException {
        PARTArray.writePARTString(this, out);
    }

    public static String toPARTString(List list) {
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
                PARTArray.toPARTStringCoordinator(sb);
                continue;
            }
            sb.append(PARTValue.toPARTString(value));
        }
        sb.append(']');
        return sb.toString();
    }

    private static void toPARTStringCoordinator(StringBuffer sb) {
        sb.append("null");
    }

    @Override
    public String toPARTString() {
        return PARTArray.toPARTString(this);
    }

    @Override
    public String toString() {
        return this.toPARTString();
    }

    private static class PARTArrayHome {
        private Writer out;

        public PARTArrayHome(Writer out) {
            this.out = out;
        }

        public void invoke() throws IOException {
            this.out.write("null");
        }
    }

}

