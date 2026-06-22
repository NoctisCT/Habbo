/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.field.PreciseDurationDateTimeField;

final class BasicDayOfYearDateTimeField
extends PreciseDurationDateTimeField {
    private static final long serialVersionUID = -6821236822336841037L;
    private final BasicChronology iChronology;

    BasicDayOfYearDateTimeField(BasicChronology basicChronology, DurationField durationField) {
        super(DateTimeFieldType.dayOfYear(), durationField);
        this.iChronology = basicChronology;
    }

    public int get(long l) {
        return this.iChronology.getDayOfYear(l);
    }

    public DurationField getRangeDurationField() {
        return this.iChronology.years();
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMaximumValue() {
        return this.iChronology.getDaysInYearMax();
    }

    public int getMaximumValue(long l) {
        int n = this.iChronology.getYear(l);
        return this.iChronology.getDaysInYear(n);
    }

    public int getMaximumValue(ReadablePartial readablePartial) {
        if (readablePartial.isSupported(DateTimeFieldType.year())) {
            int n = readablePartial.get(DateTimeFieldType.year());
            return this.iChronology.getDaysInYear(n);
        }
        return this.iChronology.getDaysInYearMax();
    }

    public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
        int n = readablePartial.size();
        for (int i = 0; i < n; ++i) {
            if (readablePartial.getFieldType(i) != DateTimeFieldType.year()) continue;
            int n2 = nArray[i];
            return this.iChronology.getDaysInYear(n2);
        }
        return this.iChronology.getDaysInYearMax();
    }

    protected int getMaximumValueForSet(long l, int n) {
        int n2 = this.iChronology.getDaysInYearMax() - 1;
        return n > n2 || n < 1 ? this.getMaximumValue(l) : n2;
    }

    public boolean isLeap(long l) {
        return this.iChronology.isLeapDay(l);
    }

    private Object readResolve() {
        return this.iChronology.dayOfYear();
    }
}

