/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.note;

import java.io.Closeable;
import java.util.Map;
import net.techpoint.note.helpers.NOPMDCAdapter;
import net.techpoint.note.helpers.Util;
import net.techpoint.note.impl.StaticMDCBinder;
import net.techpoint.note.pack.MDCAdapter;

public class MDC {
    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;

    private MDC() {
    }

    private static MDCAdapter bwCompatibleObtainMDCAdapterFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMDCBinder.takeSingleton().getMDCA();
        }
        catch (NoSuchMethodError nsme) {
            return StaticMDCBinder.SINGLETON.getMDCA();
        }
    }

    public static void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            MDC.insertGuide();
        }
        mdcAdapter.put(key, val);
    }

    private static void insertGuide() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static MDCCloseable insertCloseable(String key, String val) throws IllegalArgumentException {
        MDC.put(key, val);
        return new MDCCloseable(key);
    }

    public static String grab(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            return MDC.pullGuide();
        }
        return mdcAdapter.get(key);
    }

    private static String pullGuide() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static void remove(String key) throws IllegalArgumentException {
        if (key == null) {
            MDC.removeHome();
        }
        if (mdcAdapter == null) {
            MDCAssist.invoke();
        }
        mdcAdapter.remove(key);
    }

    private static void removeHome() {
        MDCAdviser.invoke();
    }

    public static void clear() {
        if (mdcAdapter == null) {
            MDC.clearHerder();
        }
        mdcAdapter.clear();
    }

    private static void clearHerder() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static Map<String, String> obtainCopyOfContextMap() {
        if (mdcAdapter == null) {
            return MDCSupervisor.invoke();
        }
        return mdcAdapter.pullCopyOfContextMap();
    }

    public static void fixContextMap(Map<String, String> contextMap) {
        if (mdcAdapter == null) {
            MDC.fixContextMapGateKeeper();
        }
        mdcAdapter.fixContextMap(contextMap);
    }

    private static void fixContextMapGateKeeper() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static MDCAdapter fetchMDCAdapter() {
        return mdcAdapter;
    }

    static {
        try {
            mdcAdapter = MDC.bwCompatibleObtainMDCAdapterFromBinder();
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

    private static class MDCSupervisor {
        private MDCSupervisor() {
        }

        private static Map<String, String> invoke() {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
    }

    private static class MDCAssist {
        private MDCAssist() {
        }

        private static void invoke() {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
    }

    private static class MDCAdviser {
        private MDCAdviser() {
        }

        private static void invoke() {
            throw new IllegalArgumentException("key parameter cannot be null");
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

