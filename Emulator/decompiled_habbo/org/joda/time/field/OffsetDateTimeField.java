/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.FieldUtils;

public class OffsetDateTimeField
extends DecoratedDateTimeField {
    private static final long serialVersionUID = 3145790132623583142L;
    private final int iOffset;
    private final int iMin;
    private final int iMax;

    public OffsetDateTimeField(DateTimeField dateTimeField, int n) {
        this(dateTimeField, dateTimeField == null ? null : dateTimeField.getType(), n, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public OffsetDateTimeField(DateTimeField dateTimeField, DateTimeFieldType dateTimeFieldType, int n) {
        this(dateTimeField, dateTimeFieldType, n, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public OffsetDateTimeField(DateTimeField dateTimeField, DateTimeFieldType dateTimeFieldType, int n, int n2, int n3) {
        super(dateTimeField, dateTimeFieldType);
        if (n == 0) {
            throw new IllegalArgumentException("The offset cannot be zero");
        }
        this.iOffset = n;
        this.iMin = n2 < dateTimeField.getMinimumValue() + n ? dateTimeField.getMinimumValue() + n : n2;
        this.iMax = n3 > dateTimeField.getMaximumValue() + n ? dateTimeField.getMaximumValue() + n : n3;
    }

    public int get(long l) {
        return super.get(l) + this.iOffset;
    }

    public long add(long l, int n) {
        l = super.add(l, n);
        FieldUtils.verifyValueBounds(this, this.get(l), this.iMin, this.iMax);
        return l;
    }

    public long add(long l, long l2) {
        l = super.add(l, l2);
        FieldUtils.verifyValueBounds(this, this.get(l), this.iMin, this.iMax);
        return l;
    }

    public long addWrapField(long l, int n) {
        return this.set(l, FieldUtils.getWrappedValue(this.get(l), n, this.iMin, this.iMax));
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, this.iMin, this.iMax);
        return super.set(l, n - this.iOffset);
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
        return this.iMin;
    }

    public int getMaximumValue() {
        return this.iMax;
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

    public int getOffset() {
        return this.iOffset;
    }
}

