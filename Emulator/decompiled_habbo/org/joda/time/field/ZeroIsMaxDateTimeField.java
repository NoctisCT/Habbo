/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.FieldUtils;

public final class ZeroIsMaxDateTimeField
extends DecoratedDateTimeField {
    private static final long serialVersionUID = 961749798233026866L;

    public ZeroIsMaxDateTimeField(DateTimeField dateTimeField, DateTimeFieldType dateTimeFieldType) {
        super(dateTimeField, dateTimeFieldType);
        if (dateTimeField.getMinimumValue() != 0) {
            throw new IllegalArgumentException("Wrapped field's minumum value must be zero");
        }
    }

    public int get(long l) {
        int n = this.getWrappedField().get(l);
        if (n == 0) {
            n = this.getMaximumValue();
        }
        return n;
    }

    public long add(long l, int n) {
        return this.getWrappedField().add(l, n);
    }

    public long add(long l, long l2) {
        return this.getWrappedField().add(l, l2);
    }

    public long addWrapField(long l, int n) {
        return this.getWrappedField().addWrapField(l, n);
    }

    public int[] addWrapField(ReadablePartial readablePartial, int n, int[] nArray, int n2) {
        return this.getWrappedField().addWrapField(readablePartial, n, nArray, n2);
    }

    public int getDifference(long l, long l2) {
        return this.getWrappedField().getDifference(l, l2);
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.getWrappedField().getDifferenceAsLong(l, l2);
    }

    public long set(long l, int n) {
        int n2 = this.getMaximumValue();
        FieldUtils.verifyValueBounds(this, n, 1, n2);
        if (n == n2) {
            n = 0;
        }
        return this.getWrappedField().set(l, n);
    }

    public boolean isLeap(long l) {
        return this.getWrappedField().isLeap(l);
    }

    public int getLeapAmount(long l) {
        return this.getWrappedField().getLeapAmount(l);
    }

    public DurationField getLeapDurationField() {
        return this.getWrappedField().getLeapDurationField();
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMinimumValue(long l) {
        return 1;
    }

    public int getMinimumValue(ReadablePartial readablePartial) {
        return 1;
    }

    public int getMinimumValue(ReadablePartial readablePartial, int[] nArray) {
        return 1;
    }

    public int getMaximumValue() {
        return this.getWrappedField().getMaximumValue() + 1;
    }

    public int getMaximumValue(long l) {
        return this.getWrappedField().getMaximumValue(l) + 1;
    }

    public int getMaximumValue(ReadablePartial readablePartial) {
        return this.getWrappedField().getMaximumValue(readablePartial) + 1;
    }

    public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
        return this.getWrappedField().getMaximumValue(readablePartial, nArray) + 1;
    }

    public long roundFloor(long l) {
        return this.getWrappedField().roundFloor(l);
    }

    public long roundCeiling(long l) {
        return this.getWrappedField().roundCeiling(l);
    }

    public long roundHalfFloor(long l) {
        return this.getWrappedField().roundHalfFloor(l);
    }

    public long roundHalfCeiling(long l) {
        return this.getWrappedField().roundHalfCeiling(l);
    }

    public long roundHalfEven(long l) {
        return this.getWrappedField().roundHalfEven(l);
    }

    public long remainder(long l) {
        return this.getWrappedField().remainder(l);
    }
}

