/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

final class Android {
    private static final Class<?> MEMORY_CLASS = Android.getClassForName("libcore.io.Memory");
    private static final boolean IS_ROBOLECTRIC = Android.getClassForName("org.robolectric.Robolectric") != null;

    Android() {
    }

    static boolean isOnAndroidDevice() {
        return MEMORY_CLASS != null && !IS_ROBOLECTRIC;
    }

    static Class<?> getMemoryClass() {
        return MEMORY_CLASS;
    }

    private static <T> Class<T> getClassForName(String name) {
        try {
            return Class.forName(name);
        }
        catch (Throwable e) {
            return null;
        }
    }
}

