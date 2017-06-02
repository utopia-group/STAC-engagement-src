/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import net.cybertip.note.helpers.SubstituteLogger;
import net.cybertip.note.helpers.SubstituteLoggerService;

public class SubstituteLoggerServiceBuilder {
    private SubstituteLogger substituteLogger;

    public SubstituteLoggerServiceBuilder fixSubstituteLogger(SubstituteLogger substituteLogger) {
        this.substituteLogger = substituteLogger;
        return this;
    }

    public SubstituteLoggerService makeSubstituteLoggerService() {
        return new SubstituteLoggerService(this.substituteLogger);
    }
}

