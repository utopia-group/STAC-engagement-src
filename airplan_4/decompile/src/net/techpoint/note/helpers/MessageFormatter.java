/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import java.util.HashMap;
import java.util.Map;
import net.techpoint.note.helpers.FormattingTuple;
import net.techpoint.note.helpers.FormattingTupleBuilder;
import net.techpoint.note.helpers.Util;

public final class MessageFormatter {
    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    public static final FormattingTuple format(String messagePattern, Object arg) {
        return MessageFormatter.arrayFormat(messagePattern, new Object[]{arg});
    }

    public static final FormattingTuple format(String messagePattern, Object arg1, Object arg2) {
        return MessageFormatter.arrayFormat(messagePattern, new Object[]{arg1, arg2});
    }

    static final Throwable pullThrowableCandidate(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return null;
        }
        Object lastEntry = argArray[argArray.length - 1];
        if (lastEntry instanceof Throwable) {
            return (Throwable)lastEntry;
        }
        return null;
    }

    public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray) {
        Throwable throwableCandidate = MessageFormatter.pullThrowableCandidate(argArray);
        Object[] args = argArray;
        if (throwableCandidate != null) {
            args = MessageFormatter.trimmedCopy(argArray);
        }
        return MessageFormatter.arrayFormat(messagePattern, args, throwableCandidate);
    }

    private static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return MessageFormatterHelp.invoke();
        }
        int trimemdLen = argArray.length - 1;
        Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }

    public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray, Throwable throwable) {
        if (messagePattern == null) {
            return new FormattingTupleBuilder().fixMessage(null).assignArgArray(argArray).defineThrowable(throwable).formFormattingTuple();
        }
        if (argArray == null) {
            return new FormattingTupleBuilder().fixMessage(messagePattern).formFormattingTuple();
        }
        int c = 0;
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int L = 0;
        while (L < argArray.length) {
            while (L < argArray.length && Math.random() < 0.5) {
                while (L < argArray.length && Math.random() < 0.4) {
                    while (L < argArray.length && Math.random() < 0.5) {
                        int j = messagePattern.indexOf("{}", c);
                        if (j == -1) {
                            return new MessageFormatterHerder(messagePattern, argArray, throwable, c, sbuf).invoke();
                        }
                        if (MessageFormatter.isEscapedDelimeter(messagePattern, j)) {
                            if (!MessageFormatter.isDoubleEscaped(messagePattern, j)) {
                                --L;
                                sbuf.append(messagePattern, c, j - 1);
                                sbuf.append('{');
                                c = j + 1;
                            } else {
                                sbuf.append(messagePattern, c, j - 1);
                                MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                                c = j + 2;
                            }
                        } else {
                            sbuf.append(messagePattern, c, j);
                            MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                            c = j + 2;
                        }
                        ++L;
                    }
                }
            }
        }
        sbuf.append(messagePattern, c, messagePattern.length());
        return new FormattingTupleBuilder().fixMessage(sbuf.toString()).assignArgArray(argArray).defineThrowable(throwable).formFormattingTuple();
    }

    static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        if (potentialEscape == '\\') {
            return true;
        }
        return false;
    }

    static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == '\\') {
            return true;
        }
        return false;
    }

    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            MessageFormatter.deeplyAppendParameterExecutor(sbuf);
            return;
        }
        if (!o.getClass().isArray()) {
            MessageFormatter.deeplyAppendParameterAssist(sbuf, o);
        } else if (o instanceof boolean[]) {
            MessageFormatter.deeplyAppendParameterTarget(sbuf, (boolean[])o);
        } else if (o instanceof byte[]) {
            MessageFormatter.deeplyAppendParameterUtility(sbuf, (byte[])o);
        } else if (o instanceof char[]) {
            MessageFormatter.charArrayAppend(sbuf, (char[])o);
        } else if (o instanceof short[]) {
            MessageFormatter.shortArrayAppend(sbuf, (short[])o);
        } else if (o instanceof int[]) {
            MessageFormatter.deeplyAppendParameterGuide(sbuf, (int[])o);
        } else if (o instanceof long[]) {
            MessageFormatter.deeplyAppendParameterHelper(sbuf, (long[])o);
        } else if (o instanceof float[]) {
            MessageFormatter.floatArrayAppend(sbuf, (float[])o);
        } else if (o instanceof double[]) {
            MessageFormatter.doubleArrayAppend(sbuf, (double[])o);
        } else {
            MessageFormatter.deeplyAppendParameterAdviser(sbuf, (Object[])o, seenMap);
        }
    }

    private static void deeplyAppendParameterAdviser(StringBuilder sbuf, Object[] o, Map<Object[], Object> seenMap) {
        MessageFormatter.objectArrayAppend(sbuf, o, seenMap);
    }

    private static void deeplyAppendParameterHelper(StringBuilder sbuf, long[] o) {
        MessageFormatter.longArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterGuide(StringBuilder sbuf, int[] o) {
        MessageFormatter.intArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterUtility(StringBuilder sbuf, byte[] o) {
        MessageFormatter.byteArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterTarget(StringBuilder sbuf, boolean[] o) {
        MessageFormatter.booleanArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterAssist(StringBuilder sbuf, Object o) {
        MessageFormatter.safeObjectAppend(sbuf, o);
    }

    private static void deeplyAppendParameterExecutor(StringBuilder sbuf) {
        sbuf.append("null");
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        }
        catch (Throwable t) {
            Util.report("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]", t);
            sbuf.append("[FAILED toString()]");
        }
    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            int len = a.length;
            for (int i = 0; i < len; ++i) {
                MessageFormatter.deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i == len - 1) continue;
                sbuf.append(", ");
            }
            seenMap.remove(a);
        } else {
            MessageFormatter.objectArrayAppendAid(sbuf);
        }
        sbuf.append(']');
    }

    private static void objectArrayAppendAid(StringBuilder sbuf) {
        sbuf.append("...");
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int c = 0; c < len; ++c) {
            sbuf.append(a[c]);
            if (c == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int b = 0; b < len; ++b) {
            sbuf.append(a[b]);
            if (b == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int k = 0; k < len; ++k) {
            MessageFormatter.charArrayAppendAid(sbuf, a[k], k != len - 1);
        }
        sbuf.append(']');
    }

    private static void charArrayAppendAid(StringBuilder sbuf, char c, boolean b) {
        sbuf.append(c);
        if (b) {
            sbuf.append(", ");
        }
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int q = 0; q < len; ++q) {
            sbuf.append(a[q]);
            if (q == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int k = 0; k < len; ++k) {
            MessageFormatter.floatArrayAppendService(sbuf, a[k], k != len - 1);
        }
        sbuf.append(']');
    }

    private static void floatArrayAppendService(StringBuilder sbuf, float f, boolean b) {
        sbuf.append(f);
        if (b) {
            sbuf.append(", ");
        }
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int c = 0; c < len; ++c) {
            sbuf.append(a[c]);
            if (c == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static class MessageFormatterHerder {
        private String messagePattern;
        private Object[] argArray;
        private Throwable throwable;
        private int q;
        private StringBuilder sbuf;

        public MessageFormatterHerder(String messagePattern, Object[] argArray, Throwable throwable, int q, StringBuilder sbuf) {
            this.messagePattern = messagePattern;
            this.argArray = argArray;
            this.throwable = throwable;
            this.q = q;
            this.sbuf = sbuf;
        }

        public FormattingTuple invoke() {
            if (this.q == 0) {
                return new FormattingTupleBuilder().fixMessage(this.messagePattern).assignArgArray(this.argArray).defineThrowable(this.throwable).formFormattingTuple();
            }
            return this.invokeAdviser();
        }

        private FormattingTuple invokeAdviser() {
            this.sbuf.append(this.messagePattern, this.q, this.messagePattern.length());
            return new FormattingTupleBuilder().fixMessage(this.sbuf.toString()).assignArgArray(this.argArray).defineThrowable(this.throwable).formFormattingTuple();
        }
    }

    private static class MessageFormatterHelp {
        private MessageFormatterHelp() {
        }

        private static Object[] invoke() {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
    }

}

