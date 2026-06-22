/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.io.Serializable;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.field.FieldUtils;

public final class MillisDurationField
extends DurationField
implements Serializable {
    private static final long serialVersionUID = 2656707858124633367L;
    public static final DurationField INSTANCE = new MillisDurationField();

    private MillisDurationField() {
    }

    public DurationFieldType getType() {
        return DurationFieldType.millis();
    }

    public String getName() {
        return "millis";
    }

    public boolean isSupported() {
        return true;
    }

    public final boolean isPrecise() {
        return true;
    }

    public final long getUnitMillis() {
        return 1L;
    }

    public int getValue(long l) {
        return FieldUtils.safeToInt(l);
    }

    public long getValueAsLong(long l) {
        return l;
    }

    public int getValue(long l, long l2) {
        return FieldUtils.safeToInt(l);
    }

    public long getValueAsLong(long l, long l2) {
        return l;
    }

    public long getMillis(int n) {
        return n;
    }

    public long getMillis(long l) {
        return l;
    }

    public long getMillis(int n, long l) {
        return n;
    }

    public long getMillis(long l, long l2) {
        return l;
    }

    public long add(long l, int n) {
        return FieldUtils.safeAdd(l, (long)n);
    }

    public long add(long l, long l2) {
        return FieldUtils.safeAdd(l, l2);
    }

    public int getDifference(long l, long l2) {
        return FieldUtils.safeToInt(FieldUtils.safeSubtract(l, l2));
    }

    public long getDifferenceAsLong(long l, long l2) {
        return FieldUtils.safeSubtract(l, l2);
    }

    public int compareTo(DurationField durationField) {
        long l = durationField.getUnitMillis();
        long l2 = this.getUnitMillis();
        if (l2 == l) {
            return 0;
        }
        if (l2 < l) {
            return -1;
        }
        return 1;
    }

    public boolean equals(Object object) {
        if (object instanceof MillisDurationField) {
            return this.getUnitMillis() == ((MillisDurationField)object).getUnitMillis();
        }
        return false;
    }

    public int hashCode() {
        return (int)this.getUnitMillis();
    }

    public String toString() {
        return "DurationField[millis]";
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

