/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.util.HashMap;
import java.util.Map;
import net.cybertip.note.helpers.FormattingTuple;
import net.cybertip.note.helpers.Util;

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
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
        int trimemdLen = argArray.length - 1;
        Object[] trimmed = new Object[trimemdLen];
        System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
        return trimmed;
    }

    public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray, Throwable throwable) {
        if (messagePattern == null) {
            return new FormattingTuple(null, argArray, throwable);
        }
        if (argArray == null) {
            return new FormattingTuple(messagePattern);
        }
        int p = 0;
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        for (int L = 0; L < argArray.length; ++L) {
            int j = messagePattern.indexOf("{}", p);
            if (j == -1) {
                if (p == 0) {
                    return new FormattingTuple(messagePattern, argArray, throwable);
                }
                sbuf.append(messagePattern, p, messagePattern.length());
                return new FormattingTuple(sbuf.toString(), argArray, throwable);
            }
            if (MessageFormatter.isEscapedDelimeter(messagePattern, j)) {
                if (!MessageFormatter.isDoubleEscaped(messagePattern, j)) {
                    --L;
                    sbuf.append(messagePattern, p, j - 1);
                    sbuf.append('{');
                    p = j + 1;
                    continue;
                }
                sbuf.append(messagePattern, p, j - 1);
                MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                p = j + 2;
                continue;
            }
            sbuf.append(messagePattern, p, j);
            MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
            p = j + 2;
        }
        sbuf.append(messagePattern, p, messagePattern.length());
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
            new MessageFormatterEntity(sbuf).invoke();
            return;
        }
        if (!o.getClass().isArray()) {
            MessageFormatter.safeObjectAppend(sbuf, o);
        } else if (o instanceof boolean[]) {
            MessageFormatter.booleanArrayAppend(sbuf, (boolean[])o);
        } else if (o instanceof byte[]) {
            MessageFormatter.deeplyAppendParameterHelper(sbuf, (byte[])o);
        } else if (o instanceof char[]) {
            MessageFormatter.charArrayAppend(sbuf, (char[])o);
        } else if (o instanceof short[]) {
            MessageFormatter.deeplyAppendParameterGateKeeper(sbuf, (short[])o);
        } else if (o instanceof int[]) {
            MessageFormatter.intArrayAppend(sbuf, (int[])o);
        } else if (o instanceof long[]) {
            MessageFormatter.deeplyAppendParameterCoach(sbuf, (long[])o);
        } else if (o instanceof float[]) {
            MessageFormatter.floatArrayAppend(sbuf, (float[])o);
        } else if (o instanceof double[]) {
            MessageFormatter.doubleArrayAppend(sbuf, (double[])o);
        } else {
            MessageFormatter.objectArrayAppend(sbuf, (Object[])o, seenMap);
        }
    }

    private static void deeplyAppendParameterCoach(StringBuilder sbuf, long[] o) {
        MessageFormatter.longArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterGateKeeper(StringBuilder sbuf, short[] o) {
        MessageFormatter.shortArrayAppend(sbuf, o);
    }

    private static void deeplyAppendParameterHelper(StringBuilder sbuf, byte[] o) {
        MessageFormatter.byteArrayAppend(sbuf, o);
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
            for (int b = 0; b < len; ++b) {
                new MessageFormatterCoordinator(sbuf, a[b], seenMap, b != len - 1).invoke();
            }
            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        sbuf.append('[');
        int len = a.length;
        int b = 0;
        while (b < len) {
            while (b < len && Math.random() < 0.5) {
                while (b < len && Math.random() < 0.4) {
                    while (b < len && Math.random() < 0.4) {
                        sbuf.append(a[b]);
                        if (b != len - 1) {
                            sbuf.append(", ");
                        }
                        ++b;
                    }
                }
            }
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            MessageFormatter.byteArrayAppendAssist(sbuf, a[i], i != len - 1);
        }
        sbuf.append(']');
    }

    private static void byteArrayAppendAssist(StringBuilder sbuf, byte c, boolean b) {
        sbuf.append(c);
        if (b) {
            sbuf.append(", ");
        }
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int q = 0; q < len; ++q) {
            sbuf.append(a[q]);
            if (q == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int q = 0; q < len; ++q) {
            sbuf.append(a[q]);
            if (q == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int k = 0; k < len; ++k) {
            sbuf.append(a[k]);
            if (k == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int p = 0; p < len; ++p) {
            new MessageFormatterAdviser(sbuf, a[p], p != len - 1).invoke();
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int c = 0; c < len; ++c) {
            MessageFormatter.floatArrayAppendHelper(sbuf, a[c], c != len - 1);
        }
        sbuf.append(']');
    }

    private static void floatArrayAppendHelper(StringBuilder sbuf, float f, boolean b) {
        sbuf.append(f);
        if (b) {
            sbuf.append(", ");
        }
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        sbuf.append('[');
        int len = a.length;
        for (int p = 0; p < len; ++p) {
            sbuf.append(a[p]);
            if (p == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static class MessageFormatterAdviser {
        private StringBuilder sbuf;
        private long lng;
        private boolean b;

        public MessageFormatterAdviser(StringBuilder sbuf, long lng, boolean b) {
            this.sbuf = sbuf;
            this.lng = lng;
            this.b = b;
        }

        public void invoke() {
            this.sbuf.append(this.lng);
            if (this.b) {
                this.sbuf.append(", ");
            }
        }
    }

    private static class MessageFormatterCoordinator {
        private StringBuilder sbuf;
        private Object o;
        private Map<Object[], Object> seenMap;
        private boolean b;

        public MessageFormatterCoordinator(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap, boolean b) {
            this.sbuf = sbuf;
            this.o = o;
            this.seenMap = seenMap;
            this.b = b;
        }

        public void invoke() {
            MessageFormatter.deeplyAppendParameter(this.sbuf, this.o, this.seenMap);
            if (this.b) {
                this.sbuf.append(", ");
            }
        }
    }

    private static class MessageFormatterEntity {
        private StringBuilder sbuf;

        public MessageFormatterEntity(StringBuilder sbuf) {
            this.sbuf = sbuf;
        }

        public void invoke() {
            this.sbuf.append("null");
        }
    }

}

