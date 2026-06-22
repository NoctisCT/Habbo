/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 *  org.joda.convert.ToString
 */
package org.joda.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BasePartial;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.AbstractPartialFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class YearMonth
extends BasePartial
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = 797544782896179L;
    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[]{DateTimeFieldType.year(), DateTimeFieldType.monthOfYear()};
    public static final int YEAR = 0;
    public static final int MONTH_OF_YEAR = 1;

    public static YearMonth now() {
        return new YearMonth();
    }

    public static YearMonth now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new YearMonth(dateTimeZone);
    }

    public static YearMonth now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new YearMonth(chronology);
    }

    @FromString
    public static YearMonth parse(String string) {
        return YearMonth.parse(string, ISODateTimeFormat.localDateParser());
    }

    public static YearMonth parse(String string, DateTimeFormatter dateTimeFormatter) {
        LocalDate localDate = dateTimeFormatter.parseLocalDate(string);
        return new YearMonth(localDate.getYear(), localDate.getMonthOfYear());
    }

    public static YearMonth fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new YearMonth(calendar.get(1), calendar.get(2) + 1);
    }

    public static YearMonth fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new YearMonth(date.getYear() + 1900, date.getMonth() + 1);
    }

    public YearMonth() {
    }

    public YearMonth(DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }

    public YearMonth(Chronology chronology) {
        super(chronology);
    }

    public YearMonth(long l) {
        super(l);
    }

    public YearMonth(long l, Chronology chronology) {
        super(l, chronology);
    }

    public YearMonth(Object object) {
        super(object, null, ISODateTimeFormat.localDateParser());
    }

    public YearMonth(Object object, Chronology chronology) {
        super(object, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.localDateParser());
    }

    public YearMonth(int n, int n2) {
        this(n, n2, null);
    }

    public YearMonth(int n, int n2, Chronology chronology) {
        super(new int[]{n, n2}, chronology);
    }

    YearMonth(YearMonth yearMonth, int[] nArray) {
        super((BasePartial)yearMonth, nArray);
    }

    YearMonth(YearMonth yearMonth, Chronology chronology) {
        super((BasePartial)yearMonth, chronology);
    }

    private Object readResolve() {
        if (!DateTimeZone.UTC.equals(this.getChronology().getZone())) {
            return new YearMonth(this, this.getChronology().withUTC());
        }
        return this;
    }

    public int size() {
        return 2;
    }

    protected DateTimeField getField(int n, Chronology chronology) {
        switch (n) {
            case 0: {
                return chronology.year();
            }
            case 1: {
                return chronology.monthOfYear();
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

    public YearMonth withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if ((chronology = chronology.withUTC()) == this.getChronology()) {
            return this;
        }
        YearMonth yearMonth = new YearMonth(this, chronology);
        chronology.validate(yearMonth, this.getValues());
        return yearMonth;
    }

    public YearMonth withField(DateTimeFieldType dateTimeFieldType, int n) {
        int n2 = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(n2)) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).set(this, n2, nArray, n);
        return new YearMonth(this, nArray);
    }

    public YearMonth withFieldAdded(DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).add(this, n2, nArray, n);
        return new YearMonth(this, nArray);
    }

    public YearMonth withPeriodAdded(ReadablePeriod readablePeriod, int n) {
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
        return new YearMonth(this, nArray);
    }

    public YearMonth plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public YearMonth plusYears(int n) {
        return this.withFieldAdded(DurationFieldType.years(), n);
    }

    public YearMonth plusMonths(int n) {
        return this.withFieldAdded(DurationFieldType.months(), n);
    }

    public YearMonth minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public YearMonth minusYears(int n) {
        return this.withFieldAdded(DurationFieldType.years(), FieldUtils.safeNegate(n));
    }

    public YearMonth minusMonths(int n) {
        return this.withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(n));
    }

    public LocalDate toLocalDate(int n) {
        return new LocalDate(this.getYear(), this.getMonthOfYear(), n, this.getChronology());
    }

    public Interval toInterval() {
        return this.toInterval(null);
    }

    public Interval toInterval(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        DateTime dateTime = this.toLocalDate(1).toDateTimeAtStartOfDay(dateTimeZone);
        DateTime dateTime2 = this.plusMonths(1).toLocalDate(1).toDateTimeAtStartOfDay(dateTimeZone);
        return new Interval((ReadableInstant)dateTime, (ReadableInstant)dateTime2);
    }

    public int getYear() {
        return this.getValue(0);
    }

    public int getMonthOfYear() {
        return this.getValue(1);
    }

    public YearMonth withYear(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().year().set(this, 0, nArray, n);
        return new YearMonth(this, nArray);
    }

    public YearMonth withMonthOfYear(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().monthOfYear().set(this, 1, nArray, n);
        return new YearMonth(this, nArray);
    }

    public Property property(DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }

    public Property year() {
        return new Property(this, 0);
    }

    public Property monthOfYear() {
        return new Property(this, 1);
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.yearMonth().print(this);
    }

    public String toString(String string) {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).print(this);
    }

    public String toString(String string, Locale locale) throws IllegalArgumentException {
        if (string == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(string).withLocale(locale).print(this);
    }

    public static class Property
    extends AbstractPartialFieldProperty
    implements Serializable {
        private static final long serialVersionUID = 5727734012190224363L;
        private final YearMonth iBase;
        private final int iFieldIndex;

        Property(YearMonth yearMonth, int n) {
            this.iBase = yearMonth;
            this.iFieldIndex = n;
        }

        public DateTimeField getField() {
            return this.iBase.getField(this.iFieldIndex);
        }

        protected ReadablePartial getReadablePartial() {
            return this.iBase;
        }

        public YearMonth getYearMonth() {
            return this.iBase;
        }

        public int get() {
            return this.iBase.getValue(this.iFieldIndex);
        }

        public YearMonth addToCopy(int n) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().add(this.iBase, this.iFieldIndex, nArray, n);
            return new YearMonth(this.iBase, nArray);
        }

        public YearMonth addWrapFieldToCopy(int n) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().addWrapField(this.iBase, this.iFieldIndex, nArray, n);
            return new YearMonth(this.iBase, nArray);
        }

        public YearMonth setCopy(int n) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().set(this.iBase, this.iFieldIndex, nArray, n);
            return new YearMonth(this.iBase, nArray);
        }

        public YearMonth setCopy(String string, Locale locale) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().set(this.iBase, this.iFieldIndex, nArray, string, locale);
            return new YearMonth(this.iBase, nArray);
        }

        public YearMonth setCopy(String string) {
            return this.setCopy(string, null);
        }
    }
}

