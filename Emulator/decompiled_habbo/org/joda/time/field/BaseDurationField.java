/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.io.Serializable;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.field.FieldUtils;

public abstract class BaseDurationField
extends DurationField
implements Serializable {
    private static final long serialVersionUID = -2554245107589433218L;
    private final DurationFieldType iType;

    protected BaseDurationField(DurationFieldType durationFieldType) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        this.iType = durationFieldType;
    }

    public final DurationFieldType getType() {
        return this.iType;
    }

    public final String getName() {
        return this.iType.getName();
    }

    public final boolean isSupported() {
        return true;
    }

    public int getValue(long l) {
        return FieldUtils.safeToInt(this.getValueAsLong(l));
    }

    public long getValueAsLong(long l) {
        return l / this.getUnitMillis();
    }

    public int getValue(long l, long l2) {
        return FieldUtils.safeToInt(this.getValueAsLong(l, l2));
    }

    public long getMillis(int n) {
        return (long)n * this.getUnitMillis();
    }

    public long getMillis(long l) {
        return FieldUtils.safeMultiply(l, this.getUnitMillis());
    }

    public int getDifference(long l, long l2) {
        return FieldUtils.safeToInt(this.getDifferenceAsLong(l, l2));
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

    public String toString() {
        return "DurationField[" + this.getName() + ']';
    }
}

