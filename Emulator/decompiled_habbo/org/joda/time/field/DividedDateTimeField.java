/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.RemainderDateTimeField;
import org.joda.time.field.ScaledDurationField;

public class DividedDateTimeField
extends DecoratedDateTimeField {
    private static final long serialVersionUID = 8318475124230605365L;
    final int iDivisor;
    final DurationField iDurationField;
    final DurationField iRangeDurationField;
    private final int iMin;
    private final int iMax;

    public DividedDateTimeField(DateTimeField dateTimeField, DateTimeFieldType dateTimeFieldType, int n) {
        this(dateTimeField, dateTimeField.getRangeDurationField(), dateTimeFieldType, n);
    }

    public DividedDateTimeField(DateTimeField dateTimeField, DurationField durationField, DateTimeFieldType dateTimeFieldType, int n) {
        super(dateTimeField, dateTimeFieldType);
        if (n < 2) {
            throw new IllegalArgumentException("The divisor must be at least 2");
        }
        DurationField durationField2 = dateTimeField.getDurationField();
        this.iDurationField = durationField2 == null ? null : new ScaledDurationField(durationField2, dateTimeFieldType.getDurationType(), n);
        this.iRangeDurationField = durationField;
        this.iDivisor = n;
        int n2 = dateTimeField.getMinimumValue();
        int n3 = n2 >= 0 ? n2 / n : (n2 + 1) / n - 1;
        int n4 = dateTimeField.getMaximumValue();
        int n5 = n4 >= 0 ? n4 / n : (n4 + 1) / n - 1;
        this.iMin = n3;
        this.iMax = n5;
    }

    public DividedDateTimeField(RemainderDateTimeField remainderDateTimeField, DateTimeFieldType dateTimeFieldType) {
        this(remainderDateTimeField, null, dateTimeFieldType);
    }

    public DividedDateTimeField(RemainderDateTimeField remainderDateTimeField, DurationField durationField, DateTimeFieldType dateTimeFieldType) {
        super(remainderDateTimeField.getWrappedField(), dateTimeFieldType);
        int n = this.iDivisor = remainderDateTimeField.iDivisor;
        this.iDurationField = remainderDateTimeField.iRangeField;
        this.iRangeDurationField = durationField;
        DateTimeField dateTimeField = this.getWrappedField();
        int n2 = dateTimeField.getMinimumValue();
        int n3 = n2 >= 0 ? n2 / n : (n2 + 1) / n - 1;
        int n4 = dateTimeField.getMaximumValue();
        int n5 = n4 >= 0 ? n4 / n : (n4 + 1) / n - 1;
        this.iMin = n3;
        this.iMax = n5;
    }

    public DurationField getRangeDurationField() {
        if (this.iRangeDurationField != null) {
            return this.iRangeDurationField;
        }
        return super.getRangeDurationField();
    }

    public int get(long l) {
        int n = this.getWrappedField().get(l);
        if (n >= 0) {
            return n / this.iDivisor;
        }
        return (n + 1) / this.iDivisor - 1;
    }

    public long add(long l, int n) {
        return this.getWrappedField().add(l, n * this.iDivisor);
    }

    public long add(long l, long l2) {
        return this.getWrappedField().add(l, l2 * (long)this.iDivisor);
    }

    public long addWrapField(long l, int n) {
        return this.set(l, FieldUtils.getWrappedValue(this.get(l), n, this.iMin, this.iMax));
    }

    public int getDifference(long l, long l2) {
        return this.getWrappedField().getDifference(l, l2) / this.iDivisor;
    }

    public long getDifferenceAsLong(long l, long l2) {
        return this.getWrappedField().getDifferenceAsLong(l, l2) / (long)this.iDivisor;
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, this.iMin, this.iMax);
        int n2 = this.getRemainder(this.getWrappedField().get(l));
        return this.getWrappedField().set(l, n * this.iDivisor + n2);
    }

    public DurationField getDurationField() {
        return this.iDurationField;
    }

    public int getMinimumValue() {
        return this.iMin;
    }

    public int getMaximumValue() {
        return this.iMax;
    }

    public long roundFloor(long l) {
        DateTimeField dateTimeField = this.getWrappedField();
        return dateTimeField.roundFloor(dateTimeField.set(l, this.get(l) * this.iDivisor));
    }

    public long remainder(long l) {
        return this.set(l, this.get(this.getWrappedField().remainder(l)));
    }

    public int getDivisor() {
        return this.iDivisor;
    }

    private int getRemainder(int n) {
        if (n >= 0) {
            return n % this.iDivisor;
        }
        return this.iDivisor - 1 + (n + 1) % this.iDivisor;
    }
}

