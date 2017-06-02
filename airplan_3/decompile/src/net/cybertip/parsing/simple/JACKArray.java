/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.parsing.simple;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cybertip.parsing.simple.JACKAware;
import net.cybertip.parsing.simple.JACKStreamAware;
import net.cybertip.parsing.simple.JACKValue;

public class JACKArray
extends ArrayList
implements List,
JACKAware,
JACKStreamAware {
    private static final long serialVersionUID = 3957988303675231981L;

    public static void writeJACKString(List list, Writer out) throws IOException {
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
                out.write("null");
                continue;
            }
            JACKValue.writeJACKString(value, out);
        }
        out.write(93);
    }

    @Override
    public void writeJACKString(Writer out) throws IOException {
        JACKArray.writeJACKString(this, out);
    }

    public static String toJACKString(List list) {
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
                JACKArray.toJACKStringExecutor(sb);
                continue;
            }
            sb.append(JACKValue.toJACKString(value));
        }
        sb.append(']');
        return sb.toString();
    }

    private static void toJACKStringExecutor(StringBuffer sb) {
        sb.append("null");
    }

    @Override
    public String toJACKString() {
        return JACKArray.toJACKString(this);
    }

    @Override
    public String toString() {
        return this.toJACKString();
    }
}

