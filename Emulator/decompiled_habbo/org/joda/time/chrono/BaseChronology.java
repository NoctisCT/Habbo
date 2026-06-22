/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.chrono;

import java.io.Serializable;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.field.UnsupportedDateTimeField;
import org.joda.time.field.UnsupportedDurationField;

public abstract class BaseChronology
extends Chronology
implements Serializable {
    private static final long serialVersionUID = -7310865996721419676L;

    protected BaseChronology() {
    }

    public abstract DateTimeZone getZone();

    public abstract Chronology withUTC();

    public abstract Chronology withZone(DateTimeZone var1);

    public long getDateTimeMillis(int n, int n2, int n3, int n4) throws IllegalArgumentException {
        long l = this.year().set(0L, n);
        l = this.monthOfYear().set(l, n2);
        l = this.dayOfMonth().set(l, n3);
        return this.millisOfDay().set(l, n4);
    }

    public long getDateTimeMillis(int n, int n2, int n3, int n4, int n5, int n6, int n7) throws IllegalArgumentException {
        long l = this.year().set(0L, n);
        l = this.monthOfYear().set(l, n2);
        l = this.dayOfMonth().set(l, n3);
        l = this.hourOfDay().set(l, n4);
        l = this.minuteOfHour().set(l, n5);
        l = this.secondOfMinute().set(l, n6);
        return this.millisOfSecond().set(l, n7);
    }

    public long getDateTimeMillis(long l, int n, int n2, int n3, int n4) throws IllegalArgumentException {
        l = this.hourOfDay().set(l, n);
        l = this.minuteOfHour().set(l, n2);
        l = this.secondOfMinute().set(l, n3);
        return this.millisOfSecond().set(l, n4);
    }

    public void validate(ReadablePartial readablePartial, int[] nArray) {
        DateTimeField dateTimeField;
        int n;
        int n2;
        int n3 = readablePartial.size();
        for (n2 = 0; n2 < n3; ++n2) {
            n = nArray[n2];
            dateTimeField = readablePartial.getField(n2);
            if (n < dateTimeField.getMinimumValue()) {
                throw new IllegalFieldValueException(dateTimeField.getType(), (Number)n, (Number)dateTimeField.getMinimumValue(), null);
            }
            if (n <= dateTimeField.getMaximumValue()) continue;
            throw new IllegalFieldValueException(dateTimeField.getType(), (Number)n, null, (Number)dateTimeField.getMaximumValue());
        }
        for (n2 = 0; n2 < n3; ++n2) {
            n = nArray[n2];
            dateTimeField = readablePartial.getField(n2);
            if (n < dateTimeField.getMinimumValue(readablePartial, nArray)) {
                throw new IllegalFieldValueException(dateTimeField.getType(), (Number)n, (Number)dateTimeField.getMinimumValue(readablePartial, nArray), null);
            }
            if (n <= dateTimeField.getMaximumValue(readablePartial, nArray)) continue;
            throw new IllegalFieldValueException(dateTimeField.getType(), (Number)n, null, (Number)dateTimeField.getMaximumValue(readablePartial, nArray));
        }
    }

    public int[] get(ReadablePartial readablePartial, long l) {
        int n = readablePartial.size();
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = readablePartial.getFieldType(i).getField(this).get(l);
        }
        return nArray;
    }

    public long set(ReadablePartial readablePartial, long l) {
        int n = readablePartial.size();
        for (int i = 0; i < n; ++i) {
            l = readablePartial.getFieldType(i).getField(this).set(l, readablePartial.getValue(i));
        }
        return l;
    }

    public int[] get(ReadablePeriod readablePeriod, long l, long l2) {
        int n = readablePeriod.size();
        int[] nArray = new int[n];
        if (l != l2) {
            for (int i = 0; i < n; ++i) {
                DurationField durationField = readablePeriod.getFieldType(i).getField(this);
                int n2 = durationField.getDifference(l2, l);
                if (n2 != 0) {
                    l = durationField.add(l, n2);
                }
                nArray[i] = n2;
            }
        }
        return nArray;
    }

    public int[] get(ReadablePeriod readablePeriod, long l) {
        int n = readablePeriod.size();
        int[] nArray = new int[n];
        if (l != 0L) {
            long l2 = 0L;
            for (int i = 0; i < n; ++i) {
                DurationField durationField = readablePeriod.getFieldType(i).getField(this);
                if (!durationField.isPrecise()) continue;
                int n2 = durationField.getDifference(l, l2);
                l2 = durationField.add(l2, n2);
                nArray[i] = n2;
            }
        }
        return nArray;
    }

    public long add(ReadablePeriod readablePeriod, long l, int n) {
        if (n != 0 && readablePeriod != null) {
            int n2 = readablePeriod.size();
            for (int i = 0; i < n2; ++i) {
                long l2 = readablePeriod.getValue(i);
                if (l2 == 0L) continue;
                l = readablePeriod.getFieldType(i).getField(this).add(l, l2 * (long)n);
            }
        }
        return l;
    }

    public long add(long l, long l2, int n) {
        if (l2 == 0L || n == 0) {
            return l;
        }
        long l3 = FieldUtils.safeMultiply(l2, n);
        return FieldUtils.safeAdd(l, l3);
    }

    public DurationField millis() {
        return UnsupportedDurationField.getInstance(DurationFieldType.millis());
    }

    public DateTimeField millisOfSecond() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.millisOfSecond(), this.millis());
    }

    public DateTimeField millisOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.millisOfDay(), this.millis());
    }

    public DurationField seconds() {
        return UnsupportedDurationField.getInstance(DurationFieldType.seconds());
    }

    public DateTimeField secondOfMinute() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.secondOfMinute(), this.seconds());
    }

    public DateTimeField secondOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.secondOfDay(), this.seconds());
    }

    public DurationField minutes() {
        return UnsupportedDurationField.getInstance(DurationFieldType.minutes());
    }

    public DateTimeField minuteOfHour() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.minuteOfHour(), this.minutes());
    }

    public DateTimeField minuteOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.minuteOfDay(), this.minutes());
    }

    public DurationField hours() {
        return UnsupportedDurationField.getInstance(DurationFieldType.hours());
    }

    public DateTimeField hourOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.hourOfDay(), this.hours());
    }

    public DateTimeField clockhourOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.clockhourOfDay(), this.hours());
    }

    public DurationField halfdays() {
        return UnsupportedDurationField.getInstance(DurationFieldType.halfdays());
    }

    public DateTimeField hourOfHalfday() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.hourOfHalfday(), this.hours());
    }

    public DateTimeField clockhourOfHalfday() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.clockhourOfHalfday(), this.hours());
    }

    public DateTimeField halfdayOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.halfdayOfDay(), this.halfdays());
    }

    public DurationField days() {
        return UnsupportedDurationField.getInstance(DurationFieldType.days());
    }

    public DateTimeField dayOfWeek() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfWeek(), this.days());
    }

    public DateTimeField dayOfMonth() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfMonth(), this.days());
    }

    public DateTimeField dayOfYear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfYear(), this.days());
    }

    public DurationField weeks() {
        return UnsupportedDurationField.getInstance(DurationFieldType.weeks());
    }

    public DateTimeField weekOfWeekyear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekOfWeekyear(), this.weeks());
    }

    public DurationField weekyears() {
        return UnsupportedDurationField.getInstance(DurationFieldType.weekyears());
    }

    public DateTimeField weekyear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekyear(), this.weekyears());
    }

    public DateTimeField weekyearOfCentury() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekyearOfCentury(), this.weekyears());
    }

    public DurationField months() {
        return UnsupportedDurationField.getInstance(DurationFieldType.months());
    }

    public DateTimeField monthOfYear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.monthOfYear(), this.months());
    }

    public DurationField years() {
        return UnsupportedDurationField.getInstance(DurationFieldType.years());
    }

    public DateTimeField year() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.year(), this.years());
    }

    public DateTimeField yearOfEra() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.yearOfEra(), this.years());
    }

    public DateTimeField yearOfCentury() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.yearOfCentury(), this.years());
    }

    public DurationField centuries() {
        return UnsupportedDurationField.getInstance(DurationFieldType.centuries());
    }

    public DateTimeField centuryOfEra() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.centuryOfEra(), this.centuries());
    }

    public DurationField eras() {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }

    public DateTimeField era() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.era(), this.eras());
    }

    public abstract String toString();
}

