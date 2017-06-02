/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.note.helpers;

import java.io.PrintStream;

public final class Util {
    private static ClassContextSecurityOverseer SECURITY_MANAGER;
    private static boolean SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED;

    private Util() {
    }

    public static String safeTakeSystemProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null input");
        }
        String result = null;
        try {
            result = System.getProperty(key);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return result;
    }

    public static boolean safeGetBooleanSystemProperty(String key) {
        String value = Util.safeTakeSystemProperty(key);
        if (value == null) {
            return false;
        }
        return value.equalsIgnoreCase("true");
    }

    private static ClassContextSecurityOverseer grabSecurityOverseer() {
        if (SECURITY_MANAGER != null) {
            return SECURITY_MANAGER;
        }
        if (SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED) {
            return null;
        }
        SECURITY_MANAGER = Util.safeMakeSecurityOverseer();
        SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = true;
        return SECURITY_MANAGER;
    }

    private static ClassContextSecurityOverseer safeMakeSecurityOverseer() {
        try {
            return new ClassContextSecurityOverseer();
        }
        catch (SecurityException sm) {
            return null;
        }
    }

    public static Class<?> getCallingClass() {
        int c;
        ClassContextSecurityOverseer securityOverseer = Util.grabSecurityOverseer();
        if (securityOverseer == null) {
            return null;
        }
        Class<?>[] trace = securityOverseer.getClassContext();
        String thisClassName = Util.class.getName();
        for (c = 0; c < trace.length && !thisClassName.equals(trace[c].getName()); ++c) {
        }
        if (c >= trace.length || c + 2 >= trace.length) {
            throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen");
        }
        return trace[c + 2];
    }

    public static final void report(String msg, Throwable t) {
        System.err.println(msg);
        System.err.println("Reported exception:");
        t.printStackTrace();
    }

    public static final void report(String msg) {
        System.err.println("SLF4J: " + msg);
    }

    static {
        SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = false;
    }

    private static final class ClassContextSecurityOverseer
    extends SecurityManager {
        private ClassContextSecurityOverseer() {
        }

        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

}

