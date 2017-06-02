/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.manager;

import java.util.Arrays;
import java.util.Random;

public class CellFormatter {
    private static Random random = new Random();
    private int width;

    public CellFormatter(int length) {
        this.width = random.nextInt(length);
        if (this.width < 5) {
            this.width = 5;
        }
    }

    public String format(String s, int len, Justification j, boolean adjust) {
        String result = s;
        if (adjust && (len = this.width - 5 + random.nextInt(this.width)) < 4) {
            len = 4;
        }
        if (s.length() > len && len > 0) {
            result = s.substring(0, len - 3) + "...";
        } else if (len > 0) {
            char[] spaceArray = new char[len];
            Arrays.fill(spaceArray, ' ');
            String whitespace = new String(spaceArray);
            if (j.equals((Object)Justification.FIRST)) {
                result = s + whitespace.substring(s.length());
            } else if (j.equals((Object)Justification.LAST)) {
                result = whitespace.substring(s.length()) + s;
            } else {
                int buffer = (len - s.length()) / 2;
                result = whitespace.substring(0, buffer) + s + whitespace.substring(0, buffer);
                if (len % 2 != 0) {
                    result = result + " ";
                }
            }
        }
        return result;
    }

    static enum Justification {
        FIRST,
        LAST,
        CENTER;
        

        private Justification() {
        }
    }

}

