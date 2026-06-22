/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.hawtjni.runtime;

import org.fusesource.hawtjni.runtime.Library;

public class PointerMath {
    private static final boolean bits32 = Library.getBitModel() == 32;

    public static final long add(long ptr, long n) {
        if (bits32) {
            return (int)(ptr + n);
        }
        return ptr + n;
    }
}

