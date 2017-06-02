/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.io.Serializable;

public class NOPLoggerSupervisor
implements Serializable {
    public final void trace(String format, Object arg1, Object arg2) {
    }

    public final void debug(String msg) {
    }

    public final boolean isInfoEnabled() {
        return false;
    }

    public final void info(String msg) {
    }

    public final /* varargs */ void info(String format, Object ... argArray) {
    }

    public final void warn(String format, Object arg1, Object arg2) {
    }

    public final /* varargs */ void warn(String format, Object ... argArray) {
    }

    public final boolean isErrorEnabled() {
        return false;
    }

    public final void error(String msg) {
    }
}

