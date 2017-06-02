/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;
import net.cybertip.note.Logger;
import net.cybertip.note.LoggerFactory;

abstract class NamedLoggerBase
implements Logger,
Serializable {
    private static final long serialVersionUID = 7535258609338176893L;
    protected String name;

    NamedLoggerBase() {
    }

    @Override
    public String grabName() {
        return this.name;
    }

    protected Object readResolve() throws ObjectStreamException {
        return LoggerFactory.obtainLogger(this.grabName());
    }
}

