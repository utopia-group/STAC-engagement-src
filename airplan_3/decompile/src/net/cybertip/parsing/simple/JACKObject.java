/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.parsing.simple;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.cybertip.parsing.simple.JACKAware;
import net.cybertip.parsing.simple.JACKStreamAware;
import net.cybertip.parsing.simple.JACKValue;

public class JACKObject
extends HashMap
implements Map,
JACKAware,
JACKStreamAware {
    private static final long serialVersionUID = -503443796854799292L;

    public JACKObject() {
    }

    public JACKObject(Map map) {
        super(map);
    }

    public static void writeJACKString(Map map, Writer out) throws IOException {
        if (map == null) {
            out.write("null");
            return;
        }
        boolean first = true;
        Iterator<Map.Entry> iter = map.entrySet().iterator();
        out.write(123);
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                out.write(44);
            }
            Map.Entry entry = iter.next();
            out.write(34);
            out.write(JACKObject.escape(String.valueOf(entry.getKey())));
            out.write(34);
            out.write(58);
            JACKValue.writeJACKString(entry.getValue(), out);
        }
        out.write(125);
    }

    @Override
    public void writeJACKString(Writer out) throws IOException {
        JACKObject.writeJACKString(this, out);
    }

    public static String toJACKString(Map map) {
        if (map == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator<Map.Entry> iter = map.entrySet().iterator();
        sb.append('{');
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            Map.Entry entry = iter.next();
            JACKObject.toJACKString(String.valueOf(entry.getKey()), entry.getValue(), sb);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toJACKString() {
        return JACKObject.toJACKString(this);
    }

    private static String toJACKString(String key, Object value, StringBuffer sb) {
        sb.append('\"');
        if (key == null) {
            sb.append("null");
        } else {
            JACKValue.escape(key, sb);
        }
        sb.append('\"').append(':');
        sb.append(JACKValue.toJACKString(value));
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toJACKString();
    }

    public static String toString(String key, Object value) {
        StringBuffer sb = new StringBuffer();
        JACKObject.toJACKString(key, value, sb);
        return sb.toString();
    }

    public static String escape(String s) {
        return JACKValue.escape(s);
    }
}

