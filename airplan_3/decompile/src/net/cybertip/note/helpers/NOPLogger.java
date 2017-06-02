/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import net.cybertip.note.helpers.MarkerIgnoringBase;
import net.cybertip.note.helpers.NOPLoggerBuilder;
import net.cybertip.note.helpers.NOPLoggerSupervisor;

public class NOPLogger
extends MarkerIgnoringBase {
    private static final long serialVersionUID = -517220405410904473L;
    public static final NOPLogger NOP_LOGGER = new NOPLoggerBuilder().makeNOPLogger();
    private final NOPLoggerSupervisor NOPLoggerSupervisor = new NOPLoggerSupervisor();

    protected NOPLogger() {
    }

    @Override
    public String grabName() {
        return "NOP";
    }

    @Override
    public final boolean isTraceEnabled() {
        return false;
    }

    @Override
    public final void trace(String msg) {
    }

    @Override
    public final void trace(String format, Object arg) {
    }

    @Override
    public final void trace(String format, Object arg1, Object arg2) {
        this.NOPLoggerSupervisor.trace(format, arg1, arg2);
    }

    @Override
    public final /* varargs */ void trace(String format, Object ... argArray) {
    }

    @Override
    public final void trace(String msg, Throwable t) {
    }

    @Override
    public final boolean isDebugEnabled() {
        return false;
    }

    @Override
    public final void debug(String msg) {
        this.NOPLoggerSupervisor.debug(msg);
    }

    @Override
    public final void debug(String format, Object arg) {
    }

    @Override
    public final void debug(String format, Object arg1, Object arg2) {
    }

    @Override
    public final /* varargs */ void debug(String format, Object ... argArray) {
    }

    @Override
    public final void debug(String msg, Throwable t) {
    }

    @Override
    public final boolean isInfoEnabled() {
        return this.NOPLoggerSupervisor.isInfoEnabled();
    }

    @Override
    public final void info(String msg) {
        this.NOPLoggerSupervisor.info(msg);
    }

    @Override
    public final void info(String format, Object arg1) {
    }

    @Override
    public final void info(String format, Object arg1, Object arg2) {
    }

    @Override
    public final /* varargs */ void info(String format, Object ... argArray) {
        this.NOPLoggerSupervisor.info(format, argArray);
    }

    @Override
    public final void info(String msg, Throwable t) {
    }

    @Override
    public final boolean isWarnEnabled() {
        return false;
    }

    @Override
    public final void warn(String msg) {
    }

    @Override
    public final void warn(String format, Object arg1) {
    }

    @Override
    public final void warn(String format, Object arg1, Object arg2) {
        this.NOPLoggerSupervisor.warn(format, arg1, arg2);
    }

    @Override
    public final /* varargs */ void warn(String format, Object ... argArray) {
        this.NOPLoggerSupervisor.warn(format, argArray);
    }

    @Override
    public final void warn(String msg, Throwable t) {
    }

    @Override
    public final boolean isErrorEnabled() {
        return this.NOPLoggerSupervisor.isErrorEnabled();
    }

    @Override
    public final void error(String msg) {
        this.NOPLoggerSupervisor.error(msg);
    }

    @Override
    public final void error(String format, Object arg1) {
    }

    @Override
    public final void error(String format, Object arg1, Object arg2) {
    }

    @Override
    public final /* varargs */ void error(String format, Object ... argArray) {
    }

    @Override
    public final void error(String msg, Throwable t) {
    }
}

