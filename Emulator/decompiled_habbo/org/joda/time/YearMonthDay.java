/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.TimeOfDay;
import org.joda.time.base.BasePartial;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.AbstractPartialFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISODateTimeFormat;

@Deprecated
public final class YearMonthDay
extends BasePartial
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = 797544782896179L;
    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[]{DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()};
    public static final int YEAR = 0;
    public static final int MONTH_OF_YEAR = 1;
    public static final int DAY_OF_MONTH = 2;

    public static YearMonthDay fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new YearMonthDay(calendar.get(1), calendar.get(2) + 1, calendar.get(5));
    }

    public static YearMonthDay fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new YearMonthDay(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    public YearMonthDay() {
    }

    public YearMonthDay(DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }

    public YearMonthDay(Chronology chronology) {
        super(chronology);
    }

    public YearMonthDay(long l) {
        super(l);
    }

    public YearMonthDay(long l, Chronology chronology) {
        super(l, chronology);
    }

    public YearMonthDay(Object object) {
        super(object, null, ISODateTimeFormat.dateOptionalTimeParser());
    }

    public YearMonthDay(Object object, Chronology chronology) {
        super(object, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.dateOptionalTimeParser());
    }

    public YearMonthDay(int n, int n2, int n3) {
        this(n, n2, n3, null);
    }

    public YearMonthDay(int n, int n2, int n3, Chronology chronology) {
        super(new int[]{n, n2, n3}, chronology);
    }

    YearMonthDay(YearMonthDay yearMonthDay, int[] nArray) {
        super((BasePartial)yearMonthDay, nArray);
    }

    YearMonthDay(YearMonthDay yearMonthDay, Chronology chronology) {
        super((BasePartial)yearMonthDay, chronology);
    }

    public int size() {
        return 3;
    }

    protected DateTimeField getField(int n, Chronology chronology) {
        switch (n) {
            case 0: {
                return chronology.year();
            }
            case 1: {
                return chronology.monthOfYear();
            }
            case 2: {
                return chronology.dayOfMonth();
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

    public YearMonthDay withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if ((chronology = chronology.withUTC()) == this.getChronology()) {
            return this;
        }
        YearMonthDay yearMonthDay = new YearMonthDay(this, chronology);
        chronology.validate(yearMonthDay, this.getValues());
        return yearMonthDay;
    }

    public YearMonthDay withField(DateTimeFieldType dateTimeFieldType, int n) {
        int n2 = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(n2)) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).set(this, n2, nArray, n);
        return new YearMonthDay(this, nArray);
    }

    public YearMonthDay withFieldAdded(DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).add(this, n2, nArray, n);
        return new YearMonthDay(this, nArray);
    }

    public YearMonthDay withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            DurationFieldType durationFieldType = readablePeriod.getFieldType(i);
            int n2 = this.indexOf(durationFieldType);
            if (n2 < 0) continue;
            nArray = this.getField(n2).add(this, n2, nArray, FieldUtils.safeMultiply(readablePeriod.getValue(i), n));
        }
        return new YearMonthDay(this, nArray);
    }

    public YearMonthDay plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public YearMonthDay plusYears(int n) {
        return this.withFieldAdded(DurationFieldType.years(), n);
    }

    public YearMonthDay plusMonths(int n) {
        return this.withFieldAdded(DurationFieldType.months(), n);
    }

    public YearMonthDay plusDays(int n) {
        return this.withFieldAdded(DurationFieldType.days(), n);
    }

    public YearMonthDay minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public YearMonthDay minusYears(int n) {
        return this.withFieldAdded(DurationFieldType.years(), FieldUtils.safeNegate(n));
    }

    public YearMonthDay minusMonths(int n) {
        return this.withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(n));
    }

    public YearMonthDay minusDays(int n) {
        return this.withFieldAdded(DurationFieldType.days(), FieldUtils.safeNegate(n));
    }

    public Property property(DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }

    public LocalDate toLocalDate() {
        return new LocalDate(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), this.getChronology());
    }

    public DateTime toDateTimeAtMidnight() {
        return this.toDateTimeAtMidnight(null);
    }

    public DateTime toDateTimeAtMidnight(DateTimeZone dateTimeZone) {
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), 0, 0, 0, 0, chronology);
    }

    public DateTime toDateTimeAtCurrentTime() {
        return this.toDateTimeAtCurrentTime(null);
    }

    public DateTime toDateTimeAtCurrentTime(DateTimeZone dateTimeZone) {
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        long l = DateTimeUtils.currentTimeMillis();
        long l2 = chronology.set(this, l);
        return new DateTime(l2, chronology);
    }

    public DateMidnight toDateMidnight() {
        return this.toDateMidnight(null);
    }

    public DateMidnight toDateMidnight(DateTimeZone dateTimeZone) {
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        return new DateMidnight(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), chronology);
    }

    public DateTime toDateTime(TimeOfDay timeOfDay) {
        return this.toDateTime(timeOfDay, null);
    }

    public DateTime toDateTime(TimeOfDay timeOfDay, DateTimeZone dateTimeZone) {
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        long l = DateTimeUtils.currentTimeMillis();
        l = chronology.set(this, l);
        if (timeOfDay != null) {
            l = chronology.set(timeOfDay, l);
        }
        return new DateTime(l, chronology);
    }

    public Interval toInterval() {
        return this.toInterval(null);
    }

    public Interval toInterval(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        return this.toDateMidnight(dateTimeZone).toInterval();
    }

    public int getYear() {
        return this.getValue(0);
    }

    public int getMonthOfYear() {
        return this.getValue(1);
    }

    public int getDayOfMonth() {
        return this.getValue(2);
    }

    public YearMonthDay withYear(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().year().set(this, 0, nArray, n);
        return new YearMonthDay(this, nArray);
    }

    public YearMonthDay withMonthOfYear(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().monthOfYear().set(this, 1, nArray, n);
        return new YearMonthDay(this, nArray);
    }

    public YearMonthDay withDayOfMonth(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().dayOfMonth().set(this, 2, nArray, n);
        return new YearMonthDay(this, nArray);
    }

    public Property year() {
        return new Property(this, 0);
    }

    public Property monthOfYear() {
        return new Property(this, 1);
    }

    public Property dayOfMonth() {
        return new Property(this, 2);
    }

    public String toString() {
        return ISODateTimeFormat.yearMonthDay().print(this);
    }

    @Deprecated
    public static class Property
    extends AbstractPartialFieldProperty
    implements Serializable {
        private static final long serialVersionUID = 5727734012190224363L;
        private final YearMonthDay iYearMonthDay;
        private final int iFieldIndex;

        Property(YearMonthDay yearMonthDay, int n) {
            this.iYearMonthDay = yearMonthDay;
            this.iFieldIndex = n;
        }

        public DateTimeField getField() {
            return this.iYearMonthDay.getField(this.iFieldIndex);
        }

        protected ReadablePartial getReadablePartial() {
            return this.iYearMonthDay;
        }

        public YearMonthDay getYearMonthDay() {
            return this.iYearMonthDay;
        }

        public int get() {
            return this.iYearMonthDay.getValue(this.iFieldIndex);
        }

        public YearMonthDay addToCopy(int n) {
            int[] nArray = this.iYearMonthDay.getValues();
            nArray = this.getField().add(this.iYearMonthDay, this.iFieldIndex, nArray, n);
            return new YearMonthDay(this.iYearMonthDay, nArray);
        }

        public YearMonthDay addWrapFieldToCopy(int n) {
            int[] nArray = this.iYearMonthDay.getValues();
            nArray = this.getField().addWrapField(this.iYearMonthDay, this.iFieldIndex, nArray, n);
            return new YearMonthDay(this.iYearMonthDay, nArray);
        }

        public YearMonthDay setCopy(int n) {
            int[] nArray = this.iYearMonthDay.getValues();
            nArray = this.getField().set(this.iYearMonthDay, this.iFieldIndex, nArray, n);
            return new YearMonthDay(this.iYearMonthDay, nArray);
        }

        public YearMonthDay setCopy(String string, Locale locale) {
            int[] nArray = this.iYearMonthDay.getValues();
            nArray = this.getField().set(this.iYearMonthDay, this.iFieldIndex, nArray, string, locale);
            return new YearMonthDay(this.iYearMonthDay, nArray);
        }

        public YearMonthDay setCopy(String string) {
            return this.setCopy(string, null);
        }

        public YearMonthDay withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }

        public YearMonthDay withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
    }
}

