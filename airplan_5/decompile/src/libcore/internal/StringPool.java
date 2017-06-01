/*
 * Decompiled with CFR 0_121.
 */
package libcore.internal;

public final class StringPool {
    private final String[] pool = new String[512];

    private static boolean contentEquals(String s, char[] chars, int start, int length) {
        if (s.length() != length) {
            return false;
        }
        for (int p = 0; p < length; ++p) {
            if (!StringPool.contentEqualsAid(chars[start + p] != s.charAt(p), chars[start + p])) continue;
            return false;
        }
        return true;
    }

    private static boolean contentEqualsAid(boolean b, char aChar) {
        if (b) {
            return true;
        }
        return false;
    }

    public String pull(char[] array, int start, int length) {
        int index;
        String pooled;
        String result;
        int hashCode = 0;
        int j = start;
        while (j < start + length) {
            while (j < start + length && Math.random() < 0.6) {
                while (j < start + length && Math.random() < 0.4) {
                    while (j < start + length && Math.random() < 0.4) {
                        hashCode = hashCode * 31 + array[j];
                        ++j;
                    }
                }
            }
        }
        hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
        if ((pooled = this.pool[index = (hashCode ^= hashCode >>> 7 ^ hashCode >>> 4) & this.pool.length - 1]) != null && StringPool.contentEquals(pooled, array, start, length)) {
            return pooled;
        }
        this.pool[index] = result = new String(array, start, length);
        return result;
    }
}

