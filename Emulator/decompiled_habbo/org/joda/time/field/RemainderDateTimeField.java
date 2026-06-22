/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.DividedDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.ScaledDurationField;

public class RemainderDateTimeField
extends DecoratedDateTimeField {
    private static final long serialVersionUID = 5708241235177666790L;
    final int iDivisor;
    final DurationField iDurationField;
    final DurationField iRangeField;

    public RemainderDateTimeField(DateTimeField dateTimeField, DateTimeFieldType dateTimeFieldType, int n) {
        super(dateTimeField, dateTimeFieldType);
        if (n < 2) {
            throw new IllegalArgumentException("The divisor must be at least 2");
        }
        DurationField durationField = dateTimeField.getDurationField();
        this.iRangeField = durationField == null ? null : new ScaledDurationField(durationField, dateTimeFieldType.getRangeDurationType(), n);
        this.iDurationField = dateTimeField.getDurationField();
        this.iDivisor = n;
    }

    public RemainderDateTimeField(DateTimeField dateTimeField, DurationField durationField, DateTimeFieldType dateTimeFieldType, int n) {
        super(dateTimeField, dateTimeFieldType);
        if (n < 2) {
            throw new IllegalArgumentException("The divisor must be at least 2");
        }
        this.iRangeField = durationField;
        this.iDurationField = dateTimeField.getDurationField();
        this.iDivisor = n;
    }

    public RemainderDateTimeField(DividedDateTimeField dividedDateTimeField) {
        this(dividedDateTimeField, dividedDateTimeField.getType());
    }

    public RemainderDateTimeField(DividedDateTimeField dividedDateTimeField, DateTimeFieldType dateTimeFieldType) {
        this(dividedDateTimeField, dividedDateTimeField.getWrappedField().getDurationField(), dateTimeFieldType);
    }

    public RemainderDateTimeField(DividedDateTimeField dividedDateTimeField, DurationField durationField, DateTimeFieldType dateTimeFieldType) {
        super(dividedDateTimeField.getWrappedField(), dateTimeFieldType);
        this.iDivisor = dividedDateTimeField.iDivisor;
        this.iDurationField = durationField;
        this.iRangeField = dividedDateTimeField.iDurationField;
    }

    public int get(long l) {
        int n = this.getWrappedField().get(l);
        if (n >= 0) {
            return n % this.iDivisor;
        }
        return this.iDivisor - 1 + (n + 1) % this.iDivisor;
    }

    public long addWrapField(long l, int n) {
        return this.set(l, FieldUtils.getWrappedValue(this.get(l), n, 0, this.iDivisor - 1));
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, 0, this.iDivisor - 1);
        int n2 = this.getDivided(this.getWrappedField().get(l));
        return this.getWrappedField().set(l, n2 * this.iDivisor + n);
    }

    public DurationField getDurationField() {
        return this.iDurationField;
    }

    public DurationField getRangeDurationField() {
        return this.iRangeField;
    }

    public int getMinimumValue() {
        return 0;
    }

    public int getMaximumValue() {
        return this.iDivisor - 1;
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

    public int getDivisor() {
        return this.iDivisor;
    }

    private int getDivided(int n) {
        if (n >= 0) {
            return n / this.iDivisor;
        }
        return (n + 1) / this.iDivisor - 1;
    }
}

