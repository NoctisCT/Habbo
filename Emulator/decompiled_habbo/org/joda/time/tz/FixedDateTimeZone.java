/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;

public final class FixedDateTimeZone
extends DateTimeZone {
    private static final long serialVersionUID = -3513011772763289092L;
    private final String iNameKey;
    private final int iWallOffset;
    private final int iStandardOffset;

    public FixedDateTimeZone(String string, String string2, int n, int n2) {
        super(string);
        this.iNameKey = string2;
        this.iWallOffset = n;
        this.iStandardOffset = n2;
    }

    public String getNameKey(long l) {
        return this.iNameKey;
    }

    public int getOffset(long l) {
        return this.iWallOffset;
    }

    public int getStandardOffset(long l) {
        return this.iStandardOffset;
    }

    public int getOffsetFromLocal(long l) {
        return this.iWallOffset;
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
        String string = this.getID();
        if (string.length() == 6 && (string.startsWith("+") || string.startsWith("-"))) {
            return TimeZone.getTimeZone("GMT" + this.getID());
        }
        return new SimpleTimeZone(this.iWallOffset, this.getID());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof FixedDateTimeZone) {
            FixedDateTimeZone fixedDateTimeZone = (FixedDateTimeZone)object;
            return this.getID().equals(fixedDateTimeZone.getID()) && this.iStandardOffset == fixedDateTimeZone.iStandardOffset && this.iWallOffset == fixedDateTimeZone.iWallOffset;
        }
        return false;
    }

    public int hashCode() {
        return this.getID().hashCode() + 37 * this.iStandardOffset + 31 * this.iWallOffset;
    }
}

