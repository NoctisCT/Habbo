/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

public class ZoneInfoLogger {
    static ThreadLocal<Boolean> cVerbose = new ThreadLocal<Boolean>(){

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    public static boolean verbose() {
        return cVerbose.get();
    }

    public static void set(boolean bl) {
        cVerbose.set(bl);
    }
}

