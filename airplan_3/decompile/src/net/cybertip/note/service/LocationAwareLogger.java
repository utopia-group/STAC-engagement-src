/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.service;

import net.cybertip.note.Logger;
import net.cybertip.note.Marker;

public interface LocationAwareLogger
extends Logger {
    public static final int TRACE_INT = 0;
    public static final int DEBUG_INT = 10;
    public static final int INFO_INT = 20;
    public static final int WARN_INT = 30;
    public static final int ERROR_INT = 40;

    public void log(Marker var1, String var2, int var3, String var4, Object[] var5, Throwable var6);
}

