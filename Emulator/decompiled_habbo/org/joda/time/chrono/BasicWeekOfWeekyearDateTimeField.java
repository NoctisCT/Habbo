/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.BasicChronology;
import org.joda.time.field.PreciseDurationDateTimeField;

final class BasicWeekOfWeekyearDateTimeField
extends PreciseDurationDateTimeField {
    private static final long serialVersionUID = -1587436826395135328L;
    private final BasicChronology iChronology;

    BasicWeekOfWeekyearDateTimeField(BasicChronology basicChronology, DurationField durationField) {
        super(DateTimeFieldType.weekOfWeekyear(), durationField);
        this.iChronology = basicChronology;
    }

    public int get(long l) {
        return this.iChronology.getWeekOfWeekyear(l);
    }

    public DurationField getRangeDurationField() {
        return this.iChronology.weekyears();
    }

    public long roundFloor(long l) {
        return super.roundFloor(l + 259200000L) - 259200000L;
    }

    public long roundCeiling(long l) {
        return super.roundCeiling(l + 259200000L) - 259200000L;
    }

    public long remainder(long l) {
        return super.remainder(l + 259200000L);
    }

    public int getMinimumValue() {
        return 1;
    }

    public int getMaximumValue() {
        return 53;
    }

    public int getMaximumValue(long l) {
        int n = this.iChronology.getWeekyear(l);
        return this.iChronology.getWeeksInYear(n);
    }

    public int getMaximumValue(ReadablePartial readablePartial) {
        if (readablePartial.isSupported(DateTimeFieldType.weekyear())) {
            int n = readablePartial.get(DateTimeFieldType.weekyear());
            return this.iChronology.getWeeksInYear(n);
        }
        return 53;
    }

    public int getMaximumValue(ReadablePartial readablePartial, int[] nArray) {
        int n = readablePartial.size();
        for (int i = 0; i < n; ++i) {
            if (readablePartial.getFieldType(i) != DateTimeFieldType.weekyear()) continue;
            int n2 = nArray[i];
            return this.iChronology.getWeeksInYear(n2);
        }
        return 53;
    }

    protected int getMaximumValueForSet(long l, int n) {
        return n > 52 ? this.getMaximumValue(l) : 52;
    }

    private Object readResolve() {
        return this.iChronology.weekOfWeekyear();
    }
}

