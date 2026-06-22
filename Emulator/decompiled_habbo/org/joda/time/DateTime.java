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
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.IllegalInstantException;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;
import org.joda.time.TimeOfDay;
import org.joda.time.YearMonthDay;
import org.joda.time.base.BaseDateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class DateTime
extends BaseDateTime
implements ReadableDateTime,
Serializable {
    private static final long serialVersionUID = -5171125899451703815L;

    public static DateTime now() {
        return new DateTime();
    }

    public static DateTime now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new DateTime(dateTimeZone);
    }

    public static DateTime now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new DateTime(chronology);
    }

    @FromString
    public static DateTime parse(String string) {
        return DateTime.parse(string, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
    }

    public static DateTime parse(String string, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(string);
    }

    public DateTime() {
    }

    public DateTime(DateTimeZone dateTimeZone) {
        super(dateTimeZone);
    }

    public DateTime(Chronology chronology) {
        super(chronology);
    }

    public DateTime(long l) {
        super(l);
    }

    public DateTime(long l, DateTimeZone dateTimeZone) {
        super(l, dateTimeZone);
    }

    public DateTime(long l, Chronology chronology) {
        super(l, chronology);
    }

    public DateTime(Object object) {
        super(object, (Chronology)null);
    }

    public DateTime(Object object, DateTimeZone dateTimeZone) {
        super(object, dateTimeZone);
    }

    public DateTime(Object object, Chronology chronology) {
        super(object, DateTimeUtils.getChronology(chronology));
    }

    public DateTime(int n, int n2, int n3, int n4, int n5) {
        super(n, n2, n3, n4, n5, 0, 0);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, 0, 0, dateTimeZone);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, Chronology chronology) {
        super(n, n2, n3, n4, n5, 0, 0, chronology);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, int n6) {
        super(n, n2, n3, n4, n5, n6, 0);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, int n6, DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, n6, 0, dateTimeZone);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, int n6, Chronology chronology) {
        super(n, n2, n3, n4, n5, n6, 0, chronology);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        super(n, n2, n3, n4, n5, n6, n7);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7, DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, n6, n7, dateTimeZone);
    }

    public DateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7, Chronology chronology) {
        super(n, n2, n3, n4, n5, n6, n7, chronology);
    }

    public DateTime toDateTime() {
        return this;
    }

    public DateTime toDateTimeISO() {
        if (this.getChronology() == ISOChronology.getInstance()) {
            return this;
        }
        return super.toDateTimeISO();
    }

    public DateTime toDateTime(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        if (this.getZone() == dateTimeZone) {
            return this;
        }
        return super.toDateTime(dateTimeZone);
    }

    public DateTime toDateTime(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if (this.getChronology() == chronology) {
            return this;
        }
        return super.toDateTime(chronology);
    }

    public DateTime withMillis(long l) {
        return l == this.getMillis() ? this : new DateTime(l, this.getChronology());
    }

    public DateTime withChronology(Chronology chronology) {
        return (chronology = DateTimeUtils.getChronology(chronology)) == this.getChronology() ? this : new DateTime(this.getMillis(), chronology);
    }

    public DateTime withZone(DateTimeZone dateTimeZone) {
        return this.withChronology(this.getChronology().withZone(dateTimeZone));
    }

    public DateTime withZoneRetainFields(DateTimeZone dateTimeZone) {
        DateTimeZone dateTimeZone2;
        if ((dateTimeZone = DateTimeUtils.getZone(dateTimeZone)) == (dateTimeZone2 = DateTimeUtils.getZone(this.getZone()))) {
            return this;
        }
        long l = dateTimeZone2.getMillisKeepLocal(dateTimeZone, this.getMillis());
        return new DateTime(l, this.getChronology().withZone(dateTimeZone));
    }

    public DateTime withEarlierOffsetAtOverlap() {
        long l = this.getZone().adjustOffset(this.getMillis(), false);
        return this.withMillis(l);
    }

    public DateTime withLaterOffsetAtOverlap() {
        long l = this.getZone().adjustOffset(this.getMillis(), true);
        return this.withMillis(l);
    }

    public DateTime withDate(int n, int n2, int n3) {
        Chronology chronology = this.getChronology();
        long l = chronology.withUTC().getDateTimeMillis(n, n2, n3, this.getMillisOfDay());
        return this.withMillis(chronology.getZone().convertLocalToUTC(l, false, this.getMillis()));
    }

    public DateTime withDate(LocalDate localDate) {
        return this.withDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    public DateTime withTime(int n, int n2, int n3, int n4) {
        Chronology chronology = this.getChronology();
        long l = chronology.withUTC().getDateTimeMillis(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), n, n2, n3, n4);
        return this.withMillis(chronology.getZone().convertLocalToUTC(l, false, this.getMillis()));
    }

    public DateTime withTime(LocalTime localTime) {
        return this.withTime(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute(), localTime.getMillisOfSecond());
    }

    public DateTime withTimeAtStartOfDay() {
        return this.toLocalDate().toDateTimeAtStartOfDay(this.getZone());
    }

    public DateTime withFields(ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withMillis(this.getChronology().set(readablePartial, this.getMillis()));
    }

    public DateTime withField(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        long l = dateTimeFieldType.getField(this.getChronology()).set(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime withFieldAdded(DurationFieldType durationFieldType, int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n == 0) {
            return this;
        }
        long l = durationFieldType.getField(this.getChronology()).add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime withDurationAdded(long l, int n) {
        if (l == 0L || n == 0) {
            return this;
        }
        long l2 = this.getChronology().add(this.getMillis(), l, n);
        return this.withMillis(l2);
    }

    public DateTime withDurationAdded(ReadableDuration readableDuration, int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }

    public DateTime withPeriodAdded(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        long l = this.getChronology().add(readablePeriod, this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plus(long l) {
        return this.withDurationAdded(l, 1);
    }

    public DateTime plus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }

    public DateTime plus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }

    public DateTime plusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plusHours(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().hours().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plusMinutes(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().minutes().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plusSeconds(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().seconds().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime plusMillis(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().millis().add(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minus(long l) {
        return this.withDurationAdded(l, -1);
    }

    public DateTime minus(ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }

    public DateTime minus(ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }

    public DateTime minusYears(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().years().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minusMonths(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().months().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minusWeeks(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().weeks().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minusDays(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().days().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minusHours(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().hours().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minusMinutes(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().minutes().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minusSeconds(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().seconds().subtract(this.getMillis(), n);
        return this.withMillis(l);
    }

    public DateTime minusMillis(int n) {
        if (n == 0) {
            return this;
        }
        long l = this.getChronology().millis().subtract(this.getMillis(), n);
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
    public DateMidnight toDateMidnight() {
        return new DateMidnight(this.getMillis(), this.getChronology());
    }

    @Deprecated
    public YearMonthDay toYearMonthDay() {
        return new YearMonthDay(this.getMillis(), this.getChronology());
    }

    @Deprecated
    public TimeOfDay toTimeOfDay() {
        return new TimeOfDay(this.getMillis(), this.getChronology());
    }

    public LocalDateTime toLocalDateTime() {
        return new LocalDateTime(this.getMillis(), this.getChronology());
    }

    public LocalDate toLocalDate() {
        return new LocalDate(this.getMillis(), this.getChronology());
    }

    public LocalTime toLocalTime() {
        return new LocalTime(this.getMillis(), this.getChronology());
    }

    public DateTime withEra(int n) {
        return this.withMillis(this.getChronology().era().set(this.getMillis(), n));
    }

    public DateTime withCenturyOfEra(int n) {
        return this.withMillis(this.getChronology().centuryOfEra().set(this.getMillis(), n));
    }

    public DateTime withYearOfEra(int n) {
        return this.withMillis(this.getChronology().yearOfEra().set(this.getMillis(), n));
    }

    public DateTime withYearOfCentury(int n) {
        return this.withMillis(this.getChronology().yearOfCentury().set(this.getMillis(), n));
    }

    public DateTime withYear(int n) {
        return this.withMillis(this.getChronology().year().set(this.getMillis(), n));
    }

    public DateTime withWeekyear(int n) {
        return this.withMillis(this.getChronology().weekyear().set(this.getMillis(), n));
    }

    public DateTime withMonthOfYear(int n) {
        return this.withMillis(this.getChronology().monthOfYear().set(this.getMillis(), n));
    }

    public DateTime withWeekOfWeekyear(int n) {
        return this.withMillis(this.getChronology().weekOfWeekyear().set(this.getMillis(), n));
    }

    public DateTime withDayOfYear(int n) {
        return this.withMillis(this.getChronology().dayOfYear().set(this.getMillis(), n));
    }

    public DateTime withDayOfMonth(int n) {
        return this.withMillis(this.getChronology().dayOfMonth().set(this.getMillis(), n));
    }

    public DateTime withDayOfWeek(int n) {
        return this.withMillis(this.getChronology().dayOfWeek().set(this.getMillis(), n));
    }

    public DateTime withHourOfDay(int n) {
        return this.withMillis(this.getChronology().hourOfDay().set(this.getMillis(), n));
    }

    public DateTime withMinuteOfHour(int n) {
        return this.withMillis(this.getChronology().minuteOfHour().set(this.getMillis(), n));
    }

    public DateTime withSecondOfMinute(int n) {
        return this.withMillis(this.getChronology().secondOfMinute().set(this.getMillis(), n));
    }

    public DateTime withMillisOfSecond(int n) {
        return this.withMillis(this.getChronology().millisOfSecond().set(this.getMillis(), n));
    }

    public DateTime withMillisOfDay(int n) {
        return this.withMillis(this.getChronology().millisOfDay().set(this.getMillis(), n));
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

    public Property minuteOfDay() {
        return new Property(this, this.getChronology().minuteOfDay());
    }

    public Property minuteOfHour() {
        return new Property(this, this.getChronology().minuteOfHour());
    }

    public Property secondOfDay() {
        return new Property(this, this.getChronology().secondOfDay());
    }

    public Property secondOfMinute() {
        return new Property(this, this.getChronology().secondOfMinute());
    }

    public Property millisOfDay() {
        return new Property(this, this.getChronology().millisOfDay());
    }

    public Property millisOfSecond() {
        return new Property(this, this.getChronology().millisOfSecond());
    }

    public static final class Property
    extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -6983323811635733510L;
        private DateTime iInstant;
        private DateTimeField iField;

        Property(DateTime dateTime, DateTimeField dateTimeField) {
            this.iInstant = dateTime;
            this.iField = dateTimeField;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (DateTime)objectInputStream.readObject();
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

        public DateTime getDateTime() {
            return this.iInstant;
        }

        public DateTime addToCopy(int n) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), n));
        }

        public DateTime addToCopy(long l) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), l));
        }

        public DateTime addWrapFieldToCopy(int n) {
            return this.iInstant.withMillis(this.iField.addWrapField(this.iInstant.getMillis(), n));
        }

        public DateTime setCopy(int n) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), n));
        }

        public DateTime setCopy(String string, Locale locale) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), string, locale));
        }

        public DateTime setCopy(String string) {
            return this.setCopy(string, null);
        }

        public DateTime withMaximumValue() {
            try {
                return this.setCopy(this.getMaximumValue());
            }
            catch (RuntimeException runtimeException) {
                if (IllegalInstantException.isIllegalInstant(runtimeException)) {
                    long l = this.getChronology().getZone().previousTransition(this.getMillis() + 86400000L);
                    return new DateTime(l, this.getChronology());
                }
                throw runtimeException;
            }
        }

        public DateTime withMinimumValue() {
            try {
                return this.setCopy(this.getMinimumValue());
            }
            catch (RuntimeException runtimeException) {
                if (IllegalInstantException.isIllegalInstant(runtimeException)) {
                    long l = this.getChronology().getZone().nextTransition(this.getMillis() - 86400000L);
                    return new DateTime(l, this.getChronology());
                }
                throw runtimeException;
            }
        }

        public DateTime roundFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundFloor(this.iInstant.getMillis()));
        }

        public DateTime roundCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundCeiling(this.iInstant.getMillis()));
        }

        public DateTime roundHalfFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfFloor(this.iInstant.getMillis()));
        }

        public DateTime roundHalfCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfCeiling(this.iInstant.getMillis()));
        }

        public DateTime roundHalfEvenCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfEven(this.iInstant.getMillis()));
        }
    }
}

