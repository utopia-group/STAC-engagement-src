/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note;

import java.io.Closeable;
import java.util.Map;
import net.cybertip.note.actual.StaticMDCBinder;
import net.cybertip.note.helpers.NOPMDCAdapter;
import net.cybertip.note.helpers.Util;
import net.cybertip.note.service.MDCAdapter;

public class MDC {
    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;

    MDC() {
    }

    private static MDCAdapter bwCompatibleFetchMDCAdapterFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMDCBinder.obtainSingleton().grabMDCA();
        }
        catch (NoSuchMethodError nsme) {
            return StaticMDCBinder.SINGLETON.grabMDCA();
        }
    }

    public static void insert(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            MDCGuide.invoke();
        }
        mdcAdapter.insert(key, val);
    }

    public static MDCCloseable placeCloseable(String key, String val) throws IllegalArgumentException {
        MDC.insert(key, val);
        return new MDCCloseable(key);
    }

    public static String take(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            return MDC.obtainCoach();
        }
        return mdcAdapter.obtain(key);
    }

    private static String obtainCoach() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static void remove(String key) throws IllegalArgumentException {
        if (key == null) {
            MDC.removeCoach();
        }
        if (mdcAdapter == null) {
            MDC.removeHelper();
        }
        mdcAdapter.remove(key);
    }

    private static void removeHelper() {
        MDCGateKeeper.invoke();
    }

    private static void removeCoach() {
        throw new IllegalArgumentException("key parameter cannot be null");
    }

    public static void clear() {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        mdcAdapter.clear();
    }

    public static Map<String, String> obtainCopyOfContextMap() {
        if (mdcAdapter == null) {
            return MDCEntity.invoke();
        }
        return mdcAdapter.takeCopyOfContextMap();
    }

    public static void assignContextMap(Map<String, String> contextMap) {
        if (mdcAdapter == null) {
            MDC.defineContextMapGateKeeper();
        }
        mdcAdapter.setContextMap(contextMap);
    }

    private static void defineContextMapGateKeeper() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static MDCAdapter takeMDCAdapter() {
        return mdcAdapter;
    }

    static {
        try {
            mdcAdapter = MDC.bwCompatibleFetchMDCAdapterFromBinder();
        }
        catch (NoClassDefFoundError ncde) {
            mdcAdapter = new NOPMDCAdapter();
            String msg = ncde.getMessage();
            if (msg != null && msg.contains("StaticMDCBinder")) {
                Util.report("Failed to load class \"org.slf4j.impl.StaticMDCBinder\".");
                Util.report("Defaulting to no-operation MDCAdapter implementation.");
                Util.report("See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.");
            }
            throw ncde;
        }
        catch (Exception e) {
            Util.report("MDC binding unsuccessful.", e);
        }
    }

    private static class MDCEntity {
        private MDCEntity() {
        }

        private static Map<String, String> invoke() {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
    }

    private static class MDCGateKeeper {
        private MDCGateKeeper() {
        }

        private static void invoke() {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
    }

    private static class MDCGuide {
        private MDCGuide() {
        }

        private static void invoke() {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
    }

    public static class MDCCloseable
    implements Closeable {
        private final String key;

        private MDCCloseable(String key) {
            this.key = key;
        }

        @Override
        public void close() {
            MDC.remove(this.key);
        }
    }

}

