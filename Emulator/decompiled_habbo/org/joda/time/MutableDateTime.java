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
import org.joda.time.ReadWritableDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BaseDateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class MutableDateTime
extends BaseDateTime
implements ReadWritableDateTime,
Cloneable,
Serializable {
    private static final long serialVersionUID = 2852608688135209575L;
    public static final int ROUND_NONE = 0;
    public static final int ROUND_FLOOR = 1;
    public static final int ROUND_CEILING = 2;
    public static final int ROUND_HALF_FLOOR = 3;
    public static final int ROUND_HALF_CEILING = 4;
    public static final int ROUND_HALF_EVEN = 5;
    private DateTimeField iRoundingField;
    private int iRoundingMode;

    public static MutableDateTime now() {
        return new MutableDateTime();
    }

    public static MutableDateTime now(DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new MutableDateTime(dateTimeZone);
    }

    public static MutableDateTime now(Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new MutableDateTime(chronology);
    }

    @FromString
    public static MutableDateTime parse(String string) {
        return MutableDateTime.parse(string, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
    }

    public static MutableDateTime parse(String string, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(string).toMutableDateTime();
    }

    public MutableDateTime() {
    }

    public MutableDateTime(DateTimeZone dateTimeZone) {
        super(dateTimeZone);
    }

    public MutableDateTime(Chronology chronology) {
        super(chronology);
    }

    public MutableDateTime(long l) {
        super(l);
    }

    public MutableDateTime(long l, DateTimeZone dateTimeZone) {
        super(l, dateTimeZone);
    }

    public MutableDateTime(long l, Chronology chronology) {
        super(l, chronology);
    }

    public MutableDateTime(Object object) {
        super(object, (Chronology)null);
    }

    public MutableDateTime(Object object, DateTimeZone dateTimeZone) {
        super(object, dateTimeZone);
    }

    public MutableDateTime(Object object, Chronology chronology) {
        super(object, DateTimeUtils.getChronology(chronology));
    }

    public MutableDateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        super(n, n2, n3, n4, n5, n6, n7);
    }

    public MutableDateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7, DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, n6, n7, dateTimeZone);
    }

    public MutableDateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7, Chronology chronology) {
        super(n, n2, n3, n4, n5, n6, n7, chronology);
    }

    public DateTimeField getRoundingField() {
        return this.iRoundingField;
    }

    public int getRoundingMode() {
        return this.iRoundingMode;
    }

    public void setRounding(DateTimeField dateTimeField) {
        this.setRounding(dateTimeField, 1);
    }

    public void setRounding(DateTimeField dateTimeField, int n) {
        if (dateTimeField != null && (n < 0 || n > 5)) {
            throw new IllegalArgumentException("Illegal rounding mode: " + n);
        }
        this.iRoundingField = n == 0 ? null : dateTimeField;
        this.iRoundingMode = dateTimeField == null ? 0 : n;
        this.setMillis(this.getMillis());
    }

    public void setMillis(long l) {
        switch (this.iRoundingMode) {
            case 0: {
                break;
            }
            case 1: {
                l = this.iRoundingField.roundFloor(l);
                break;
            }
            case 2: {
                l = this.iRoundingField.roundCeiling(l);
                break;
            }
            case 3: {
                l = this.iRoundingField.roundHalfFloor(l);
                break;
            }
            case 4: {
                l = this.iRoundingField.roundHalfCeiling(l);
                break;
            }
            case 5: {
                l = this.iRoundingField.roundHalfEven(l);
            }
        }
        super.setMillis(l);
    }

    public void setMillis(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        this.setMillis(l);
    }

    public void add(long l) {
        this.setMillis(FieldUtils.safeAdd(this.getMillis(), l));
    }

    public void add(ReadableDuration readableDuration) {
        this.add(readableDuration, 1);
    }

    public void add(ReadableDuration readableDuration, int n) {
        if (readableDuration != null) {
            this.add(FieldUtils.safeMultiply(readableDuration.getMillis(), n));
        }
    }

    public void add(ReadablePeriod readablePeriod) {
        this.add(readablePeriod, 1);
    }

    public void add(ReadablePeriod readablePeriod, int n) {
        if (readablePeriod != null) {
            this.setMillis(this.getChronology().add(readablePeriod, this.getMillis(), n));
        }
    }

    public void setChronology(Chronology chronology) {
        super.setChronology(chronology);
    }

    public void setZone(DateTimeZone dateTimeZone) {
        dateTimeZone = DateTimeUtils.getZone(dateTimeZone);
        Chronology chronology = this.getChronology();
        if (chronology.getZone() != dateTimeZone) {
            this.setChronology(chronology.withZone(dateTimeZone));
        }
    }

    public void setZoneRetainFields(DateTimeZone dateTimeZone) {
        DateTimeZone dateTimeZone2;
        if ((dateTimeZone = DateTimeUtils.getZone(dateTimeZone)) == (dateTimeZone2 = DateTimeUtils.getZone(this.getZone()))) {
            return;
        }
        long l = dateTimeZone2.getMillisKeepLocal(dateTimeZone, this.getMillis());
        this.setChronology(this.getChronology().withZone(dateTimeZone));
        this.setMillis(l);
    }

    public void set(DateTimeFieldType dateTimeFieldType, int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        this.setMillis(dateTimeFieldType.getField(this.getChronology()).set(this.getMillis(), n));
    }

    public void add(DurationFieldType durationFieldType, int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n != 0) {
            this.setMillis(durationFieldType.getField(this.getChronology()).add(this.getMillis(), n));
        }
    }

    public void setYear(int n) {
        this.setMillis(this.getChronology().year().set(this.getMillis(), n));
    }

    public void addYears(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().years().add(this.getMillis(), n));
        }
    }

    public void setWeekyear(int n) {
        this.setMillis(this.getChronology().weekyear().set(this.getMillis(), n));
    }

    public void addWeekyears(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().weekyears().add(this.getMillis(), n));
        }
    }

    public void setMonthOfYear(int n) {
        this.setMillis(this.getChronology().monthOfYear().set(this.getMillis(), n));
    }

    public void addMonths(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().months().add(this.getMillis(), n));
        }
    }

    public void setWeekOfWeekyear(int n) {
        this.setMillis(this.getChronology().weekOfWeekyear().set(this.getMillis(), n));
    }

    public void addWeeks(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().weeks().add(this.getMillis(), n));
        }
    }

    public void setDayOfYear(int n) {
        this.setMillis(this.getChronology().dayOfYear().set(this.getMillis(), n));
    }

    public void setDayOfMonth(int n) {
        this.setMillis(this.getChronology().dayOfMonth().set(this.getMillis(), n));
    }

    public void setDayOfWeek(int n) {
        this.setMillis(this.getChronology().dayOfWeek().set(this.getMillis(), n));
    }

    public void addDays(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().days().add(this.getMillis(), n));
        }
    }

    public void setHourOfDay(int n) {
        this.setMillis(this.getChronology().hourOfDay().set(this.getMillis(), n));
    }

    public void addHours(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().hours().add(this.getMillis(), n));
        }
    }

    public void setMinuteOfDay(int n) {
        this.setMillis(this.getChronology().minuteOfDay().set(this.getMillis(), n));
    }

    public void setMinuteOfHour(int n) {
        this.setMillis(this.getChronology().minuteOfHour().set(this.getMillis(), n));
    }

    public void addMinutes(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().minutes().add(this.getMillis(), n));
        }
    }

    public void setSecondOfDay(int n) {
        this.setMillis(this.getChronology().secondOfDay().set(this.getMillis(), n));
    }

    public void setSecondOfMinute(int n) {
        this.setMillis(this.getChronology().secondOfMinute().set(this.getMillis(), n));
    }

    public void addSeconds(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().seconds().add(this.getMillis(), n));
        }
    }

    public void setMillisOfDay(int n) {
        this.setMillis(this.getChronology().millisOfDay().set(this.getMillis(), n));
    }

    public void setMillisOfSecond(int n) {
        this.setMillis(this.getChronology().millisOfSecond().set(this.getMillis(), n));
    }

    public void addMillis(int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().millis().add(this.getMillis(), n));
        }
    }

    public void setDate(long l) {
        this.setMillis(this.getChronology().millisOfDay().set(l, this.getMillisOfDay()));
    }

    public void setDate(ReadableInstant readableInstant) {
        ReadableDateTime readableDateTime;
        Chronology chronology;
        DateTimeZone dateTimeZone;
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        if (readableInstant instanceof ReadableDateTime && (dateTimeZone = (chronology = DateTimeUtils.getChronology((readableDateTime = (ReadableDateTime)readableInstant).getChronology())).getZone()) != null) {
            l = dateTimeZone.getMillisKeepLocal(this.getZone(), l);
        }
        this.setDate(l);
    }

    public void setDate(int n, int n2, int n3) {
        Chronology chronology = this.getChronology();
        long l = chronology.getDateTimeMillis(n, n2, n3, 0);
        this.setDate(l);
    }

    public void setTime(long l) {
        int n = ISOChronology.getInstanceUTC().millisOfDay().get(l);
        this.setMillis(this.getChronology().millisOfDay().set(this.getMillis(), n));
    }

    public void setTime(ReadableInstant readableInstant) {
        long l = DateTimeUtils.getInstantMillis(readableInstant);
        Chronology chronology = DateTimeUtils.getInstantChronology(readableInstant);
        DateTimeZone dateTimeZone = chronology.getZone();
        if (dateTimeZone != null) {
            l = dateTimeZone.getMillisKeepLocal(DateTimeZone.UTC, l);
        }
        this.setTime(l);
    }

    public void setTime(int n, int n2, int n3, int n4) {
        long l = this.getChronology().getDateTimeMillis(this.getMillis(), n, n2, n3, n4);
        this.setMillis(l);
    }

    public void setDateTime(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        long l = this.getChronology().getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        this.setMillis(l);
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

    public MutableDateTime copy() {
        return (MutableDateTime)this.clone();
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError("Clone error");
        }
    }

    public static final class Property
    extends AbstractReadableInstantFieldProperty {
        private static final long serialVersionUID = -4481126543819298617L;
        private MutableDateTime iInstant;
        private DateTimeField iField;

        Property(MutableDateTime mutableDateTime, DateTimeField dateTimeField) {
            this.iInstant = mutableDateTime;
            this.iField = dateTimeField;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (MutableDateTime)objectInputStream.readObject();
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

        public MutableDateTime getMutableDateTime() {
            return this.iInstant;
        }

        public MutableDateTime add(int n) {
            this.iInstant.setMillis(this.getField().add(this.iInstant.getMillis(), n));
            return this.iInstant;
        }

        public MutableDateTime add(long l) {
            this.iInstant.setMillis(this.getField().add(this.iInstant.getMillis(), l));
            return this.iInstant;
        }

        public MutableDateTime addWrapField(int n) {
            this.iInstant.setMillis(this.getField().addWrapField(this.iInstant.getMillis(), n));
            return this.iInstant;
        }

        public MutableDateTime set(int n) {
            this.iInstant.setMillis(this.getField().set(this.iInstant.getMillis(), n));
            return this.iInstant;
        }

        public MutableDateTime set(String string, Locale locale) {
            this.iInstant.setMillis(this.getField().set(this.iInstant.getMillis(), string, locale));
            return this.iInstant;
        }

        public MutableDateTime set(String string) {
            this.set(string, null);
            return this.iInstant;
        }

        public MutableDateTime roundFloor() {
            this.iInstant.setMillis(this.getField().roundFloor(this.iInstant.getMillis()));
            return this.iInstant;
        }

        public MutableDateTime roundCeiling() {
            this.iInstant.setMillis(this.getField().roundCeiling(this.iInstant.getMillis()));
            return this.iInstant;
        }

        public MutableDateTime roundHalfFloor() {
            this.iInstant.setMillis(this.getField().roundHalfFloor(this.iInstant.getMillis()));
            return this.iInstant;
        }

        public MutableDateTime roundHalfCeiling() {
            this.iInstant.setMillis(this.getField().roundHalfCeiling(this.iInstant.getMillis()));
            return this.iInstant;
        }

        public MutableDateTime roundHalfEven() {
            this.iInstant.setMillis(this.getField().roundHalfEven(this.iInstant.getMillis()));
            return this.iInstant;
        }
    }
}

