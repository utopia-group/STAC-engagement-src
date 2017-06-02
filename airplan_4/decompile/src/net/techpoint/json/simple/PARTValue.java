/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.json.simple;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import net.techpoint.json.simple.PARTArray;
import net.techpoint.json.simple.PARTAware;
import net.techpoint.json.simple.PARTObject;
import net.techpoint.json.simple.PARTStreamAware;
import net.techpoint.json.simple.grabber.PARTReader;
import net.techpoint.json.simple.grabber.ParseFailure;

public class PARTValue {
    public static Object parse(Reader in) {
        try {
            PARTReader reader = new PARTReader();
            return reader.parse(in);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Object parse(String s) {
        StringReader in = new StringReader(s);
        return PARTValue.parse(in);
    }

    public static Object parseWithFailure(Reader in) throws IOException, ParseFailure {
        PARTReader reader = new PARTReader();
        return reader.parse(in);
    }

    public static Object parseWithFailure(String s) throws ParseFailure {
        PARTReader reader = new PARTReader();
        return reader.parse(s);
    }

    public static void writePARTString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }
        if (value instanceof String) {
            PARTValue.writePARTStringAid((String)value, out);
            return;
        }
        if (value instanceof Double) {
            PARTValue.writePARTStringAdviser(value, out);
            return;
        }
        if (value instanceof Float) {
            new PARTValueHelper(value, out).invoke();
            return;
        }
        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }
        if (value instanceof Boolean) {
            out.write(value.toString());
            return;
        }
        if (value instanceof PARTStreamAware) {
            ((PARTStreamAware)value).writePARTString(out);
            return;
        }
        if (value instanceof PARTAware) {
            out.write(((PARTAware)value).toPARTString());
            return;
        }
        if (value instanceof Map) {
            PARTObject.writePARTString((Map)value, out);
            return;
        }
        if (value instanceof List) {
            PARTArray.writePARTString((List)value, out);
            return;
        }
        out.write(value.toString());
    }

    private static void writePARTStringAdviser(Object value, Writer out) throws IOException {
        if (((Double)value).isInfinite() || ((Double)value).isNaN()) {
            out.write("null");
        } else {
            out.write(value.toString());
        }
    }

    private static void writePARTStringAid(String value, Writer out) throws IOException {
        out.write(34);
        out.write(PARTValue.escape(value));
        out.write(34);
    }

    public static String toPARTString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + PARTValue.escape((String)value) + "\"";
        }
        if (value instanceof Double) {
            return PARTValue.toPARTStringEntity(value);
        }
        if (value instanceof Float) {
            return PARTValue.toPARTStringTarget(value);
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof PARTAware) {
            return ((PARTAware)value).toPARTString();
        }
        if (value instanceof Map) {
            return PARTObject.toPARTString((Map)value);
        }
        if (value instanceof List) {
            return PARTArray.toPARTString((List)value);
        }
        return value.toString();
    }

    private static String toPARTStringTarget(Object value) {
        if (((Float)value).isInfinite() || ((Float)value).isNaN()) {
            return "null";
        }
        return value.toString();
    }

    private static String toPARTStringEntity(Object value) {
        if (((Double)value).isInfinite() || ((Double)value).isNaN()) {
            return "null";
        }
        return value.toString();
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        PARTValue.escape(s, sb);
        return sb.toString();
    }

    static void escape(String s, StringBuffer sb) {
        block10 : for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\"': {
                    sb.append("\\\"");
                    continue block10;
                }
                case '\\': {
                    sb.append("\\\\");
                    continue block10;
                }
                case '\b': {
                    sb.append("\\b");
                    continue block10;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block10;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block10;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block10;
                }
                case '\t': {
                    sb.append("\\t");
                    continue block10;
                }
                case '/': {
                    sb.append("\\/");
                    continue block10;
                }
                default: {
                    if (ch >= '\u0000' && ch <= '\u001f' || ch >= '' && ch <= '\u009f' || ch >= '\u2000' && ch <= '\u20ff') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); ++k) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                        continue block10;
                    }
                    PARTValue.escapeUtility(sb, ch);
                }
            }
        }
    }

    private static void escapeUtility(StringBuffer sb, char ch) {
        sb.append(ch);
    }

    private static class PARTValueHelper {
        private Object value;
        private Writer out;

        public PARTValueHelper(Object value, Writer out) {
            this.value = value;
            this.out = out;
        }

        public void invoke() throws IOException {
            if (((Float)this.value).isInfinite() || ((Float)this.value).isNaN()) {
                this.out.write("null");
            } else {
                this.out.write(this.value.toString());
            }
        }
    }

}

