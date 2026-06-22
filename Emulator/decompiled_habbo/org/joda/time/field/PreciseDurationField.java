/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DurationFieldType;
import org.joda.time.field.BaseDurationField;
import org.joda.time.field.FieldUtils;

public class PreciseDurationField
extends BaseDurationField {
    private static final long serialVersionUID = -8346152187724495365L;
    private final long iUnitMillis;

    public PreciseDurationField(DurationFieldType durationFieldType, long l) {
        super(durationFieldType);
        this.iUnitMillis = l;
    }

    public final boolean isPrecise() {
        return true;
    }

    public final long getUnitMillis() {
        return this.iUnitMillis;
    }

    public long getValueAsLong(long l, long l2) {
        return l / this.iUnitMillis;
    }

    public long getMillis(int n, long l) {
        return (long)n * this.iUnitMillis;
    }

    public long getMillis(long l, long l2) {
        return FieldUtils.safeMultiply(l, this.iUnitMillis);
    }

    public long add(long l, int n) {
        long l2 = (long)n * this.iUnitMillis;
        return FieldUtils.safeAdd(l, l2);
    }

    public long add(long l, long l2) {
        long l3 = FieldUtils.safeMultiply(l2, this.iUnitMillis);
        return FieldUtils.safeAdd(l, l3);
    }

    public long getDifferenceAsLong(long l, long l2) {
        long l3 = FieldUtils.safeSubtract(l, l2);
        return l3 / this.iUnitMillis;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof PreciseDurationField) {
            PreciseDurationField preciseDurationField = (PreciseDurationField)object;
            return this.getType() == preciseDurationField.getType() && this.iUnitMillis == preciseDurationField.iUnitMillis;
        }
        return false;
    }

    public int hashCode() {
        long l = this.iUnitMillis;
        int n = (int)(l ^ l >>> 32);
        return n += this.getType().hashCode();
    }
}

