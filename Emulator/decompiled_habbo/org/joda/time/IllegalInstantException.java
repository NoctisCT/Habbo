/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;

public class IllegalInstantException
extends IllegalArgumentException {
    private static final long serialVersionUID = 2858712538216L;

    public IllegalInstantException(String string) {
        super(string);
    }

    public IllegalInstantException(long l, String string) {
        super(IllegalInstantException.createMessage(l, string));
    }

    private static String createMessage(long l, String string) {
        String string2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").print(new Instant(l));
        String string3 = string != null ? " (" + string + ")" : "";
        return "Illegal instant due to time zone offset transition (daylight savings time 'gap'): " + string2 + string3;
    }

    public static boolean isIllegalInstant(Throwable throwable) {
        if (throwable instanceof IllegalInstantException) {
            return true;
        }
        if (throwable.getCause() != null && throwable.getCause() != throwable) {
            return IllegalInstantException.isIllegalInstant(throwable.getCause());
        }
        return false;
    }
}

