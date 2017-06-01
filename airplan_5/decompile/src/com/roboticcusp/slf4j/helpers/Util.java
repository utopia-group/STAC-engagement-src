/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.slf4j.helpers;

import java.io.PrintStream;

public final class Util {
    private static ClassContextSecurityConductor SECURITY_MANAGER;
    private static boolean SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED;

    Util() {
    }

    public static String safePullSystemProperty(String key) {
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

    public static boolean safeObtainBooleanSystemProperty(String key) {
        String value = Util.safePullSystemProperty(key);
        if (value == null) {
            return false;
        }
        return value.equalsIgnoreCase("true");
    }

    private static ClassContextSecurityConductor takeSecurityConductor() {
        if (SECURITY_MANAGER != null) {
            return SECURITY_MANAGER;
        }
        if (SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED) {
            return null;
        }
        SECURITY_MANAGER = Util.safeComposeSecurityConductor();
        SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = true;
        return SECURITY_MANAGER;
    }

    private static ClassContextSecurityConductor safeComposeSecurityConductor() {
        try {
            return new ClassContextSecurityConductor();
        }
        catch (SecurityException sm) {
            return null;
        }
    }

    public static Class<?> obtainCallingClass() {
        int i;
        ClassContextSecurityConductor securityConductor = Util.takeSecurityConductor();
        if (securityConductor == null) {
            return null;
        }
        Class<?>[] trace = securityConductor.getClassContext();
        String thisClassName = Util.class.getName();
        for (i = 0; i < trace.length && !thisClassName.equals(trace[i].getName()); ++i) {
        }
        if (i >= trace.length || i + 2 >= trace.length) {
            throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen");
        }
        return trace[i + 2];
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

    private static final class ClassContextSecurityConductor
    extends SecurityManager {
        private ClassContextSecurityConductor() {
        }

        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

}

