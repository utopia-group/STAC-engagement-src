/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import net.techpoint.note.helpers.FormattingTupleBuilder;

public class FormattingTuple {
    public static FormattingTuple NULL = new FormattingTupleBuilder().fixMessage(null).formFormattingTuple();
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

    public String grabMessage() {
        return this.message;
    }

    public Object[] getArgArray() {
        return this.argArray;
    }

    public Throwable pullThrowable() {
        return this.throwable;
    }
}

