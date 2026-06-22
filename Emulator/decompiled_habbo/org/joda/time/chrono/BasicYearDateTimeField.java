/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.ImpreciseDateTimeField;

class BasicYearDateTimeField
extends ImpreciseDateTimeField {
    private static final long serialVersionUID = -98628754872287L;
    protected final BasicChronology iChronology;

    BasicYearDateTimeField(BasicChronology basicChronology) {
        super(DateTimeFieldType.year(), basicChronology.getAverageMillisPerYear());
        this.iChronology = basicChronology;
    }

    public boolean isLenient() {
        return false;
    }

    public int get(long l) {
        return this.iChronology.getYear(l);
    }

    public long add(long l, int n) {
        if (n == 0) {
            return l;
        }
        int n2 = this.get(l);
        int n3 = FieldUtils.safeAdd(n2, n);
        return this.set(l, n3);
    }

    public long add(long l, long l2) {
        return this.add(l, FieldUtils.safeToInt(l2));
    }

    public long addWrapField(long l, int n) {
        if (n == 0) {
            return l;
        }
        int n2 = this.iChronology.getYear(l);
        int n3 = FieldUtils.getWrappedValue(n2, n, this.iChronology.getMinYear(), this.iChronology.getMaxYear());
        return this.set(l, n3);
    }

    public long set(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, this.iChronology.getMinYear(), this.iChronology.getMaxYear());
        return this.iChronology.setYear(l, n);
    }

    public long setExtended(long l, int n) {
        FieldUtils.verifyValueBounds(this, n, this.iChronology.getMinYear() - 1, this.iChronology.getMaxYear() + 1);
        return this.iChronology.setYear(l, n);
    }

    public long getDifferenceAsLong(long l, long l2) {
        if (l < l2) {
            return -this.iChronology.getYearDifference(l2, l);
        }
        return this.iChronology.getYearDifference(l, l2);
    }

    public DurationField getRangeDurationField() {
        return null;
    }

    public boolean isLeap(long l) {
        return this.iChronology.isLeapYear(this.get(l));
    }

    public int getLeapAmount(long l) {
        if (this.iChronology.isLeapYear(this.get(l))) {
            return 1;
        }
        return 0;
    }

    public DurationField getLeapDurationField() {
        return this.iChronology.days();
    }

    public int getMinimumValue() {
        return this.iChronology.getMinYear();
    }

    public int getMaximumValue() {
        return this.iChronology.getMaxYear();
    }

    public long roundFloor(long l) {
        return this.iChronology.getYearMillis(this.get(l));
    }

    public long roundCeiling(long l) {
        int n = this.get(l);
        long l2 = this.iChronology.getYearMillis(n);
        if (l != l2) {
            l = this.iChronology.getYearMillis(n + 1);
        }
        return l;
    }

    public long remainder(long l) {
        return l - this.roundFloor(l);
    }

    private Object readResolve() {
        return this.iChronology.year();
    }
}

