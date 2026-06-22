/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.convert.FromString
 */
package org.joda.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import org.joda.convert.FromString;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.YearMonthDay;
import org.joda.time.base.BaseDateTime;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@Deprecated
public final class DateMidnight
extends BaseDateTime
implements ReadableDateTime,
Serializable {
    private static final long serialVersionUID = 156371964018738L;

    public static DateMidnight now() {
        return new DateMidnight();
    }

    public static DateMidnight now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new DateMidnight(dateTimeZone);
    }

    public static DateMidnight now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new DateMidnight(chronology);
    }

    @FromString
    public static DateMidnight parse(String string) {
        return DateMidnight.parse(string, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
    }

    public static DateMidnight parse(String string, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(string).toDateMidnight();
    }

    public DateMidnight() {
    }

    public DateMidnight(DateTimeZone dateTimeZone) {
        super(dateTimeZone);
    }

    public DateMidnight(Chronology chronology) {
        super(chronology);
    }

    public DateMidnight(long l) {
        super(l);
    }

    public DateMidnight(long l, DateTimeZone dateTimeZone) {
        super(l, dateTimeZone);
    }

    public DateMidnight(long l, Chronology chronology) {
        super(l, chronology);
    }

    public DateMidnight(Object object) {
        super(object, (Chronology)null);
    }

    public DateMidnight(Object object, DateTimeZone dateTimeZone) {
        super(object, dateTimeZone);
    }

    public DateMidnight(Object object, Chronology chronology) {
        super(object, DateTimeUtils.getChronology(chronology));
    }

    public DateMidnight(int n, int n2, int n3) {
        super(n, n2, n3, 0, 0, 0, 0);
    }

    public DateMidnight(int n, int n2, int n3, DateTimeZone dateTimeZone) {
        super(n, n2, n3, 0, 0, 0, 0, dateTimeZone);
    }

    public DateMidnight(int n, int n2, int n3, Chronology chronology) {
        super(n, n2, n3, 0, 0, 0, 0, chronology);
    }

    protected long checkInstant(long l, Chronology chronology) {
        return chronology.dayOfMonth().roundFloor(l);
    }

    public DateMidnight withMillis(long l) {
        Chronology chronology = this.getChronology();
        return (l = this.checkInstant(l, chronology)) == this.getMillis() ? this : new DateMidnight(l, chronology);
    }

    public DateMidnight withChronology(Chronology chronology) {
        return chronology == this.getChronology() ? this : new DateMidnight(this.getMillis(), chronology);
    }

    public DateMidnight withZoneRetainFields(DateTimeZone dateTimeZone) {
        DateTimeZone dateTimeZone2;
        if ((dateTimeZone = DateTimeUtils.getZone(dateTimeZone)) == (dateTimeZone2 = DateTimeUtils.getZone(this.getZone()))) {
            return this;
        }
        long l = dateTimeZone2.getMillisKeepLocal(dateTimeZone, this.getMillis());
        return new DateMidnight(l, this.getChronology().withZone(dateTimeZone));
    }

    public DateMidnight withFields(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withMillis(this.getChronology().set(readablePartial, this.getMillis()));
    }

    public DateMidnight withField(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        long l = dateTimeFieldType.getField(this.getChronology()).set(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight withFieldAdded(DurationFieldType durationFieldType, int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n == 0) {
            return this;
        }
        long l = durationFieldType.getField(this.getChronology()).add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight withDurationAdded(long l, int n) {
        if (l == 0L || n == 0) {
            return this;
        }
        long l2 = this.getChronology().add(this.getMillis(), l, n);
        return this.withMillis(l2);
    }

    public DateMidnight withDurationAdded(ReadableDuration readableDuration, int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }

    public DateMidnight withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        long l = this.getChronology().add(readablePeriod, this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight plus(long l) {
        return this.withDurationAdded(l, 1);
    }

    public DateMidnight plus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }

    public DateMidnight plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public DateMidnight plusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight plusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight plusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight plusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight minus(long l) {
        return this.withDurationAdded(l, -1);
    }

    public DateMidnight minus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }

    public DateMidnight minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public DateMidnight minusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight minusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight minusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateMidnight minusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public Property property(DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        DateTimeField dateTimeField = dateTimeFieldType.getField(this.getChronology());
        if (!dateTimeField.isSupported()) {
            throw new IllegalArgumentException("Field '" + dateTimeFieldType + "' is not supported");
        }
        return new Property(this, dateTimeField);
    }

    @Deprecated
    public YearMonthDay toYearMonthDay() {
        return new YearMonthDay(this.getMillis(), this.getChronology());
    }

    public LocalDate toLocalDate() {
        return new LocalDate(this.getMillis(), this.getChronology());
    }

    public Interval toInterval() {
        Chronology chronology = this.getChronology();
        long l = this.getMillis();
        long l2 = DurationFieldType.days().getField(chronology).add(l, 1);
        return new Interval(l, l2, chronology);
    }

    public DateMidnight withEra(int n) {
        return this.withMillis(this.getChronology().era().set(this.getMillis(), n));
    }

    public DateMidnight withCenturyOfEra(int n) {
        return this.withMillis(this.getChronology().centuryOfEra().set(this.getMillis(), n));
    }

    public DateMidnight withYearOfEra(int n) {
        return this.withMillis(this.getChronology().yearOfEra().set(this.getMillis(), n));
    }

    public DateMidnight withYearOfCentury(int n) {
        return this.withMillis(this.getChronology().yearOfCentury().set(this.getMillis(), n));
    }

    public DateMidnight withYear(int n) {
        return this.withMillis(this.getChronology().year().set(this.getMillis(), n));
    }

    public DateMidnight withWeekyear(int n) {
        return this.withMillis(this.getChronology().weekyear().set(this.getMillis(), n));
    }

    public DateMidnight withMonthOfYear(int n) {
        return this.withMillis(this.getChronology().monthOfYear().set(this.getMillis(), n));
    }

    public DateMidnight withWeekOfWeekyear(int n) {
        return this.withMillis(this.getChronology().weekOfWeekyear().set(this.getMillis(), n));
    }

    public DateMidnight withDayOfYear(int n) {
        return this.withMillis(this.getChronology().dayOfYear().set(this.getMillis(), n));
    }

    public DateMidnight withDayOfMonth(int n) {
        return this.withMillis(this.getChronology().dayOfMonth().set(this.getMillis(), n));
    }

    public DateMidnight withDayOfWeek(int n) {
        return this.withMillis(this.getChronology().dayOfWeek().set(this.getMillis(), n));
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

    public static final class Property
    extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = 257629620L;
        private DateMidnight iInstant;
        private DateTimeField iField;

        Property(DateMidnight dateMidnight, DateTimeField dateTimeField) {
            this.iInstant = dateMidnight;
            this.iField = dateTimeField;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (DateMidnight)objectInputStream.readObject();
            DateTimeFieldType dateTimeFieldType = (DateTimeFieldType)objectInputStream.readObject();
            this.iField = dateTimeFieldType.getField(this.iInstant.getChronology());
        }

        public DateTimeField getField() {
            return this.iField;
        }

        protected long getMillis() {
            return this.iInstant.getMillis();
        }

        protected Chronology getChronology() {
            return this.iInstant.getChronology();
        }

        public DateMidnight getDateMidnight() {
            return this.iInstant;
        }

        public DateMidnight addToCopy(int n) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), n));
        }

        public DateMidnight addToCopy(long l) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), l));
        }

        public DateMidnight addWrapFieldToCopy(int n) {
            return this.iInstant.withMillis(this.iField.addWrapField(this.iInstant.getMillis(), n));
        }

        public DateMidnight setCopy(int n) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), n));
        }

        public DateMidnight setCopy(String string, Locale locale) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), string, locale));
        }

        public DateMidnight setCopy(String string) {
            return this.setCopy(string, null);
        }

        public DateMidnight withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }

        public DateMidnight withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }

        public DateMidnight roundFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundFloor(this.iInstant.getMillis()));
        }

        public DateMidnight roundCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundCeiling(this.iInstant.getMillis()));
        }

        public DateMidnight roundHalfFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfFloor(this.iInstant.getMillis()));
        }

        public DateMidnight roundHalfCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfCeiling(this.iInstant.getMillis()));
        }

        public DateMidnight roundHalfEvenCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfEven(this.iInstant.getMillis()));
        }
    }
}

