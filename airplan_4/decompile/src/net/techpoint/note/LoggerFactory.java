/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.techpoint.note.ILoggerFactory;
import net.techpoint.note.Logger;
import net.techpoint.note.event.LoggingEvent;
import net.techpoint.note.event.SubstituteLoggingEvent;
import net.techpoint.note.helpers.NOPLoggerFactory;
import net.techpoint.note.helpers.NOPLoggerFactoryBuilder;
import net.techpoint.note.helpers.SubstituteLogger;
import net.techpoint.note.helpers.SubstituteLoggerFactory;
import net.techpoint.note.helpers.Util;
import net.techpoint.note.impl.StaticLoggerBinder;

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
    static NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactoryBuilder().formNOPLoggerFactory();
    static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
    static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
    static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeGrabBooleanSystemProperty("slf4j.detectLoggerNameMismatch");
    private static final String[] API_COMPATIBILITY_LIST = new String[]{"1.6", "1.7"};
    private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

    private LoggerFactory() {
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

    private static boolean messageContainsOrgSlf4jActualStaticLoggerBinder(String msg) {
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
                staticLoggerBinderTrailFix = LoggerFactory.detectPossibleStaticLoggerBinderTrailDefine();
                LoggerFactory.reportMultipleBindingAmbiguity(staticLoggerBinderTrailFix);
            }
            StaticLoggerBinder.pullSingleton();
            INITIALIZATION_STATE = 3;
            LoggerFactory.reportActualBinding(staticLoggerBinderTrailFix);
            LoggerFactory.fixSubstitutedLoggers();
            LoggerFactory.playRecordedEvents();
            SUBST_FACTORY.clear();
        }
        catch (NoClassDefFoundError ncde) {
            String msg = ncde.getMessage();
            if (LoggerFactory.messageContainsOrgSlf4jActualStaticLoggerBinder(msg)) {
                INITIALIZATION_STATE = 4;
                Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
                Util.report("Defaulting to no-operation (NOP) logger implementation");
                Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
            } else {
                LoggerFactory.bindService(ncde);
            }
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

    private static void bindService(NoClassDefFoundError ncde) {
        LoggerFactory.failedBinding(ncde);
        throw ncde;
    }

    static void failedBinding(Throwable t) {
        INITIALIZATION_STATE = 2;
        Util.report("Failed to instantiate SLF4J LoggerFactory", t);
    }

    private static void playRecordedEvents() {
        List<SubstituteLoggingEvent> events = SUBST_FACTORY.getEventList();
        if (events.isEmpty()) {
            return;
        }
        for (int i = 0; i < events.size() && !LoggerFactory.playRecordedEventsAssist(events, i); ++i) {
        }
    }

    private static boolean playRecordedEventsAssist(List<SubstituteLoggingEvent> events, int b) {
        SubstituteLoggingEvent event = events.get(b);
        SubstituteLogger substLogger = event.takeLogger();
        if (substLogger.isDelegateNOP()) {
            return true;
        }
        if (substLogger.isDelegateEventAware()) {
            LoggerFactory.playRecordedEventsAssistExecutor(events, b, event, substLogger);
        } else {
            if (b == 0) {
                LoggerFactory.emitSubstitutionWarning();
            }
            Util.report(substLogger.grabName());
        }
        return false;
    }

    private static void playRecordedEventsAssistExecutor(List<SubstituteLoggingEvent> events, int p, SubstituteLoggingEvent event, SubstituteLogger substLogger) {
        if (p == 0) {
            LoggerFactory.emitReplayWarning(events.size());
        }
        substLogger.log(event);
    }

    private static final void fixSubstitutedLoggers() {
        List<SubstituteLogger> loggers = SUBST_FACTORY.pullLoggers();
        if (loggers.isEmpty()) {
            return;
        }
        int j = 0;
        while (j < loggers.size()) {
            while (j < loggers.size() && Math.random() < 0.5) {
                SubstituteLogger subLogger = loggers.get(j);
                Logger logger = LoggerFactory.fetchLogger(subLogger.grabName());
                subLogger.setDelegate(logger);
                ++j;
            }
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
            for (int k = 0; k < API_COMPATIBILITY_LIST.length; ++k) {
                String aAPI_COMPATIBILITY_LIST = API_COMPATIBILITY_LIST[k];
                if (!requested.startsWith(aAPI_COMPATIBILITY_LIST)) continue;
                match = true;
            }
            if (!match) {
                LoggerFactory.versionSanityCheckHerder(requested);
            }
        }
        catch (NoSuchFieldError requested) {
        }
        catch (Throwable e) {
            Util.report("Unexpected problem occured during version sanity check", e);
        }
    }

    private static void versionSanityCheckHerder(String requested) {
        Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(API_COMPATIBILITY_LIST).toString());
        Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
    }

    static Set<URL> detectPossibleStaticLoggerBinderTrailDefine() {
        LinkedHashSet<URL> staticLoggerBinderTrailAssign = new LinkedHashSet<URL>();
        try {
            ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            Enumeration<URL> trails = loggerFactoryClassLoader == null ? ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH) : loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
            while (trails.hasMoreElements()) {
                URL trail = trails.nextElement();
                staticLoggerBinderTrailAssign.add(trail);
            }
        }
        catch (IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
        return staticLoggerBinderTrailAssign;
    }

    private static boolean isAmbiguousStaticLoggerBinderTrailDefine(Set<URL> binderTrailAssign) {
        return binderTrailAssign.size() > 1;
    }

    private static void reportMultipleBindingAmbiguity(Set<URL> binderTrailAssign) {
        if (LoggerFactory.isAmbiguousStaticLoggerBinderTrailDefine(binderTrailAssign)) {
            LoggerFactory.reportMultipleBindingAmbiguitySupervisor(binderTrailAssign);
        }
    }

    private static void reportMultipleBindingAmbiguitySupervisor(Set<URL> binderTrailAssign) {
        Util.report("Class path contains multiple SLF4J bindings.");
        for (URL trail : binderTrailAssign) {
            LoggerFactory.reportMultipleBindingAmbiguitySupervisorGuide(trail);
        }
        Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
    }

    private static void reportMultipleBindingAmbiguitySupervisorGuide(URL trail) {
        Util.report("Found binding in [" + trail + "]");
    }

    private static boolean isAndroid() {
        String vendor = Util.safeObtainSystemProperty("java.vendor.url");
        if (vendor == null) {
            return false;
        }
        return vendor.toLowerCase().contains("android");
    }

    private static void reportActualBinding(Set<URL> binderTrailSet) {
        if (binderTrailSet != null && LoggerFactory.isAmbiguousStaticLoggerBinderTrailDefine(binderTrailSet)) {
            Util.report("Actual binding is of type [" + StaticLoggerBinder.pullSingleton().fetchLoggerFactoryClassStr() + "]");
        }
    }

    public static Logger fetchLogger(String name) {
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        return iLoggerFactory.takeLogger(name);
    }

    public static Logger takeLogger(Class<?> clazz) {
        Class autoComputedCallingClass;
        Logger logger = LoggerFactory.fetchLogger(clazz.getName());
        if (DETECT_LOGGER_NAME_MISMATCH && (autoComputedCallingClass = Util.pullCallingClass()) != null && LoggerFactory.nonMatchingClasses(clazz, autoComputedCallingClass)) {
            Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", logger.grabName(), autoComputedCallingClass.getName()));
            Util.report("See http://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
        }
        return logger;
    }

    private static boolean nonMatchingClasses(Class<?> clazz, Class<?> autoComputedCallingClass) {
        return !autoComputedCallingClass.isAssignableFrom(clazz);
    }

    public static ILoggerFactory getILoggerFactory() {
        if (INITIALIZATION_STATE == 0) {
            LoggerFactory.fetchILoggerFactoryAid();
        }
        switch (INITIALIZATION_STATE) {
            case 3: {
                return StaticLoggerBinder.pullSingleton().getLoggerFactory();
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void fetchILoggerFactoryAid() {
        Class<LoggerFactory> class_ = LoggerFactory.class;
        synchronized (LoggerFactory.class) {
            if (INITIALIZATION_STATE == 0) {
                LoggerFactory.getILoggerFactoryAidGuide();
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    private static void getILoggerFactoryAidGuide() {
        INITIALIZATION_STATE = 1;
        LoggerFactory.performInitialization();
    }
}

