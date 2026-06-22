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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.joda.convert.FromString;
import org.joda.convert.ToString;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
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

public final class LocalTime
extends BaseLocal
implements ReadablePartial,
Serializable {
    private static final long serialVersionUID = -12873158713873L;
    public static final LocalTime MIDNIGHT = new LocalTime(0, 0, 0, 0);
    private static final int HOUR_OF_DAY = 0;
    private static final int MINUTE_OF_HOUR = 1;
    private static final int SECOND_OF_MINUTE = 2;
    private static final int MILLIS_OF_SECOND = 3;
    private static final Set<DurationFieldType> TIME_DURATION_TYPES = new HashSet<DurationFieldType>();
    private final long iLocalMillis;
    private final Chronology iChronology;

    public static LocalTime now() {
        return new LocalTime();
    }

    public static LocalTime now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalTime(dateTimeZone);
    }

    public static LocalTime now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalTime(chronology);
    }

    @FromString
    public static LocalTime parse(String string) {
        return LocalTime.parse(string, ISODateTimeFormat.localTimeParser());
    }

    public static LocalTime parse(String string, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseLocalTime(string);
    }

    public static LocalTime fromMillisOfDay(long l) {
        return LocalTime.fromMillisOfDay(l, null);
    }

    public static LocalTime fromMillisOfDay(long l, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology).withUTC();
        return new LocalTime(l, chronology);
    }

    public static LocalTime fromCalendarFields(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new LocalTime(calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(14));
    }

    public static LocalTime fromDateFields(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new LocalTime(date.getHours(), date.getMinutes(), date.getSeconds(), ((int)(date.getTime() % 1000L) + 1000) % 1000);
    }

    public LocalTime() {
        this(DateTimeUtils.currentTimeMillis(), (Chronology)ISOChronology.getInstance());
    }

    public LocalTime(DateTimeZone dateTimeZone) {
        this(DateTimeUtils.currentTimeMillis(), (Chronology)ISOChronology.getInstance(dateTimeZone));
    }

    public LocalTime(Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }

    public LocalTime(long l) {
        this(l, (Chronology)ISOChronology.getInstance());
    }

    public LocalTime(long l, DateTimeZone dateTimeZone) {
        this(l, (Chronology)ISOChronology.getInstance(dateTimeZone));
    }

    public LocalTime(long l, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        long l2 = chronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, l);
        chronology = chronology.withUTC();
        this.iLocalMillis = chronology.millisOfDay().get(l2);
        this.iChronology = chronology;
    }

    public LocalTime(Object object) {
        this(object, (Chronology)null);
    }

    public LocalTime(Object object, DateTimeZone dateTimeZone) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        Chronology chronology = partialConverter.getChronology(object, dateTimeZone);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        int[] nArray = partialConverter.getPartialValues(this, object, chronology, ISODateTimeFormat.localTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(0L, nArray[0], nArray[1], nArray[2], nArray[3]);
    }

    public LocalTime(Object object, Chronology chronology) {
        PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(object);
        chronology = partialConverter.getChronology(object, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        int[] nArray = partialConverter.getPartialValues(this, object, chronology, ISODateTimeFormat.localTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(0L, nArray[0], nArray[1], nArray[2], nArray[3]);
    }

    public LocalTime(int n, int n2) {
        this(n, n2, 0, 0, ISOChronology.getInstanceUTC());
    }

    public LocalTime(int n, int n2, int n3) {
        this(n, n2, n3, 0, ISOChronology.getInstanceUTC());
    }

    public LocalTime(int n, int n2, int n3, int n4) {
        this(n, n2, n3, n4, ISOChronology.getInstanceUTC());
    }

    public LocalTime(int n, int n2, int n3, int n4, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology).withUTC();
        long l = chronology.getDateTimeMillis(0L, n, n2, n3, n4);
        this.iChronology = chronology;
        this.iLocalMillis = l;
    }

    private Object readResolve() {
        if (this.iChronology == null) {
            return new LocalTime(this.iLocalMillis, (Chronology)ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
            return new LocalTime(this.iLocalMillis, this.iChronology.withUTC());
        }
        return this;
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

    public int getValue(int n) {
        switch (n) {
            case 0: {
                return this.getChronology().hourOfDay().get(this.getLocalMillis());
            }
            case 1: {
                return this.getChronology().minuteOfHour().get(this.getLocalMillis());
            }
            case 2: {
                return this.getChronology().secondOfMinute().get(this.getLocalMillis());
            }
            case 3: {
                return this.getChronology().millisOfSecond().get(this.getLocalMillis());
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
        if (!this.isSupported(dateTimeFieldType.getDurationType())) {
            return false;
        }
        DurationFieldType durationFieldType = dateTimeFieldType.getRangeDurationType();
        return this.isSupported(durationFieldType) || durationFieldType == DurationFieldType.days();
    }

    public boolean isSupported(DurationFieldType durationFieldType) {
        if (durationFieldType == null) {
            return false;
        }
        DurationField durationField = durationFieldType.getField(this.getChronology());
        if (TIME_DURATION_TYPES.contains(durationFieldType) || durationField.getUnitMillis() < this.getChronology().days().getUnitMillis()) {
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
        if (object instanceof LocalTime) {
            LocalTime localTime = (LocalTime)object;
            if (this.iChronology.equals(localTime.iChronology)) {
                return this.iLocalMillis == localTime.iLocalMillis;
            }
        }
        return super.equals(object);
    }

    public int compareTo(ReadablePartial readablePartial) {
        if (this == readablePartial) {
            return 0;
        }
        if (readablePartial instanceof LocalTime) {
            LocalTime localTime = (LocalTime)readablePartial;
            if (this.iChronology.equals(localTime.iChronology)) {
                return this.iLocalMillis < localTime.iLocalMillis ? -1 : (this.iLocalMillis == localTime.iLocalMillis ? 0 : 1);
            }
        }
        return super.compareTo(readablePartial);
    }

    LocalTime withLocalMillis(long l) {
        return l == this.getLocalMillis() ? this : new LocalTime(l, this.getChronology());
    }

    public LocalTime withFields(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().set(readablePartial, this.getLocalMillis()));
    }

    public LocalTime withField(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (!this.isSupported(dateTimeFieldType)) {
            throw new IllegalArgumentException("Field '" + dateTimeFieldType + "' is not supported");
        }
        long l = dateTimeFieldType.getField(this.getChronology()).set(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime withFieldAdded(DurationFieldType durationFieldType, int n) {
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

    public LocalTime withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        long l = this.getChronology().add(readablePeriod, this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public LocalTime plusHours(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().hours().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime plusMinutes(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().minutes().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime plusSeconds(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().seconds().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime plusMillis(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().millis().add(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public LocalTime minusHours(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().hours().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime minusMinutes(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().minutes().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime minusSeconds(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().seconds().subtract(this.getLocalMillis(), n);
        return this.withLocalMillis(l);
    }

    public LocalTime minusMillis(int n) {
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

    public LocalTime withHourOfDay(int n) {
        return this.withLocalMillis(this.getChronology().hourOfDay().set(this.getLocalMillis(), n));
    }

    public LocalTime withMinuteOfHour(int n) {
        return this.withLocalMillis(this.getChronology().minuteOfHour().set(this.getLocalMillis(), n));
    }

    public LocalTime withSecondOfMinute(int n) {
        return this.withLocalMillis(this.getChronology().secondOfMinute().set(this.getLocalMillis(), n));
    }

    public LocalTime withMillisOfSecond(int n) {
        return this.withLocalMillis(this.getChronology().millisOfSecond().set(this.getLocalMillis(), n));
    }

    public LocalTime withMillisOfDay(int n) {
        return this.withLocalMillis(this.getChronology().millisOfDay().set(this.getLocalMillis(), n));
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

    public DateTime toDateTimeToday() {
        return this.toDateTimeToday(null);
    }

    public DateTime toDateTimeToday(DateTimeZone dateTimeZone) {
        Chronology chronology = this.getChronology().withZone(dateTimeZone);
        long l = DateTimeUtils.currentTimeMillis();
        long l2 = chronology.set(this, l);
        return new DateTime(l2, chronology);
    }

    @ToString
    public String toString() {
        return ISODateTimeFormat.time().print(this);
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
        TIME_DURATION_TYPES.add(DurationFieldType.millis());
        TIME_DURATION_TYPES.add(DurationFieldType.seconds());
        TIME_DURATION_TYPES.add(DurationFieldType.minutes());
        TIME_DURATION_TYPES.add(DurationFieldType.hours());
    }

    public static final class Property
    extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -325842547277223L;
        private transient LocalTime iInstant;
        private transient DateTimeField iField;

        Property(LocalTime localTime, DateTimeField dateTimeField) {
            this.iInstant = localTime;
            this.iField = dateTimeField;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (LocalTime)objectInputStream.readObject();
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

        public LocalTime getLocalTime() {
            return this.iInstant;
        }

        public LocalTime addCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }

        public LocalTime addCopy(long l) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), l));
        }

        public LocalTime addNoWrapToCopy(int n) {
            long l = this.iField.add(this.iInstant.getLocalMillis(), n);
            long l2 = this.iInstant.getChronology().millisOfDay().get(l);
            if (l2 != l) {
                throw new IllegalArgumentException("The addition exceeded the boundaries of LocalTime");
            }
            return this.iInstant.withLocalMillis(l);
        }

        public LocalTime addWrapFieldToCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), n));
        }

        public LocalTime setCopy(int n) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), n));
        }

        public LocalTime setCopy(String string, Locale locale) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), string, locale));
        }

        public LocalTime setCopy(String string) {
            return this.setCopy(string, null);
        }

        public LocalTime withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }

        public LocalTime withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }

        public LocalTime roundFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
        }

        public LocalTime roundCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
        }

        public LocalTime roundHalfFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
        }

        public LocalTime roundHalfCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
        }

        public LocalTime roundHalfEvenCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
        }
    }
}

