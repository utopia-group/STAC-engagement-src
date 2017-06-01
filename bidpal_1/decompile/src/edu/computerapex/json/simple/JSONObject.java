/*
 * Decompiled with CFR 0_121.
 */
package edu.computerapex.json.simple;

import edu.computerapex.json.simple.JSONAware;
import edu.computerapex.json.simple.JSONStreamAware;
import edu.computerapex.json.simple.JSONValue;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JSONObject
extends HashMap
implements Map,
JSONAware,
JSONStreamAware {
    private static final long serialVersionUID = -503443796854799292L;

    public JSONObject() {
    }

    public JSONObject(Map map) {
        super(map);
    }

    public static void writeJSONString(Map map, Writer out) throws IOException {
        if (map == null) {
            out.write("null");
            return;
        }
        boolean first = true;
        Iterator iter = map.entrySet().iterator();
        out.write(123);
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                out.write(44);
            }
            Map.Entry entry = (Map.Entry) iter.next();
            out.write(34);
            out.write(JSONObject.escape(String.valueOf(entry.getKey())));
            out.write(34);
            out.write(58);
            JSONValue.writeJSONString(entry.getValue(), out);
        }
        out.write(125);
    }

    @Override
    public void writeJSONString(Writer out) throws IOException {
        JSONObject.writeJSONString(this, out);
    }

    public static String toJSONString(Map map) {
        if (map == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator iter = map.entrySet().iterator();
        sb.append('{');
        while (iter.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            Map.Entry entry = (Map.Entry) iter.next();
            JSONObject.toJSONString(String.valueOf(entry.getKey()), entry.getValue(), sb);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }

    private static String toJSONString(String key, Object value, StringBuffer sb) {
        sb.append('\"');
        if (key == null) {
            sb.append("null");
        } else {
            JSONValue.escape(key, sb);
        }
        sb.append('\"').append(':');
        sb.append(JSONValue.toJSONString(value));
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    public static String toString(String key, Object value) {
        StringBuffer sb = new StringBuffer();
        JSONObject.toJSONString(key, value, sb);
        return sb.toString();
    }

    public static String escape(String s) {
        return JSONValue.escape(s);
    }
}

