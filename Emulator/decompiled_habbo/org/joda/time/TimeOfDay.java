/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BasePartial;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.AbstractPartialFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISODateTimeFormat;

@Deprecated
public final class TimeOfDay
extends BasePartial
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = 3633353405803318660L;
    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[]{DateTimeFieldType.hourOfDay(), DateTimeFieldType.minuteOfHour(), DateTimeFieldType.secondOfMinute(), DateTimeFieldType.millisOfSecond()};
    public static final TimeOfDay MIDNIGHT = new TimeOfDay(0, 0, 0, 0);
    public static final int HOUR_OF_DAY = 0;
    public static final int MINUTE_OF_HOUR = 1;
    public static final int SECOND_OF_MINUTE = 2;
    public static final int MILLIS_OF_SECOND = 3;

    public static TimeOfDay fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new TimeOfDay(calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(14));
    }

    public static TimeOfDay fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new TimeOfDay(date.getHours(), date.getMinutes(), date.getSeconds(), ((int)(date.getTime() % 1000L) + 1000) % 1000);
    }

    public static TimeOfDay fromMillisOfDay(long l) {
        return TimeOfDay.fromMillisOfDay(l, null);
    }

    public static TimeOfDay fromMillisOfDay(long l, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        chronology = chronology.withUTC();
        return new TimeOfDay(l, chronology);
    }

    public TimeOfDay() {
    }

    public TimeOfDay(DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }

    public TimeOfDay(Chronology chronology) {
        super(chronology);
    }

    public TimeOfDay(long l) {
        super(l);
    }

    public TimeOfDay(long l, Chronology chronology) {
        super(l, chronology);
    }

    public TimeOfDay(Object object) {
        super(object, null, ISODateTimeFormat.timeParser());
    }

    public TimeOfDay(Object object, Chronology chronology) {
        super(object, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.timeParser());
    }

    public TimeOfDay(int n, int n2) {
        this(n, n2, 0, 0, null);
    }

    public TimeOfDay(int n, int n2, Chronology chronology) {
        this(n, n2, 0, 0, chronology);
    }

    public TimeOfDay(int n, int n2, int n3) {
        this(n, n2, n3, 0, null);
    }

    public TimeOfDay(int n, int n2, int n3, Chronology chronology) {
        this(n, n2, n3, 0, chronology);
    }

    public TimeOfDay(int n, int n2, int n3, int n4) {
        this(n, n2, n3, n4, null);
    }

    public TimeOfDay(int n, int n2, int n3, int n4, Chronology chronology) {
        super(new int[]{n, n2, n3, n4}, chronology);
    }

    TimeOfDay(TimeOfDay timeOfDay, int[] nArray) {
        super((BasePartial)timeOfDay, nArray);
    }

    TimeOfDay(TimeOfDay timeOfDay, Chronology chronology) {
        super((BasePartial)timeOfDay, chronology);
    }

    public int size() {
        return 4;
    }

    protected DateTimeField getField(int n, Chronology chronology) {
        switch (n) {
            case 0: {
                return chronology.hourOfDay();
            }
            case 1: {
                return chronology.minuteOfHour();
            }
            case 2: {
                return chronology.secondOfMinute();
            }
            case 3: {
                return chronology.millisOfSecond();
            }
        }
        throw new IndexOutOfBoundsException("Invalid index: " + n);
    }

    public DateTimeFieldType getFieldType(int n) {
        return FIELD_TYPES[n];
    }

    public DateTimeFieldType[] getFieldTypes() {
        return (DateTimeFieldType[])FIELD_TYPES.clone();
    }

    public TimeOfDay withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if ((chronology = chronology.withUTC()) == this.getChronology()) {
            return this;
        }
        TimeOfDay timeOfDay = new TimeOfDay(this, chronology);
        chronology.validate(timeOfDay, this.getValues());
        return timeOfDay;
    }

    public TimeOfDay withField(DateTimeFieldType dateTimeFieldType, int n) {
        int n2 = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(n2)) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).set(this, n2, nArray, n);
        return new TimeOfDay(this, nArray);
    }

    public TimeOfDay withFieldAdded(DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).addWrapPartial(this, n2, nArray, n);
        return new TimeOfDay(this, nArray);
    }

    public TimeOfDay withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            DurationFieldType durationFieldType = readablePeriod.getFieldType(i);
            int n2 = this.indexOf(durationFieldType);
            if (n2 < 0) continue;
            nArray = this.getField(n2).addWrapPartial(this, n2, nArray, FieldUtils.safeMultiply(readablePeriod.getValue(i), n));
        }
        return new TimeOfDay(this, nArray);
    }

    public TimeOfDay plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public TimeOfDay plusHours(int n) {
        return this.withFieldAdded(DurationFieldType.hours(), n);
    }

    public TimeOfDay plusMinutes(int n) {
        return this.withFieldAdded(DurationFieldType.minutes(), n);
    }

    public TimeOfDay plusSeconds(int n) {
        return this.withFieldAdded(DurationFieldType.seconds(), n);
    }

    public TimeOfDay plusMillis(int n) {
        return this.withFieldAdded(DurationFieldType.millis(), n);
    }

    public TimeOfDay minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public TimeOfDay minusHours(int n) {
        return this.withFieldAdded(DurationFieldType.hours(), FieldUtils.safeNegate(n));
    }

    public TimeOfDay minusMinutes(int n) {
        return this.withFieldAdded(DurationFieldType.minutes(), FieldUtils.safeNegate(n));
    }

    public TimeOfDay minusSeconds(int n) {
        return this.withFieldAdded(DurationFieldType.seconds(), FieldUtils.safeNegate(n));
    }

    public TimeOfDay minusMillis(int n) {
        return this.withFieldAdded(DurationFieldType.millis(), FieldUtils.safeNegate(n));
    }

    public Property property(DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }

    public LocalTime toLocalTime() {
        return new LocalTime(this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute(), this.getMillisOfSecond(), this.getChronology());
    }

    public DateTime toDateTimeToday() {
        return this.toDateTimeToday(null);
    }

    public DateTime toDateTimeToday(DateTimeZone dateTimeZone) {
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        long l = DateTimeUtils.currentTimeMillis();
        long l2 = chronology.set(this, l);
        return new DateTime(l2, chronology);
    }

    public int getHourOfDay() {
        return this.getValue(0);
    }

    public int getMinuteOfHour() {
        return this.getValue(1);
    }

    public int getSecondOfMinute() {
        return this.getValue(2);
    }

    public int getMillisOfSecond() {
        return this.getValue(3);
    }

    public TimeOfDay withHourOfDay(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().hourOfDay().set(this, 0, nArray, n);
        return new TimeOfDay(this, nArray);
    }

    public TimeOfDay withMinuteOfHour(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().minuteOfHour().set(this, 1, nArray, n);
        return new TimeOfDay(this, nArray);
    }

    public TimeOfDay withSecondOfMinute(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().secondOfMinute().set(this, 2, nArray, n);
        return new TimeOfDay(this, nArray);
    }

    public TimeOfDay withMillisOfSecond(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().millisOfSecond().set(this, 3, nArray, n);
        return new TimeOfDay(this, nArray);
    }

    public Property hourOfDay() {
        return new Property(this, 0);
    }

    public Property minuteOfHour() {
        return new Property(this, 1);
    }

    public Property secondOfMinute() {
        return new Property(this, 2);
    }

    public Property millisOfSecond() {
        return new Property(this, 3);
    }

    public String toString() {
        return ISODateTimeFormat.tTime().print(this);
    }

    @Deprecated
    public static class Property
    extends AbstractPartialFieldProperty
    implements Serializable {
        private static final long serialVersionUID = 5598459141741063833L;
        private final TimeOfDay iTimeOfDay;
        private final int iFieldIndex;

        Property(TimeOfDay timeOfDay, int n) {
            this.iTimeOfDay = timeOfDay;
            this.iFieldIndex = n;
        }

        public DateTimeField getField() {
            return this.iTimeOfDay.getField(this.iFieldIndex);
        }

        protected ReadablePartial getReadablePartial() {
            return this.iTimeOfDay;
        }

        public TimeOfDay getTimeOfDay() {
            return this.iTimeOfDay;
        }

        public int get() {
            return this.iTimeOfDay.getValue(this.iFieldIndex);
        }

        public TimeOfDay addToCopy(int n) {
            int[] nArray = this.iTimeOfDay.getValues();
            nArray = this.getField().addWrapPartial(this.iTimeOfDay, this.iFieldIndex, nArray, n);
            return new TimeOfDay(this.iTimeOfDay, nArray);
        }

        public TimeOfDay addNoWrapToCopy(int n) {
            int[] nArray = this.iTimeOfDay.getValues();
            nArray = this.getField().add(this.iTimeOfDay, this.iFieldIndex, nArray, n);
            return new TimeOfDay(this.iTimeOfDay, nArray);
        }

        public TimeOfDay addWrapFieldToCopy(int n) {
            int[] nArray = this.iTimeOfDay.getValues();
            nArray = this.getField().addWrapField(this.iTimeOfDay, this.iFieldIndex, nArray, n);
            return new TimeOfDay(this.iTimeOfDay, nArray);
        }

        public TimeOfDay setCopy(int n) {
            int[] nArray = this.iTimeOfDay.getValues();
            nArray = this.getField().set(this.iTimeOfDay, this.iFieldIndex, nArray, n);
            return new TimeOfDay(this.iTimeOfDay, nArray);
        }

        public TimeOfDay setCopy(String string, Locale locale) {
            int[] nArray = this.iTimeOfDay.getValues();
            nArray = this.getField().set(this.iTimeOfDay, this.iFieldIndex, nArray, string, locale);
            return new TimeOfDay(this.iTimeOfDay, nArray);
        }

        public TimeOfDay setCopy(String string) {
            return this.setCopy(string, null);
        }

        public TimeOfDay withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }

        public TimeOfDay withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
    }
}

