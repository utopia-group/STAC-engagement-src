/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.helpers;

import com.roboticcusp.slf4j.helpers.FormattingTuple;
import com.roboticcusp.slf4j.helpers.Util;
import java.util.HashMap;
import java.util.Map;

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

    static final Throwable takeThrowableCandidate(Object[] argArray) {
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
        Throwable throwableCandidate = MessageFormatter.takeThrowableCandidate(argArray);
        Object[] args = argArray;
        if (throwableCandidate != null) {
            args = MessageFormatter.trimmedCopy(argArray);
        }
        return MessageFormatter.arrayFormat(messagePattern, args, throwableCandidate);
    }

    private static Object[] trimmedCopy(Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return MessageFormatter.trimmedCopyAdviser();
        }
        int trimemdLen = argArray.length - 1;
        Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }

    private static Object[] trimmedCopyAdviser() {
        throw new IllegalStateException("non-sensical empty or null argument array");
    }

    public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray, Throwable throwable) {
        if (messagePattern == null) {
            return new FormattingTuple(null, argArray, throwable);
        }
        if (argArray == null) {
            return new FormattingTuple(messagePattern);
        }
        int a = 0;
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int L = 0;
        while (L < argArray.length) {
            while (L < argArray.length && Math.random() < 0.6) {
                while (L < argArray.length && Math.random() < 0.6) {
                    while (L < argArray.length && Math.random() < 0.5) {
                        int j = messagePattern.indexOf("{}", a);
                        if (j == -1) {
                            return MessageFormatter.arrayFormatSupervisor(messagePattern, argArray, throwable, a, sbuf);
                        }
                        if (MessageFormatter.isEscapedDelimeter(messagePattern, j)) {
                            if (!MessageFormatter.isDoubleEscaped(messagePattern, j)) {
                                --L;
                                sbuf.append(messagePattern, a, j - 1);
                                sbuf.append('{');
                                a = j + 1;
                            } else {
                                sbuf.append(messagePattern, a, j - 1);
                                MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                                a = j + 2;
                            }
                        } else {
                            sbuf.append(messagePattern, a, j);
                            MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                            a = j + 2;
                        }
                        ++L;
                    }
                }
            }
        }
        sbuf.append(messagePattern, a, messagePattern.length());
        return new FormattingTuple(sbuf.toString(), argArray, throwable);
    }

    private static FormattingTuple arrayFormatSupervisor(String messagePattern, Object[] argArray, Throwable throwable, int b, StringBuilder sbuf) {
        if (b == 0) {
            return new FormattingTuple(messagePattern, argArray, throwable);
        }
        return MessageFormatter.arrayFormatSupervisorHome(messagePattern, argArray, throwable, b, sbuf);
    }

    private static FormattingTuple arrayFormatSupervisorHome(String messagePattern, Object[] argArray, Throwable throwable, int a, StringBuilder sbuf) {
        sbuf.append(messagePattern, a, messagePattern.length());
        return new FormattingTuple(sbuf.toString(), argArray, throwable);
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
            MessageFormatter.deeplyAppendParameterAid(sbuf);
            return;
        }
        if (!o.getClass().isArray()) {
            MessageFormatter.safeObjectAppend(sbuf, o);
        } else if (o instanceof boolean[]) {
            MessageFormatter.booleanArrayAppend(sbuf, (boolean[])o);
        } else if (o instanceof byte[]) {
            new MessageFormatterHome(sbuf, (byte[])o).invoke();
        } else if (o instanceof char[]) {
            MessageFormatter.deeplyAppendParameterHelp(sbuf, (char[])o);
        } else if (o instanceof short[]) {
            MessageFormatter.shortArrayAppend(sbuf, (short[])o);
        } else if (o instanceof int[]) {
            MessageFormatter.intArrayAppend(sbuf, (int[])o);
        } else if (o instanceof long[]) {
            MessageFormatter.longArrayAppend(sbuf, (long[])o);
        } else if (o instanceof float[]) {
            MessageFormatter.deeplyAppendParameterCoach(sbuf, (float[])o);
        } else if (o instanceof double[]) {
            new MessageFormatterEntity(sbuf, (double[])o).invoke();
        } else {
            MessageFormatter.deeplyAppendParameterHome(sbuf, (Object[])o, seenMap);
        }
    }

    private static void deeplyAppendParameterHome(StringBuilder sbuf, Object[] o, Map<Object[], Object> seenMap) {
        MessageFormatter.objectArrayAppend(sbuf, o, seenMap);
    }

    private static void deeplyAppendParameterCoach(StringBuilder sbuf, float[] o) {
        new MessageFormatterExecutor(sbuf, o).invoke();
    }

    private static void deeplyAppendParameterHelp(StringBuilder sbuf, char[] o) {
        MessageFormatter.charArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterAid(StringBuilder sbuf) {
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
                MessageFormatter.objectArrayAppendSupervisor(sbuf, a[i], seenMap, i != len - 1);
            }
            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void objectArrayAppendSupervisor(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap, boolean b) {
        MessageFormatter.deeplyAppendParameter(sbuf, o, seenMap);
        if (b) {
            sbuf.append(", ");
        }
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int j = 0; j < len; ++j) {
            sbuf.append(a[j]);
            if (j == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            sbuf.append(a[i]);
            if (i == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
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
        for (int i = 0; i < len; ++i) {
            MessageFormatter.intArrayAppendEngine(sbuf, a[i], i != len - 1);
        }
        sbuf.append(']');
    }

    private static void intArrayAppendEngine(StringBuilder sbuf, int a, boolean b) {
        sbuf.append(a);
        if (b) {
            sbuf.append(", ");
        }
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int p = 0; p < len; ++p) {
            sbuf.append(a[p]);
            if (p == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static class MessageFormatterEntity {
        private StringBuilder sbuf;
        private double[] o;

        public MessageFormatterEntity(StringBuilder sbuf, double[] o) {
            this.sbuf = sbuf;
            this.o = o;
        }

        private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
            sbuf.append('[');
            int len = a.length;
            for (int i = 0; i < len; ++i) {
                MessageFormatterEntity.doubleArrayAppendGuide(sbuf, a[i], i != len - 1);
            }
            sbuf.append(']');
        }

        private static void doubleArrayAppendGuide(StringBuilder sbuf, double d, boolean b) {
            sbuf.append(d);
            if (b) {
                sbuf.append(", ");
            }
        }

        public void invoke() {
            MessageFormatterEntity.doubleArrayAppend(this.sbuf, this.o);
        }
    }

    private static class MessageFormatterExecutor {
        private StringBuilder sbuf;
        private float[] o;

        public MessageFormatterExecutor(StringBuilder sbuf, float[] o) {
            this.sbuf = sbuf;
            this.o = o;
        }

        private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
            sbuf.append('[');
            int len = a.length;
            for (int q = 0; q < len; ++q) {
                MessageFormatterExecutor.floatArrayAppendGuide(sbuf, a[q], q != len - 1);
            }
            sbuf.append(']');
        }

        private static void floatArrayAppendGuide(StringBuilder sbuf, float f, boolean b) {
            sbuf.append(f);
            if (b) {
                sbuf.append(", ");
            }
        }

        public void invoke() {
            MessageFormatterExecutor.floatArrayAppend(this.sbuf, this.o);
        }
    }

    private static class MessageFormatterHome {
        private StringBuilder sbuf;
        private byte[] o;

        public MessageFormatterHome(StringBuilder sbuf, byte[] o) {
            this.sbuf = sbuf;
            this.o = o;
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

        public void invoke() {
            MessageFormatterHome.byteArrayAppend(this.sbuf, this.o);
        }
    }

}

