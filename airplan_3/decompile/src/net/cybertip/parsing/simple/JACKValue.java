/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.parsing.simple;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import net.cybertip.parsing.simple.JACKArray;
import net.cybertip.parsing.simple.JACKAware;
import net.cybertip.parsing.simple.JACKObject;
import net.cybertip.parsing.simple.JACKStreamAware;
import net.cybertip.parsing.simple.retriever.JACKExtractor;
import net.cybertip.parsing.simple.retriever.ParseTrouble;

public class JACKValue {
    public static Object parse(Reader in) {
        try {
            JACKExtractor extractor = new JACKExtractor();
            return extractor.parse(in);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Object parse(String s) {
        StringReader in = new StringReader(s);
        return JACKValue.parse(in);
    }

    public static Object parseWithTrouble(Reader in) throws IOException, ParseTrouble {
        JACKExtractor extractor = new JACKExtractor();
        return extractor.parse(in);
    }

    public static Object parseWithTrouble(String s) throws ParseTrouble {
        JACKExtractor extractor = new JACKExtractor();
        return extractor.parse(s);
    }

    public static void writeJACKString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }
        if (value instanceof String) {
            out.write(34);
            out.write(JACKValue.escape((String)value));
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
            out.write(value.toString());
            return;
        }
        if (value instanceof JACKStreamAware) {
            new JACKValueUtility((JACKStreamAware)value, out).invoke();
            return;
        }
        if (value instanceof JACKAware) {
            JACKValue.writeJACKStringTarget((JACKAware)value, out);
            return;
        }
        if (value instanceof Map) {
            JACKValue.writeJACKStringSupervisor((Map)value, out);
            return;
        }
        if (value instanceof List) {
            JACKValue.writeJACKStringHerder((List)value, out);
            return;
        }
        out.write(value.toString());
    }

    private static void writeJACKStringHerder(List value, Writer out) throws IOException {
        JACKArray.writeJACKString(value, out);
    }

    private static void writeJACKStringSupervisor(Map value, Writer out) throws IOException {
        JACKObject.writeJACKString(value, out);
    }

    private static void writeJACKStringTarget(JACKAware value, Writer out) throws IOException {
        out.write(value.toJACKString());
    }

    public static String toJACKString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + JACKValue.escape((String)value) + "\"";
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
        if (value instanceof JACKAware) {
            return ((JACKAware)value).toJACKString();
        }
        if (value instanceof Map) {
            return JACKObject.toJACKString((Map)value);
        }
        if (value instanceof List) {
            return JACKArray.toJACKString((List)value);
        }
        return value.toString();
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        JACKValue.escape(s, sb);
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
                    sb.append(ch);
                }
            }
        }
    }

    private static class JACKValueUtility {
        private JACKStreamAware value;
        private Writer out;

        public JACKValueUtility(JACKStreamAware value, Writer out) {
            this.value = value;
            this.out = out;
        }

        public void invoke() throws IOException {
            this.value.writeJACKString(this.out);
        }
    }

}

