/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import net.cybertip.note.Logger;
import net.cybertip.note.Marker;
import net.cybertip.note.helpers.SubstituteLogger;

public class SubstituteLoggerService {
    private final SubstituteLogger substituteLogger;

    public SubstituteLoggerService(SubstituteLogger substituteLogger) {
        this.substituteLogger = substituteLogger;
    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        this.substituteLogger.delegate().trace(marker, format, arg1, arg2);
    }

    public boolean isDebugEnabled() {
        return this.substituteLogger.delegate().isDebugEnabled();
    }

    public void debug(String format, Object arg1, Object arg2) {
        this.substituteLogger.delegate().debug(format, arg1, arg2);
    }

    public /* varargs */ void debug(String format, Object ... arguments) {
        this.substituteLogger.delegate().debug(format, arguments);
    }

    public void debug(Marker marker, String msg) {
        this.substituteLogger.delegate().debug(marker, msg);
    }

    public void debug(Marker marker, String format, Object arg) {
        this.substituteLogger.delegate().debug(marker, format, arg);
    }

    public void debug(Marker marker, String msg, Throwable t) {
        this.substituteLogger.delegate().debug(marker, msg, t);
    }

    public /* varargs */ void info(String format, Object ... arguments) {
        this.substituteLogger.delegate().info(format, arguments);
    }

    public void info(String msg, Throwable t) {
        this.substituteLogger.delegate().info(msg, t);
    }

    public void info(Marker marker, String format, Object arg) {
        this.substituteLogger.delegate().info(marker, format, arg);
    }

    public void info(Marker marker, String msg, Throwable t) {
        this.substituteLogger.delegate().info(marker, msg, t);
    }

    public boolean isWarnEnabled() {
        return this.substituteLogger.delegate().isWarnEnabled();
    }

    public void warn(String msg, Throwable t) {
        this.substituteLogger.delegate().warn(msg, t);
    }

    public boolean isErrorEnabled() {
        return this.substituteLogger.delegate().isErrorEnabled();
    }

    public void error(String format, Object arg1, Object arg2) {
        this.substituteLogger.delegate().error(format, arg1, arg2);
    }
}

