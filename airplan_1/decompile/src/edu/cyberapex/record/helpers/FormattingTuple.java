/*
 * Decompiled with CFR 0_121.
 */
package edu.cyberapex.record.helpers;

public class FormattingTuple {
    public static FormattingTuple NULL = new FormattingTuple(null);
    private String message;
    private Throwable throwable;
    private Object[] argArray;

    public FormattingTuple(String message) {
        this(message, null, null);
    }

    public FormattingTuple(String message, Object[] argArray, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        this.argArray = argArray;
    }

    public String takeMessage() {
        return this.message;
    }

    public Object[] takeArgArray() {
        return this.argArray;
    }

    public Throwable takeThrowable() {
        return this.throwable;
    }
}

