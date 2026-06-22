/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.util;

public class DebugUtils {
    public static StackTraceElement getCallerCallerStacktrace() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; ++i) {
            StackTraceElement ste = stElements[i];
            if (ste.getClassName().equals(DebugUtils.class.getName()) || ste.getClassName().indexOf("java.lang.Thread") == 0) continue;
            if (callerClassName == null) {
                callerClassName = ste.getClassName();
                continue;
            }
            if (callerClassName.equals(ste.getClassName())) continue;
            return ste;
        }
        return null;
    }
}

