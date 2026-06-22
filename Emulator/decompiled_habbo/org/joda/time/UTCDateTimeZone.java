/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;

final class UTCDateTimeZone
extends DateTimeZone {
    static final DateTimeZone INSTANCE = new UTCDateTimeZone();
    private static final long serialVersionUID = -3513011772763289092L;

    public UTCDateTimeZone() {
        super("UTC");
    }

    public String getNameKey(long l) {
        return "UTC";
    }

    public int getOffset(long l) {
        return 0;
    }

    public int getStandardOffset(long l) {
        return 0;
    }

    public int getOffsetFromLocal(long l) {
        return 0;
    }

    public boolean isFixed() {
        return true;
    }

    public long nextTransition(long l) {
        return l;
    }

    public long previousTransition(long l) {
        return l;
    }

    public TimeZone toTimeZone() {
        return new SimpleTimeZone(0, this.getID());
    }

    public boolean equals(Object object) {
        return object instanceof UTCDateTimeZone;
    }

    public int hashCode() {
        return this.getID().hashCode();
    }
}

