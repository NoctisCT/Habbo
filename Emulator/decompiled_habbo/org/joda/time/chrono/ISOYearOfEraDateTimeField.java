/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.FieldUtils;

class ISOYearOfEraDateTimeField
extends DecoratedDateTimeField {
    private static final long serialVersionUID = 7037524068969447317L;
    static final DateTimeField INSTANCE = new ISOYearOfEraDateTimeField();

    private ISOYearOfEraDateTimeField() {
        super(GregorianChronology.getInstanceUTC().year(), DateTimeFieldType.yearOfEra());
    }

    public DurationField getRangeDurationField() {
        return GregorianChronology.getInstanceUTC().eras();
    }

    public int get(long l) {
        int n = this.getWrappedField().get(l);
        return n < 0 ? -n : n;
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
        FieldUtils.verifyValueBounds(this, n, 0, this.getMaximumValue());
        if (this.getWrappedField().get(l) < 0) {
            n = -n;
        }
        return super.set(l, n);
    }

    public int getMinimumValue() {
        return 0;
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
        return INSTANCE;
    }
}

