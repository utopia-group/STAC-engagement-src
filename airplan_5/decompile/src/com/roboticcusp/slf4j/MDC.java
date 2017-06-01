/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j;

import com.roboticcusp.slf4j.helpers.NOPMDCAdapter;
import com.roboticcusp.slf4j.helpers.Util;
import com.roboticcusp.slf4j.inplace.StaticMDCBinder;
import com.roboticcusp.slf4j.service.MDCAdapter;
import java.io.Closeable;
import java.util.Map;

public class MDC {
    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;

    private MDC() {
    }

    private static MDCAdapter bwCompatibleGrabMDCAdapterFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMDCBinder.takeSingleton().getMDCA();
        }
        catch (NoSuchMethodError nsme) {
            return StaticMDCBinder.SINGLETON.getMDCA();
        }
    }

    public static void insert(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            MDC.placeService();
        }
        mdcAdapter.insert(key, val);
    }

    private static void placeService() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static MDCCloseable putCloseable(String key, String val) throws IllegalArgumentException {
        MDC.insert(key, val);
        return new MDCCloseable(key);
    }

    public static String pull(String key) throws IllegalArgumentException {
        if (key == null) {
            return MDC.obtainHelp();
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        return mdcAdapter.grab(key);
    }

    private static String obtainHelp() {
        throw new IllegalArgumentException("key parameter cannot be null");
    }

    public static void remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            MDC.removeHome();
        }
        mdcAdapter.remove(key);
    }

    private static void removeHome() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static void clear() {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        mdcAdapter.clear();
    }

    public static Map<String, String> fetchCopyOfContextMap() {
        if (mdcAdapter == null) {
            return MDC.takeCopyOfContextMapUtility();
        }
        return mdcAdapter.fetchCopyOfContextMap();
    }

    private static Map<String, String> takeCopyOfContextMapUtility() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static void defineContextMap(Map<String, String> contextMap) {
        if (mdcAdapter == null) {
            MDC.fixContextMapWorker();
        }
        mdcAdapter.fixContextMap(contextMap);
    }

    private static void fixContextMapWorker() {
        throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
    }

    public static MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    static {
        try {
            mdcAdapter = MDC.bwCompatibleGrabMDCAdapterFromBinder();
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

