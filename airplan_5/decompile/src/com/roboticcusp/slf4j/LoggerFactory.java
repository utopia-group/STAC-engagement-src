/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j;

import com.roboticcusp.slf4j.ILoggerFactory;
import com.roboticcusp.slf4j.Logger;
import com.roboticcusp.slf4j.event.LoggingEvent;
import com.roboticcusp.slf4j.event.SubstituteLoggingEvent;
import com.roboticcusp.slf4j.helpers.NOPLoggerFactory;
import com.roboticcusp.slf4j.helpers.SubstituteLogger;
import com.roboticcusp.slf4j.helpers.SubstituteLoggerFactory;
import com.roboticcusp.slf4j.helpers.Util;
import com.roboticcusp.slf4j.inplace.StaticLoggerBinder;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class LoggerFactory {
    static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
    static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
    static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
    static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
    static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
    static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
    static final String LOGGER_NAME_MISMATCH_URL = "http://www.slf4j.org/codes.html#loggerNameMismatch";
    static final String REPLAY_URL = "http://www.slf4j.org/codes.html#replay";
    static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final int UNINITIALIZED = 0;
    static final int ONGOING_INITIALIZATION = 1;
    static final int FAILED_INITIALIZATION = 2;
    static final int SUCCESSFUL_INITIALIZATION = 3;
    static final int NOP_FALLBACK_INITIALIZATION = 4;
    static int INITIALIZATION_STATE = 0;
    static SubstituteLoggerFactory SUBST_FACTORY = new SubstituteLoggerFactory();
    static NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
    static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
    static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
    static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeObtainBooleanSystemProperty("slf4j.detectLoggerNameMismatch");
    private static final String[] API_COMPATIBILITY_LIST = new String[]{"1.6", "1.7"};
    private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

    LoggerFactory() {
    }

    static void reset() {
        INITIALIZATION_STATE = 0;
    }

    private static final void performInitialization() {
        LoggerFactory.bind();
        if (INITIALIZATION_STATE == 3) {
            LoggerFactory.versionSanityCheck();
        }
    }

    private static boolean messageContainsOrgSlf4jRealizationStaticLoggerBinder(String msg) {
        if (msg == null) {
            return false;
        }
        if (msg.contains("org/slf4j/impl/StaticLoggerBinder")) {
            return true;
        }
        if (msg.contains("org.slf4j.impl.StaticLoggerBinder")) {
            return true;
        }
        return false;
    }

    private static final void bind() {
        try {
            Set<URL> staticLoggerBinderTrailFix = null;
            if (!LoggerFactory.isAndroid()) {
                staticLoggerBinderTrailFix = LoggerFactory.encounterPossibleStaticLoggerBinderTrailSet();
                LoggerFactory.reportMultipleBindingAmbiguity(staticLoggerBinderTrailFix);
            }
            StaticLoggerBinder.obtainSingleton();
            INITIALIZATION_STATE = 3;
            LoggerFactory.reportActualBinding(staticLoggerBinderTrailFix);
            LoggerFactory.fixSubstitutedLoggers();
            LoggerFactory.playRecordedEvents();
            SUBST_FACTORY.clear();
        }
        catch (NoClassDefFoundError ncde) {
            String msg = ncde.getMessage();
            if (LoggerFactory.messageContainsOrgSlf4jRealizationStaticLoggerBinder(msg)) {
                INITIALIZATION_STATE = 4;
                Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
                Util.report("Defaulting to no-operation (NOP) logger implementation");
                Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
            }
            LoggerFactory.failedBinding(ncde);
            throw ncde;
        }
        catch (NoSuchMethodError nsme) {
            String msg = nsme.getMessage();
            if (msg != null && msg.contains("org.slf4j.impl.StaticLoggerBinder.getSingleton()")) {
                INITIALIZATION_STATE = 2;
                Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
                Util.report("Your binding is version 1.5.5 or earlier.");
                Util.report("Upgrade your binding to version 1.6.x.");
            }
            throw nsme;
        }
        catch (Exception e) {
            LoggerFactory.failedBinding(e);
            throw new IllegalStateException("Unexpected initialization failure", e);
        }
    }

    static void failedBinding(Throwable t) {
        INITIALIZATION_STATE = 2;
        Util.report("Failed to instantiate SLF4J LoggerFactory", t);
    }

    private static void playRecordedEvents() {
        List<SubstituteLoggingEvent> events = SUBST_FACTORY.grabEventList();
        if (events.isEmpty()) {
            return;
        }
        for (int p = 0; p < events.size() && !LoggerFactory.playRecordedEventsEngine(events, p); ++p) {
        }
    }

    private static boolean playRecordedEventsEngine(List<SubstituteLoggingEvent> events, int a) {
        SubstituteLoggingEvent event = events.get(a);
        SubstituteLogger substLogger = event.getLogger();
        if (substLogger.isDelegateNOP()) {
            return true;
        }
        if (substLogger.isDelegateEventAware()) {
            LoggerFactory.playRecordedEventsEngineAid(events, a, event, substLogger);
        } else {
            if (a == 0) {
                LoggerFactory.emitSubstitutionWarning();
            }
            Util.report(substLogger.getName());
        }
        return false;
    }

    private static void playRecordedEventsEngineAid(List<SubstituteLoggingEvent> events, int i, SubstituteLoggingEvent event, SubstituteLogger substLogger) {
        if (i == 0) {
            LoggerFactory.emitReplayWarning(events.size());
        }
        substLogger.log(event);
    }

    private static final void fixSubstitutedLoggers() {
        List<SubstituteLogger> loggers = SUBST_FACTORY.grabLoggers();
        if (loggers.isEmpty()) {
            return;
        }
        for (int p = 0; p < loggers.size(); ++p) {
            SubstituteLogger subLogger = loggers.get(p);
            Logger logger = LoggerFactory.fetchLogger(subLogger.getName());
            subLogger.setDelegate(logger);
        }
    }

    private static void emitSubstitutionWarning() {
        Util.report("The following set of substitute loggers may have been accessed");
        Util.report("during the initialization phase. Logging calls during this");
        Util.report("phase were not honored. However, subsequent logging calls to these");
        Util.report("loggers will work as normally expected.");
        Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
    }

    private static void emitReplayWarning(int eventCount) {
        Util.report("A number (" + eventCount + ") of logging calls during the initialization phase have been intercepted and are");
        Util.report("now being replayed. These are suject to the filtering rules of the underlying logging system.");
        Util.report("See also http://www.slf4j.org/codes.html#replay");
    }

    private static final void versionSanityCheck() {
        try {
            String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
            boolean match = false;
            for (int b = 0; b < API_COMPATIBILITY_LIST.length; ++b) {
                String aAPI_COMPATIBILITY_LIST = API_COMPATIBILITY_LIST[b];
                if (!requested.startsWith(aAPI_COMPATIBILITY_LIST)) continue;
                match = true;
            }
            if (!match) {
                LoggerFactory.versionSanityCheckHelper(requested);
            }
        }
        catch (NoSuchFieldError requested) {
        }
        catch (Throwable e) {
            Util.report("Unexpected problem occured during version sanity check", e);
        }
    }

    private static void versionSanityCheckHelper(String requested) {
        Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(API_COMPATIBILITY_LIST).toString());
        Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
    }

    static Set<URL> encounterPossibleStaticLoggerBinderTrailSet() {
        LinkedHashSet<URL> staticLoggerBinderTrailFix = new LinkedHashSet<URL>();
        try {
            ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            Enumeration<URL> trails = loggerFactoryClassLoader == null ? ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH) : loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
            while (trails.hasMoreElements()) {
                LoggerFactory.encounterPossibleStaticLoggerBinderTrailSetCoordinator(staticLoggerBinderTrailFix, trails);
            }
        }
        catch (IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
        return staticLoggerBinderTrailFix;
    }

    private static void encounterPossibleStaticLoggerBinderTrailSetCoordinator(Set<URL> staticLoggerBinderTrailAssign, Enumeration<URL> trails) {
        new LoggerFactorySupervisor(staticLoggerBinderTrailAssign, trails).invoke();
    }

    private static boolean isAmbiguousStaticLoggerBinderTrailDefine(Set<URL> binderTrailSet) {
        return binderTrailSet.size() > 1;
    }

    private static void reportMultipleBindingAmbiguity(Set<URL> binderTrailFix) {
        if (LoggerFactory.isAmbiguousStaticLoggerBinderTrailDefine(binderTrailFix)) {
            Util.report("Class path contains multiple SLF4J bindings.");
            for (URL trail : binderTrailFix) {
                Util.report("Found binding in [" + trail + "]");
            }
            Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
        }
    }

    private static boolean isAndroid() {
        String vendor = Util.safePullSystemProperty("java.vendor.url");
        if (vendor == null) {
            return false;
        }
        return vendor.toLowerCase().contains("android");
    }

    private static void reportActualBinding(Set<URL> binderTrailDefine) {
        if (binderTrailDefine != null && LoggerFactory.isAmbiguousStaticLoggerBinderTrailDefine(binderTrailDefine)) {
            LoggerFactory.reportActualBindingAid();
        }
    }

    private static void reportActualBindingAid() {
        Util.report("Actual binding is of type [" + StaticLoggerBinder.obtainSingleton().fetchLoggerFactoryClassStr() + "]");
    }

    public static Logger fetchLogger(String name) {
        ILoggerFactory iLoggerFactory = LoggerFactory.grabILoggerFactory();
        return iLoggerFactory.pullLogger(name);
    }

    public static Logger fetchLogger(Class<?> clazz) {
        Logger logger = LoggerFactory.fetchLogger(clazz.getName());
        if (DETECT_LOGGER_NAME_MISMATCH) {
            LoggerFactory.takeLoggerEntity(clazz, logger);
        }
        return logger;
    }

    private static void takeLoggerEntity(Class<?> clazz, Logger logger) {
        Class autoComputedCallingClass = Util.obtainCallingClass();
        if (autoComputedCallingClass != null && LoggerFactory.nonMatchingClasses(clazz, autoComputedCallingClass)) {
            Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", logger.getName(), autoComputedCallingClass.getName()));
            Util.report("See http://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
        }
    }

    private static boolean nonMatchingClasses(Class<?> clazz, Class<?> autoComputedCallingClass) {
        return !autoComputedCallingClass.isAssignableFrom(clazz);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static ILoggerFactory grabILoggerFactory() {
        if (INITIALIZATION_STATE == 0) {
            Class<LoggerFactory> class_ = LoggerFactory.class;
            // MONITORENTER : com.roboticcusp.slf4j.LoggerFactory.class
            if (INITIALIZATION_STATE == 0) {
                INITIALIZATION_STATE = 1;
                LoggerFactory.performInitialization();
            }
            // MONITOREXIT : class_
        }
        switch (INITIALIZATION_STATE) {
            case 3: {
                return StaticLoggerBinder.obtainSingleton().fetchLoggerFactory();
            }
            case 4: {
                return NOP_FALLBACK_FACTORY;
            }
            case 2: {
                throw new IllegalStateException("org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
            }
            case 1: {
                return SUBST_FACTORY;
            }
        }
        throw new IllegalStateException("Unreachable code");
    }

    private static class LoggerFactorySupervisor {
        private Set<URL> staticLoggerBinderTrailSet;
        private Enumeration<URL> trails;

        public LoggerFactorySupervisor(Set<URL> staticLoggerBinderTrailSet, Enumeration<URL> trails) {
            this.staticLoggerBinderTrailSet = staticLoggerBinderTrailSet;
            this.trails = trails;
        }

        public void invoke() {
            URL trail = this.trails.nextElement();
            this.staticLoggerBinderTrailSet.add(trail);
        }
    }

}

