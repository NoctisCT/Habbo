/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.field.DecoratedDurationField;
import org.joda.time.field.FieldUtils;

public class ScaledDurationField
extends DecoratedDurationField {
    private static final long serialVersionUID = -3205227092378684157L;
    private final int iScalar;

    public ScaledDurationField(DurationField durationField, DurationFieldType durationFieldType, int n) {
        super(durationField, durationFieldType);
        if (n == 0 || n == 1) {
            throw new IllegalArgumentException("The scalar must not be 0 or 1");
        }
        this.iScalar = n;
    }

    public int getValue(long l) {
        return this.getWrappedField().getValue(l) / this.iScalar;
    }

    public long getValueAsLong(long l) {
        return this.getWrappedField().getValueAsLong(l) / (long)this.iScalar;
    }

    public int getValue(long l, long l2) {
        return this.getWrappedField().getValue(l, l2) / this.iScalar;
    }

    public long getValueAsLong(long l, long l2) {
        return this.getWrappedField().getValueAsLong(l, l2) / (long)this.iScalar;
    }

    public long getMillis(int n) {
        long l = (long)n * (long)this.iScalar;
        return this.getWrappedField().getMillis(l);
    }

    public long getMillis(long l) {
        long l2 = FieldUtils.safeMultiply(l, this.iScalar);
        return this.getWrappedField().getMillis(l2);
    }

    public long getMillis(int n, long l) {
        long l2 = (long)n * (long)this.iScalar;
        return this.getWrappedField().getMillis(l2, l);
    }

    public long getMillis(long l, long l2) {
        long l3 = FieldUtils.safeMultiply(l, this.iScalar);
        return this.getWrappedField().getMillis(l3, l2);
    }

    public long add(long l, int n) {
        long l2 = (long)n * (long)this.iScalar;
        return this.getWrappedField().add(l, l2);
    }

    public long add(long l, long l2) {
        long l3 = FieldUtils.safeMultiply(l2, this.iScalar);
        return this.getWrappedField().add(l, l3);
    }

    public int getDifference(long l, long l2) {
        return this.getWrappedField().getDifference(l, l2) / this.iScalar;
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.getWrappedField().getDifferenceAsLong(l, l2) / (long)this.iScalar;
    }

    public long getUnitMillis() {
        return this.getWrappedField().getUnitMillis() * (long)this.iScalar;
    }

    public int getScalar() {
        return this.iScalar;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ScaledDurationField) {
            ScaledDurationField scaledDurationField = (ScaledDurationField)object;
            return this.getWrappedField().equals(scaledDurationField.getWrappedField()) && this.getType() == scaledDurationField.getType() && this.iScalar == scaledDurationField.iScalar;
        }
        return false;
    }

    public int hashCode() {
        long l = this.iScalar;
        int n = (int)(l ^ l >>> 32);
        n += this.getType().hashCode();
        return n += this.getWrappedField().hashCode();
    }
}

