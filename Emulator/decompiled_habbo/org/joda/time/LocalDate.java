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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BaseLocal;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.PartialConverter;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class LocalDate
extends BaseLocal
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = -8775358157899L;
    private static final int YEAR = 0;
    private static final int MONTH_OF_YEAR = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final Set<DurationFieldType> DATE_DURATION_TYPES = new HashSet<DurationFieldType>();
    private final long iLocalMillis;
    private final Chronology iChronology;
    private transient int iHash;

    public static LocalDate now() {
        return new LocalDate();
    }

    public static LocalDate now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalDate(dateTimeZone);
    }

    public static LocalDate now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalDate(chronology);
    }

    @FromString
    public static LocalDate parse(String string) {
        return LocalDate.parse(string, ISODateTimeFormat.localDateParser());
    }

    public static LocalDate parse(String string, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseLocalDate(string);
    }

    public static LocalDate fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        int n = calendar.get(0);
        int n2 = calendar.get(1);
        return new LocalDate(n == 1 ? n2 : 1 - n2, calendar.get(2) + 1, calendar.get(5));
    }

    public static LocalDate fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (date.getTime() < 0L) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            return LocalDate.fromCalendarFields(gregorianCalendar);
        }
        return new LocalDate(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    public LocalDate() {
        this(DateTimeUtils.currentTimeMillis(), (Chronology)ISOChronology.getInstance());
    }

    public LocalDate(DateTimeZone dateTimeZone) {
        this(DateTimeUtils.currentTimeMillis(), (Chronology)ISOChronology.getInstance(dateTimeZone));
    }

    public LocalDate(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }

    public LocalDate(long l) {
        this(l, (Chronology)ISOChronology.getInstance());
    }

    public LocalDate(long l, DateTimeZone dateTimeZone) {
        this(l, (Chronology)ISOChronology.getInstance(dateTimeZone));
    }

    public LocalDate(long l, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        long l2 = chronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, l);
        chronology = chronology.withUTC();
        this.iLocalMillis = chronology.dayOfMonth().roundFloor(l2);
        this.iChronology = chronology;
    }

    public LocalDate(Object object) {
        this(object, (Chronology)null);
    }

    public LocalDate(Object object, DateTimeZone dateTimeZone) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        Chronology chronology = partialConverter.getChronology(object, dateTimeZone);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        int[] nArray = partialConverter.getPartialValues(this, object, chronology, ISODateTimeFormat.localDateParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(nArray[0], nArray[1], nArray[2], 0);
    }

    public LocalDate(Object object, Chronology chronology) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        chronology = partialConverter.getChronology(object, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        int[] nArray = partialConverter.getPartialValues(this, object, chronology, ISODateTimeFormat.localDateParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(nArray[0], nArray[1], nArray[2], 0);
    }

    public LocalDate(int n, int n2, int n3) {
        this(n, n2, n3, ISOChronology.getInstanceUTC());
    }

    public LocalDate(int n, int n2, int n3, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology).withUTC();
        long l = chronology.getDateTimeMillis(n, n2, n3, 0);
        this.iChronology = chronology;
        this.iLocalMillis = l;
    }

    private Object readResolve() {
        if (this.iChronology == null) {
            return new LocalDate(this.iLocalMillis, (Chronology)ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
            return new LocalDate(this.iLocalMillis, this.iChronology.withUTC());
        }
        return this;
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
        }
        throw new IndexOutOfBoundsException("Invalid index: " + n);
    }

    public int get(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        if (!this.isSupported(dateTimeFieldType)) {
            throw new IllegalArgumentException("Field '" + dateTimeFieldType + "' is not supported");
        }
        return dateTimeFieldType.getField(this.getChronology()).get(this.getLocalMillis());
    }

    public boolean isSupported(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            return false;
        }
        DurationFieldType durationFieldType = dateTimeFieldType.getDurationType();
        if (DATE_DURATION_TYPES.contains(durationFieldType) || durationFieldType.getField(this.getChronology()).getUnitMillis() >= this.getChronology().days().getUnitMillis()) {
            return dateTimeFieldType.getField(this.getChronology()).isSupported();
        }
        return false;
    }

    public boolean isSupported(DurationFieldType durationFieldType) {
        if (durationFieldType == null) {
            return false;
        }
        DurationField durationField = durationFieldType.getField(this.getChronology());
        if (DATE_DURATION_TYPES.contains(durationFieldType) || durationField.getUnitMillis() >= this.getChronology().days().getUnitMillis()) {
            return durationField.isSupported();
        }
        return false;
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
        if (object instanceof LocalDate) {
            LocalDate localDate = (LocalDate)object;
            if (this.iChronology.equals(localDate.iChronology)) {
                return this.iLocalMillis == localDate.iLocalMillis;
            }
        }
        return super.equals(object);
    }

    public int hashCode() {
        int n = this.iHash;
        if (n == 0) {
            n = this.iHash = super.hashCode();
        }
        return n;
    }

    public int compareTo(ReadablePartial readablePartial) {
        if (this == readablePartial) {
            return 0;
        }
        if (readablePartial instanceof LocalDate) {
            LocalDate localDate = (LocalDate)readablePartial;
            if (this.iChronology.equals(localDate.iChronology)) {
                return this.iLocalMillis < localDate.iLocalMillis ? -1 : (this.iLocalMillis == localDate.iLocalMillis ? 0 : 1);
            }
        }
        return super.compareTo(readablePartial);
    }

    public DateTime toDateTimeAtStartOfDay() {
        return this.toDateTimeAtStartOfDay(null);
    }

    public DateTime toDateTimeAtStartOfDay(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        long l = this.getLocalMillis() + 21600000L;
        long l2 = dateTimeZone.convertLocalToUTC(l, false);
        l2 = chronology.dayOfMonth().roundFloor(l2);
        return new DateTime(l2, chronology).withEarlierOffsetAtOverlap();
    }

    @Deprecated
    public DateTime toDateTimeAtMidnight() {
        return this.toDateTimeAtMidnight(null);
    }

    @Deprecated
    public DateTime toDateTimeAtMidnight(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), 0, 0, 0, 0, chronology);
    }

    public DateTime toDateTimeAtCurrentTime() {
        return this.toDateTimeAtCurrentTime(null);
    }

    public DateTime toDateTimeAtCurrentTime(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        long l = DateTimeUtils.currentTimeMillis();
        long l2 = chronology.set(this, l);
        return new DateTime(l2, chronology);
    }

    @Deprecated
    public DateMidnight toDateMidnight() {
        return this.toDateMidnight(null);
    }

    @Deprecated
    public DateMidnight toDateMidnight(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        return new DateMidnight(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), chronology);
    }

    public LocalDateTime toLocalDateTime(LocalTime localTime) {
        if (localTime == null) {
            throw new IllegalArgumentException("The time must not be null");
        }
        if (this.getChronology() != localTime.getChronology()) {
            throw new IllegalArgumentException("The chronology of the time does not match");
        }
        long l = this.getLocalMillis() + localTime.getLocalMillis();
        return new LocalDateTime(l, this.getChronology());
    }

    public DateTime toDateTime(LocalTime localTime) {
        return this.toDateTime(localTime, null);
    }

    public DateTime toDateTime(LocalTime localTime, DateTimeZone dateTimeZone) {
        if (localTime == null) {
            return this.toDateTimeAtCurrentTime(dateTimeZone);
        }
        if (this.getChronology() != localTime.getChronology()) {
            throw new IllegalArgumentException("The chronology of the time does not match");
        }
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute(), localTime.getMillisOfSecond(), chronology);
    }

    public Interval toInterval() {
        return this.toInterval(null);
    }

    public Interval toInterval(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        DateTime dateTime = this.toDateTimeAtStartOfDay(dateTimeZone);
        DateTime dateTime2 = this.plusDays(1).toDateTimeAtStartOfDay(dateTimeZone);
        return new Interval((ReadableInstant)dateTime, (ReadableInstant)dateTime2);
    }

    public Date toDate() {
        Date date;
        int n = this.getDayOfMonth();
        Date date2 = new Date(this.getYear() - 1900, this.getMonthOfYear() - 1, n);
        LocalDate localDate = LocalDate.fromDateFields(date2);
        if (localDate.isBefore(this)) {
            while (!localDate.equals(this)) {
                date2.setTime(date2.getTime() + 3600000L);
                localDate = LocalDate.fromDateFields(date2);
            }
            while (date2.getDate() == n) {
                date2.setTime(date2.getTime() - 1000L);
            }
            date2.setTime(date2.getTime() + 1000L);
        } else if (localDate.equals(this) && (date = new Date(date2.getTime() - (long)TimeZone.getDefault().getDSTSavings())).getDate() == n) {
            date2 = date;
        }
        return date2;
    }

    LocalDate withLocalMillis(long l) {
        l = this.iChronology.dayOfMonth().roundFloor(l);
        return l == this.getLocalMillis() ? this : new LocalDate(l, this.getChronology());
    }

    public LocalDate withFields(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().set(readablePartial, this.getLocalMillis()));
    }

    public LocalDate withField(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (!this.isSupported(dateTimeFieldType)) {
            throw new IllegalArgumentException("Field '" + dateTimeFieldType + "' is not supported");
        }
        long l = dateTimeFieldType.getField(this.getChronology()).set(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate withFieldAdded(DurationFieldType durationFieldType, int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (!this.isSupported(durationFieldType)) {
            throw new IllegalArgumentException("Field '" + durationFieldType + "' is not supported");
        }
        if (n == 0) {
            return this;
        }
        long l = durationFieldType.getField(this.getChronology()).add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        long l = this.getLocalMillis();
        Chronology chronology = this.getChronology();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            long l2 = FieldUtils.safeMultiply(readablePeriod.getValue(i), n);
            DurationFieldType durationFieldType = readablePeriod.getFieldType(i);
            if (!this.isSupported(durationFieldType)) continue;
            l = durationFieldType.getField(chronology).add(l, l2);
        }
        return this.withLocalMillis(l);
    }

    public LocalDate plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public LocalDate plusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate plusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate plusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate plusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public LocalDate minusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate minusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate minusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalDate minusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().subtract(this.getLocalMillis(), n);
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

    public LocalDate withEra(int n) {
        return this.withLocalMillis(this.getChronology().era().set(this.getLocalMillis(), n));
    }

    public LocalDate withCenturyOfEra(int n) {
        return this.withLocalMillis(this.getChronology().centuryOfEra().set(this.getLocalMillis(), n));
    }

    public LocalDate withYearOfEra(int n) {
        return this.withLocalMillis(this.getChronology().yearOfEra().set(this.getLocalMillis(), n));
    }

    public LocalDate withYearOfCentury(int n) {
        return this.withLocalMillis(this.getChronology().yearOfCentury().set(this.getLocalMillis(), n));
    }

    public LocalDate withYear(int n) {
        return this.withLocalMillis(this.getChronology().year().set(this.getLocalMillis(), n));
    }

    public LocalDate withWeekyear(int n) {
        return this.withLocalMillis(this.getChronology().weekyear().set(this.getLocalMillis(), n));
    }

    public LocalDate withMonthOfYear(int n) {
        return this.withLocalMillis(this.getChronology().monthOfYear().set(this.getLocalMillis(), n));
    }

    public LocalDate withWeekOfWeekyear(int n) {
        return this.withLocalMillis(this.getChronology().weekOfWeekyear().set(this.getLocalMillis(), n));
    }

    public LocalDate withDayOfYear(int n) {
        return this.withLocalMillis(this.getChronology().dayOfYear().set(this.getLocalMillis(), n));
    }

    public LocalDate withDayOfMonth(int n) {
        return this.withLocalMillis(this.getChronology().dayOfMonth().set(this.getLocalMillis(), n));
    }

    public LocalDate withDayOfWeek(int n) {
        return this.withLocalMillis(this.getChronology().dayOfWeek().set(this.getLocalMillis(), n));
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

    @ToString
    public String toString() {
        return ISODateTimeFormat.date().print(this);
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

    static {
        DATE_DURATION_TYPES.add(DurationFieldType.days());
        DATE_DURATION_TYPES.add(DurationFieldType.weeks());
        DATE_DURATION_TYPES.add(DurationFieldType.months());
        DATE_DURATION_TYPES.add(DurationFieldType.weekyears());
        DATE_DURATION_TYPES.add(DurationFieldType.years());
        DATE_DURATION_TYPES.add(DurationFieldType.centuries());
        DATE_DURATION_TYPES.add(DurationFieldType.eras());
    }

    public static final class Property
    extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -3193829732634L;
        private transient LocalDate iInstant;
        private transient DateTimeField iField;

        Property(LocalDate localDate, DateTimeField dateTimeField) {
            this.iInstant = localDate;
            this.iField = dateTimeField;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (LocalDate)objectInputStream.readObject();
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

        public LocalDate getLocalDate() {
            return this.iInstant;
        }

        public LocalDate addToCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }

        public LocalDate addWrapFieldToCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), n));
        }

        public LocalDate setCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), n));
        }

        public LocalDate setCopy(String string, Locale locale) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), string, locale));
        }

        public LocalDate setCopy(String string) {
            return this.setCopy(string, null);
        }

        public LocalDate withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }

        public LocalDate withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }

        public LocalDate roundFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
        }

        public LocalDate roundCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
        }

        public LocalDate roundHalfFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
        }

        public LocalDate roundHalfCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
        }

        public LocalDate roundHalfEvenCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
        }
    }
}

