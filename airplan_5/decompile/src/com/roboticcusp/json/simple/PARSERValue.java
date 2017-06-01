/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.json.simple;

import com.roboticcusp.json.simple.PARSERArray;
import com.roboticcusp.json.simple.PARSERAware;
import com.roboticcusp.json.simple.PARSERObject;
import com.roboticcusp.json.simple.PARSERStreamAware;
import com.roboticcusp.json.simple.reader.PARSERGrabber;
import com.roboticcusp.json.simple.reader.ParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class PARSERValue {
    public static Object parse(Reader in) {
        try {
            PARSERGrabber grabber = new PARSERGrabber();
            return grabber.parse(in);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Object parse(String s) {
        StringReader in = new StringReader(s);
        return PARSERValue.parse(in);
    }

    public static Object parseWithException(Reader in) throws IOException, ParseException {
        PARSERGrabber grabber = new PARSERGrabber();
        return grabber.parse(in);
    }

    public static Object parseWithException(String s) throws ParseException {
        PARSERGrabber grabber = new PARSERGrabber();
        return grabber.parse(s);
    }

    public static void writePARSERString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }
        if (value instanceof String) {
            out.write(34);
            out.write(PARSERValue.escape((String)value));
            out.write(34);
            return;
        }
        if (value instanceof Double) {
            if (((Double)value).isInfinite() || ((Double)value).isNaN()) {
                out.write("null");
            } else {
                out.write(value.toString());
            }
            return;
        }
        if (value instanceof Float) {
            if (((Float)value).isInfinite() || ((Float)value).isNaN()) {
                out.write("null");
            } else {
                out.write(value.toString());
            }
            return;
        }
        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }
        if (value instanceof Boolean) {
            PARSERValue.writePARSERStringService(value, out);
            return;
        }
        if (value instanceof PARSERStreamAware) {
            PARSERValue.writePARSERStringGateKeeper((PARSERStreamAware)value, out);
            return;
        }
        if (value instanceof PARSERAware) {
            PARSERValue.writePARSERStringHome((PARSERAware)value, out);
            return;
        }
        if (value instanceof Map) {
            PARSERObject.writePARSERString((Map)value, out);
            return;
        }
        if (value instanceof List) {
            PARSERArray.writePARSERString((List)value, out);
            return;
        }
        out.write(value.toString());
    }

    private static void writePARSERStringHome(PARSERAware value, Writer out) throws IOException {
        out.write(value.toPARSERString());
    }

    private static void writePARSERStringGateKeeper(PARSERStreamAware value, Writer out) throws IOException {
        value.writePARSERString(out);
    }

    private static void writePARSERStringService(Object value, Writer out) throws IOException {
        out.write(value.toString());
    }

    public static String toPARSERString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + PARSERValue.escape((String)value) + "\"";
        }
        if (value instanceof Double) {
            if (((Double)value).isInfinite() || ((Double)value).isNaN()) {
                return "null";
            }
            return value.toString();
        }
        if (value instanceof Float) {
            if (((Float)value).isInfinite() || ((Float)value).isNaN()) {
                return "null";
            }
            return value.toString();
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof PARSERAware) {
            return ((PARSERAware)value).toPARSERString();
        }
        if (value instanceof Map) {
            return PARSERObject.toPARSERString((Map)value);
        }
        if (value instanceof List) {
            return PARSERArray.toPARSERString((List)value);
        }
        return value.toString();
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        PARSERValue.escape(s, sb);
        return sb.toString();
    }

    static void escape(String s, StringBuffer sb) {
        block10 : for (int q = 0; q < s.length(); ++q) {
            char ch = s.charAt(q);
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
                        int k = 0;
                        while (k < 4 - ss.length()) {
                            while (k < 4 - ss.length() && Math.random() < 0.6) {
                                while (k < 4 - ss.length() && Math.random() < 0.5) {
                                    while (k < 4 - ss.length() && Math.random() < 0.4) {
                                        sb.append('0');
                                        ++k;
                                    }
                                }
                            }
                        }
                        sb.append(ss.toUpperCase());
                        continue block10;
                    }
                    sb.append(ch);
                }
            }
        }
    }
}

