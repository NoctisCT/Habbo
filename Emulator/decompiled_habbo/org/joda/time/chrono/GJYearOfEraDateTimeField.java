/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.FieldUtils;

final class GJYearOfEraDateTimeField
extends DecoratedDateTimeField {
    private static final long serialVersionUID = -5961050944769862059L;
    private final BasicChronology iChronology;

    GJYearOfEraDateTimeField(DateTimeField dateTimeField, BasicChronology basicChronology) {
        super(dateTimeField, DateTimeFieldType.yearOfEra());
        this.iChronology = basicChronology;
    }

    public DurationField getRangeDurationField() {
        return this.iChronology.eras();
    }

    public int get(long l) {
        int n = this.getWrappedField().get(l);
        if (n <= 0) {
            n = 1 - n;
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
        FieldUtils.verifyValueBounds(this, n, 1, this.getMaximumValue());
        if (this.iChronology.getYear(l) <= 0) {
            n = 1 - n;
        }
        return super.set(l, n);
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMaximumValue() {
        return this.getWrappedField().getMaximumValue();
    }

    public long roundFloor(long l) {
        return this.getWrappedField().roundFloor(l);
    }

    public long roundCeiling(long l) {
        return this.getWrappedField().roundCeiling(l);
    }

    public long remainder(long l) {
        return this.getWrappedField().remainder(l);
    }

    private Object readResolve() {
        return this.iChronology.yearOfEra();
    }
}

