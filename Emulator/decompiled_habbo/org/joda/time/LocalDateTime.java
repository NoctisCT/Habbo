/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 *  org.joda.convert.ToString
 */
package org.joda.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BaseLocal;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.PartialConverter;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class LocalDateTime
extends BaseLocal
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = -268716875315837168L;
    private static final int YEAR = 0;
    private static final int MONTH_OF_YEAR = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final int MILLIS_OF_DAY = 3;
    private final long iLocalMillis;
    private final Chronology iChronology;

    public static LocalDateTime now() {
        return new LocalDateTime();
    }

    public static LocalDateTime now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalDateTime(dateTimeZone);
    }

    public static LocalDateTime now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalDateTime(chronology);
    }

    @FromString
    public static LocalDateTime parse(String string) {
        return LocalDateTime.parse(string, ISODateTimeFormat.localDateOptionalTimeParser());
    }

    public static LocalDateTime parse(String string, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseLocalDateTime(string);
    }

    public static LocalDateTime fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        int n = calendar.get(0);
        int n2 = calendar.get(1);
        return new LocalDateTime(n == 1 ? n2 : 1 - n2, calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(14));
    }

    public static LocalDateTime fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (date.getTime() < 0L) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            return LocalDateTime.fromCalendarFields(gregorianCalendar);
        }
        return new LocalDateTime(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds(), ((int)(date.getTime() % 1000L) + 1000) % 1000);
    }

    public LocalDateTime() {
        this(DateTimeUtils.currentTimeMillis(), (Chronology)ISOChronology.getInstance());
    }

    public LocalDateTime(DateTimeZone dateTimeZone) {
        this(DateTimeUtils.currentTimeMillis(), (Chronology)ISOChronology.getInstance(dateTimeZone));
    }

    public LocalDateTime(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }

    public LocalDateTime(long l) {
        this(l, (Chronology)ISOChronology.getInstance());
    }

    public LocalDateTime(long l, DateTimeZone dateTimeZone) {
        this(l, (Chronology)ISOChronology.getInstance(dateTimeZone));
    }

    public LocalDateTime(long l, Chronology chronology) {
        long l2;
        chronology = DateTimeUtils.getChronology(chronology);
        this.iLocalMillis = l2 = chronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, l);
        this.iChronology = chronology.withUTC();
    }

    public LocalDateTime(Object object) {
        this(object, (Chronology)null);
    }

    public LocalDateTime(Object object, DateTimeZone dateTimeZone) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        Chronology chronology = partialConverter.getChronology(object, dateTimeZone);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        int[] nArray = partialConverter.getPartialValues(this, object, chronology, ISODateTimeFormat.localDateOptionalTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(nArray[0], nArray[1], nArray[2], nArray[3]);
    }

    public LocalDateTime(Object object, Chronology chronology) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        chronology = partialConverter.getChronology(object, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        int[] nArray = partialConverter.getPartialValues(this, object, chronology, ISODateTimeFormat.localDateOptionalTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(nArray[0], nArray[1], nArray[2], nArray[3]);
    }

    public LocalDateTime(int n, int n2, int n3, int n4, int n5) {
        this(n, n2, n3, n4, n5, 0, 0, ISOChronology.getInstanceUTC());
    }

    public LocalDateTime(int n, int n2, int n3, int n4, int n5, int n6) {
        this(n, n2, n3, n4, n5, n6, 0, ISOChronology.getInstanceUTC());
    }

    public LocalDateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        this(n, n2, n3, n4, n5, n6, n7, ISOChronology.getInstanceUTC());
    }

    public LocalDateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology).withUTC();
        long l = chronology.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        this.iChronology = chronology;
        this.iLocalMillis = l;
    }

    private Object readResolve() {
        if (this.iChronology == null) {
            return new LocalDateTime(this.iLocalMillis, (Chronology)ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
            return new LocalDateTime(this.iLocalMillis, this.iChronology.withUTC());
        }
        return this;
    }

    public int size() {
        return 4;
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
            case 3: {
                return chronology.millisOfDay();
            }
        }
        throw new IndexOutOfBoundsException("Invalid index: " + n);
    }

    public int getValue(int n) {
        switch (n) {
            case 0: {
                return this.getChronology().year().get(this.getLocalMillis());
            }
            case 1: {
                return this.getChronology().monthOfYear().get(this.getLocalMillis());
            }
            case 2: {
                return this.getChronology().dayOfMonth().get(this.getLocalMillis());
            }
            case 3: {
                return this.getChronology().millisOfDay().get(this.getLocalMillis());
            }
        }
        throw new IndexOutOfBoundsException("Invalid index: " + n);
    }

    public int get(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        return dateTimeFieldType.getField(this.getChronology()).get(this.getLocalMillis());
    }

    public boolean isSupported(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            return false;
        }
        return dateTimeFieldType.getField(this.getChronology()).isSupported();
    }

    public boolean isSupported(DurationFieldType durationFieldType) {
        if (durationFieldType == null) {
            return false;
        }
        return durationFieldType.getField(this.getChronology()).isSupported();
    }

    protected long getLocalMillis() {
        return this.iLocalMillis;
    }

    public Chronology getChronology() {
        return this.iChronology;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime)object;
            if (this.iChronology.equals(localDateTime.iChronology)) {
                return this.iLocalMillis == localDateTime.iLocalMillis;
            }
        }
        return super.equals(object);
    }

    public int compareTo(ReadablePartial readablePartial) {
        if (this == readablePartial) {
            return 0;
        }
        if (readablePartial instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime)readablePartial;
            if (this.iChronology.equals(localDateTime.iChronology)) {
                return this.iLocalMillis < localDateTime.iLocalMillis ? -1 : (this.iLocalMillis == localDateTime.iLocalMillis ? 0 : 1);
            }
        }
        return super.compareTo(readablePartial);
    }

    public DateTime toDateTime() {
        return this.toDateTime((DateTimeZone)null);
    }

    public DateTime toDateTime(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        Chronology chronology = this.iChronology.withZone(dateTimeZone);
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute(), this.getMillisOfSecond(), chronology);
    }

    public LocalDate toLocalDate() {
        return new LocalDate(this.getLocalMillis(), this.getChronology());
    }

    public LocalTime toLocalTime() {
        return new LocalTime(this.getLocalMillis(), this.getChronology());
    }

    public Date toDate() {
        int n = this.getDayOfMonth();
        Date date = new Date(this.getYear() - 1900, this.getMonthOfYear() - 1, n, this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute());
        date.setTime(date.getTime() + (long)this.getMillisOfSecond());
        return this.correctDstTransition(date, TimeZone.getDefault());
    }

    public Date toDate(TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.clear();
        calendar.set(this.getYear(), this.getMonthOfYear() - 1, this.getDayOfMonth(), this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute());
        Date date = calendar.getTime();
        date.setTime(date.getTime() + (long)this.getMillisOfSecond());
        return this.correctDstTransition(date, timeZone);
    }

    private Date correctDstTransition(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        LocalDateTime localDateTime = LocalDateTime.fromCalendarFields(calendar);
        if (localDateTime.isBefore(this)) {
            while (localDateTime.isBefore(this)) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + 60000L);
                localDateTime = LocalDateTime.fromCalendarFields(calendar);
            }
            while (!localDateTime.isBefore(this)) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() - 1000L);
                localDateTime = LocalDateTime.fromCalendarFields(calendar);
            }
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 1000L);
        } else if (localDateTime.equals(this)) {
            Calendar calendar2 = Calendar.getInstance(timeZone);
            calendar2.setTimeInMillis(calendar.getTimeInMillis() - (long)timeZone.getDSTSavings());
            localDateTime = LocalDateTime.fromCalendarFields(calendar2);
            if (localDateTime.equals(this)) {
                calendar = calendar2;
            }
        }
        return calendar.getTime();
    }

    LocalDateTime withLocalMillis(long l) {
        return l == this.getLocalMillis() ? this : new LocalDateTime(l, this.getChronology());
    }

    public LocalDateTime withDate(int n, int n2, int n3) {
        Chronology chronology = this.getChronology();
        long l = this.getLocalMillis();
        l = chronology.year().set(l, n);
        l = chronology.monthOfYear().set(l, n2);
        l = chronology.dayOfMonth().set(l, n3);
        return this.withLocalMillis(l);
    }

    public LocalDateTime withTime(int n, int n2, int n3, int n4) {
        Chronology chronology = this.getChronology();
        long l = this.getLocalMillis();
        l = chronology.hourOfDay().set(l, n);
        l = chronology.minuteOfHour().set(l, n2);
        l = chronology.secondOfMinute().set(l, n3);
        l = chronology.millisOfSecond().set(l, n4);
        return this.withLocalMillis(l);
    }

    public LocalDateTime withFields(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().set(readablePartial, this.getLocalMillis()));
    }

    public LocalDateTime withField(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        long l = dateTimeFieldType.getField(this.getChronology()).set(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime withFieldAdded(DurationFieldType durationFieldType, int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n == 0) {
            return this;
        }
        long l = durationFieldType.getField(this.getChronology()).add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime withDurationAdded(ReadableDuration readableDuration, int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        long l = this.getChronology().add(this.getLocalMillis(), readableDuration.getMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        long l = this.getChronology().add(readablePeriod, this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }

    public LocalDateTime plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public LocalDateTime plusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plusHours(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().hours().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plusMinutes(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().minutes().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plusSeconds(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().seconds().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime plusMillis(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().millis().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }

    public LocalDateTime minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public LocalDateTime minusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minusHours(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().hours().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minusMinutes(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().minutes().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minusSeconds(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().seconds().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDateTime minusMillis(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().millis().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public Property property(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        if (!this.isSupported(dateTimeFieldType)) {
            throw new IllegalArgumentException("Field '" + dateTimeFieldType + "' is not supported");
        }
        return new Property(this, dateTimeFieldType.getField(this.getChronology()));
    }

    public int getEra() {
        return this.getChronology().era().get(this.getLocalMillis());
    }

    public int getCenturyOfEra() {
        return this.getChronology().centuryOfEra().get(this.getLocalMillis());
    }

    public int getYearOfEra() {
        return this.getChronology().yearOfEra().get(this.getLocalMillis());
    }

    public int getYearOfCentury() {
        return this.getChronology().yearOfCentury().get(this.getLocalMillis());
    }

    public int getYear() {
        return this.getChronology().year().get(this.getLocalMillis());
    }

    public int getWeekyear() {
        return this.getChronology().weekyear().get(this.getLocalMillis());
    }

    public int getMonthOfYear() {
        return this.getChronology().monthOfYear().get(this.getLocalMillis());
    }

    public int getWeekOfWeekyear() {
        return this.getChronology().weekOfWeekyear().get(this.getLocalMillis());
    }

    public int getDayOfYear() {
        return this.getChronology().dayOfYear().get(this.getLocalMillis());
    }

    public int getDayOfMonth() {
        return this.getChronology().dayOfMonth().get(this.getLocalMillis());
    }

    public int getDayOfWeek() {
        return this.getChronology().dayOfWeek().get(this.getLocalMillis());
    }

    public int getHourOfDay() {
        return this.getChronology().hourOfDay().get(this.getLocalMillis());
    }

    public int getMinuteOfHour() {
        return this.getChronology().minuteOfHour().get(this.getLocalMillis());
    }

    public int getSecondOfMinute() {
        return this.getChronology().secondOfMinute().get(this.getLocalMillis());
    }

    public int getMillisOfSecond() {
        return this.getChronology().millisOfSecond().get(this.getLocalMillis());
    }

    public int getMillisOfDay() {
        return this.getChronology().millisOfDay().get(this.getLocalMillis());
    }

    public LocalDateTime withEra(int n) {
        return this.withLocalMillis(this.getChronology().era().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withCenturyOfEra(int n) {
        return this.withLocalMillis(this.getChronology().centuryOfEra().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withYearOfEra(int n) {
        return this.withLocalMillis(this.getChronology().yearOfEra().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withYearOfCentury(int n) {
        return this.withLocalMillis(this.getChronology().yearOfCentury().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withYear(int n) {
        return this.withLocalMillis(this.getChronology().year().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withWeekyear(int n) {
        return this.withLocalMillis(this.getChronology().weekyear().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withMonthOfYear(int n) {
        return this.withLocalMillis(this.getChronology().monthOfYear().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withWeekOfWeekyear(int n) {
        return this.withLocalMillis(this.getChronology().weekOfWeekyear().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withDayOfYear(int n) {
        return this.withLocalMillis(this.getChronology().dayOfYear().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withDayOfMonth(int n) {
        return this.withLocalMillis(this.getChronology().dayOfMonth().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withDayOfWeek(int n) {
        return this.withLocalMillis(this.getChronology().dayOfWeek().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withHourOfDay(int n) {
        return this.withLocalMillis(this.getChronology().hourOfDay().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withMinuteOfHour(int n) {
        return this.withLocalMillis(this.getChronology().minuteOfHour().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withSecondOfMinute(int n) {
        return this.withLocalMillis(this.getChronology().secondOfMinute().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withMillisOfSecond(int n) {
        return this.withLocalMillis(this.getChronology().millisOfSecond().set(this.getLocalMillis(), n));
    }

    public LocalDateTime withMillisOfDay(int n) {
        return this.withLocalMillis(this.getChronology().millisOfDay().set(this.getLocalMillis(), n));
    }

    public Property era() {
        return new Property(this, this.getChronology().era());
    }

    public Property centuryOfEra() {
        return new Property(this, this.getChronology().centuryOfEra());
    }

    public Property yearOfCentury() {
        return new Property(this, this.getChronology().yearOfCentury());
    }

    public Property yearOfEra() {
        return new Property(this, this.getChronology().yearOfEra());
    }

    public Property year() {
        return new Property(this, this.getChronology().year());
    }

    public Property weekyear() {
        return new Property(this, this.getChronology().weekyear());
    }

    public Property monthOfYear() {
        return new Property(this, this.getChronology().monthOfYear());
    }

    public Property weekOfWeekyear() {
        return new Property(this, this.getChronology().weekOfWeekyear());
    }

    public Property dayOfYear() {
        return new Property(this, this.getChronology().dayOfYear());
    }

    public Property dayOfMonth() {
        return new Property(this, this.getChronology().dayOfMonth());
    }

    public Property dayOfWeek() {
        return new Property(this, this.getChronology().dayOfWeek());
    }

    public Property hourOfDay() {
        return new Property(this, this.getChronology().hourOfDay());
    }

    public Property minuteOfHour() {
        return new Property(this, this.getChronology().minuteOfHour());
    }

    public Property secondOfMinute() {
        return new Property(this, this.getChronology().secondOfMinute());
    }

    public Property millisOfSecond() {
        return new Property(this, this.getChronology().millisOfSecond());
    }

    public Property millisOfDay() {
        return new Property(this, this.getChronology().millisOfDay());
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.dateTime().print(this);
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

    public static final class Property
    extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -358138762846288L;
        private transient LocalDateTime iInstant;
        private transient DateTimeField iField;

        Property(LocalDateTime localDateTime, DateTimeField dateTimeField) {
            this.iInstant = localDateTime;
            this.iField = dateTimeField;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (LocalDateTime)objectInputStream.readObject();
            DateTimeFieldType dateTimeFieldType = (DateTimeFieldType)objectInputStream.readObject();
            this.iField = dateTimeFieldType.getField(this.iInstant.getChronology());
        }

        public DateTimeField getField() {
            return this.iField;
        }

        protected long getMillis() {
            return this.iInstant.getLocalMillis();
        }

        protected Chronology getChronology() {
            return this.iInstant.getChronology();
        }

        public LocalDateTime getLocalDateTime() {
            return this.iInstant;
        }

        public LocalDateTime addToCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }

        public LocalDateTime addToCopy(long l) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), l));
        }

        public LocalDateTime addWrapFieldToCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), n));
        }

        public LocalDateTime setCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), n));
        }

        public LocalDateTime setCopy(String string, Locale locale) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), string, locale));
        }

        public LocalDateTime setCopy(String string) {
            return this.setCopy(string, null);
        }

        public LocalDateTime withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }

        public LocalDateTime withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }

        public LocalDateTime roundFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
        }

        public LocalDateTime roundCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
        }

        public LocalDateTime roundHalfFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
        }

        public LocalDateTime roundHalfCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
        }

        public LocalDateTime roundHalfEvenCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
        }
    }
}

