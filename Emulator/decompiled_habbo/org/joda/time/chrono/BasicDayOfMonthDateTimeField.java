/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.field.PreciseDurationDateTimeField;

final class BasicDayOfMonthDateTimeField
extends PreciseDurationDateTimeField {
    private static final long serialVersionUID = -4677223814028011723L;
    private final BasicChronology iChronology;

    BasicDayOfMonthDateTimeField(BasicChronology basicChronology, DurationField durationField) {
        super(DateTimeFieldType.dayOfMonth(), durationField);
        this.iChronology = basicChronology;
    }

    public int get(long l) {
        return this.iChronology.getDayOfMonth(l);
    }

    public DurationField getRangeDurationField() {
        return this.iChronology.months();
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMaximumValue() {
        return this.iChronology.getDaysInMonthMax();
    }

    public int getMaximumValue(long l) {
        return this.iChronology.getDaysInMonthMax(l);
    }

    public int getMaximumValue(ReadablePartial readablePartial) {
        if (readablePartial.isSupported(DateTimeFieldType.monthOfYear())) {
            int n = readablePartial.get(DateTimeFieldType.monthOfYear());
            if (readablePartial.isSupported(DateTimeFieldType.year())) {
                int n2 = readablePartial.get(DateTimeFieldType.year());
                return this.iChronology.getDaysInYearMonth(n2, n);
            }
            return this.iChronology.getDaysInMonthMax(n);
        }
        return this.getMaximumValue();
    }

    public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
        int n = readablePartial.size();
        for (int i = 0; i < n; ++i) {
            if (readablePartial.getFieldType(i) != DateTimeFieldType.monthOfYear()) continue;
            int n2 = nArray[i];
            for (int j = 0; j < n; ++j) {
                if (readablePartial.getFieldType(j) != DateTimeFieldType.year()) continue;
                int n3 = nArray[j];
                return this.iChronology.getDaysInYearMonth(n3, n2);
            }
            return this.iChronology.getDaysInMonthMax(n2);
        }
        return this.getMaximumValue();
    }

    protected int getMaximumValueForSet(long l, int n) {
        return this.iChronology.getDaysInMonthMaxForSet(l, n);
    }

    public boolean isLeap(long l) {
        return this.iChronology.isLeapDay(l);
    }

    private Object readResolve() {
        return this.iChronology.dayOfMonth();
    }
}

