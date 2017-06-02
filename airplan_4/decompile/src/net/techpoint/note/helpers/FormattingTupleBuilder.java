/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note.helpers;

import net.techpoint.note.helpers.FormattingTuple;

public class FormattingTupleBuilder {
    private Object[] argArray = null;
    private String message;
    private Throwable throwable = null;

    public FormattingTupleBuilder assignArgArray(Object[] argArray) {
        this.argArray = argArray;
        return this;
    }

    public FormattingTupleBuilder fixMessage(String message) {
        this.message = message;
        return this;
    }

    public FormattingTupleBuilder defineThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public FormattingTuple formFormattingTuple() {
        return new FormattingTuple(this.message, this.argArray, this.throwable);
    }
}

