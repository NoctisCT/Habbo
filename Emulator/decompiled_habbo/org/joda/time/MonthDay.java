/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 *  org.joda.convert.ToString
 */
package org.joda.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BasePartial;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.AbstractPartialFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

public final class MonthDay
extends BasePartial
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = 2954560699050434609L;
    private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[]{DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()};
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder().appendOptional(ISODateTimeFormat.localDateParser().getParser()).appendOptional(DateTimeFormat.forPattern("--MM-dd").getParser()).toFormatter();
    public static final int MONTH_OF_YEAR = 0;
    public static final int DAY_OF_MONTH = 1;

    public static MonthDay now() {
        return new MonthDay();
    }

    public static MonthDay now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new MonthDay(dateTimeZone);
    }

    public static MonthDay now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new MonthDay(chronology);
    }

    @FromString
    public static MonthDay parse(String string) {
        return MonthDay.parse(string, PARSER);
    }

    public static MonthDay parse(String string, DateTimeFormatter dateTimeFormatter) {
        LocalDate localDate = dateTimeFormatter.parseLocalDate(string);
        return new MonthDay(localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    public static MonthDay fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new MonthDay(calendar.get(2) + 1, calendar.get(5));
    }

    public static MonthDay fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new MonthDay(date.getMonth() + 1, date.getDate());
    }

    public MonthDay() {
    }

    public MonthDay(DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }

    public MonthDay(Chronology chronology) {
        super(chronology);
    }

    public MonthDay(long l) {
        super(l);
    }

    public MonthDay(long l, Chronology chronology) {
        super(l, chronology);
    }

    public MonthDay(Object object) {
        super(object, null, ISODateTimeFormat.localDateParser());
    }

    public MonthDay(Object object, Chronology chronology) {
        super(object, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.localDateParser());
    }

    public MonthDay(int n, int n2) {
        this(n, n2, null);
    }

    public MonthDay(int n, int n2, Chronology chronology) {
        super(new int[]{n, n2}, chronology);
    }

    MonthDay(MonthDay monthDay, int[] nArray) {
        super((BasePartial)monthDay, nArray);
    }

    MonthDay(MonthDay monthDay, Chronology chronology) {
        super((BasePartial)monthDay, chronology);
    }

    private Object readResolve() {
        if (!DateTimeZone.UTC.equals(this.getChronology().getZone())) {
            return new MonthDay(this, this.getChronology().withUTC());
        }
        return this;
    }

    public int size() {
        return 2;
    }

    protected DateTimeField getField(int n, Chronology chronology) {
        switch (n) {
            case 0: {
                return chronology.monthOfYear();
            }
            case 1: {
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

    public MonthDay withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if ((chronology = chronology.withUTC()) == this.getChronology()) {
            return this;
        }
        MonthDay monthDay = new MonthDay(this, chronology);
        chronology.validate(monthDay, this.getValues());
        return monthDay;
    }

    public MonthDay withField(DateTimeFieldType dateTimeFieldType, int n) {
        int n2 = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(n2)) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).set(this, n2, nArray, n);
        return new MonthDay(this, nArray);
    }

    public MonthDay withFieldAdded(DurationFieldType durationFieldType, int n) {
        int n2 = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        int[] nArray = this.getValues();
        nArray = this.getField(n2).add(this, n2, nArray, n);
        return new MonthDay(this, nArray);
    }

    public MonthDay withPeriodAdded(ReadablePeriod readablePeriod, int n) {
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
        return new MonthDay(this, nArray);
    }

    public MonthDay plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public MonthDay plusMonths(int n) {
        return this.withFieldAdded(DurationFieldType.months(), n);
    }

    public MonthDay plusDays(int n) {
        return this.withFieldAdded(DurationFieldType.days(), n);
    }

    public MonthDay minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public MonthDay minusMonths(int n) {
        return this.withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(n));
    }

    public MonthDay minusDays(int n) {
        return this.withFieldAdded(DurationFieldType.days(), FieldUtils.safeNegate(n));
    }

    public LocalDate toLocalDate(int n) {
        return new LocalDate(n, this.getMonthOfYear(), this.getDayOfMonth(), this.getChronology());
    }

    public int getMonthOfYear() {
        return this.getValue(0);
    }

    public int getDayOfMonth() {
        return this.getValue(1);
    }

    public MonthDay withMonthOfYear(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().monthOfYear().set(this, 0, nArray, n);
        return new MonthDay(this, nArray);
    }

    public MonthDay withDayOfMonth(int n) {
        int[] nArray = this.getValues();
        nArray = this.getChronology().dayOfMonth().set(this, 1, nArray, n);
        return new MonthDay(this, nArray);
    }

    public Property property(DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }

    public Property monthOfYear() {
        return new Property(this, 0);
    }

    public Property dayOfMonth() {
        return new Property(this, 1);
    }

    @ToString
    public String toString() {
        ArrayList<DateTimeFieldType> arrayList = new ArrayList<DateTimeFieldType>();
        arrayList.add(DateTimeFieldType.monthOfYear());
        arrayList.add(DateTimeFieldType.dayOfMonth());
        return ISODateTimeFormat.forFields(arrayList, true, true).print(this);
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
        private final MonthDay iBase;
        private final int iFieldIndex;

        Property(MonthDay monthDay, int n) {
            this.iBase = monthDay;
            this.iFieldIndex = n;
        }

        public DateTimeField getField() {
            return this.iBase.getField(this.iFieldIndex);
        }

        protected ReadablePartial getReadablePartial() {
            return this.iBase;
        }

        public MonthDay getMonthDay() {
            return this.iBase;
        }

        public int get() {
            return this.iBase.getValue(this.iFieldIndex);
        }

        public MonthDay addToCopy(int n) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().add(this.iBase, this.iFieldIndex, nArray, n);
            return new MonthDay(this.iBase, nArray);
        }

        public MonthDay addWrapFieldToCopy(int n) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().addWrapField(this.iBase, this.iFieldIndex, nArray, n);
            return new MonthDay(this.iBase, nArray);
        }

        public MonthDay setCopy(int n) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().set(this.iBase, this.iFieldIndex, nArray, n);
            return new MonthDay(this.iBase, nArray);
        }

        public MonthDay setCopy(String string, Locale locale) {
            int[] nArray = this.iBase.getValues();
            nArray = this.getField().set(this.iBase, this.iFieldIndex, nArray, string, locale);
            return new MonthDay(this.iBase, nArray);
        }

        public MonthDay setCopy(String string) {
            return this.setCopy(string, null);
        }
    }
}

